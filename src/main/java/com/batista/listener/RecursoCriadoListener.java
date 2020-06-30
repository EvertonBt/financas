package com.batista.listener;

import java.net.URI;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.batista.event.RecursoCriadoEvent;

// essa classe fica escutando o evento RecursoCriadoEvent e é chamado qnd o evento é acionado

@Component
public class RecursoCriadoListener implements ApplicationListener<RecursoCriadoEvent> {

	// pega os atributos do Event e passa p/ o método adicionarLocation
	@Override
	public void onApplicationEvent(RecursoCriadoEvent event) {
		HttpServletResponse response = event.getResponse();
		Long codigo = event.getCodigo();
		
		adicionarLocation(response, codigo);
		
	}

	// método q efetivamente gera a uri do location
	public void adicionarLocation(HttpServletResponse response, Long codigo) {
		
		// gerando a uri do recurso (location) após salvar (formato: http://localhost:8010/categorias/6 )
    	URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{codigo}")
    			.buildAndExpand(codigo).toUri();
    	response.setHeader("Location", uri.toASCIIString());
    	
	}
	
}
