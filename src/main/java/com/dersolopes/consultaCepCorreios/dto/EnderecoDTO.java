package com.dersolopes.consultaCepCorreios.dto;

import java.time.LocalDateTime;

public record EnderecoDTO(
    String cep,
    String logradouro,
    String complemento,
    String bairro,
    String localidade,
    String uf,
    String ibge,
    String gia,
    String ddd,
    String siafi,
    String formato,
    LocalDateTime timestampConsulta,
    Long duracaoMs
) {
    public EnderecoDTO {
        if (cep == null) cep = "";
        if (logradouro == null) logradouro = "";
        if (complemento == null) complemento = "";
        if (bairro == null) bairro = "";
        if (localidade == null) localidade = "";
        if (uf == null) uf = "";
        if (ibge == null) ibge = "";
        if (gia == null) gia = "";
        if (ddd == null) ddd = "";
        if (siafi == null) siafi = "";
        if (formato == null) formato = "";
    }
}
