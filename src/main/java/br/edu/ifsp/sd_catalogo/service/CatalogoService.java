package br.edu.ifsp.sd_catalogo.service;

import br.edu.ifsp.sd_catalogo.dto.PrecoResponseDTO;
import br.edu.ifsp.sd_catalogo.dto.ProdutoRequestDTO;
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
        log.info("Consultando todos os produtos");
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

        Double preco = getPrecoAtualDoSdPreco(id);
        return new ProdutoResponseDTO(produto.getId(), produto.getNome(), produto.getDescricao(), preco);
    }

    public ProdutoResponseDTO createProduto(ProdutoRequestDTO dto) {
        log.info("Criando novo produto: {}", dto.nome());

        if (dto.nome() == null || dto.nome().isBlank() || dto.preco() == null) {
            throw new IllegalArgumentException("Nome e Preço são obrigatórios para criar um produto.");
        }

        Produto novoProduto = new Produto();
        novoProduto.setNome(dto.nome());
        novoProduto.setDescricao(dto.descricao());

        Produto produtoSalvo = produtoRepository.save(novoProduto);

        try {
            restTemplate.exchange(
                    sdPrecoUrl + "/preco/novo/" + produtoSalvo.getId(),
                    HttpMethod.POST,
                    new org.springframework.http.HttpEntity<>(Map.of("valor", dto.preco())),
                    Void.class
            );
            log.info("Novo preço cadastrado no sd-preco para o produto: {}", produtoSalvo.getId());
        } catch (Exception e) {
            log.error("Erro ao registrar o preço no sd-preco para o id {}: {}", produtoSalvo.getId(), e.getMessage());
        }

        return new ProdutoResponseDTO(
                produtoSalvo.getId(),
                produtoSalvo.getNome(),
                produtoSalvo.getDescricao(),
                dto.preco()
        );
    }

    public ProdutoResponseDTO updateProduto(Long id, ProdutoRequestDTO dto) {
        log.info("Atualizando produto com id={}", id);

        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ProdutoNaoEncontradoException("Produto não encontrado"));

        if (dto.nome() != null && !dto.nome().isBlank()) {
            produto.setNome(dto.nome());
        }
        if (dto.descricao() != null && !dto.descricao().isBlank()) {
            produto.setDescricao(dto.descricao());
        }
        produtoRepository.save(produto);

        Double precoAtualizado;

        if (dto.preco() != null) {
            try {
                restTemplate.exchange(
                        sdPrecoUrl + "/preco/atualizar/" + id,
                        HttpMethod.PUT,
                        new org.springframework.http.HttpEntity<>(Map.of("valor", dto.preco())),
                        Void.class
                );
                precoAtualizado = dto.preco();
                log.info("Preço atualizado no sd-preco: {}", precoAtualizado);
            } catch (Exception e) {
                log.error("Erro ao atualizar preço no sd-preco para o id {}: {}", id, e.getMessage());
                precoAtualizado = getPrecoAtualDoSdPreco(id);
            }
        } else {
            precoAtualizado = getPrecoAtualDoSdPreco(id);
        }

        return new ProdutoResponseDTO(produto.getId(), produto.getNome(), produto.getDescricao(), precoAtualizado);
    }

    public void deleteProduto(Long id) {
        log.info("Excluindo produto com id={}", id);

        if (!produtoRepository.existsById(id)) {
            throw new ProdutoNaoEncontradoException("Produto não encontrado");
        }

        produtoRepository.deleteById(id);
        log.info("Produto {} removido do banco local (catálogo)", id);

        try {
            restTemplate.exchange(
                    sdPrecoUrl + "/preco/delete/" + id,
                    HttpMethod.DELETE,
                    null,
                    Void.class
            );
            log.info("Apagado do sd-preco");
        } catch (Exception e) {
            log.error("Erro ao solicitar exclusão no sd-preco para o id {}: {}", id, e.getMessage());
        }
    }

    private Double getPrecoAtualDoSdPreco(Long id) {
        try {
            PrecoResponseDTO precoResponse = restTemplate.getForObject(
                    sdPrecoUrl + "/preco/" + id,
                    PrecoResponseDTO.class
            );
            if (precoResponse != null) {
                return precoResponse.preco();
            }
        } catch (Exception e) {
            log.error("Erro ao comunicar com sd-preco para o id {}: {}", id, e.getMessage());
        }
        return null;
    }
}