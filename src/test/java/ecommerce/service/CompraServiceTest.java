package ecommerce.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ecommerce.dto.CompraDTO;
import ecommerce.dto.DisponibilidadeDTO;
import ecommerce.dto.EstoqueBaixaDTO;
import ecommerce.dto.PagamentoDTO;
import ecommerce.entity.CarrinhoDeCompras;
import ecommerce.entity.Cliente;
import ecommerce.entity.ItemCompra;
import ecommerce.entity.Produto;
import ecommerce.entity.TipoCliente;
import ecommerce.entity.TipoProduto;
import ecommerce.external.IEstoqueExternal;
import ecommerce.external.IPagamentoExternal;

@ExtendWith(MockitoExtension.class)
class CompraServiceTest {

	@Mock
	private CarrinhoDeComprasService carrinhoService;

	@Mock
	private ClienteService clienteService;

	@Mock
	private IEstoqueExternal estoqueExternal;

	@Mock
	private IPagamentoExternal pagamentoExternal;

	@InjectMocks
	private CompraService compraService;

	@Test
	void deveFinalizarCompraComSucesso() {
		Long carrinhoId = 1L;
		CarrinhoDeCompras carrinhoMock = criarCarrinhoMock();

		when(carrinhoService.buscarPorId(carrinhoId)).thenReturn(carrinhoMock);
		when(estoqueExternal.verificarDisponibilidade(any())).thenReturn(new DisponibilidadeDTO(List.of(1L, 2L)));
		when(pagamentoExternal.autorizarPagamento(anyLong(), any())).thenReturn(new PagamentoDTO(true, 12345L));
		when(estoqueExternal.darBaixa(any())).thenReturn(new EstoqueBaixaDTO(true));

		CompraDTO resultado = compraService.finalizarCompra(carrinhoId);

		assertNotNull(resultado);
		assertTrue(resultado.sucesso());
		assertEquals(12345L, resultado.transacaoPagamentoId());
		assertEquals("Compra finalizada com sucesso.", resultado.mensagem());
		verify(carrinhoService, times(1)).buscarPorId(carrinhoId);
		verify(estoqueExternal, times(1)).darBaixa(any());
	}

	@Test
	void deveLancarExcecaoQuandoEstoqueInsuficiente() {
		Long carrinhoId = 1L;
		CarrinhoDeCompras carrinhoMock = criarCarrinhoMock();
		when(carrinhoService.buscarPorId(carrinhoId)).thenReturn(carrinhoMock);

		when(estoqueExternal.verificarDisponibilidade(any())).thenReturn(new DisponibilidadeDTO(List.of()));

		IllegalStateException exception = assertThrows(IllegalStateException.class,
				() -> compraService.finalizarCompra(carrinhoId));
		assertEquals("Itens fora de estoque.", exception.getMessage());
		verify(estoqueExternal, never()).darBaixa(any());
	}

	@Test
	void deveLancarExcecaoQuandoPagamentoNaoAutorizado() {
		Long carrinhoId = 1L;
		CarrinhoDeCompras carrinhoMock = criarCarrinhoMock();

		when(carrinhoService.buscarPorId(carrinhoId)).thenReturn(carrinhoMock);
		when(estoqueExternal.verificarDisponibilidade(any())).thenReturn(new DisponibilidadeDTO(List.of(1L, 2L)));
		when(pagamentoExternal.autorizarPagamento(anyLong(), any())).thenReturn(new PagamentoDTO(false, null));

		IllegalStateException exception = assertThrows(IllegalStateException.class,
				() -> compraService.finalizarCompra(carrinhoId));
		assertEquals("Pagamento não autorizado.", exception.getMessage());
		verify(estoqueExternal, never()).darBaixa(any());
	}

	@Test
	void deveCancelarPagamentoQuandoErroAoAtualizarEstoque() {
		Long carrinhoId = 1L;
		CarrinhoDeCompras carrinhoMock = criarCarrinhoMock();

		when(carrinhoService.buscarPorId(carrinhoId)).thenReturn(carrinhoMock);
		when(estoqueExternal.verificarDisponibilidade(any())).thenReturn(new DisponibilidadeDTO(List.of(1L, 2L)));
		when(pagamentoExternal.autorizarPagamento(anyLong(), any())).thenReturn(new PagamentoDTO(true, 12345L));
		when(estoqueExternal.darBaixa(any())).thenReturn(new EstoqueBaixaDTO(false));

		IllegalStateException exception = assertThrows(IllegalStateException.class,
				() -> compraService.finalizarCompra(carrinhoId));

		assertEquals("Erro ao dar baixa no estoque.", exception.getMessage());
		verify(pagamentoExternal, times(1)).cancelarPagamento(12345L);
	}

