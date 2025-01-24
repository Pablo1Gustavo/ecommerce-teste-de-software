package ecommerce.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import ecommerce.dto.CompraDTO;
import ecommerce.service.CompraService;

@WebMvcTest(CompraController.class)
@ExtendWith(SpringExtension.class)
class CompraControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private CompraService compraService;

	@Test
	void deveRetornar200QuandoCompraForFinalizadaComSucesso() throws Exception {
		Long carrinhoId = 1L;
		CompraDTO compraMock = new CompraDTO(true, 12345L, "Compra finalizada com sucesso!");
		when(compraService.finalizarCompra(carrinhoId)).thenReturn(compraMock);

		mockMvc.perform(
				post("/finalizar").param("carrinhoId", carrinhoId.toString()).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.sucesso").value(true))
				.andExpect(jsonPath("$.transacaoPagamentoId").value(12345))
				.andExpect(jsonPath("$.mensagem").value("Compra finalizada com sucesso!"));

		verify(compraService, times(1)).finalizarCompra(carrinhoId);
	}

	@Test
	void deveRetornar400QuandoIdDoCarrinhoForInvalido() throws Exception {
		Long carrinhoId = 1L;
		when(compraService.finalizarCompra(carrinhoId))
				.thenThrow(new IllegalArgumentException("O ID do carrinho é inválido"));

		mockMvc.perform(
				post("/finalizar").param("carrinhoId", carrinhoId.toString()).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.sucesso").value(false))
				.andExpect(jsonPath("$.mensagem").value("O ID do carrinho é inválido"));

		verify(compraService, times(1)).finalizarCompra(carrinhoId);
	}

	@Test
	void deveRetornar409QuandoHouverConflitoNoEstadoDoCarrinho() throws Exception {
		Long carrinhoId = 1L;
		when(compraService.finalizarCompra(carrinhoId))
				.thenThrow(new IllegalStateException("Conflito no estado do carrinho"));

		mockMvc.perform(
				post("/finalizar").param("carrinhoId", carrinhoId.toString()).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isConflict()).andExpect(jsonPath("$.sucesso").value(false))
				.andExpect(jsonPath("$.mensagem").value("Conflito no estado do carrinho"));

		verify(compraService, times(1)).finalizarCompra(carrinhoId);
	}

	@Test
	void deveRetornar500QuandoErroNaoForTratado() throws Exception {
		Long carrinhoId = 1L;
		when(compraService.finalizarCompra(carrinhoId)).thenThrow(new RuntimeException("Erro inesperado"));

		mockMvc.perform(
				post("/finalizar").param("carrinhoId", carrinhoId.toString()).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError()).andExpect(jsonPath("$.sucesso").value(false))
				.andExpect(jsonPath("$.mensagem").value("Erro inesperado ao processar compra."));

		verify(compraService, times(1)).finalizarCompra(carrinhoId);
	}

	@Test
	void deveRetornar400QuandoParametroIdEstiverAusente() throws Exception {
		mockMvc.perform(post("/finalizar").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());

		verify(compraService, never()).finalizarCompra(any());
	}

	@Test
	void deveRetornar400QuandoIdDoCarrinhoNaoForNumerico() throws Exception {
		mockMvc.perform(post("/finalizar").param("carrinhoId", "abc").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

		verify(compraService, never()).finalizarCompra(any());
	}
}
