package br.edu.ifsp.sd_catalogo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
@Schema(description = "Entidade de produto do catálogo")
public class Produto {

    @Id
    @Schema(description = "ID do produto", example = "1")
    private Long id;

    @Schema(description = "Nome do produto", example = "Mouse Gamer")
    private String nome;

    @Schema(description = "Descrição do produto", example = "Mouse com 6 botões e RGB")
    private String descricao;
}