	@ParameterizedTest
	@CsvSource({ "BRONZE, 5, 0", "BRONZE, 6, 12", "PRATA, 6, 6", "OURO, 6, 0", "PRATA, 12, 24", "OURO, 12, 0",
			"BRONZE, 51, 357" })
	void deveCalcularFreteBaseadoEmPesoETipoCliente(String tipoCliente, int pesoCarrinho,
			BigDecimal valorFreteEsperado) {
		Cliente cliente = new Cliente(1L, "Cliente Teste", "Endereço Teste", TipoCliente.valueOf(tipoCliente));
		CarrinhoDeCompras carrinho = criarCarrinhoComPeso(pesoCarrinho, cliente);

		BigDecimal resultado = compraService.cacularFrete(carrinho);

		resultado = resultado.setScale(2, RoundingMode.HALF_UP);
		valorFreteEsperado = valorFreteEsperado.setScale(2, RoundingMode.HALF_UP);

		assertEquals(valorFreteEsperado, resultado);
	}

	@ParameterizedTest
	@CsvSource({ "499, 499", "500, 500", "1000, 900", "1500, 1200" })
	void deveAplicarDescontoBaseadoNoValor(BigDecimal valorInicial, BigDecimal valorEsperado) {
		BigDecimal resultado = compraService.aplicarDescontoValor(valorInicial);

		resultado = resultado.setScale(2, RoundingMode.HALF_UP);
		valorEsperado = valorEsperado.setScale(2, RoundingMode.HALF_UP);

		assertEquals(valorEsperado, resultado);
	}

	@ParameterizedTest
	@CsvSource({
			// peso <= 5 -> frete 0
			"1, 1, 1, 1, 0", "2, 1, 3, 1, 0", "2, 1, 2, 1, 0", "4, 1, 1, 1, 0",
			// 5 < peso < 10 -> frete 2 * peso
			"5, 1, 1, 1, 12", "5, 1, 1, 2, 14", "3, 1, 3, 2, 18", "2, 1, 6, 1, 16",
			// 10 < peso < 50 -> frete 4 * peso
			"9, 1, 1, 1, 40", "8, 2, 2, 1, 72", "12, 1, 3, 1, 60", "15, 1, 10, 2, 140", "24, 2, 1, 1, 196",
			// peso >= 50 -> frete 7 * peso
			"24, 2, 2, 1, 350", "25, 2, 10, 1, 420", "20, 3, 10, 1, 490", "30, 1, 25, 1, 385", "40, 2, 10, 1, 630" })
	public void calcular_frete_por_preso(int pesoItem1, long quantidadeItem1, int pesoItem2, long quantidadeItem2,
			BigDecimal precoEsperado) {
		var cliente = new Cliente(0L, "Eiji", "Rua Teste", TipoCliente.OURO);
		var produto1 = new Produto(0L, "Lanchinho", "teste", BigDecimal.ONE, pesoItem1, TipoProduto.ALIMENTO);
		var produto2 = new Produto(1L, "Lanchinho", "teste", BigDecimal.ONE, pesoItem2, TipoProduto.ALIMENTO);

		var itemsCompra = Arrays.asList(new ItemCompra(0L, produto1, quantidadeItem1),
				new ItemCompra(1L, produto2, quantidadeItem2));

		var carrinho = new CarrinhoDeCompras(0L, cliente, itemsCompra, LocalDate.now());

		var valorFrete = compraService.calcularFretePorPeso(carrinho);

		assertEquals(precoEsperado.doubleValue(), valorFrete.doubleValue());
	}

	@ParameterizedTest
	@CsvSource({ "OURO, 0, 0", "OURO, 10, 0", "OURO, 123, 0", "OURO, 1000, 0", "OURO, 10000, 0",

			"PRATA, 0, 0", "PRATA, 10, 5", "PRATA, 100.5, 50.25", "PRATA, 123, 61.5", "PRATA, 1000, 500",

			"BRONZE, 0, 0", "BRONZE, 10, 10", "BRONZE, 123, 123", "BRONZE, 1000, 1000", "BRONZE, 10000, 10000" })
	public void calcular_desconto_do_frete_para_cliente(TipoCliente tipoCliente, BigDecimal valor,
			BigDecimal valorEsperado) {
		var cliente = new Cliente(0L, "Eiji", "Rua Teste", tipoCliente);

		var valorComDesconto = compraService.calcularFreteComDescontoCliente(cliente, valor);
		assertEquals(valorEsperado.doubleValue(), valorComDesconto.doubleValue());
	}

