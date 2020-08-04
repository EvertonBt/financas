package com.batista.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("batista")
public class HabilitarSegurancaApi {
	
	private final Seguranca seguranca = new Seguranca();
	private String origemPermitida = "http://localhost:4200";
	
	public Seguranca getSeguranca() {
		return this.seguranca;
	}	
	
	public String getOrigemPermitida() {
		return origemPermitida;
	}

	public void setOrigemPermitida(String origemPermitida) {
		this.origemPermitida = origemPermitida;
	}

	public static class Seguranca {
		private boolean enableHttps; // usado p/ ativar/desativar o uso do https, por padrão é false, ñ usando o https
		
		public boolean isEnableHttps() {
			return this.enableHttps;
		}
		
		public void setEnableHttps(boolean enableHttps) {
			this.enableHttps = enableHttps;
		}
	}
}


