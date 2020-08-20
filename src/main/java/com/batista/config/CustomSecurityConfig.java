package com.batista.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@Profile("oauth-security")
@Configuration
@EnableWebSecurity
@EnableAuthorizationServer
@EnableResourceServer
public class CustomSecurityConfig extends WebSecurityConfigurerAdapter {

	 @Override
	   @Bean
	   public AuthenticationManager authenticationManagerBean() throws Exception {
	      // provides the default AuthenticationManager as a Bean
	      return super.authenticationManagerBean();
	   }

}