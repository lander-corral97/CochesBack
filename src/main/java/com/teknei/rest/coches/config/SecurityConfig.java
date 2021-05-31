package com.teknei.rest.coches.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.teknei.rest.coches.filter.JWTAuthorizationFilter;

/**
 * Configuración de Spring security
 * 
 * @author LANDER
 *
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	/**
	 * Configuración de la seguridad de la web. Habilita el endpoint de la consola
	 * de H2.
	 */
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/h2/**");
	}

	/**
	 * Configuración de la seguridad. Añade un filtro para que todos los endpoints
	 * necesiten un token. (excepto el de login)
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.headers().frameOptions().disable();
		http.addFilterAfter(new JWTAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
		http.authorizeRequests().antMatchers("/login").permitAll().anyRequest().authenticated();
	}
}
