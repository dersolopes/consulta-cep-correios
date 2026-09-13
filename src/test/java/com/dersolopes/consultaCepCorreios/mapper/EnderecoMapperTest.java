package com.dersolopes.consultaCepCorreios.mapper;

import com.dersolopes.consultaCepCorreios.dto.EnderecoDTO;
import com.dersolopes.consultaCepCorreios.entities.Endereco;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EnderecoMapperTest {

    private EnderecoMapper enderecoMapper;

    @BeforeEach
    void setUp() {
        enderecoMapper = new EnderecoMapper();
    }

    @Test
    void testJsonParaDto() {
        String json = "{\"cep\":\"06322-320\",\"logradouro\":\"Rua Teste\",\"complemento\":\"\",\"bairro\":\"Bairro Teste\",\"localidade\":\"Cidade Teste\",\"uf\":\"SP\",\"ibge\":\"1234567\",\"gia\":\"1234\",\"ddd\":\"11\",\"siafi\":\"1234\"}";
        
        EnderecoDTO result = enderecoMapper.jsonParaDto(json, "json", 500L);

        assertNotNull(result);
        assertEquals("06322320", result.cep());
        assertEquals("Rua Teste", result.logradouro());
        assertEquals("Bairro Teste", result.bairro());
        assertEquals("Cidade Teste", result.localidade());
        assertEquals("SP", result.uf());
        assertEquals("JSON", result.formato());
        assertEquals(500L, result.duracaoMs());
    }

    @Test
    void testJsonParaDtoWithHyphenRemoval() {
        String json = "{\"cep\":\"06322-320\",\"logradouro\":\"Rua Teste\"}";
        
        EnderecoDTO result = enderecoMapper.jsonParaDto(json, "json", 300L);

        assertEquals("06322320", result.cep());
    }

    @Test
    void testDtoParaEntidade() {
        EnderecoDTO dto = new EnderecoDTO(
            "06322320", "Rua Teste", "", "Bairro Teste",
            "Cidade Teste", "SP", "1234567", "1234", "11", "1234",
            "JSON", LocalDateTime.now(), 500L
        );

        Endereco result = enderecoMapper.dtoParaEntidade(dto);

        assertNotNull(result);
        assertEquals("06322320", result.getCep());
        assertEquals("Rua Teste", result.getLogradouro());
        assertEquals("Bairro Teste", result.getBairro());
        assertEquals("Cidade Teste", result.getLocalidade());
        assertEquals("SP", result.getUf());
        assertEquals("JSON", result.getFormato());
    }

    @Test
    void testEntidadeParaDto() {
        Endereco entity = new Endereco(
            "06322320", "Rua Teste", "", "Bairro Teste",
            "Cidade Teste", "SP", "1234567", "1234", "11", "1234",
            "JSON", LocalDateTime.now(), 500L
        );

        EnderecoDTO result = enderecoMapper.entidadeParaDto(entity);

        assertNotNull(result);
        assertEquals("06322320", result.cep());
        assertEquals("Rua Teste", result.logradouro());
        assertEquals("Bairro Teste", result.bairro());
        assertEquals("Cidade Teste", result.localidade());
        assertEquals("SP", result.uf());
        assertEquals("JSON", result.formato());
    }

    @Test
    void testJsonParaDtoWithXmlFormat() {
        String json = "xml content";
        
        EnderecoDTO result = enderecoMapper.jsonParaDto(json, "xml", 200L);

        assertNotNull(result);
        assertEquals("XML_FORMAT", result.cep());
    }
}
