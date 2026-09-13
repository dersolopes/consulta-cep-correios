package com.dersolopes.consultaCepCorreios.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "enderecos")
@Data
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 9)
    private String cep;

    @Column(length = 255)
    private String logradouro;

    @Column(length = 100)
    private String complemento;

    @Column(length = 100)
    private String bairro;

    @Column(nullable = false, length = 100)
    private String localidade;

    @Column(nullable = false, length = 2)
    private String uf;

    @Column(length = 10)
    private String ibge;

    @Column(length = 5)
    private String gia;

    @Column(length = 3)
    private String ddd;

    @Column(length = 7)
    private String siafi;

    @Column(nullable = false, length = 10)
    private String formato;

    @Column(nullable = false)
    private LocalDateTime timestampConsulta;

    @Column(nullable = false)
    private Long duracaoMs;

    public Endereco() {
    }

    public Endereco(String cep, String logradouro, String complemento, String bairro,
                    String localidade, String uf, String ibge, String gia, String ddd,
                    String siafi, String formato, LocalDateTime timestampConsulta, Long duracaoMs) {
        this.cep = cep;
        this.logradouro = logradouro;
        this.complemento = complemento;
        this.bairro = bairro;
        this.localidade = localidade;
        this.uf = uf;
        this.ibge = ibge;
        this.gia = gia;
        this.ddd = ddd;
        this.siafi = siafi;
        this.formato = formato;
        this.timestampConsulta = timestampConsulta;
        this.duracaoMs = duracaoMs;
    }

}
