package com.dersolopes.consultaCepCorreios.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.springframework.stereotype.Service;

@Service
public class ViaCepService {

    public String buscarEnderecoPorCep(String cep, String formato) {
        // 1. Criamos o cliente HTTP
        HttpClient client = HttpClient.newHttpClient();

        // Se vier jsonp no formato substituimos para /json/?callback=callback_name
        if (formato.equals("jsonp")) {
            formato = "json/?callback=callback_name";
        }

        // 2. Montamos a URL da API gratuita do ViaCEP
        // Agora o formato (/json, /xml, /jsonp) vem como variável
        String url = "https://viacep.com.br/ws/" + cep + "/" + formato + "/";


        // 3. Construímos a requisição GET
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            // 4. Enviamos a requisição e guardamos a resposta como texto (String)
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 5. Retornamos o JSON que a API do ViaCEP nos devolveu
            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"erro\": \"Não foi possível buscar o CEP\"}";
        }
    }
}
