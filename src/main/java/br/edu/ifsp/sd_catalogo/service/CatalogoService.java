package br.edu.ifsp.sd_catalogo.service;

import br.edu.ifsp.sd_catalogo.dto.PrecoResponseDTO;
import br.edu.ifsp.sd_catalogo.dto.ProdutoResponseDTO;
import br.edu.ifsp.sd_catalogo.exception.ProdutoNaoEncontradoException;
import br.edu.ifsp.sd_catalogo.model.Produto;
import br.edu.ifsp.sd_catalogo.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CatalogoService {

    private final ProdutoRepository produtoRepository;
    private final RestTemplate restTemplate;

    @Value("${sd-preco.url}")
    private String sdPrecoUrl;

    public List<ProdutoResponseDTO> getAllProdutos() {
        List<Produto> produtos = produtoRepository.findAll();
        log.info("Buscando todos os produtos do catálogo");

        List<Long> ids = produtos.stream()
                .map(Produto::getId)
                .toList();

        log.info("Buscando preços para os produtos: {}", ids);
        Map<Long, Double> precos = restTemplate.exchange(
                sdPrecoUrl + "/preco/lote",
                HttpMethod.POST,
                new HttpEntity<>(ids),
                new ParameterizedTypeReference<Map<Long, Double>>() {}
        ).getBody();
        log.info("Preços recebidos: {}", precos);

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

        Produto produto;

        try {
            log.info("Buscando produto com id={}", id);
            produto = produtoRepository.findById(id)
                    .orElseThrow(() -> new ProdutoNaoEncontradoException("Produto não encontrado"));
        } catch (RuntimeException e) {
            log.warn("Produto com id={} não encontrado", id);
            throw e;
        }

        PrecoResponseDTO precoResponse = null;
        
        try {
            log.info("Buscando preço para o produto id={}", id);
            precoResponse = restTemplate.getForObject(
                    sdPrecoUrl + "/preco/" + id,
                    PrecoResponseDTO.class
            );
        } catch (RestClientException e) {
            log.warn("Erro ao buscar preço para o produto id={}: {}", id, e.getMessage());
        }
        
        Double preco = precoResponse != null ? precoResponse.preco() : null;


        return new ProdutoResponseDTO(produto.getId(), produto.getNome(), produto.getDescricao(), preco);
    }
}