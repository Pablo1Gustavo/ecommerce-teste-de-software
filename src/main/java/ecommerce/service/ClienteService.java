package ecommerce.service;

import org.springframework.stereotype.Service;

import ecommerce.entity.Cliente;
import ecommerce.repository.ClienteRepository;

@Service
public class ClienteService {
	
	private final ClienteRepository repository;
	
	public ClienteService(ClienteRepository repository)
	{
		this.repository = repository;
	}
	
	public Cliente buscarPorId(Long clienteId)
	{
		return repository.findById(clienteId)
			.orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
	}
}
