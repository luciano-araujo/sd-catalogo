package br.edu.ifsp.sd_catalogo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ProdutoResponseDTO(
        @Schema(description = "ID do produto", example = "1")
        Long id,

        @Schema(description = "Nome do produto", example = "Mouse Gamer")
        String nome,

        @Schema(description = "Descrição do produto", example = "Mouse com 6 botões e RGB")
        String descricao,

        @Schema(description = "Preço consultado no sd-preco", example = "99.90")
        Double preco) {
}