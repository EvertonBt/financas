package com.batista.Resource;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.batista.model.Pessoa;
import com.batista.service.*;
import com.batista.repository.PessoaRepository;


@RestController
@RequestMapping("/pessoas")
public class PessoaResource {

	@Autowired
	private PessoaRepository repository;
	
	@Autowired
	private PessoaService service;
	
	// listagem de pessoas sem paginação
	@GetMapping
	public ResponseEntity<?> listar(){
		List<Pessoa> pessoas = this.repository.findAll();
		return !pessoas.isEmpty() ? ResponseEntity.ok(pessoas) : ResponseEntity.noContent().build();
	}
	
	// listagem p/ pesquisa c/ paginação e filtro usando o critério de nome
	@GetMapping(params="nome")
	public Page<Pessoa> resumir(String nome, Pageable pageable) {
		return this.repository.findByNomeContaining(nome, pageable);
	}
	
	// salva uma nova pessoa
	@PostMapping
	public ResponseEntity<Pessoa> criar(@Valid @RequestBody Pessoa pessoa, HttpServletResponse response) {
	    Pessoa pessoaSalva = this.repository.save(pessoa);

    	// gerando a uri do recurso (location) após salvar (formato: http://localhost:8010/categorias/6 )
    	URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{codigo}")
    			.buildAndExpand(pessoaSalva.getCodigo()).toUri();
    	response.setHeader("Location", uri.toASCIIString());
    	
    	// retorna o código 201 se der certo e a categoria salva
    	return ResponseEntity.created(uri).body(pessoaSalva);
		
	}
	
	
	// pesquisa pessoa especifica pelo codigo
	@GetMapping("/{codigo}")
	public ResponseEntity<Pessoa> buscarPessoaPorCodigo(@PathVariable Long codigo) {
		Optional<Pessoa> op = this.repository.findById(codigo);
		if(op.isPresent()) {
			return ResponseEntity.ok(op.get()); // retorna 200 ok
		}
		else {
			return ResponseEntity.notFound().build(); // retorna not found 404
		}
	}
	
	// Atualiza todos os dados
	@PutMapping("/{codigo}")
	public ResponseEntity<Pessoa> atualizar(@PathVariable Long codigo, @Valid @RequestBody Pessoa pessoa) {
	   Pessoa pessoaSalva =  this.service.atualizarPessoa(codigo, pessoa);
	   return ResponseEntity.ok(pessoaSalva);
	}
	
	// Atualiza parciamente somente o campo ativo
	/*
	 - se der certo e atualizar o campo "ativo" ele retorna 204 no content
	 - se passar codigo inexistente na url ele lança EmptyResultDataAccessExceptio nretorna  q ao ser tratada emite 404 not found
	 - se passar um tipo de dado inválido no json p/ o campo ativo q só aceita booelan ele emite um 400 Bad Request
	 - se passar null ou nada no json p/ o campo ativo ele invoca HttpMessageNotReadableException q ao ser tratada emite 400 BAD request
	 
	 */
	@PutMapping("/ativo/{codigo}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void atualizarParcialmente(@PathVariable Long codigo, @RequestBody Boolean ativo) {
		this.service.atualizaPessoaAtivo(codigo, ativo);
	}
	
	
	// se der certo e conseguir deletar ele retorna 204 no content, caso o dado já tenha sido apagado ele gera uma exceção q será tratada no 
	// Exception config
	@DeleteMapping("/{codigo}")
	@ResponseStatus(HttpStatus.NO_CONTENT) 
	public void remover(@PathVariable Long codigo) {
		this.repository.deleteById(codigo);
	}
	
}
