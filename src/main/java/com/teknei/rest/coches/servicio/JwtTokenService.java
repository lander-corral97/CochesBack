package com.teknei.rest.coches.servicio;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Servicio que devuelve los Token
 * 
 * @author LANDER
 *
 */
@Service
public class JwtTokenService {

	private static final String KEY = "clave_random";

	private static final Long TIEMPO_EXP = 2629800000L; // 1 mes

	private static final List<GrantedAuthority> GRANTED_AUTHORITIES = AuthorityUtils
			.commaSeparatedStringToAuthorityList("ROLE_USER");

	/**
	 * Retorna un Token, que sirve para los endpoints
	 * 
	 * @param username Usuario que necesita token
	 * @return Token Bearer
	 */
	public String getJWTToken(String username) {
		return "Bearer " + Jwts.builder().setId("CochesJWT").setSubject(username)
				.claim("authorities",
						GRANTED_AUTHORITIES.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + TIEMPO_EXP))
				.signWith(SignatureAlgorithm.HS512, KEY.getBytes()).compact();
	}

}
