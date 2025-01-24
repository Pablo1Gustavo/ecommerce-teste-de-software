package ecommerce.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ecommerce.entity.Cliente;
import ecommerce.repository.ClienteRepository;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

	@Mock
	private ClienteRepository repository;

	@InjectMocks
	private ClienteService clienteService;

	@Test
	void deveBuscarClientePorIdComSucesso() {
		Long clienteId = 1L;
		Cliente clienteMock = criarClienteMock();
		when(repository.findById(clienteId)).thenReturn(Optional.of(clienteMock));

		Cliente resultado = clienteService.buscarPorId(clienteId);

		assertNotNull(resultado);
		assertEquals(clienteId, resultado.getId());
		assertEquals("João Silva", resultado.getNome());
		verify(repository, times(1)).findById(clienteId);
	}

	@Test
	void deveLancarExcecaoQuandoClienteNaoForEncontrado() {
		Long clienteId = 1L;
		when(repository.findById(clienteId)).thenReturn(Optional.empty());

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> clienteService.buscarPorId(clienteId));
		assertEquals("Cliente não encontrado", exception.getMessage());
		verify(repository, times(1)).findById(clienteId);
	}

	@Test
	void naoDeveBuscarClienteQuandoIdForNulo() {
		assertThrows(IllegalArgumentException.class, () -> clienteService.buscarPorId(null));
	}

	private Cliente criarClienteMock() {
		Cliente cliente = new Cliente();
		cliente.setId(1L);
		cliente.setNome("João Silva");
		cliente.setEndereco("Rua das Flores, 123");
		cliente.setTipo(ecommerce.entity.TipoCliente.OURO);
		return cliente;
	}
}
