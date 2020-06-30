package com.batista.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	// Fazendo basic authencition + guardando credenciais em memória

	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/categorias").permitAll() // permite acesso p/ todos na url /categorias
				.anyRequest().authenticated() // as demais rotas só podem ser acessadas por pessoas autenticadas
				.and()
			.httpBasic().and() // hbilita o basic authentication
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and() // desabilita o uso de sessões,ñ guardando o estado
			.csrf().disable(); // desabilita a proteção contra csrf q para APIs é desncessária
	}
	
}