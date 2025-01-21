package ecommerce.external;

import java.math.BigDecimal;

import ecommerce.dto.PagamentoDTO;

public interface IPagamentoExternal
{
	BigDecimal obterSaldoCliente(Long clienteId);
	boolean obterStatusPagamento(Long pagamentoTransacaoId);
	PagamentoDTO autorizarPagamento(Long clienteId, BigDecimal custoTotal);
	void cancelarPagamento(Long pagamentoTransacaoId);
}
