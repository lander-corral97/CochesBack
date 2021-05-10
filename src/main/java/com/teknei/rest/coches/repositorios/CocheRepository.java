package com.teknei.rest.coches.repositorios;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.teknei.rest.coches.modelo.Coches;
import com.teknei.rest.coches.modelo.Marcas;

public interface CocheRepository extends CrudRepository<Coches, Long> {

	List<Coches> findByMarca(Marcas marca);

}
