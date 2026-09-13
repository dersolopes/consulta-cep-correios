package com.dersolopes.consultaCepCorreios.exceptions;

// Exceção customizada simples
public class CepInvalidoException extends RuntimeException {
    public CepInvalidoException(String mensagem) {
        super(mensagem);
    }
}
