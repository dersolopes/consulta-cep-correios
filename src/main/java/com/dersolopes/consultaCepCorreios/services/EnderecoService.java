package com.dersolopes.consultaCepCorreios.services;

import com.dersolopes.consultaCepCorreios.controllers.EnderecoController;
import com.dersolopes.consultaCepCorreios.dto.EnderecoDTO;
import com.dersolopes.consultaCepCorreios.entities.Endereco;
import com.dersolopes.consultaCepCorreios.mapper.EnderecoMapper;
import com.dersolopes.consultaCepCorreios.repositories.EnderecoRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EnderecoService {
    private final EnderecoRepository enderecoRepository;
    private final EnderecoMapper enderecoMapper;

    private static final Logger logger = LoggerFactory.getLogger(EnderecoController.class);


    public List<EnderecoDTO> buscarHistoricoPorCep(String cep) {
        List<Endereco> enderecos = enderecoRepository.findByCepOrderByTimestampConsultaDesc(cep);
        logger.info("Metodo 'buscarHistoricoPorCep', quantidade de registros= " + enderecos.size());
        return enderecos.stream()
                .map(enderecoMapper::entidadeParaDto)
                .collect(Collectors.toList());
    }

    public List<EnderecoDTO> obterTodoHistorico() {
        List<Endereco> enderecos = enderecoRepository.findByOrderByTimestampConsultaDesc();
        logger.info("Metodo 'obterTodoHistorico', quantidade de registros= " + enderecos.size());
        return enderecos.stream()
                .map(enderecoMapper::entidadeParaDto)
                .collect(Collectors.toList());
    }

}