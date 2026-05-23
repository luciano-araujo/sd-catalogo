package br.edu.ifsp.sd_catalogo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ProdutoRequestDTO(
        @Schema(description = "Nome do produto (opcional)", example = "Teclado Mecânico")
        String nome,

        @Schema(description = "Descrição do produto (opcional)", example = "Teclado RGB switch blue")
        String descricao,

        @Schema(description = "Preço do produto a ser atualizado no sd-preco (opcional)", example = "199.90")
        Double preco
) {
}