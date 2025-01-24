package ecommerce.service;

import java.math.BigDecimal;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import ecommerce.dto.CompraDTO;
import ecommerce.dto.DisponibilidadeDTO;
import ecommerce.dto.EstoqueBaixaDTO;
import ecommerce.dto.PagamentoDTO;
import ecommerce.dto.ProdutoComQuantidadeDTO;
import ecommerce.entity.CarrinhoDeCompras;
import ecommerce.entity.Cliente;
import ecommerce.entity.TipoCliente;
import ecommerce.external.IEstoqueExternal;
import ecommerce.external.IPagamentoExternal;
import jakarta.transaction.Transactional;

@Service
public class CompraService {
	private final CarrinhoDeComprasService carrinhoService;

	private final IEstoqueExternal estoqueExternal;
	private final IPagamentoExternal pagamentoExternal;

	public CompraService(CarrinhoDeComprasService carrinhoService, ClienteService clienteService,
			IEstoqueExternal estoqueExternal, IPagamentoExternal pagamentoExternal) {
		this.carrinhoService = carrinhoService;

		this.estoqueExternal = estoqueExternal;
		this.pagamentoExternal = pagamentoExternal;
	}

	@Transactional
	public CompraDTO finalizarCompra(Long carrinhoId) {
		CarrinhoDeCompras carrinho = carrinhoService.buscarPorId(carrinhoId);

		var idsProdutosComQuantidades = carrinho.getItens().stream()
				.filter(i -> i != null && i.getProduto() != null)
				.map(i -> new ProdutoComQuantidadeDTO(i.getProduto().getId(), i.getQuantidade()))
				.collect(Collectors.toList());

		if (idsProdutosComQuantidades.isEmpty()) {
			throw new IllegalStateException("Nenhum produto válido no carrinho.");
		}

		DisponibilidadeDTO disponibilidade = estoqueExternal.verificarDisponibilidade(idsProdutosComQuantidades);

		if (disponibilidade.idsProdutosDisponiveis().isEmpty()) {
			throw new IllegalStateException("Itens fora de estoque.");
		}

		PagamentoDTO pagamento = pagamentoExternal.autorizarPagamento(carrinho.getCliente().getId(),
				carrinho.obterValorTotal());

		if (!pagamento.autorizado()) {
			throw new IllegalStateException("Pagamento não autorizado.");
		}

		EstoqueBaixaDTO baixaDTO = estoqueExternal.darBaixa(idsProdutosComQuantidades);

		if (!baixaDTO.sucesso()) {
			pagamentoExternal.cancelarPagamento(pagamento.transacaoId());
			throw new IllegalStateException("Erro ao dar baixa no estoque.");
		}

		CompraDTO compraDTO = new CompraDTO(true, pagamento.transacaoId(), "Compra finalizada com sucesso.");

		return compraDTO;
	}

	public BigDecimal calcularFreteComDescontoCliente(Cliente cliente, BigDecimal valorFrete) {
		var tipo = cliente.getTipo();

		if (tipo == TipoCliente.OURO) {
			return BigDecimal.ZERO;
		}
		if (tipo == TipoCliente.PRATA) {
			return valorFrete.multiply(BigDecimal.valueOf(0.5));
		}
		return valorFrete;
	}

	public BigDecimal calcularFretePorPeso(CarrinhoDeCompras carrinho) {
		var peso = carrinho.obterPesoTotal();

		if (peso <= 5) {
			return BigDecimal.ZERO;
		}
		if (peso < 10) {
			return BigDecimal.valueOf(2 * peso);
		}
		if (peso < 50) {
			return BigDecimal.valueOf(4 * peso);
		}
		return BigDecimal.valueOf(7 * peso);
	}

	public BigDecimal cacularFrete(CarrinhoDeCompras carrinho) {
		return calcularFreteComDescontoCliente(carrinho.getCliente(), calcularFretePorPeso(carrinho));
	}

	public BigDecimal aplicarDescontoValor(BigDecimal valorFinal) {
		if (valorFinal.compareTo(BigDecimal.valueOf(1000)) > 0) {
			return valorFinal.multiply(BigDecimal.valueOf(0.8));
		}
		if (valorFinal.compareTo(BigDecimal.valueOf(500)) > 0) {
			return valorFinal.multiply(BigDecimal.valueOf(0.9));
		}

		return valorFinal;
	}

	public BigDecimal calcularCustoTotal(CarrinhoDeCompras carrinho) {
		return aplicarDescontoValor(carrinho.obterValorTotal()).add(cacularFrete(carrinho));
	}
}
