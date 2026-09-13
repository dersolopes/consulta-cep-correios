package com.dersolopes.consultaCepCorreios.mapper;

import com.dersolopes.consultaCepCorreios.dto.EnderecoResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class EnderecoMapper {

    private static final Logger logger = LoggerFactory.getLogger(EnderecoMapper.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    public EnderecoResponse mapearJsonParaResponse(String json, String formato, long duracaoMs) {
        try {
            EnderecoResponse response = new EnderecoResponse();
            
            if ("json".equalsIgnoreCase(formato) || "jsonp".equalsIgnoreCase(formato)) {
                JsonNode root = objectMapper.readTree(json);
                
                response.setCep(getTextValue(root, "cep"));
                response.setLogradouro(getTextValue(root, "logradouro"));
                response.setComplemento(getTextValue(root, "complemento"));
                response.setBairro(getTextValue(root, "bairro"));
                response.setLocalidade(getTextValue(root, "localidade"));
                response.setUf(getTextValue(root, "uf"));
                response.setIbge(getTextValue(root, "ibge"));
                response.setGia(getTextValue(root, "gia"));
                response.setDdd(getTextValue(root, "ddd"));
                response.setSiafi(getTextValue(root, "siafi"));
            } else if ("xml".equalsIgnoreCase(formato)) {
                // Para XML, retorna o texto cru por enquanto
                // Futuramente pode-se implementar parsing XML completo
                logger.debug("[MAPEAMENTO_XML] Retornando XML cru para formato XML");
                response.setCep("XML_FORMAT");
                response.setLogradouro(json);
            }
            
            response.setFormato(formato.toUpperCase());
            response.setTimestampConsulta(LocalDateTime.now());
            response.setDuracaoMs(duracaoMs);
            
            return response;
        } catch (Exception e) {
            logger.error("[ERRO_MAPEAMENTO] Erro ao mapear resposta: {}", e.getMessage());
            throw new RuntimeException("Erro ao mapear resposta da API: " + e.getMessage());
        }
    }

    private String getTextValue(JsonNode node, String fieldName) {
        JsonNode field = node.get(fieldName);
        return field != null && !field.isNull() ? field.asText() : "";
    }
}
