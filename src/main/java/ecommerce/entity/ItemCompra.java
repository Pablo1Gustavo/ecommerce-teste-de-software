package ecommerce.entity;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@Table(name="itens_compras")
public class ItemCompra
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produto produto;

    private Long quantidade;

	public ItemCompra() {
	}

	public ItemCompra(Long id, Produto produto, Long quantidade) {
		this.id = id;
		this.produto = produto;
		this.quantidade = quantidade;
	}

	public BigDecimal obterSubtotalValor() {
		return produto.getPreco().multiply(BigDecimal.valueOf(quantidade));
	}

	public Integer obterSubtotalPeso() {
		return produto.getPeso() * quantidade.intValue();
	}

	// Getters e Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	public Long getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Long quantidade) {
		this.quantidade = quantidade;
	}
}
