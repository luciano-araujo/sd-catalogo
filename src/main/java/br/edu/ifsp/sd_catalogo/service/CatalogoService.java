package br.edu.ifsp.sd_catalogo.service;

import br.edu.ifsp.sd_catalogo.dto.ProdutoResponseDTO;
import br.edu.ifsp.sd_catalogo.model.Produto;
import br.edu.ifsp.sd_catalogo.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CatalogoService {

    private final ProdutoRepository produtoRepository;
    private final RestTemplate restTemplate;

    @Value("${sd-preco.url}")
    private String sdPrecoUrl;

    public List<ProdutoResponseDTO> getAllProdutos() {
        List<Produto> produtos = produtoRepository.findAll();

        List<Long> ids = produtos.stream()
                .map(Produto::getId)
                .toList();

        Map<Long, Double> precos = restTemplate.exchange(
                sdPrecoUrl + "/preco/lote",
                HttpMethod.POST,
                new org.springframework.http.HttpEntity<>(ids),
                new ParameterizedTypeReference<Map<Long, Double>>() {}
        ).getBody();

        return produtos.stream()
                .map(produto -> new ProdutoResponseDTO(
                        produto.getId(),
                        produto.getNome(),
                        produto.getDescricao(),
                        precos != null ? precos.get(produto.getId()) : null
                ))
                .toList();
    }

    public ProdutoResponseDTO getProduto(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        Double preco = restTemplate.getForObject(
                sdPrecoUrl + "/preco/" + id,
                Double.class
        );

        return new ProdutoResponseDTO(produto.getId(), produto.getNome(), produto.getDescricao(), preco);
    }
}