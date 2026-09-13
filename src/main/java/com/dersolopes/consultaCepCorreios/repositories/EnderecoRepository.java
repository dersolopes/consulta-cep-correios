package com.dersolopes.consultaCepCorreios.repositories;

import com.dersolopes.consultaCepCorreios.entities.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Long> {

    // Busca por CEP específico e ordena as consultas por data decrescente
    List<Endereco> findByCepOrderByTimestampConsultaDesc(String cep);

    // Busca TODOS os endereços do banco e ordena por data decrescente
    List<Endereco> findByOrderByTimestampConsultaDesc();}
