package com.teknei.rest.coches.repositorios;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.teknei.rest.coches.modelo.Coches;

/**
 * 
 * Repositorio con Spring Data JPA. (Todas las funcionalidades + encontrar coche por id Marca)<br>
 * En este caso es para los coches.
 * 
 * @author Lander Corral
 * @Version 1.0
 *
 */
public interface CocheRepository extends CrudRepository<Coches, Long> {

	@Query("Select c From Coches c Where marca.id = :id")
	Iterable<Coches> findByMarcaId(Long id);
	
}
