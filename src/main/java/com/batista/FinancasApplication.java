package com.batista;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.batista.config.HabilitarSegurancaApi;

@SpringBootApplication
@EnableConfigurationProperties(HabilitarSegurancaApi.class)
public class FinancasApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinancasApplication.class, args);
	}

}
