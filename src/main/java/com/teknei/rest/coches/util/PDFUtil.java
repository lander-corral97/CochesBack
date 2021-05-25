package com.teknei.rest.coches.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.web.client.RestTemplate;

import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.teknei.rest.coches.modelo.Coches;
import com.teknei.rest.coches.modelo.Marcas;

/**
 * Clase de utilidad para crear un PDF a partir de los datos en BBDD
 * 
 * @author LANDER
 *
 */
public class PDFUtil {

	private static final DeviceRgb FILA_IMPAR = new DeviceRgb(207, 213, 234);
	private static final DeviceRgb FILA_PAR = new DeviceRgb(233, 235, 245);

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

		Paragraph para = new Paragraph(marca).setFontColor(new DeviceRgb(68, 114, 196)).setFontSize(20f).setBold();

		doc.add(para);

		float[] anchuraColumnas = { 225F, 225F };
		Table table = new Table(anchuraColumnas);

		table.addCell(new Cell().setBold().setBackgroundColor(new DeviceRgb(68, 114, 196)).setFontColor(Color.WHITE)
				.add("Modelo").setBorder(Border.NO_BORDER));
		table.addCell(new Cell().setBold().setBackgroundColor(new DeviceRgb(68, 114, 196)).setFontColor(Color.WHITE)
				.add("Matrícula").setBorder(Border.NO_BORDER));

		int colorCounter = 1;

		for (Coches coche : coches) {
			if (coche.getMarca().getNombre().equals(marca)) {
				table.addCell(new Cell().add(coche.getModelo())
						.setBackgroundColor(colorCounter % 2 == 1 ? FILA_IMPAR : FILA_PAR).setBorder(Border.NO_BORDER));
				table.addCell(new Cell().add(coche.getMatricula())
						.setBackgroundColor(colorCounter % 2 == 1 ? FILA_IMPAR : FILA_PAR).setBorder(Border.NO_BORDER));
				colorCounter++;
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
