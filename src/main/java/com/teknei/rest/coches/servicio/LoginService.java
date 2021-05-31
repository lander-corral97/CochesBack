package com.teknei.rest.coches.servicio;

import java.math.BigInteger;
import java.security.MessageDigest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teknei.rest.coches.modelo.Usuario;
import com.teknei.rest.coches.repositorios.UsuarioRepository;

/**
 * Servicio de login.
 * 
 * @author LANDER
 *
 */
@Service
public class LoginService {

	@Autowired
	private UsuarioRepository usuarioRepo;

	/**
	 * Introduce el nombre y contraseña de un usuario y devuelve un booleano
	 * dependiendo de si existe o no.
	 * 
	 * @param usuarioNombre Nombre del usuario.
	 * @param password      Contraseña del usuario.
	 * @return booleano dependiendo si hay un usuario con ese nombre y contraseña o
	 *         no.
	 */
	public boolean login(String usuarioNombre, String password) {
		boolean correcto = false;
		Usuario usuario = usuarioRepo.findByNombre(usuarioNombre);

		if (usuario != null && usuario.getPassword().equals(passwordSHA1(password))) {
			correcto = true;
		}

		return correcto;
	}

	/**
	 * Transforma la contraseña a un formato SHA1
	 * 
	 * @param password Contraseña
	 * @return Contraseña con formato SHA1
	 */
	private String passwordSHA1(String password) {
		String sha1 = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			digest.reset();
			digest.update(password.getBytes("utf8"));
			sha1 = String.format("%040x", new BigInteger(1, digest.digest()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sha1;
	}

}
