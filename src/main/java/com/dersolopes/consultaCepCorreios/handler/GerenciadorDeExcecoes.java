package com.dersolopes.consultaCepCorreios.handler;

import com.dersolopes.consultaCepCorreios.exceptions.CepInvalidoException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GerenciadorDeExcecoes {

    private static final Logger logger = LoggerFactory.getLogger(GerenciadorDeExcecoes.class);

    // Captura erros de validação do Bean Validation (@Validated)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> tratarValidacao(ConstraintViolationException ex) {
        String mensagens = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        logger.error("[EXCECAO_VALIDACAO] Mensagens: {}, Status: 400, Timestamp: {}", mensagens, java.time.LocalDateTime.now());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("erro", mensagens));
    }

    // Captura especificamente a nossa exceção de CEP inválido/não encontrado
    @ExceptionHandler(CepInvalidoException.class)
    public ResponseEntity<Map<String, String>> tratarCepInvalido(CepInvalidoException ex) {
        logger.error("[EXCECAO_TRATADA] Tipo: CepInvalidoException, Mensagem: {}, Status: 400, Timestamp: {}", ex.getMessage(), java.time.LocalDateTime.now());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST) // Código HTTP 400
                .body(Map.of("erro", ex.getMessage()));
    }

    // Captura qualquer outro erro inesperado (ex: servidor ViaCEP fora do ar)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> tratarErroGenerico(Exception ex) {
        logger.error("[EXCECAO_GENERICA] Tipo: {}, Mensagem: {}, Status: 500, Timestamp: {}", ex.getClass().getSimpleName(), ex.getMessage(), java.time.LocalDateTime.now());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR) // Código HTTP 500
                .body(Map.of("erro", "Ocorreu um erro interno no servidor: " + ex.getMessage()));
    }
}
