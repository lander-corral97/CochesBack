package com.teknei.rest.coches;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;

import com.teknei.rest.coches.modelo.Coches;
import com.teknei.rest.coches.repositorios.CocheRepository;

@SpringBootApplication
public class CochesRestApplication {

	public static void main(String[] args) {
		SpringApplication.run(CochesRestApplication.class, args);
		
		//Sacamos el JSON de coches como String
		RestTemplate restTemplate = new RestTemplate();
		String result = restTemplate.getForObject("http://localhost:8080/coches", String.class);

		System.out.println(result);
		
		// Lo dividimos
		String[] resultList = result.split("modelo\" : \"|matricula\" : \"|coches/");
		
		// Creamos una lista de Strings, que serán los atributos.
		List<String> strings = new ArrayList<>();
		
		// Hacemos un bucle para saber que partes coger y añadir a la lista de Strings, y cual no debemos coger.
		// Empezamos en 1 ya que la primera parte no nos interesa.
		for (int i = 1; i < resultList.length; i++) {
			// Cogemos sólo modelo, matrícula o marca. Sacamos un substring hasta las siguientes comillas.
			String atributos = resultList[i].substring(0, resultList[i].indexOf("\""));
			// Añadimos SI la lista de strings está vacío (el primero se meterá ya que siempre será un modelo o SI la
			// lista no está vacío Y lo que vamos a añadir no está ya en la lista (en este caso será para las marcas,
			// ya que no aparece la marca en sí misma, sino el un href para saberla y queremos sacar, en este caso, 
			// el id del coche, para llamar con el RestTemplate a otra URI) Y también comprobamos que el string no 
			// contenga el substring /marca (esto se debe a que para sacar el id de la marca tenemos que llamar a cierta
			// URI, para ello necesitamos el id del coche, que ya los hemos sacado en anteriores iteraciones).
			if (strings.isEmpty()|| (!strings.isEmpty() && !atributos.equals(strings.get(strings.size() -1)) && !atributos.contains("/marca"))) {
				strings.add(atributos);
			}
		}
		
		for (int i = 0; i < strings.size() - 3; i = i + 3) {
			
		}
	}

}
