package com.teknei.rest.coches.repositorios;

import org.springframework.data.repository.CrudRepository;

import com.teknei.rest.coches.modelo.Usuario;

public interface UsuarioRepository extends CrudRepository<Usuario, Long>{

	Usuario findByNombre(String username);

}
