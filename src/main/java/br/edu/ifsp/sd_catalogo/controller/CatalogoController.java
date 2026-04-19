package br.edu.ifsp.sd_catalogo.controller;

import br.edu.ifsp.sd_catalogo.dto.ProdutoResponseDTO;
import br.edu.ifsp.sd_catalogo.service.CatalogoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/catalogo")
@RequiredArgsConstructor
@Tag(name = "Catálogo", description = "Operações do serviço de catálogo de produtos")
@CrossOrigin(origins = "http://localhost:5173")
public class CatalogoController {

    private final CatalogoService catalogoService;

    @GetMapping("/produtos")
    @Operation(
            summary = "Lista todos os produtos",
            description = "Retorna todos os produtos do catálogo"
    )
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<ProdutoResponseDTO>> getAllProdutos() {
        return ResponseEntity.ok(catalogoService.getAllProdutos());
    }

    @GetMapping("/produto/{id}")
    @Operation(
            summary = "Busca produto por ID",
            description = "Retorna os dados do produto por ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto encontrado"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<ProdutoResponseDTO> getProduto(@PathVariable Long id) {
        return ResponseEntity.ok(catalogoService.getProduto(id));
    }
}