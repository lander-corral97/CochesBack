package com.teknei.rest.coches.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.teknei.rest.coches.modelo.Coches;

@RepositoryRestResource(collectionResourceRel = "marcas", path = "marcas")
public interface CocheRepository extends CrudRepository<Coches, Long> {

	@Query("Select c From Coches c where c.marca = (Select m From Marcas m Where m.id = :id)")
	List<Coches> findByMarca(@Param("id") Long id);

}
