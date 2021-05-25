package com.teknei.rest.coches.controller;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.teknei.rest.coches.modelo.Coches;
import com.teknei.rest.coches.modelo.Marcas;
import com.teknei.rest.coches.repositorios.CocheRepository;
import com.teknei.rest.coches.repositorios.MarcaRepository;
import com.teknei.rest.coches.util.FileNetUtil;
import com.teknei.rest.coches.util.PDFUtil;

/**
 * Controlador para sacar Coches y Marcas,
 * y para introducir Coches;
 * 
 * @author LANDER
 *
 */
@RestController
@CrossOrigin("*")
public class CocheMarcaController {

	@Autowired
	private CocheRepository cocheRepo;
	
	@Autowired
	private MarcaRepository marcaRepo;
	
	/**
	 * Saca la lista de Coches, con Marca
	 * 
	 * @return Lista de Coches
	 */
	@GetMapping("/coches")
	public Iterable<Coches> getCoches() {
		return cocheRepo.findAll();
	}
	
	/**
	 * Saca la lista de Marcas
	 * 
	 * @return Lista de Marcas
	 */
	@GetMapping("/marcas")
	public Iterable<Marcas> getMarcas() {
		return marcaRepo.findAll();
	}
	
	/**
	 * Saca una lista de Coches cuyo id de Marca sea la variable introducide
	 * 
	 * @param id Id de la marca
	 * @return Lista de Coches cuyo id de marca sea id
	 */
	@GetMapping("/coches/{id}")
	public Iterable<Coches> getCochesPorMarca(@PathVariable Long id) {
		return cocheRepo.findByMarcaId(id);
	}
	
	/**
	 * Introduce un coche a la BBDD
	 * 
	 * @param coche Coche a introducir
	 * @return Coche introducido
	 */
	@PostMapping("/coches")
	@ResponseStatus(code = HttpStatus.CREATED)
	public Coches post(@RequestBody Coches coche) {
		return cocheRepo.save(coche);
	}
	
	/**
	 * Crea los PDF y los sube al Filenet
	 */
	@GetMapping("/subir-filenet")
	public void subirFilenet() {
		List<Coches> coches = PDFUtil.sacarListaCoches();

		Set<Marcas> marcas = PDFUtil.getMarcas(coches);

		try {
			for (Marcas marca : marcas) {
				PDFUtil.crearPDF(coches, marca.getNombre());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		FileNetUtil.getObjectStore(marcas);
	}
}
