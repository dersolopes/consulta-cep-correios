package com.dersolopes.consultaCepCorreios.controllers;

import com.dersolopes.consultaCepCorreios.services.ViaCepService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/endereco")
public class        EnderecoController {

    // Injetamos o serviço que criamos no Passo 3
    private final ViaCepService viaCepService;

    public EnderecoController(ViaCepService viaCepService) {
        this.viaCepService = viaCepService;
    }

    // Criamos uma rota GET que aceita um CEP na URL: /api/endereco/01001000
    @GetMapping("/{cep}/{formato}")
    public String obterEndereco(@PathVariable String cep,
                                @PathVariable String formato) {
        // Chamamos o HttpClient do Java para buscar os dados lá fora
        return viaCepService.buscarEnderecoPorCep(cep,formato.toLowerCase());
    }
}
