package com.batista.Resource;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.batista.event.RecursoCriadoEvent;
import com.batista.model.Lancamento;
import com.batista.projection.LancamentoResumo;
import com.batista.repository.LancamentoFilter;
import com.batista.repository.LancamentoRepository;
import com.batista.service.LancamentoService;

@RestController
@RequestMapping("/lancamentos")
public class LancamentoResource {

	@Autowired
	private LancamentoRepository repository;
	
	@Autowired
	private LancamentoService service;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	// retorna a lista de todos os lancamentos
	@GetMapping
	public ResponseEntity<?> listar() {
		List<Lancamento> lancamentos = this.repository.findAll();
		return !lancamentos.isEmpty() ? ResponseEntity.ok(lancamentos) : ResponseEntity.noContent().build();
	}
	
	// listagem p/ pesquisa, passando filtros por critério de descrição, data de vencimento "de" e "até"
	@GetMapping("/pesquisar")
	public  List<Lancamento> pesquisar(LancamentoFilter filter) {
		return this.repository.filtrar(filter);
	}
	
	// sm ao anterior, lista c/ possibilidade de passar, além dos filtros,  tb paginação
	@GetMapping("/paginacao")
	public Page<Lancamento> paginacao(LancamentoFilter filter, Pageable pageable) {
		return this.repository.filtroComPaginacao(filter, pageable);
	}
	
	@GetMapping(path="/paginacao", params="resumo") // se tiver o param resumo ele executa esse método em vez do de cima
	public Page<LancamentoResumo> resumir(LancamentoFilter filter, Pageable pageable) {
		return this.repository.resumir(filter, pageable);
	}
	
	
	// retorna um lancamento pelo codigo
	@GetMapping("/{codigo}")
	public ResponseEntity<?> buscarLancamentoPorCodigo(@PathVariable Long codigo) {
	
	Optional<Lancamento> opt = this.repository.findById(codigo);
		if(opt.isPresent()) {
			return ResponseEntity.ok(opt.get());
		}
		else {
			return ResponseEntity.notFound().build();
		}
	}
	//Salvando um novo lancamento, no json além dos dados especifficos de lancamentos 
	// é preciso informar o código da pessoa e da categoria (os demais dados de pessoa e categoria ñ precisam ser informados)
	// se passar um id de categoria ou pessoa inexistente gera a exceção DataIntegrityViolationException q é tratada no ExceptinConfig
    @PostMapping 
    public ResponseEntity<Lancamento> criar(@Valid @RequestBody Lancamento lancamento, HttpServletResponse response) {
    	Lancamento lancamentoSalvo = this.service.salvar(lancamento);
    	this.publisher.publishEvent(new RecursoCriadoEvent(this, response, lancamentoSalvo.getCodigo()));
    	return ResponseEntity.status(HttpStatus.CREATED).body(lancamentoSalvo);
    }
    
    // apagando lançamentos pelo código
    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void apagarLancamento(@PathVariable Long codigo) {
    	this.repository.deleteById(codigo);
    }
   
}
