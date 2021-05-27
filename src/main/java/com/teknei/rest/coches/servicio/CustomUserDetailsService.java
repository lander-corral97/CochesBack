package com.teknei.rest.coches.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.teknei.rest.coches.modelo.Usuario;
import com.teknei.rest.coches.repositorios.UsuarioRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UsuarioRepository usuarioRepo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Usuario usuario = usuarioRepo.findByNombre(username);
		CustomUserDetails userDetails = null;
		if (usuario != null) {
			userDetails = new CustomUserDetails();
			userDetails.setUsuario(usuario);
		} else {
			throw new UsernameNotFoundException("No hay usuario con nombre: " + username);
		}
		return userDetails;
	}

}
