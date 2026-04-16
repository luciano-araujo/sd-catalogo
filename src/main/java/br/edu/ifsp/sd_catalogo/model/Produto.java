package br.edu.ifsp.sd_catalogo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Produto {

    @Id
    private Long id;
    private String nome;
    private String descricao;
}