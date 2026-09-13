package com.dersolopes.consultaCepCorreios.services;

import com.dersolopes.consultaCepCorreios.entities.Endereco;
import com.dersolopes.consultaCepCorreios.mapper.EnderecoMapper;
import com.dersolopes.consultaCepCorreios.repositories.EnderecoRepository;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ViaCepService {

    private static final Logger logger = LoggerFactory.getLogger(ViaCepService.class);
    private final EnderecoRepository enderecoRepository;
    private final EnderecoMapper enderecoMapper;

    public ViaCepService(EnderecoRepository enderecoRepository, EnderecoMapper enderecoMapper) {
        this.enderecoRepository = enderecoRepository;
        this.enderecoMapper = enderecoMapper;
    }

    @Cacheable(value = "cepCache", key = "#cep + '-' + #formato")
    @Transactional
    public String buscarEnderecoPorCep(String cep, String formato) {
        long inicio = System.currentTimeMillis();
        // 1. Criamos o cliente HTTP
        HttpClient client = HttpClient.newHttpClient();
        String url;

        // Monta a URL removendo a barra final se for JSONP
        if ("jsonp".equalsIgnoreCase(formato)) {
            url = "https://viacep.com.br/ws/" + cep + "/json/?callback=callback_name";
        } else {
            url = "https://viacep.com.br/ws/" + cep + "/" + formato + "/";
        }

        logger.debug("[CHAMADA_API_EXTERNA] URL: {}, CEP: {}, Formato: {}", url, cep, formato.toUpperCase());
        
        // 2. Montamos a URL da API gratuita do ViaCEP
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            // 4. Enviamos a requisição e guardamos a resposta como texto (String)
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            logger.debug("[RESPOSTA_API_EXTERNA] Status: {}, CEP: {}, Tamanho: {} bytes", response.statusCode(), cep, response.body().length());

            // Se o ViaCEP responder com erro HTTP (ex: CEP inválido)
            if (response.statusCode() >= 400) {
                logger.warn("[API_EXTERNA_ERRO] CEP: {}, Status: {}, Mensagem: Formato do CEP está inválido", cep, response.statusCode());
                throw new RuntimeException("O formato do CEP está inválido.");
            }
            // 5. Armazenamos o JSON que a API do ViaCEP nos devolveu
            String corpoResposta = response.body();

            // 6. ViaCEP avisa se o CEP não existe colocando "erro": true no corpo
            if (corpoResposta.contains("\"erro\"") && corpoResposta.contains("true")) {
                logger.warn("[CEP_NAO_ENCONTRADO] CEP: {}, Mensagem: CEP fornecido não foi encontrado na base ViaCEP", cep);
                throw new RuntimeException("O CEP fornecido não foi encontrado.");
            }
            // 7. Devolvemos a resposta armazenada
            logger.info("[API_EXTERNA_SUCESSO] CEP: {}, Status: {}", cep, response.statusCode());
            long duracao = System.currentTimeMillis() - inicio;
            logger.debug("[DURACAO_REQUISICAO] CEP: {}, Duracao: {}ms", cep, duracao);
            
            // 8. Salvar histórico no banco (apenas para JSON, não XML)
            if ("json".equalsIgnoreCase(formato)) {
                salvarHistorico(corpoResposta, formato, duracao);
            }
            
            return corpoResposta;
        } catch (Exception e) {
            logger.error("[ERRO_SERVICO] CEP: {}, Erro: {}, StackTrace: {}", cep, e.getMessage(), e.getStackTrace()[0].toString());
            // REPASSA o erro original para o GerenciadorDeExcecoes poder ler o e.getMessage()
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void salvarHistorico(String json, String formato, long duracaoMs) {
        try {
            var dto = enderecoMapper.jsonParaDto(json, formato, duracaoMs);
            var entidade = enderecoMapper.dtoParaEntidade(dto);
            enderecoRepository.save(entidade);
            logger.info("[HISTORICO_SALVO] CEP: {}, Formato: {}", dto.cep(), formato.toUpperCase());
        } catch (Exception e) {
            logger.error("[ERRO_SALVAR_HISTORICO] Erro ao salvar histórico: {}", e.getMessage());
            // Não lança exceção para não quebrar a resposta da API
        }
    }
}
