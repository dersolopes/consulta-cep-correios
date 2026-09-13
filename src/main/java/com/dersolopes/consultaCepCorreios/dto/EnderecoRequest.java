package com.dersolopes.consultaCepCorreios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class EnderecoRequest {

    @NotBlank(message = "CEP é obrigatório")
    @Size(min = 8, max = 8, message = "CEP deve ter exatamente 8 dígitos")
    @Pattern(regexp = "\\d+", message = "CEP deve conter apenas números")
    private String cep;

    @NotBlank(message = "Formato é obrigatório")
    @Pattern(regexp = "json|xml|jsonp", message = "Formato deve ser json, xml ou jsonp")
    private String formato;

    public EnderecoRequest() {
    }

    public EnderecoRequest(String cep, String formato) {
        this.cep = cep;
        this.formato = formato;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }
}
