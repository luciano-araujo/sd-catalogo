package br.edu.ifsp.sd_catalogo.service;

import br.edu.ifsp.sd_catalogo.dto.ProdutoResponseDTO;
import org.springframework.stereotype.Service;

@Service
public class CatalogoService {

    public ProdutoResponseDTO getProduto(Long id) {
        // Lógica para buscar o produto e consultar o preço no sd-preco
        // Aqui você pode usar um cliente HTTP para fazer a chamada ao serviço de preços
        // e retornar os dados do produto com o preço atualizado
        return new ProdutoResponseDTO(id, "Produto Exemplo", "Descrição do produto", 99.99);
    }
}