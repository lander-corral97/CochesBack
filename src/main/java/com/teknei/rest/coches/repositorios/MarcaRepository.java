package com.teknei.rest.coches.repositorios;

import org.springframework.data.repository.CrudRepository;

import com.teknei.rest.coches.modelo.Marcas;

/**
 * 
 * Repositorio con Spring Data JPA. (Todas las funcionalidades) <br>
 * En este caso es para las marcas.
 * 
 * @author Lander Corral
 * @Version 1.0
 *
 */
public interface MarcaRepository extends CrudRepository<Marcas, Long> {

}
