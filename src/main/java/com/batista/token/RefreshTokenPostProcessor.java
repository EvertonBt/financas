package com.batista.token;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.batista.config.HabilitarSegurancaApi;

// essa classe captura o retorno do objeto OuAth2AccessToken q traz o access token e o refresh token no body da resposta
// e guarda o refreh token num cookie p/ q tenha mais segurança
@ControllerAdvice
public class RefreshTokenPostProcessor implements ResponseBodyAdvice<OAuth2AccessToken> {

	 @Autowired
	 public HabilitarSegurancaApi seguranca;
	
	 // ele pode retornar um  OuAth2AccessToken em várias situações, esse método especifica q só será captura qnd o objeto for retornado
	// num contexto de resposta a um requisição de refresh token, ou seja, qnd o gername é igual a "postAccessToken"
	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return returnType.getMethod().getName().equals("postAccessToken");
	}

	// qnd essa situação especifica ocorrer ele vai remover o valor do refresh token presente no corpo da requisição
	// e armz num header http, mais precisamento num cookie (ele chama os dois métodos abaixo dele):
	@Override
	public OAuth2AccessToken beforeBodyWrite(OAuth2AccessToken body, MethodParameter returnType,
			MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
			ServerHttpRequest request, ServerHttpResponse response) {
		
		HttpServletRequest req = ((ServletServerHttpRequest) request).getServletRequest();
		HttpServletResponse resp = ((ServletServerHttpResponse) response).getServletResponse();
		// captura o refresh token do corpo da requisição
		DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) body;
		
		String refreshToken = body.getRefreshToken().getValue();
		adicionarRefreshTokenNoCookie(refreshToken, req, resp);
		removerRefreshTokenDoBody(token);
		
		return body;
	}
   
	// remove o refresh token do corpo da requisição, pois ele foi movido agora p/ o header
	private void removerRefreshTokenDoBody(DefaultOAuth2AccessToken token) {
		token.setRefreshToken(null);
	}

	// adiciona um novo cookie contendo o refresh token
	private void adicionarRefreshTokenNoCookie(String refreshToken, HttpServletRequest req, HttpServletResponse resp) {
		Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken); // criando o cookie
		refreshTokenCookie.setHttpOnly(true); // indica q é um cookie apenas do protocolo http
		refreshTokenCookie.setSecure(this.seguranca.getSeguranca().isEnableHttps()); // indica se será encriptado em ssl (true, usado em produção) ou ñ (false, usado em dev)
		refreshTokenCookie.setPath(req.getContextPath() + "/oauth/token"); // p/ qual caminho ele vai associar o cookie
		refreshTokenCookie.setMaxAge(2592000); // tempo de expirção (30 dias)
		resp.addCookie(refreshTokenCookie); // e adiciona cookie na resposta da requisição
	}

}