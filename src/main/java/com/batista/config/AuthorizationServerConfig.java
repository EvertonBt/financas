package com.batista.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import com.batista.token.CustomTokenEnhancer;

@Profile("oauth-security")
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory() // os clientes abaixo são guardados em memória, mas vc poderia fazer usando jdbc p/ guardar no BD
			.withClient("angular") // configuraçoes do cliente 1 
			.secret(passwordEncoder().encode("@ngul@r0")) // senha do client 1
			.scopes("read", "write") // poderá ler método c/ escopo de leitura e de escrita
			.authorizedGrantTypes("password", "refresh_token") // define o valor da  chave grant_type
			.accessTokenValiditySeconds(300) // tempo de validade do access token em segundos
			.refreshTokenValiditySeconds(3600 * 24) // tempo de validade do refresh token em segundps
		  .and()
		    .withClient("mobile") //configurações client 2
			.secret(passwordEncoder().encode("mobile")) // senha do cliente 2
			.scopes("read") // tem escopo somente de leitura, ou seja, poderá ler somente os métodos q exigem somente esopo de leitura
			.authorizedGrantTypes("password", "refresh_token") // define o valor da ch no header grant_type, o valor password será usado qnd quiser se autenticar p/ obter o access token e o refresh token; já o valor refresh token será usado qnd quiser renovar o access token após ser expirado
			.accessTokenValiditySeconds(300)
			.refreshTokenValiditySeconds(3600 * 24);
	}
	
	// esses 3 métodos abaixo ativam o jwt p/ ser usado junto c/ o oauth
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		// permite adicionar info c/ o nome do usuário logado
		TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
		tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), accessTokenConverter()));
		
		endpoints
			.tokenStore(tokenStore())
			.tokenEnhancer(tokenEnhancerChain)
			.reuseRefreshTokens(false) // faz c/ q a medida q o refresh token esteja sendo constantemente usado ele nunca expire
			.authenticationManager(authenticationManager);
	}
	

	private TokenEnhancer tokenEnhancer() {
	    return new CustomTokenEnhancer();
	}

	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();
		accessTokenConverter.setSigningKey("secret"); // senha q valida o token (fica na terceira parte do jwt)
		return accessTokenConverter;
	}

	@Bean
	public TokenStore tokenStore() {
		return new JwtTokenStore(accessTokenConverter());
	}
	
	// define o algoritmo de criptografia (obrigatorio ao usar o spring security)
	@Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}