package com.teknei.rest.coches;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.security.auth.Subject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;

import com.filenet.api.collection.AccessPermissionList;
import com.filenet.api.collection.ContentElementList;
import com.filenet.api.collection.DocumentSet;
import com.filenet.api.collection.ObjectStoreSet;
import com.filenet.api.constants.AutoClassify;
import com.filenet.api.constants.AutoUniqueName;
import com.filenet.api.constants.CheckinType;
import com.filenet.api.constants.ClassNames;
import com.filenet.api.constants.DefineSecurityParentage;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.constants.ReservationType;
import com.filenet.api.core.Connection;
import com.filenet.api.core.ContentTransfer;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ReferentialContainmentRelationship;
import com.filenet.api.core.Folder;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.util.Id;
import com.filenet.api.util.UserContext;
import com.filenet.apiimpl.core.DocumentImpl;
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
		getObjectStore(marcas);

	}

	/**
	 * Crea el documento
	 * 
	 * @param os    El ObjectStore
	 * @param marca Marca por cada documento
	 */
	private static void crearDocumento(ObjectStore os, Marcas marca) {
		// Create a document instance.

		com.filenet.api.core.Document doc = Factory.Document.createInstance(os, "Coches");

		// Set document properties.
		doc.getProperties().putValue("DocumentTitle", marca.getNombre());
		doc.set_MimeType("application/pdf");

		// references to the document and file you are working with
		File file = new File("src/main/resources/pdf/" + marca.getNombre() + ".pdf");
		try {
			ContentTransfer contentTransfer = Factory.ContentTransfer.createInstance();
			InputStream inputStream = new FileInputStream(file);
			ContentElementList contentList = Factory.ContentTransfer.createList();
			contentTransfer.setCaptureSource(inputStream);
			contentList.add(contentTransfer);

			doc.set_ContentElements(contentList);
			doc.save(RefreshMode.REFRESH);

			doc.checkin(AutoClassify.DO_NOT_AUTO_CLASSIFY, CheckinType.MAJOR_VERSION);
			doc.save(RefreshMode.REFRESH);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		// File the document.
		Folder folder = Factory.Folder.getInstance(os, ClassNames.FOLDER,
				new Id("{C08E7E79-0000-C41A-994A-7DC02002A8B5}"));
		ReferentialContainmentRelationship rcr = folder.file(doc, AutoUniqueName.AUTO_UNIQUE, marca.getNombre(),
				DefineSecurityParentage.DO_NOT_DEFINE_SECURITY_PARENTAGE);
		rcr.save(RefreshMode.NO_REFRESH);
	}

	/**
	 * Obtiene el ObjectStore
	 * 
	 * @return El ObjectStore
	 */
	private static void getObjectStore(Set<Marcas> marcas) {
		Connection conn = Factory.Connection.getConnection("http://34.234.153.200/wsi/FNCEWS40MTOM");
		Subject subject = UserContext.createSubject(conn, "Lander", "Hola1234$", "FileNetP8WSI");
		UserContext.get().pushSubject(subject);

		ObjectStore store = null;

		try {
			// Get default domain.
			Domain domain = Factory.Domain.fetchInstance(conn, null, null);
			System.out.println("Domain: " + domain.get_Name());

			// Get object stores for domain.
			ObjectStoreSet osSet = domain.get_ObjectStores();
			Iterator<ObjectStore> osIter = osSet.iterator();

			while (osIter.hasNext()) {
				store = osIter.next();
				System.out.println("Object store: " + store.get_Name());
				if (store.get_Name().equalsIgnoreCase("teknei")) {
					break;
				}
			}
		} finally {
			borrarDocumentos(store);
			for (Marcas marca : marcas) {
				crearDocumento(store, marca);
			}
			System.out.println("Connection to Content Platform Engine successful");
			UserContext.get().popSubject();
		}
	}

	private static void borrarDocumentos(ObjectStore os) {
		Folder folder = Factory.Folder.fetchInstance(os, "/PDF_Coches", null);
		DocumentSet ds = folder.get_ContainedDocuments();
		
		Iterator i = ds.iterator();
		
		if (i.hasNext()) {
			while (i.hasNext()) {
				DocumentImpl doc = (DocumentImpl) i.next();
				com.filenet.api.core.Document docBorrar = Factory.Document.getInstance(os, "Coches", doc.get_Id());
				docBorrar.delete();
				docBorrar.save(RefreshMode.NO_REFRESH);
			}
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

		Paragraph para = new Paragraph(marca).setFontColor(new DeviceRgb(0, 0, 255)).setFontSize(20f);

		doc.add(para);

		float[] anchuraColumnas = { 225F, 225F };
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
		RestTemplate restTemplate = new RestTemplate();
		Coches[] coches = restTemplate.getForObject("http://localhost:8080/coches", Coches[].class);

		return Arrays.asList(coches);
	}

}