	@ParameterizedTest
	@MethodSource("fornecerDadosParaCalcularCustoTotal")
	void deveCalcularCustoTotalComSucesso(BigDecimal valorTotal, int pesoCarrinho, TipoCliente tipoCliente,
			BigDecimal custoEsperado) {
		Cliente cliente = new Cliente(1L, "Cliente Teste", "Endereço Teste", tipoCliente);
		CarrinhoDeCompras carrinho = criarCarrinho(valorTotal, pesoCarrinho, cliente);

		BigDecimal resultado = compraService.calcularCustoTotal(carrinho);

		resultado = resultado.setScale(2, RoundingMode.HALF_UP);
		custoEsperado = custoEsperado.setScale(2, RoundingMode.HALF_UP);

		assertEquals(custoEsperado, resultado);
	}

	@Test
	void deveLancarExcecaoQuandoCarrinhoEstiverVazio() {
		CarrinhoDeCompras carrinhoVazio = new CarrinhoDeCompras(1L, null, List.of(), null);
		when(carrinhoService.buscarPorId(anyLong())).thenReturn(carrinhoVazio);

		IllegalStateException exception = assertThrows(IllegalStateException.class,
				() -> compraService.finalizarCompra(1L));
		assertEquals("Nenhum produto válido no carrinho.", exception.getMessage());
		verify(estoqueExternal, never()).verificarDisponibilidade(any());
	}

	@Test
	void deveLancarExcecaoQuandoCarrinhoContemItensInvalidos() {
		
		List<ItemCompra> items = new ArrayList<>();
		
		ItemCompra itemNull = null;
		items.add(itemNull);
		items.add(new ItemCompra(2L, null, 1L));
		
	    CarrinhoDeCompras carrinhoMock = new CarrinhoDeCompras(1L, null, items, null);
	    when(carrinhoService.buscarPorId(1L)).thenReturn(carrinhoMock);

	    IllegalStateException exception = assertThrows(IllegalStateException.class,
	            () -> compraService.finalizarCompra(1L));
	    assertEquals("Nenhum produto válido no carrinho.", exception.getMessage());
	}

	
	private static Stream<Arguments> fornecerDadosParaCalcularCustoTotal() {
		return Stream.of(Arguments.of(BigDecimal.valueOf(499), 4, TipoCliente.BRONZE, BigDecimal.valueOf(499)),
				Arguments.of(BigDecimal.valueOf(500), 6, TipoCliente.PRATA, BigDecimal.valueOf(506)),
				Arguments.of(BigDecimal.valueOf(1000), 12, TipoCliente.OURO, BigDecimal.valueOf(900)),
				Arguments.of(BigDecimal.valueOf(1500), 50, TipoCliente.BRONZE, BigDecimal.valueOf(1200).add(BigDecimal.valueOf(350))),
				Arguments.of(BigDecimal.valueOf(0), 0, TipoCliente.BRONZE, BigDecimal.ZERO)
		);
	}

	private CarrinhoDeCompras criarCarrinho(BigDecimal valorTotal, int pesoCarrinho, Cliente cliente) {
		CarrinhoDeCompras carrinho = mock(CarrinhoDeCompras.class);

		when(carrinho.obterValorTotal()).thenReturn(valorTotal);
		when(carrinho.obterPesoTotal()).thenReturn(pesoCarrinho);
		when(carrinho.getCliente()).thenReturn(cliente);

		return carrinho;
	}

	private CarrinhoDeCompras criarCarrinhoMock() {
		Cliente cliente = new Cliente(1L, "Cliente Teste", "Endereço Teste", TipoCliente.BRONZE);
		Produto produto1 = new Produto(1L, "Produto 1", "Descrição", new BigDecimal("100"), 5, TipoProduto.ALIMENTO);
		Produto produto2 = new Produto(2L, "Produto 2", "Descrição", new BigDecimal("200"), 10, TipoProduto.ELETRONICO);
		ItemCompra item1 = new ItemCompra(1L, produto1, 2L);
		ItemCompra item2 = new ItemCompra(2L, produto2, 1L);
		return new CarrinhoDeCompras(1L, cliente, List.of(item1, item2), null);
	}

	private CarrinhoDeCompras criarCarrinhoComPeso(int peso, Cliente cliente) {
		Produto produto = new Produto(1L, "Produto", "Descrição", new BigDecimal("100"), peso, TipoProduto.ALIMENTO);
		ItemCompra item = new ItemCompra(1L, produto, 1L);
		return new CarrinhoDeCompras(1L, cliente, List.of(item), null);
	}
}
