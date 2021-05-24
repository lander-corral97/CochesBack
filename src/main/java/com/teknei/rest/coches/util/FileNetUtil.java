package com.teknei.rest.coches.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import javax.security.auth.Subject;

import com.filenet.api.collection.ContentElementList;
import com.filenet.api.collection.DocumentSet;
import com.filenet.api.collection.ObjectStoreSet;
import com.filenet.api.constants.AutoClassify;
import com.filenet.api.constants.AutoUniqueName;
import com.filenet.api.constants.CheckinType;
import com.filenet.api.constants.ClassNames;
import com.filenet.api.constants.DefineSecurityParentage;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Connection;
import com.filenet.api.core.ContentTransfer;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.core.ReferentialContainmentRelationship;
import com.filenet.api.util.Id;
import com.filenet.api.util.UserContext;
import com.filenet.apiimpl.core.DocumentImpl;
import com.teknei.rest.coches.modelo.Marcas;

public class FileNetUtil {

	static final Logger LOG = Logger.getLogger("FileNetUtil.class");
	
	private FileNetUtil() {
		
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
	public static void getObjectStore(Set<Marcas> marcas) {
		Connection conn = Factory.Connection.getConnection("http://34.234.153.200/wsi/FNCEWS40MTOM");
		Subject subject = UserContext.createSubject(conn, "Lander", "Hola1234$", "FileNetP8WSI");
		UserContext.get().pushSubject(subject);

		ObjectStore store = null;

		try {
			// Get default domain.
			Domain domain = Factory.Domain.fetchInstance(conn, null, null);
			LOG.info("Domain: " + domain.get_Name());

			// Get object stores for domain.
			ObjectStoreSet osSet = domain.get_ObjectStores();
			Iterator<ObjectStore> osIter = osSet.iterator();

			while (osIter.hasNext()) {
				store = osIter.next();
				LOG.info("Object store: " + store.get_Name());
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
}
