package com.batista.Resource;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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
import com.batista.model.Categoria;
import com.batista.repository.CategoriaRepository;

// Resource equivale ao controller
@RestController // convert o retorno do controlador no formato json
@RequestMapping("/categorias")
public class CategoriaResource {

	// injeta uma instancia do repositorio
	@Autowired
	private CategoriaRepository repository;
	
// injeta um publisher p/ invocar o Event q  chama o listener p/ gerar a uri/location, isso é usado p/ evitar duplicação de código
	@Autowired 
	private ApplicationEventPublisher publisher;
	
	// faz listagem, mas dessa forma ele vai retornar um array vazio [] caso ñ exista dados
	@GetMapping
	public List<Categoria> listar() {
		return this.repository.findAll(); 
	
	}
   
	// sm ao anterior, mas dessa forma ele envia um código 204 no content caso a lista esteja vazia  
    @GetMapping("/diferente")
		public ResponseEntity<?> listarDiferente() {
		List<Categoria> categorias = this.repository.findAll();
		return !categorias.isEmpty() ? ResponseEntity.ok(categorias) : ResponseEntity.noContent().build();

	}
    
    // salvando a categoria
    @PostMapping
  //  @ResponseStatus(HttpStatus.CREATED)  retorna o 201 created caso dê certo, ao usar o ResponseEntivy ñ é necessário
    public ResponseEntity<Categoria> criar(@Valid @RequestBody Categoria categoria, HttpServletResponse response) {
    	Categoria categoriaSalva = this.repository.save(categoria);
    	
    	// chama o publisher p/ gerar  a uri do recurso (location) após salvar (formato: http://localhost:8010/categorias/6 )
    	// passa 3 param: a classe q chama o Event, o response, e o codigo p/ montar a uri
    	this.publisher.publishEvent(new RecursoCriadoEvent(this, response, categoriaSalva.getCodigo()));
    	
    	// retorna o código 201 se der certo e a categoria salva
    	return ResponseEntity.status(HttpStatus.CREATED).body(categoriaSalva);
    }
    
    // Buscando categoria pelo codigo
    @GetMapping("/{codigo}")
    public ResponseEntity<Categoria> buscarPeloCodigo(@PathVariable Long codigo) {
    	Optional<Categoria> optional =  this.repository.findById(codigo);
    	if(optional.isPresent()) {
    		return ResponseEntity.ok(optional.get()); // retorna 200 ok + categoria buscada
    	}
    	else {
    		return ResponseEntity.notFound().build(); // retorna 404 not found
    	}
    }
    

    @DeleteMapping("/{codigo}") 
    @ResponseStatus(HttpStatus.NO_CONTENT) // retorna 204 no content se der certo a deleção
    public void removerCategoria(@PathVariable Long codigo) {
    	this.repository.deleteById(codigo);
    	
    }
    
 
}
