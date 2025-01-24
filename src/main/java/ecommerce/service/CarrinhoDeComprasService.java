package ecommerce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ecommerce.entity.CarrinhoDeCompras;
import ecommerce.repository.CarrinhoDeComprasRepository;

@Service
public class CarrinhoDeComprasService {
	
	@Autowired
	private CarrinhoDeComprasRepository repository;

	public CarrinhoDeCompras buscarPorId(Long carrinhoId) {
		return repository.findById(carrinhoId)
				.orElseThrow(() -> new IllegalArgumentException("Carrinho não encontrado."));
	}
}
