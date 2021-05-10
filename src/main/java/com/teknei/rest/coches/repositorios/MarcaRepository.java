package com.teknei.rest.coches.repositorios;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.teknei.rest.coches.modelo.Marcas;

@RepositoryRestResource(collectionResourceRel = "marcas", path = "marcas")
public interface MarcaRepository extends CrudRepository<Marcas, Long> {

}
