package ecommerce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ecommerce.entity.Cliente;
import ecommerce.repository.ClienteRepository;

@Service
public class ClienteService {

	@Autowired
	private ClienteRepository repository;

	public Cliente buscarPorId(Long clienteId) {
		return repository.findById(clienteId).orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
	}
}
