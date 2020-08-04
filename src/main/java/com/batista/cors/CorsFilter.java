package com.batista.cors;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.batista.config.HabilitarSegurancaApi;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // define com prioridade alta na ordem de execução dos componentes do spring 
public class CorsFilter implements Filter { // define q é um tipo Filter

	// define quais origem serão permitidas fazer requisições p/ o servidor
	@Autowired
	private HabilitarSegurancaApi seguranca; // TODO: Configurar para diferentes ambientes
	
	// método principal q é sempre executado interceptando a requisição antes de chegar no restante da aplicação
	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		// captura a requisição e a resposta http
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		
		// define alguns headers obrigatórios
		response.setHeader("Access-Control-Allow-Origin", this.seguranca.getOrigemPermitida()); // define a origem permitida
        response.setHeader("Access-Control-Allow-Credentials", "true"); // define q ele acesse os valores presentes nos cookies
		
       // esse código só é executadi se o methodo enviado for do tipo OPTIONS, indicando q é uma requisição q pede autorização (CORS) e
       // se a origem da requisição for igual a definida acima como origem permitida
		if ("OPTIONS".equals(request.getMethod()) && this.seguranca.getOrigemPermitida().equals(request.getHeader("Origin"))) {
			response.setHeader("Access-Control-Allow-Methods", "POST, GET, DELETE, PUT, OPTIONS"); // define os métodos permitidos
        	response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept"); // define os headers do tipo authorization e content type c/ permitidos
        	response.setHeader("Access-Control-Max-Age", "3600"); // define o tempo de cache até fazer o proximo pedido c/ valendo 1 hora
			
			response.setStatus(HttpServletResponse.SC_OK);
		} else {
			chain.doFilter(req, resp); 
		}
		
	}
	
	@Override
	public void destroy() {
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

}
