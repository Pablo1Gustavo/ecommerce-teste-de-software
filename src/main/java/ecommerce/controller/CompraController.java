package ecommerce.controller;

import ecommerce.dto.CompraDTO;
import ecommerce.service.CompraService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/compras")
public class CompraController
{
	private final CompraService compraService;

	public CompraController(CompraService compraService)
	{
		this.compraService = compraService;
	}

	@PostMapping("/{carrinhoId}/finalizar")
	public ResponseEntity<CompraDTO> finalizarCompra(@PathVariable Long carrinhoId)
	{
		try {
			CompraDTO compraDTO = compraService.finalizarCompra(carrinhoId);
			return ResponseEntity.ok(compraDTO);
		}
		catch (IllegalArgumentException e)
		{
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new CompraDTO(false, null, e.getMessage()));
		}
		catch (IllegalStateException e)
		{
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
				.body(new CompraDTO(false, null, e.getMessage()));
		}
		catch (Exception e)
		{
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new CompraDTO(false, null, "Erro inesperado ao processar compra."));
		}
	}
}
