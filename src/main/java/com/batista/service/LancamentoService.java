package com.batista.service;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
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
	
	// Atualiza lançamento
	public Lancamento atualizaLancamento(Long codigo, Lancamento lancamento) {
	
     // com base no codigo testa se o lancamento passado existe no BD
	 Optional<Lancamento> opt = this.lancamentoRepository.findById(codigo);	 
	 if(!opt.isPresent()) {
		 throw new IllegalArgumentException();
	 }
	 // extrai o lancamneot salvo no BD de dentro do optional
	 Lancamento lancamentoSalvo = opt.get();
	 
	 // compara se o a pessoa associada ao lancamento atualizado é a mesma q a q está salva no Bd, se ñ for ele realiza uma validação
	 if(!lancamento.getPessoa().equals(lancamentoSalvo.getPessoa())) {
		 validaPessoa(lancamento);
	 }
	 
	 // após a validação ele copia os dados do lançamento atualizado p/ o objeto do BD e atualiza o banco c/ o lancamento autalizado
	 BeanUtils.copyProperties(lancamento, lancamentoSalvo, "codigo");
	 return this.lancamentoRepository.save(lancamentoSalvo);
	 
	}
	
	private void validaPessoa(Lancamento lancamento) {
	   Pessoa pessoa = null;
	   
	   if(lancamento.getPessoa().getCodigo() != null) {
		   pessoa = this.pessoaRepository.findById(lancamento.getPessoa().getCodigo()).get();
	   }
	   
	   if(pessoa == null || pessoa.isInativo()) {
		   throw new PessoaInexistenteOuInativaException();
	   }
	}
	
}
