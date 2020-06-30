package com.batista.service;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Service;

import com.batista.model.Pessoa;
import com.batista.repository.PessoaRepository;;

@Service
public class PessoaService {

	@Autowired
	PessoaRepository repository;

	// Atualiza todos os dados 
	public Pessoa atualizarPessoa(Long codigo, Pessoa pessoa) {

		Pessoa pessoaSalva = this.buscarPessoaPorCodigo(codigo);
		// copia os dados do objeto pessoa passado no objeto pessoaSalva do BD, exceto o
		// código q já está definido corretamente
		BeanUtils.copyProperties(pessoa, pessoaSalva, "codigo");
		return this.repository.save(pessoaSalva);
	}
	
	//Atualiza somente o campo 'ativo' mudando de true p/ false ou vice-versa;
	public void atualizaPessoaAtivo(Long codigo, Boolean ativo) {
	   Pessoa pessoaSalva = this.buscarPessoaPorCodigo(codigo);
	   pessoaSalva.setAtivo(ativo);
	   this.repository.save(pessoaSalva);
		
	}
   

	
	private Pessoa buscarPessoaPorCodigo(Long codigo) {
		// busca a pessoa salva no BD pelo codigo informado, ele retorna um optional,
		// tem q usar o get() p/ retornar uma Pessoa
		Optional<Pessoa> opt = this.repository.findById(codigo);
		if (opt.isPresent()) {
			return opt.get(); // se existir, retorna a pessoa do BD
		} else {
			throw new EmptyResultDataAccessException(1);
		}
	}


}
