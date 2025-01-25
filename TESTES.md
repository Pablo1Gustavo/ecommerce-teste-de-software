
## Testes no `CompraControllerTest`

Os testes do `CompraController` verificam o comportamento da API ao finalizar uma compra.

### Métodos

- `deveRetornar200QuandoCompraForFinalizadaComSucesso`  
  Valida que, dado um carrinho válido, a compra é finalizada com sucesso (HTTP 200).

- `deveRetornar400QuandoIdDoCarrinhoForInvalido`  
  Verifica se o sistema retorna HTTP 400 quando o ID do carrinho é inválido.

- `deveRetornar409QuandoHouverConflitoNoEstadoDoCarrinho`  
  Garante que um erro HTTP 409 é retornado quando há conflito no estado do carrinho.

- `deveRetornar500QuandoErroNaoForTratado`  
  Testa o retorno HTTP 500 para erros não tratados durante a finalização da compra.

- `deveRetornar400QuandoParametroIdEstiverAusente`  
  Confirma que a ausência do parâmetro `carrinhoId` retorna um erro HTTP 400.

- `deveRetornar400QuandoIdDoCarrinhoNaoForNumerico`  
  Valida que valores não numéricos no parâmetro `carrinhoId` resultam em HTTP 400.

## Testes no `CarrinhoDeComprasServiceTest`

Estes testes verificam o comportamento do serviço responsável pela gestão de carrinhos de compras.

### Métodos

- `deveBuscarCarrinhoPorIdComSucesso`  
  Valida que um carrinho válido é retornado ao buscar por ID.

- `deveLancarExcecaoQuandoCarrinhoNaoForEncontrado`  
  Garante que uma exceção é lançada quando o carrinho não é encontrado.

- `naoDeveBuscarCarrinhoQuandoIdForNulo`  
  Confirma que um ID nulo resulta em exceção.

## Testes no `ClienteServiceTest`

Os testes do serviço de clientes validam as operações relacionadas à busca de informações de clientes.

### Métodos

- `deveBuscarClientePorIdComSucesso`  
  Verifica se um cliente válido é retornado ao buscar pelo ID.

- `deveLancarExcecaoQuandoClienteNaoForEncontrado`  
  Garante que uma exceção é lançada para clientes não encontrados.

- `naoDeveBuscarClienteQuandoIdForNulo`  
  Testa que IDs nulos lançam uma exceção.

## Testes no `CompraServiceTest`

Os testes do `CompraService` verificam a lógica de negócios da finalização de compras.

### Métodos

- `deveFinalizarCompraComSucesso`  
  Valida o fluxo completo de finalização de compra com sucesso.

- `deveLancarExcecaoQuandoEstoqueInsuficiente`  
  Garante que uma exceção é lançada quando não há estoque suficiente.

- `deveLancarExcecaoQuandoPagamentoNaoAutorizado`  
  Testa o comportamento ao lidar com pagamentos não autorizados.

- `deveCancelarPagamentoQuandoErroAoAtualizarEstoque`  
  Valida que pagamentos são cancelados quando ocorre erro ao atualizar o estoque.

- `deveCalcularFreteBaseadoEmPesoETipoCliente`  
  Verifica o cálculo de frete com base no peso do carrinho e no tipo do cliente.

- `deveAplicarDescontoBaseadoNoValor`  
  Testa o cálculo de descontos com base no valor total do carrinho.

- `calcular_frete_por_preso`  
  Valida o cálculo de frete em diferentes cenários de peso.

- `calcular_desconto_do_frete_para_cliente`  
  Testa a aplicação de descontos de frete baseados no tipo de cliente.

- `deveCalcularCustoTotalComSucesso`  
  Confirma que o custo total do carrinho é calculado corretamente.

- `deveLancarExcecaoQuandoCarrinhoEstiverVazio`  
  Verifica que um carrinho vazio lança exceção.

- `deveLancarExcecaoQuandoCarrinhoContemItensInvalidos`  
  Testa se uma exceção é lançada quando o carrinho contém itens inválidos.

- `deveIgnorarItensInvalidosNoCarrinho`  
  Valida que itens inválidos no carrinho são ignorados adequadamente.

- `deveCriarListaDeProdutoComQuantidadeCorretamente`  
  Garante que a lista de produtos com quantidade seja criada corretamente a partir de um carrinho válido.

- `deveLancarExcecaoQuandoDisponibilidadeForNula`  
  Verifica se uma exceção é lançada quando a verificação de disponibilidade de estoque retorna nulo.

- `deveLancarExcecaoQuandoValorTotalCarrinhoForNulo`  
  Garante que uma exceção é lançada quando o valor total do carrinho é nulo.

- `deveAplicarDescontoCorretamente`  
  Valida a aplicação correta de descontos baseados no valor do carrinho.

- `deveCriarProdutoComQuantidadeDTOsValidos`  
  Assegura que a transformação de itens do carrinho em DTOs de produto com quantidade é realizada corretamente.

- `deveCalcularCustoTotalComSucesso`  
  Testa o cálculo correto do custo total do carrinho com base em peso, valor e descontos aplicados.

---
