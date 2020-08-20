package com.batista.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.batista.model.Pessoa;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, Long> {

	// ao criar o método dessa forma ele já reconhece q deve fazer pesquisa pelo nome
	public Page<Pessoa> findByNomeContaining(String nome, Pageable pageable);
}
