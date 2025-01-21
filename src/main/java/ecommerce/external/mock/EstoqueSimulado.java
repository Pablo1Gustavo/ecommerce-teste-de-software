package ecommerce.external.mock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import ecommerce.dto.DisponibilidadeDTO;
import ecommerce.dto.EstoqueBaixaDTO;
import ecommerce.dto.ProdutoComQuantidadeDTO;
import ecommerce.external.IEstoqueExternal;

@Service
public class EstoqueSimulado implements IEstoqueExternal
{
    private Map<Long, Long> quantidadeEmEstoque = new HashMap<>();

    public EstoqueSimulado(List<ProdutoComQuantidadeDTO> idsProdutosComQuantidades)
    {
        idsProdutosComQuantidades.forEach(item ->
            quantidadeEmEstoque.put(item.produtoId(), item.quantidade()));
    }

    public EstoqueSimulado() {}

    public void adicionarAoEstoque(long produtoId, Long quantidade)
    {
        quantidadeEmEstoque.put(produtoId, quantidadeEmEstoque.get(produtoId) + quantidade);
    }

    public void removeDoEstoque(long produtoId, Long quantidadeRetirar)
    {
        var quantidadeNoEstoque = quantidadeEmEstoque.get(produtoId);

        if (quantidadeNoEstoque < quantidadeRetirar)
        {
            throw new IllegalStateException("Produto " + produtoId + " fora de estoque.");
        }

        quantidadeEmEstoque.put(produtoId, quantidadeNoEstoque - quantidadeRetirar);
    }

    @Override
    public DisponibilidadeDTO verificarDisponibilidade(List<ProdutoComQuantidadeDTO> idsProdutosComQuantidades)
    {
       var idsDisponiveis = idsProdutosComQuantidades.stream()
            .filter(item -> quantidadeEmEstoque.getOrDefault(item.produtoId(), 0L) > item.quantidade())
            .map(ProdutoComQuantidadeDTO::produtoId)
            .collect(Collectors.toList());
    
        return new DisponibilidadeDTO(idsDisponiveis);
    }

    @Override
    public EstoqueBaixaDTO darBaixa(List<ProdutoComQuantidadeDTO> idsProdutosComQuantidades)
    {
        try {
            idsProdutosComQuantidades.forEach(item -> removeDoEstoque(item.produtoId(), item.quantidade()));
        }
        catch (IllegalStateException e)
        {
            return new EstoqueBaixaDTO(false);
        }
        return new EstoqueBaixaDTO(true);
    }
}
