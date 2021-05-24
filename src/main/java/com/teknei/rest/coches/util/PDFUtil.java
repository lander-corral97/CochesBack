package com.teknei.rest.coches.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.web.client.RestTemplate;

import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.teknei.rest.coches.modelo.Coches;
import com.teknei.rest.coches.modelo.Marcas;

public class PDFUtil {

	private PDFUtil() {
		
	}
	
	/**
	 * Devuelve todas las marcas. (Todas son únicas)
	 * 
	 * @param coches Lista de coches, para que salgan cada una de las marcas
	 * @return Marcas de forma única.
	 */
	public static Set<Marcas> getMarcas(List<Coches> coches) {
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
	public static void crearPDF(List<Coches> coches, String marca) throws Exception {
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
	public static List<Coches> sacarListaCoches() {
		RestTemplate restTemplate = new RestTemplate();
		Coches[] coches = restTemplate.getForObject("http://localhost:8080/coches", Coches[].class);

		return Arrays.asList(coches);
	}
	
}
