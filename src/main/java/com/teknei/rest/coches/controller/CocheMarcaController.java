package com.teknei.rest.coches.controller;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.teknei.rest.coches.modelo.Coches;
import com.teknei.rest.coches.modelo.Marcas;
import com.teknei.rest.coches.modelo.Usuario;
import com.teknei.rest.coches.repositorios.CocheRepository;
import com.teknei.rest.coches.repositorios.MarcaRepository;
import com.teknei.rest.coches.repositorios.UsuarioRepository;
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
	
	@Autowired
	private UsuarioRepository usuarioRepo;
	
	@Autowired
	private BCryptPasswordEncoder passEncoder;
	
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
	 * Coge el coche cuyo id es el parámetro
	 * 
	 * @param id Id de coche
	 * @return Coche cuyo id es el parámetro
	 */
	@GetMapping("/coches/{id}")
	public Coches getById(@PathVariable Long id) {
		return cocheRepo.findById(id).isPresent()?cocheRepo.findById(id).get():null;
	}

	/**
	 * Saca una lista de Coches cuyo id de Marca sea la variable introducide
	 * 
	 * @param id Id de la marca
	 * @return Lista de Coches cuyo id de marca sea id
	 */
	@GetMapping("/coches/marca/{idMarca}")
	public Iterable<Coches> getCochesPorMarca(@PathVariable Long idMarca) {
		return cocheRepo.findByMarcaId(idMarca);
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
	 * Introduce un coche a la BBDD
	 * 
	 * @param coche Coche a introducir
	 * @return Coche introducido
	 */
	@PostMapping("/coches")
	public Coches post(@RequestBody Coches coche) {
		return cocheRepo.save(coche);
	}

	/**
	 * Modifica un coche de la BBDD
	 * 
	 * @param coche Coche a modificar
	 * @return Coche modificado
	 */
	@PutMapping("/coches")
	public Coches put(@RequestBody Coches coche) {
		return cocheRepo.save(coche);
	}
	
	/**
	 * Borra un coche en base a un id
	 * 
	 * @param id Id del coche a borrar
	 */
	@DeleteMapping("/coches/{id}")
	public void delete(@PathVariable Long id) {
		cocheRepo.deleteById(id);
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
	
	@PostMapping("/prueba")
	public void pruebaInsert(@RequestBody Usuario usuario) {
		String pwd = usuario.getPassword();
		String encryptPwd = passEncoder.encode(pwd);
		usuario.setPassword(encryptPwd);
		usuarioRepo.save(usuario);
	}
	
}
