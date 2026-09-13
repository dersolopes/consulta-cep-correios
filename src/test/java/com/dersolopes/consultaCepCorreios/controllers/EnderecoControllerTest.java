package com.dersolopes.consultaCepCorreios.controllers;

import com.dersolopes.consultaCepCorreios.dto.EnderecoDTO;
import com.dersolopes.consultaCepCorreios.mapper.EnderecoMapper;
import com.dersolopes.consultaCepCorreios.repositories.EnderecoRepository;
import com.dersolopes.consultaCepCorreios.services.EnderecoService;
import com.dersolopes.consultaCepCorreios.services.ViaCepService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EnderecoController.class)
class EnderecoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ViaCepService viaCepService;

    @MockBean
    private EnderecoService enderecoService;

    @MockBean
    private EnderecoRepository enderecoRepository;

    @MockBean
    private EnderecoMapper enderecoMapper;

    @Test
    void testObterEndereco() throws Exception {
        String mockResponse = "{\"cep\":\"06322-320\",\"logradouro\":\"Rua Teste\"}";
        when(viaCepService.buscarEnderecoPorCep(anyString(), anyString())).thenReturn(mockResponse);

        mockMvc.perform(get("/api/endereco/06322320/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cep").value("06322-320"));
    }

    @Test
    void testObterEnderecoInvalidCep() throws Exception {
        mockMvc.perform(get("/api/endereco/123/json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testObterEnderecoInvalidFormat() throws Exception {
        mockMvc.perform(get("/api/endereco/06322320/invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testHistoricoPorCep() throws Exception {
        EnderecoDTO dto = new EnderecoDTO(
                "06322320", "Rua Teste", "", "Bairro Teste",
                "Cidade Teste", "SP", "1234567", "1234", "11", "1234",
                "JSON", LocalDateTime.now(), 500L
        );
        List<EnderecoDTO> mockList = Arrays.asList(dto);
        when(enderecoService.buscarHistoricoPorCep(anyString())).thenReturn(mockList);

        mockMvc.perform(get("/api/endereco/historico/06322320"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cep").value("06322320"));
    }

    @Test
    void testHistoricoPorCepInvalidCep() throws Exception {
        mockMvc.perform(get("/api/endereco/historico/123"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testHistoricoPorCepEmpty() throws Exception {
        when(enderecoService.buscarHistoricoPorCep(anyString())).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/endereco/historico/06322320"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testHistorico() throws Exception {
        EnderecoDTO dto = new EnderecoDTO(
                "06322320", "Rua Teste", "", "Bairro Teste",
                "Cidade Teste", "SP", "1234567", "1234", "11", "1234",
                "JSON", LocalDateTime.now(), 500L
        );
        List<EnderecoDTO> mockList = Arrays.asList(dto);
        when(enderecoService.obterTodoHistorico()).thenReturn(mockList);

        mockMvc.perform(get("/api/endereco/historico"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cep").value("06322320"));
    }

    @Test
    void testHistoricoEmpty() throws Exception {
        when(enderecoService.obterTodoHistorico()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/endereco/historico"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
