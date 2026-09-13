package com.dersolopes.consultaCepCorreios.controllers;

import com.dersolopes.consultaCepCorreios.dto.EnderecoResponse;
import com.dersolopes.consultaCepCorreios.services.ViaCepService;
import jakarta.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/endereco")
@Validated
public class        EnderecoController {

    private static final Logger logger = LoggerFactory.getLogger(EnderecoController.class);

    // Injetamos o serviço que criamos no Passo 3
    private final ViaCepService viaCepService;

    public EnderecoController(ViaCepService viaCepService) {
        this.viaCepService = viaCepService;
    }

    // Criamos uma rota GET que aceita um CEP na URL: /api/endereco/01001000
    @GetMapping("/{cep}/{formato}")
    public EnderecoResponse obterEndereco(
            @PathVariable @Pattern(regexp = "\\d{8}", message = "CEP deve ter exatamente 8 dígitos numéricos") String cep,
            @PathVariable @Pattern(regexp = "json|xml|jsonp", message = "Formato deve ser json, xml ou jsonp") String formato) {
        long inicio = System.currentTimeMillis();
        logger.info("[INICIO_REQUISICAO] CEP: {}, Formato: {}, Timestamp: {}", cep, formato.toUpperCase(), java.time.LocalDateTime.now());
        
        try {
            // Chamamos o HttpClient do Java para buscar os dados lá fora
            EnderecoResponse resultado = viaCepService.buscarEnderecoPorCep(cep, formato.toLowerCase());
            long duracao = System.currentTimeMillis() - inicio;
            logger.info("[REQUISICAO_SUCESSO] CEP: {}, Formato: {}, Duracao: {}ms, Timestamp: {}", cep, formato.toUpperCase(), duracao, java.time.LocalDateTime.now());
            return resultado;
        } catch (Exception e) {
            long duracao = System.currentTimeMillis() - inicio;
            logger.error("[REQUISICAO_ERRO] CEP: {}, Formato: {}, Duracao: {}ms, Erro: {}, Timestamp: {}", cep, formato.toUpperCase(), duracao, e.getMessage(), java.time.LocalDateTime.now());
            throw e;
        }
    }
}
