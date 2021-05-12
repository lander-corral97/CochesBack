package com.teknei.rest.coches.repositorios;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.teknei.rest.coches.modelo.Marcas;

/**
 * 
 * Repositorio Rest con Spring Data JPA. (Todas las funcionalidades) En este
 * caso es para las marcas.
 * 
 * @author Lander Corral
 * @Version 1.0
 *
 */
@RepositoryRestResource(collectionResourceRel = "marcas", path = "marcas")
@CrossOrigin(origins = "*")
public interface MarcaRepository extends CrudRepository<Marcas, Long> {

}
