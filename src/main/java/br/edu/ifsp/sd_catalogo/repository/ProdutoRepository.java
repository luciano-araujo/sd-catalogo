package br.edu.ifsp.sd_catalogo.repository;

import br.edu.ifsp.sd_catalogo.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}