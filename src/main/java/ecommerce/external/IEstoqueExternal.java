package ecommerce.external;

import java.util.List;

import ecommerce.dto.DisponibilidadeDTO;
import ecommerce.dto.EstoqueBaixaDTO;
import ecommerce.dto.ProdutoComQuantidadeDTO;

public interface IEstoqueExternal
{
	public DisponibilidadeDTO verificarDisponibilidade(List<ProdutoComQuantidadeDTO> idsProdutosComQuantidades);
	public EstoqueBaixaDTO darBaixa(List<ProdutoComQuantidadeDTO>  idsProdutosComQuantidades);
}
