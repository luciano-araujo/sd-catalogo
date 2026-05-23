package br.edu.ifsp.sd_catalogo.controller;

import br.edu.ifsp.sd_catalogo.dto.ProdutoRequestDTO;
import br.edu.ifsp.sd_catalogo.dto.ProdutoResponseDTO;
import br.edu.ifsp.sd_catalogo.service.CatalogoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/catalogo")
@RequiredArgsConstructor
@Tag(name = "Catálogo", description = "Operações do serviço de catálogo de produtos")
@CrossOrigin(origins = "*")
public class CatalogoController {

    private final CatalogoService catalogoService;

    @GetMapping("/produtos")
    @Operation(summary = "Lista todos os produtos", description = "Retorna todos os produtos do catálogo")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<ProdutoResponseDTO>> getAllProdutos() {
        return ResponseEntity.ok(catalogoService.getAllProdutos());
    }

    @GetMapping("/produto/{id}")
    @Operation(summary = "Busca produto por ID", description = "Retorna os dados do produto por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto encontrado"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<ProdutoResponseDTO> getProduto(@PathVariable Long id) {
        return ResponseEntity.ok(catalogoService.getProduto(id));
    }

    @PostMapping("/produto/novo")
    @Operation(summary = "Cria um novo produto", description = "Adiciona produto no catálogo e registra o novo preço")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Produto criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida (Erro de atributos)")
    })
    public ResponseEntity<ProdutoResponseDTO> createProduto(@RequestBody ProdutoRequestDTO request) {
        ProdutoResponseDTO criado = catalogoService.createProduto(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @PutMapping("/produto/atualiza/{id}")
    @Operation(summary = "Atualiza parcialmente os dados do produto", description = "Modifica nome, descricao ou preço")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto e preço atualizados"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<ProdutoResponseDTO> updateProduto(@PathVariable Long id, @RequestBody ProdutoRequestDTO request) {
        return ResponseEntity.ok(catalogoService.updateProduto(id, request));
    }

    @DeleteMapping("/produto/remove/{id}")
    @Operation(summary = "Deleta o produto", description = "Remove do BD local e exclui também do sd-preco")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<Void> deleteProduto(@PathVariable Long id) {
        catalogoService.deleteProduto(id);
        return ResponseEntity.noContent().build();
    }
}