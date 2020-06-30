package com.batista.event;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationEvent;

// Event q ao ser acionado invoca um listner p/ criar a uri do location ao salvar uma pessoa/categoria
public class RecursoCriadoEvent extends ApplicationEvent{
	
	private static final long serialVersionUID = 1L;
	
	private HttpServletResponse response;
	private Long codigo;
    
    // recebe c/ param a classe q está invocando, o response e o código p/ gerar a uri	
	public RecursoCriadoEvent(Object source, HttpServletResponse response, Long codigo) {
		super(source);
	    this.response  = response;
	    this.codigo = codigo;
	    
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public Long getCodigo() {
		return codigo;
	}

	
}
