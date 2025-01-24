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

import ecommerce.entity.CarrinhoDeCompras;
import ecommerce.repository.CarrinhoDeComprasRepository;

@ExtendWith(MockitoExtension.class)
class CarrinhoDeComprasServiceTest {

    @Mock
    private CarrinhoDeComprasRepository repository;

    @InjectMocks
    private CarrinhoDeComprasService carrinhoDeComprasService;

    @Test
    void deveBuscarCarrinhoPorIdComSucesso() {
        Long carrinhoId = 1L;
        CarrinhoDeCompras carrinhoMock = criarCarrinhoMock();
        when(repository.findById(carrinhoId)).thenReturn(Optional.of(carrinhoMock));

        CarrinhoDeCompras resultado = carrinhoDeComprasService.buscarPorId(carrinhoId);

        assertNotNull(resultado);
        assertEquals(carrinhoId, resultado.getId());
        verify(repository, times(1)).findById(carrinhoId);
    }

    @Test
    void deveLancarExcecaoQuandoCarrinhoNaoForEncontrado() {
        Long carrinhoId = 1L;
        when(repository.findById(carrinhoId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> carrinhoDeComprasService.buscarPorId(carrinhoId));
        assertEquals("Carrinho não encontrado.", exception.getMessage());
        verify(repository, times(1)).findById(carrinhoId);
    }

    @Test
    void naoDeveBuscarCarrinhoQuandoIdForNulo() {
        assertThrows(IllegalArgumentException.class, () -> carrinhoDeComprasService.buscarPorId(null));
    }

    private CarrinhoDeCompras criarCarrinhoMock() {
        CarrinhoDeCompras carrinho = new CarrinhoDeCompras();
        carrinho.setId(1L);
        return carrinho;
    }
}
