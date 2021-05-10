package com.teknei.rest.coches.repositorios;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.teknei.rest.coches.modelo.Coches;

/**
 * 
 * Repositorio Rest con Spring Data JPA. (Todas las funcionalidades)<br>
 * En este caso es para los coches.
 * 
 * @author Lander Corral
 * @Version 1.0
 *
 */
@RepositoryRestResource(collectionResourceRel = "coches", path = "coches")
public interface CocheRepository extends CrudRepository<Coches, Long> {

}
