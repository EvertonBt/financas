package com.batista.token;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import com.batista.config.UsuarioSistema;

public class CustomTokenEnhancer implements TokenEnhancer{

	// recebe a autenticação do usuário e permite adicionar informações a ele, como o nome do usuário logado
	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
			
		UsuarioSistema usuarioSistema = (UsuarioSistema) authentication.getPrincipal();
			
			Map<String, Object> addInfo = new HashMap<>();
			addInfo.put("nome", usuarioSistema.getUsuario().getNome());
			
			((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(addInfo);
			return accessToken;
		}

	}


