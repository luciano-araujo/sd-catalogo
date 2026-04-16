package br.edu.ifsp.sd_catalogo.dto;

public record ProdutoResponseDTO(
        Long id,
        String nome,
        String descricao,
        Double preco) {
}