package com.batista.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.batista.exception.PessoaInexistenteOuInativaException;
import com.batista.model.Lancamento;
import com.batista.model.Pessoa;
import com.batista.repository.LancamentoRepository;
import com.batista.repository.PessoaRepository;

@Service
public class LancamentoService {
	
	@Autowired
	PessoaRepository pessoaRepository;
	
	@Autowired
	LancamentoRepository lancamentoRepository;

	//só permite salvar o lançamento se a pessoa associada ñ for inexistente nem inativa
	public Lancamento salvar(Lancamento lancamento) {
	     // recupera a pessoa a partir do lancamento passado e verifica se ela existe e se está inativa
		Optional<Pessoa> opt = this.pessoaRepository.findById(lancamento.getPessoa().getCodigo());
	    
	    if(!opt.isPresent() || opt.get().isInativo()) {
	    	throw new PessoaInexistenteOuInativaException();
	    }
	    return this.lancamentoRepository.save(lancamento);
	}
}
