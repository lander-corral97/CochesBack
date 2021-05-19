package com.teknei.rest.coches;

import java.io.Console;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.security.auth.Subject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;

import com.filenet.api.collection.ObjectStoreSet;
import com.filenet.api.constants.AutoClassify;
import com.filenet.api.constants.AutoUniqueName;
import com.filenet.api.constants.CheckinType;
import com.filenet.api.constants.ClassNames;
import com.filenet.api.constants.DefineSecurityParentage;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Connection;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ReferentialContainmentRelationship;
import com.filenet.api.core.Folder;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.util.Id;
import com.filenet.api.util.UserContext;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.teknei.rest.coches.modelo.Coches;
import com.teknei.rest.coches.modelo.Marcas;

@SpringBootApplication
public class CochesRestApplication {

	public static void main(String[] args) {
		SpringApplication.run(CochesRestApplication.class, args);
		
		List<Coches> coches = sacarListaCoches();
		
		Set<Marcas> marcas = getMarcas(coches);
		
		try {
			for (Marcas marca : marcas) {
				crearPDF(coches, marca.getNombre());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ObjectStore os = getObjectStore();
		
		for (Marcas marca : marcas) {
			crearDocumento(os, marca);
		}
	}

	/**
	 * Crea el documento
	 * 
	 * @param os El ObjectStore
	 * @param marca Marca por cada documento
	 */
	private static void crearDocumento(ObjectStore os, Marcas marca) {
		// Create a document instance.
		
		// TODO: ¿QUÉ PASA?
		com.filenet.api.core.Document doc = Factory.Document.createInstance(os, "Coches");

		// Set document properties.
		doc.getProperties().putValue("DocumentTitle", marca.getNombre());
		doc.set_MimeType("application/pdf");

		// Check in the document.
		doc.checkin(AutoClassify.DO_NOT_AUTO_CLASSIFY, CheckinType.MAJOR_VERSION);
		doc.save(RefreshMode.NO_REFRESH);

		// File the document.
		Folder folder = Factory.Folder.getInstance(os, ClassNames.FOLDER,
		        new Id("{C08E7E79-0000-C41A-994A-7DC02002A8B5}"));
		ReferentialContainmentRelationship rcr = folder.file(doc,
		        AutoUniqueName.AUTO_UNIQUE, marca.getNombre(),
		        DefineSecurityParentage.DO_NOT_DEFINE_SECURITY_PARENTAGE);
		rcr.save(RefreshMode.NO_REFRESH);
	}

	/**
	 * Obtiene el ObjectStore
	 * 
	 * @return El ObjectStore
	 */
	private static ObjectStore getObjectStore() {
		Connection conn = Factory.Connection.getConnection("http://34.234.153.200/wsi/FNCEWS40MTOM");
		Subject subject = UserContext.createSubject(conn, "Lander", "Hola1234$", "FileNetP8WSI");
		UserContext.get().pushSubject(subject);
		
		try
	    {
	       // Get default domain.
	       Domain domain = Factory.Domain.fetchInstance(conn, null, null);
	       System.out.println("Domain: " + domain.get_Name());

	       // Get object stores for domain.
	       ObjectStoreSet osSet = domain.get_ObjectStores();
	       ObjectStore store = null;
	       Iterator<ObjectStore> osIter = osSet.iterator();

	       while (osIter.hasNext()) 
	       {
		      store = (ObjectStore) osIter.next();
	          System.out.println("Object store: " + store.get_Name());
	    	  if (store.get_Name().equalsIgnoreCase("teknei")) {
	    		  break;
	    	  }
	       }
	       return store;
	    }
	    finally
	    {
		   System.out.println("Connection to Content Platform Engine successful");
	       UserContext.get().popSubject();
	    }
	}

	/**
	 * Devuelve todas las marcas. (Todas son únicas)
	 * 
	 * @param coches Lista de coches, para que salgan cada una de las marcas
	 * @return Marcas de forma única.
	 */
	private static Set<Marcas> getMarcas(List<Coches> coches) {
		Set<Marcas> marcas = new HashSet<>();
		
		for (Coches coche : coches) {
			marcas.add(coche.getMarca());
		}
		
		return marcas;
	}

	/**
	 * Crea un PDF a partir de la lista de coches
	 * 
	 * @param coches Lista de coches rellena
	 * @throws Exception
	 */
	private static void crearPDF(List<Coches> coches, String marca) throws Exception {
		PdfWriter writer = new PdfWriter("src/main/resources/pdf/" + marca + ".pdf");
		
		PdfDocument pdfDoc = new PdfDocument(writer);
		
		Document doc = new Document(pdfDoc);
		
		Paragraph para = new Paragraph(marca)
                .setFontColor(new DeviceRgb(0, 0, 255))
                .setFontSize(20f);
		
		doc.add(para);
		
		float[] anchuraColumnas = {225F, 225F};
		Table table = new Table(anchuraColumnas);
		
		table.addCell(new Cell().setBold().add("Modelo"));
		table.addCell(new Cell().setBold().add("Matrícula"));
		
		for (Coches coche : coches) {
			if (coche.getMarca().getNombre().equals(marca)) {
				table.addCell(new Cell().add(coche.getModelo()));
				table.addCell(new Cell().add(coche.getMatricula()));
			}
		}
		
		doc.add(table);
		
		doc.close();
	}

	/**
	 * Devuelve la lista de coches rellena.
	 * 
	 * @return Lista de Coches rellenada.
	 */
	private static List<Coches> sacarListaCoches() {
		List<Coches> coches = new ArrayList<>();
		
		//Sacamos el JSON de coches como String
		RestTemplate restTemplate = new RestTemplate();
		String result = restTemplate.getForObject("http://localhost:8080/coches", String.class);

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

		// Guarda todos los coches. La lista de Strings sigue este orden (modelo => matrícula => idMarca) y así
		// hasta que se acabe la lista.
		for (int i = 0; i < strings.size() - 2; i = i + 3) {
			Coches coche = new Coches();
			
			coche.setModelo(strings.get(i));
			coche.setMatricula(strings.get(i+1));
			
			// Para las marcas sólo tenemos el id, por ello, llamamos al método de sacar el nombre de la marca, ya
			// que necesitamos el nombre de marca.
			Marcas marca = new Marcas();
			marca.setNombre(getNombreMarca(restTemplate, strings.get(i+2)));
			
			coche.setMarca(marca);
			
			coches.add(coche);
		}
		
		return coches;
	}


	/**
	 * Método para sacar el nombre de la marca, necesario para el PDF
	 * 
	 * @param restTemplate Template para poder sacar el JSON de la marca del coche.
	 * @param idMarca Id a buscar
	 * @return Nombre de la Marca
	 */
	private static String getNombreMarca(RestTemplate restTemplate, String idMarca) {
		String result = restTemplate.getForObject("http://localhost:8080/coches/" + idMarca + "/marca", String.class);
		
		String[] resultMarca = result.split("nombre\" : \"");
		
		return resultMarca[1].substring(0, resultMarca[1].indexOf("\""));
	}

}
