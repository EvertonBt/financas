package com.batista.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;

@Configuration
@EnableWebSecurity
@EnableResourceServer // transoforma a classe num servidor de recursos
@EnableGlobalMethodSecurity(prePostEnabled = true) // permite q vc defina as permissões de acesso de forma indivual p/ cada método
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
    
	@Autowired
	UserDetailsService userDetailsService;
	
	@Autowired
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
		
		/*
		auth.inMemoryAuthentication()
			.withUser("admin").password("admin").roles("ROLE");
			*/
	}
	
	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/categorias").permitAll() // define q a url /categoria pode ser acessada por qq um
				.anyRequest().authenticated() // define q as demais urls só podem ser acessadas por pessoas autenticadas
				.and()
			//	.httpBasic().and() // ativa o basic autentication, está comentado pois debailitei p/ q ele use o 
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and() // faz c/ q a api ñ guarde estado das requisições ou seja q ñ guarde info de sessão
			.csrf().disable(); // usado somente p/ aplicações web q permite a injeção de cdogio js malicioso, no caso  de pais rest ñ há necessidade dessa proteção
	}
	
	
	// esse método faz o mesmo q o .sessionManagement acima, ele desabilita o uso de sessões p/ q ñ guarde o estado das requisições
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.stateless(true);
	}
	
	// método q define o algortimo de codificação
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	// em conjunto c/ o EnableGlobalMethodSecurity(prePostEnabled = true) ele define as permissões de acesso indivual por método
	@Bean
	public MethodSecurityExpressionHandler createExpressionHandler() {
		return new OAuth2MethodSecurityExpressionHandler();
	}
	
}