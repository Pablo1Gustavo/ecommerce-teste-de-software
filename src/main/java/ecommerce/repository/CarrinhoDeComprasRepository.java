package ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ecommerce.entity.CarrinhoDeCompras;

@Repository
public interface CarrinhoDeComprasRepository extends JpaRepository<CarrinhoDeCompras, Long>
{}
