package com.dersolopes.consultaCepCorreios.services;

import com.dersolopes.consultaCepCorreios.dto.EnderecoDTO;
import com.dersolopes.consultaCepCorreios.entities.Endereco;
import com.dersolopes.consultaCepCorreios.mapper.EnderecoMapper;
import com.dersolopes.consultaCepCorreios.repositories.EnderecoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnderecoServiceTest {

    @Mock
    private EnderecoRepository enderecoRepository;

    @Mock
    private EnderecoMapper enderecoMapper;

    @InjectMocks
    private EnderecoService enderecoService;

    private Endereco endereco;
    private EnderecoDTO enderecoDTO;

    @BeforeEach
    void setUp() {
        endereco = new Endereco(
            "06322320", "Rua Antônio Lopes da Silva", "", "Jardim Carapicuíba",
            "Carapicuíba", "SP", "3510609", "2550", "11", "6313",
            "JSON", LocalDateTime.now(), 500L
        );

        enderecoDTO = new EnderecoDTO(
            "06322320", "Rua Antônio Lopes da Silva", "", "Jardim Carapicuíba",
            "Carapicuíba", "SP", "3510609", "2550", "11", "6313",
            "JSON", LocalDateTime.now(), 500L
        );
    }

    @Test
    void testBuscarHistoricoPorCep() {
        when(enderecoRepository.findByCepOrderByTimestampConsultaDesc(anyString()))
            .thenReturn(Arrays.asList(endereco));
        when(enderecoMapper.entidadeParaDto(endereco)).thenReturn(enderecoDTO);

        List<EnderecoDTO> result = enderecoService.buscarHistoricoPorCep("06322320");

        assertEquals(1, result.size());
        assertEquals("06322320", result.get(0).cep());
    }

    @Test
    void testBuscarHistoricoPorCepEmpty() {
        when(enderecoRepository.findByCepOrderByTimestampConsultaDesc(anyString()))
            .thenReturn(Arrays.asList());

        List<EnderecoDTO> result = enderecoService.buscarHistoricoPorCep("06322320");

        assertEquals(0, result.size());
    }

    @Test
    void testObterTodoHistorico() {
        when(enderecoRepository.findByOrderByTimestampConsultaDesc())
            .thenReturn(Arrays.asList(endereco));
        when(enderecoMapper.entidadeParaDto(endereco)).thenReturn(enderecoDTO);

        List<EnderecoDTO> result = enderecoService.obterTodoHistorico();

        assertEquals(1, result.size());
        assertEquals("06322320", result.get(0).cep());
    }

    @Test
    void testObterTodoHistoricoEmpty() {
        when(enderecoRepository.findByOrderByTimestampConsultaDesc())
            .thenReturn(Arrays.asList());

        List<EnderecoDTO> result = enderecoService.obterTodoHistorico();

        assertEquals(0, result.size());
    }
}
