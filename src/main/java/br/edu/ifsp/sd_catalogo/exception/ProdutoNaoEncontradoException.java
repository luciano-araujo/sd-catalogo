package br.edu.ifsp.sd_catalogo.exception;

public class ProdutoNaoEncontradoException extends RuntimeException{

    public ProdutoNaoEncontradoException(String message){
        super(message);
    }

}