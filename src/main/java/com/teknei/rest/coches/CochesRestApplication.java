package com.teknei.rest.coches;

import java.util.List;
import java.util.Set;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.teknei.rest.coches.modelo.Coches;
import com.teknei.rest.coches.modelo.Marcas;
import com.teknei.rest.coches.util.FileNetUtil;
import com.teknei.rest.coches.util.PDFUtil;

@SpringBootApplication
public class CochesRestApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(CochesRestApplication.class, args);

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
