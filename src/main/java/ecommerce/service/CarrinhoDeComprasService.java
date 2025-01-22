package ecommerce.service;

import org.springframework.stereotype.Service;

import ecommerce.entity.CarrinhoDeCompras;
import ecommerce.entity.Cliente;
import ecommerce.repository.CarrinhoDeComprasRepository;

@Service
public class CarrinhoDeComprasService
{
	private final CarrinhoDeComprasRepository repository;
	
	public CarrinhoDeComprasService(CarrinhoDeComprasRepository repository)
	{
		this.repository = repository;
	}

	public CarrinhoDeCompras buscarPorId(Long carrinhoId)
	{
		return repository.findById(carrinhoId)
			.orElseThrow(() -> new IllegalArgumentException("Carrinho não encontrado."));
	}
}
