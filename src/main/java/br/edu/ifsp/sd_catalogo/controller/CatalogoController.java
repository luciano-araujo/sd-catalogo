package br.edu.ifsp.sd_catalogo.controller;

import br.edu.ifsp.sd_catalogo.dto.ProdutoResponseDTO;
import br.edu.ifsp.sd_catalogo.service.CatalogoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/catalogo")
@RequiredArgsConstructor
@Tag(name = "Catálogo", description = "Operações do serviço de catálogo de produtos")
public class CatalogoController {

    private final CatalogoService catalogoService;

    @GetMapping("/produto/{id}")
    @Operation(
            summary = "Busca produto por ID",
            description = "Retorna os dados do produto com o preço consultado no sd-preco"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto encontrado"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<ProdutoResponseDTO> getProduto(@PathVariable Long id) {
        return ResponseEntity.ok(catalogoService.getProduto(id));
    }
}