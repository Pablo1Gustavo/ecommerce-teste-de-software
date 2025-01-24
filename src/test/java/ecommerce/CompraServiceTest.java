package ecommerce;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import ecommerce.entity.CarrinhoDeCompras;
import ecommerce.entity.Cliente;
import ecommerce.entity.ItemCompra;
import ecommerce.entity.Produto;
import ecommerce.entity.TipoCliente;
import ecommerce.entity.TipoProduto;
import ecommerce.service.CompraService;

public class CompraServiceTest
{
    private static final CompraService compraService = new CompraService(null, null, null, null);

    @ParameterizedTest
    @CsvSource({
        "OURO, 0, 0",
        "OURO, 10, 0",
        "OURO, 123, 0",
        "OURO, 1000, 0",
        "OURO, 10000, 0",

        "PRATA, 0, 0",
        "PRATA, 10, 5",
        "PRATA, 100.5, 50.25",
        "PRATA, 123, 61.5",
        "PRATA, 1000, 500",

        "BRONZE, 0, 0",
        "BRONZE, 10, 10",
        "BRONZE, 123, 123",
        "BRONZE, 1000, 1000",
        "BRONZE, 10000, 10000"
    })
    public void calcular_desconto_do_frete_para_cliente(TipoCliente tipoCliente, BigDecimal valor, BigDecimal valorEsperado) 
    {
        var cliente = new Cliente(0L, "Eiji", "Rua Teste", tipoCliente);
    
        var valorComDesconto = compraService.calcularFreteComDescontoCliente(cliente, valor);
        assertEquals(valorEsperado.doubleValue(), valorComDesconto.doubleValue());
    }
    

    @ParameterizedTest
    @CsvSource({
        // peso <= 5 -> frete 0
        "1, 1, 1, 1, 0",
        "2, 1, 3, 1, 0",
        "2, 1, 2, 1, 0",
        "4, 1, 1, 1, 0",
        // 5 < peso < 10 -> frete 2 * peso
        "5, 1, 1, 1, 12",
        "5, 1, 1, 2, 14",
        "3, 1, 3, 2, 18",
        "2, 1, 6, 1, 16",
        // 10 < peso < 50 -> frete 4 * peso
        "9, 1, 1, 1, 40",
        "8, 2, 2, 1, 72", 
        "12, 1, 3, 1, 60",
        "15, 1, 10, 2, 140",
        "24, 2, 1, 1, 196",
        // peso >= 50 -> frete 7 * peso
        "24, 2, 2, 1, 350",
        "25, 2, 10, 1, 420",
        "20, 3, 10, 1, 490",
        "30, 1, 25, 1, 385",
        "40, 2, 10, 1, 630"
    })
    public void calcular_frete_por_preso(
        int pesoItem1, int quantidadeItem1,
        int pesoItem2, int quantidadeItem2,
        BigDecimal precoEsperado
    ) {
        var cliente = new Cliente(0L, "Eiji", "Rua Teste", TipoCliente.OURO);
        var produto1 = new Produto(0L, "Lanchinho", "teste", BigDecimal.ONE, pesoItem1, TipoProduto.ALIMENTO);
        var produto2 = new Produto(1L, "Lanchinho", "teste", BigDecimal.ONE, pesoItem2, TipoProduto.ALIMENTO);

        var itemsCompra = Arrays.asList(
            new ItemCompra(0L, produto1, quantidadeItem1),
            new ItemCompra(1L, produto2, quantidadeItem2)
        );

        var carrinho = new CarrinhoDeCompras(0L, cliente, itemsCompra, LocalDate.now());

        var valorFrete = compraService.calcularFretePorPeso(carrinho);

        assertEquals(precoEsperado.doubleValue(), valorFrete.doubleValue());
    }
}
