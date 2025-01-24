package ecommerce.controller;

import ecommerce.dto.CompraDTO;
import ecommerce.service.CompraService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@Tag(name = "Compra Controller", description = "Gerencia as operações relacionadas a compras")
public class CompraController {
    private final CompraService compraService;

    public CompraController(CompraService compraService) {
        this.compraService = compraService;
    }

    @Operation(
        summary = "Finalizar compra",
        description = "Finaliza a compra com base no ID do carrinho fornecido"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Compra finalizada com sucesso",
            content = @Content(
            		mediaType = "application/json", 
					examples = @ExampleObject(value = """
                    {
                      "sucesso": true,
                      "transacaoPagamentoId": null,
                      "mensagem": "Compra finalizada com sucesso!"
                    }
                """),
            		schema = @Schema(implementation = CompraDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Requisição inválida. O ID do carrinho pode estar incorreto",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "sucesso": false,
                      "transacaoPagamentoId": null,
                      "mensagem": "O ID do carrinho é inválido"
                    }
                """),
                schema = @Schema(implementation = CompraDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Conflito. A compra não pode ser finalizada devido a problemas no estado do carrinho",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "sucesso": false,
                      "transacaoPagamentoId": null,
                      "mensagem": "Conflito no estado do carrinho"
                    }
                """),
                schema = @Schema(implementation = CompraDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Erro inesperado ao processar a compra",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "sucesso": false,
                      "transacaoPagamentoId": null,
                      "mensagem": "Erro inesperado ao processar compra."
                    }
                """),
                schema = @Schema(implementation = CompraDTO.class)
            )
        )
    })
    @PostMapping("/finalizar")
    public ResponseEntity<CompraDTO> finalizarCompra(@RequestParam Long carrinhoId) {
        try {
            CompraDTO compraDTO = compraService.finalizarCompra(carrinhoId);
            return ResponseEntity.ok(compraDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new CompraDTO(false, null, e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new CompraDTO(false, null, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new CompraDTO(false, null, "Erro inesperado ao processar compra."));
        }
    }
}
