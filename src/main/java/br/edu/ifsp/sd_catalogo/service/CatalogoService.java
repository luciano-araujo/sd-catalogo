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
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
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

        List<Long> ids = produtos.stream()
                .map(Produto::getId)
                .toList();

        Map<Long, Double> precosTemp = new HashMap<>();

        try {
            Map<Long, Double> response = restTemplate.exchange(
                    sdPrecoUrl + "/preco/lote",
                    HttpMethod.POST,
                    new org.springframework.http.HttpEntity<>(ids),
                    new ParameterizedTypeReference<Map<Long, Double>>() {
                    }
            ).getBody();

            if (response != null) {
                precosTemp = response;
            }
            log.info("Preços consultados em lote com sucesso.");
        } catch (Exception e) {
            log.error("Falha ao consultar lote de preços no sd-preco: {}", e.getMessage());
        }

        final Map<Long, Double> precosFinais = precosTemp;

        return produtos.stream()
                .map(produto -> new ProdutoResponseDTO(
                        produto.getId(),
                        produto.getNome(),
                        produto.getDescricao(),
                        precosFinais.get(produto.getId())
                ))
                .toList();
    }

    public ProdutoResponseDTO getProduto(Long id) {

        Produto produto;

        try {
            log.info("Buscando produto com id={}", id);
            produto = produtoRepository.findById(id)
                    .orElseThrow(() -> new ProdutoNaoEncontradoException("Produto não encontrado"));
        } catch (ProdutoNaoEncontradoException e) {
            log.warn("Produto com id={} não encontrado", id);
            throw e;
        }

        Double preco = null;
        try {
            PrecoResponseDTO precoResponse = restTemplate.getForObject(
                    sdPrecoUrl + "/preco/" + id,
                    PrecoResponseDTO.class
            );
            if (precoResponse != null) {
                preco = precoResponse.preco();
            }
        } catch (Exception e) {
            log.error("Erro ao comunicar com sd-preco para o id {}: {}", id, e.getMessage());
        }

        return new ProdutoResponseDTO(produto.getId(), produto.getNome(), produto.getDescricao(), preco);
    }
}