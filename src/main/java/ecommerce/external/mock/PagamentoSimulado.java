package ecommerce.external.mock;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Service;

import ecommerce.dto.PagamentoDTO;
import ecommerce.external.IPagamentoExternal;

@Service
public class PagamentoSimulado implements IPagamentoExternal
{
    private record DadosTransacao (
        Long clienteId,
        BigDecimal valor,
        Boolean aprovada
    ) {}

    Map<Long, BigDecimal> saldoClientes = new HashMap<>();
    Map<Long, DadosTransacao> dadosTransacoes = new HashMap<>();

    public void atualizarSaldoCliente(Long clienteId, BigDecimal valor)
    {
        saldoClientes.put(clienteId, valor);
    }

    public Long gerarIdTransacao()
    {
        return new Random().nextLong();
    }

    @Override
    public BigDecimal obterSaldoCliente(Long clienteId)
    {
        return saldoClientes.getOrDefault(clienteId, BigDecimal.ZERO);
    }

    @Override
    public boolean obterStatusPagamento(Long pagamentoTransacaoId)
    {
        var dadosTransacao = dadosTransacoes.get(pagamentoTransacaoId);

        if (dadosTransacao == null)
        {
            throw new IllegalStateException("Transação " + pagamentoTransacaoId + " não encontrada.");
        }

        return dadosTransacao.aprovada();
    }

    @Override
    public PagamentoDTO autorizarPagamento(Long clienteId, BigDecimal custoTotal)
    {
        var id = gerarIdTransacao();
        var saldoCleinte = obterSaldoCliente(clienteId);
        boolean saldoInsuficiente = saldoCleinte.compareTo(custoTotal) < 0;

        if (saldoInsuficiente)
        {
            return new PagamentoDTO(false, id);
        }

        saldoClientes.put(clienteId, saldoCleinte.subtract(custoTotal));
        dadosTransacoes.put(id, new DadosTransacao(clienteId, custoTotal, true));

        return new PagamentoDTO(true, id);
    }

    @Override
    public void cancelarPagamento(Long pagamentoTransacaoId)
    {
        var dadosTransacao = dadosTransacoes.get(pagamentoTransacaoId);

        if (dadosTransacao == null)
        {
            throw new IllegalStateException("Transação " + pagamentoTransacaoId + " não encontrada.");
        }

        var saldoCliente = saldoClientes.get(dadosTransacao.clienteId());

        saldoClientes.put(dadosTransacao.clienteId(), saldoCliente.add(dadosTransacao.valor()));
        dadosTransacoes.put(pagamentoTransacaoId, new DadosTransacao(
            dadosTransacao.clienteId(), dadosTransacao.valor(), false
        ));
    }
}
