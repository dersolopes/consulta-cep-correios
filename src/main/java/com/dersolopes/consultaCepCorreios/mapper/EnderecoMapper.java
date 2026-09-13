package com.dersolopes.consultaCepCorreios.mapper;

import com.dersolopes.consultaCepCorreios.dto.EnderecoDTO;
import com.dersolopes.consultaCepCorreios.entities.Endereco;
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

    public EnderecoDTO jsonParaDto(String json, String formato, long duracaoMs) {
        try {
            String cep = "", logradouro = "", complemento = "", bairro = "",
                   localidade = "", uf = "", ibge = "", gia = "", ddd = "", siafi = "";
            
            if ("json".equalsIgnoreCase(formato) || "jsonp".equalsIgnoreCase(formato)) {
                JsonNode root = objectMapper.readTree(json);
                
                cep = getTextValue(root, "cep").replace("-", "");
                logradouro = getTextValue(root, "logradouro");
                complemento = getTextValue(root, "complemento");
                bairro = getTextValue(root, "bairro");
                localidade = getTextValue(root, "localidade");
                uf = getTextValue(root, "uf");
                ibge = getTextValue(root, "ibge");
                gia = getTextValue(root, "gia");
                ddd = getTextValue(root, "ddd");
                siafi = getTextValue(root, "siafi");
            } else if ("xml".equalsIgnoreCase(formato)) {
                // Para XML, não fazemos parsing por enquanto
                logger.debug("[MAPEAMENTO_XML] XML não suportado para DTO, retornando DTO vazio");
                cep = "XML_FORMAT";
            }
            
            return new EnderecoDTO(
                cep, logradouro, complemento, bairro, localidade, uf,
                ibge, gia, ddd, siafi, formato.toUpperCase(),
                LocalDateTime.now(), duracaoMs
            );
        } catch (Exception e) {
            logger.error("[ERRO_MAPEAMENTO] Erro ao mapear JSON para DTO: {}", e.getMessage());
            throw new RuntimeException("Erro ao mapear resposta da API: " + e.getMessage());
        }
    }

    public Endereco dtoParaEntidade(EnderecoDTO dto) {
        return new Endereco(
            dto.cep(), dto.logradouro(), dto.complemento(), dto.bairro(),
            dto.localidade(), dto.uf(), dto.ibge(), dto.gia(), dto.ddd(),
            dto.siafi(), dto.formato(), dto.timestampConsulta(), dto.duracaoMs()
        );
    }

    public EnderecoDTO entidadeParaDto(Endereco entity) {
        return new EnderecoDTO(
            entity.getCep(), entity.getLogradouro(), entity.getComplemento(),
            entity.getBairro(), entity.getLocalidade(), entity.getUf(),
            entity.getIbge(), entity.getGia(), entity.getDdd(), entity.getSiafi(),
            entity.getFormato(), entity.getTimestampConsulta(), entity.getDuracaoMs()
        );
    }

    private String getTextValue(JsonNode node, String fieldName) {
        JsonNode field = node.get(fieldName);
        return field != null && !field.isNull() ? field.asText() : "";
    }
}
