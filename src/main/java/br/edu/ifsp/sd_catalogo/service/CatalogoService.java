package br.edu.ifsp.sd_catalogo.service;

import br.edu.ifsp.sd_catalogo.dto.ProdutoResponseDTO;
import br.edu.ifsp.sd_catalogo.model.Produto;
import br.edu.ifsp.sd_catalogo.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class CatalogoService {

    private final ProdutoRepository produtoRepository;
    private final RestTemplate restTemplate;

    public ProdutoResponseDTO getProduto(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        Double preco = restTemplate.getForObject(
                "http://sd-preco:8081/preco/" + id, Double.class
        );

        return new ProdutoResponseDTO(produto.getId(), produto.getNome(), produto.getDescricao(), preco);
    }
}