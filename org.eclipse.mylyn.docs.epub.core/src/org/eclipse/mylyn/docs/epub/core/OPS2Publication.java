/*******************************************************************************
 * Copyright (c) 2011,2012 Torkild U. Resheim.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *   Torkild U. Resheim - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.docs.epub.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.FeatureMapUtil;
import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.mylyn.docs.epub.core.ILogger.Severity;
import org.eclipse.mylyn.docs.epub.ncx.DocTitle;
import org.eclipse.mylyn.docs.epub.ncx.Head;
import org.eclipse.mylyn.docs.epub.ncx.Meta;
import org.eclipse.mylyn.docs.epub.ncx.NCXFactory;
import org.eclipse.mylyn.docs.epub.ncx.NCXPackage;
import org.eclipse.mylyn.docs.epub.ncx.NavMap;
import org.eclipse.mylyn.docs.epub.ncx.Ncx;
import org.eclipse.mylyn.docs.epub.ncx.Text;
import org.eclipse.mylyn.docs.epub.ncx.util.NCXResourceFactoryImpl;
import org.eclipse.mylyn.docs.epub.ncx.util.NCXResourceImpl;
import org.eclipse.mylyn.docs.epub.opf.Guide;
import org.eclipse.mylyn.docs.epub.opf.Item;
import org.eclipse.mylyn.docs.epub.opf.Itemref;
import org.eclipse.mylyn.docs.epub.opf.Manifest;
import org.eclipse.mylyn.docs.epub.opf.Metadata;
import org.eclipse.mylyn.docs.epub.opf.OPFFactory;
import org.eclipse.mylyn.docs.epub.opf.Spine;
import org.eclipse.mylyn.internal.docs.epub.core.EPUBXMLHelperImp;
import org.eclipse.mylyn.internal.docs.epub.core.OPS2Validator;
import org.eclipse.mylyn.internal.docs.epub.core.TOCGenerator;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This type represents one EPUB revision 2.0.1 formatted publication. It maintains a data structure representing the
 * entire publication and API for building it.
 * <p>
 * <b>Please note that this API is provisional and should not yet be used to build applications.</b>
 * </p>
 * 
 * @author Torkild U. Resheim
 * @see http://www.niso.org/workrooms/daisy/Z39-86-2005.html
 */
public class OPS2Publication extends OPSPublication {

	/** MIME type for NCX documents */
	private static final String MIMETYPE_NCX = "application/x-dtbncx+xml"; //$NON-NLS-1$

	private static final String NCX_FILE_SUFFIX = "ncx"; //$NON-NLS-1$

	/** Identifier of the table of contents file */
	private static final String TABLE_OF_CONTENTS_ID = "ncx"; //$NON-NLS-1$

	/** Default name for the table of contents */
	private static final String TOCFILE_NAME = "toc.ncx"; //$NON-NLS-1$

	/** The table of contents */
	private Ncx ncxTOC;

	/**
	 * Creates a new EPUB.
	 */
	public OPS2Publication() {
		super();
		setup();
	}

	/**
	 * Creates a new EPUB logging all event to the specified logger.
	 */
	public OPS2Publication(ILogger logger) {
		super(logger);
		setup();
	}

	/**
	 * This mechanism will traverse the spine of the publication (which is representing the reading order) and parse
	 * each file for information that can be used to assemble a table of contents.
	 * 
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	@Override
	protected void generateTableOfContents() throws Exception {
		log("Generating table of contents for OPS", Severity.INFO, indent++);
		NavMap navMap = NCXFactory.eINSTANCE.createNavMap();
		ncxTOC.setNavMap(navMap);
		ncxTOC.setVersion("2005-1"); //$NON-NLS-1$
		// Create the required head element
		Head head = NCXFactory.eINSTANCE.createHead();
		ncxTOC.setHead(head);
		Meta meta = NCXFactory.eINSTANCE.createMeta();
		meta.setName("dtb:uid"); //$NON-NLS-1$
		meta.setContent(getIdentifier().getMixed().getValue(0).toString());
		head.getMetas().add(meta);
		DocTitle docTitle = NCXFactory.eINSTANCE.createDocTitle();
		Text text = NCXFactory.eINSTANCE.createText();
		FeatureMapUtil.addText(text.getMixed(), "Table of contents"); //$NON-NLS-1$
		docTitle.setText(text);
		ncxTOC.setDocTitle(docTitle);
		int playOrder = 0;
		// Iterate over the spine
		EList<Itemref> spineItems = getSpine().getSpineItems();
		EList<Item> manifestItems = opfPackage.getManifest().getItems();
		for (Itemref itemref : spineItems) {
			Item referencedItem = null;
			String id = itemref.getIdref();
			// Find the manifest item that is referenced
			for (Item item : manifestItems) {
				if (item.getId().equals(id)) {
					referencedItem = item;
					break;
				}
			}
			if (referencedItem != null && !referencedItem.isNoToc()) {
				File file = new File(referencedItem.getFile());
				FileInputStream fis = new FileInputStream(file);
				log(MessageFormat.format("Parsing {0}", referencedItem.getHref()), Severity.VERBOSE, indent);
				playOrder = TOCGenerator.parse(new InputSource(fis), referencedItem.getHref(), ncxTOC, playOrder);
			}
		}
		indent--;
	}

	@Override
	public Object getTableOfContents() {
		return ncxTOC;
	}

	@Override
	protected String getVersion() {
		return "2.0"; //$NON-NLS-1$
	}

	@Override
	protected void readTableOfContents(File tocFile) throws IOException {
		ResourceSet resourceSet = new ResourceSetImpl();
		URI fileURI = URI.createFileURI(tocFile.getAbsolutePath());
		Resource resource = resourceSet.createResource(fileURI);
		resource.load(null);
		ncxTOC = (Ncx) resource.getContents().get(0);
	}

	/**
	 * Registers a new resource factory for NCX data structures. This is normally done through Eclipse extension points
	 * but we also need to be able to create this factory without the Eclipse runtime.
	 */
	private void registerNCXResourceFactory() {
		// Register package so that it is available even without the Eclipse runtime
		@SuppressWarnings("unused")
		NCXPackage packageInstance = NCXPackage.eINSTANCE;

		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(NCX_FILE_SUFFIX,
				new NCXResourceFactoryImpl() {
					@Override
					public Resource createResource(URI uri) {
						NCXResourceImpl xmiResource = new NCXResourceImpl(uri) {

							@Override
							protected XMLHelper createXMLHelper() {
								EPUBXMLHelperImp xmlHelper = new EPUBXMLHelperImp();
								return xmlHelper;
							}

						};
						Map<Object, Object> loadOptions = xmiResource.getDefaultLoadOptions();
						Map<Object, Object> saveOptions = xmiResource.getDefaultSaveOptions();
						// We use extended metadata
						saveOptions.put(XMLResource.OPTION_EXTENDED_META_DATA, Boolean.TRUE);
						loadOptions.put(XMLResource.OPTION_EXTENDED_META_DATA, Boolean.TRUE);
						// Required in order to correctly read in attributes
						loadOptions.put(XMLResource.OPTION_LAX_FEATURE_PROCESSING, Boolean.TRUE);
						// Treat "href" attributes as features
						loadOptions.put(XMLResource.OPTION_USE_ENCODED_ATTRIBUTE_STYLE, Boolean.TRUE);
						// UTF-8 encoding is required per specification
						saveOptions.put(XMLResource.OPTION_ENCODING, XML_ENCODING);
						// Do not download any external DTDs.
						Map<String, Object> parserFeatures = new HashMap<String, Object>();
						parserFeatures.put("http://xml.org/sax/features/validation", Boolean.FALSE); //$NON-NLS-1$
						parserFeatures.put("http://apache.org/xml/features/nonvalidating/load-external-dtd", //$NON-NLS-1$
								Boolean.FALSE);
						loadOptions.put(XMLResource.OPTION_PARSER_FEATURES, parserFeatures);
						return xmiResource;
					}

				});
	}

	@Override
	public void setTableOfContents(File ncxFile) {
		// Add the file to the publication and make sure we use the table of
		// contents identifier.
		Item item = addItem(opfPackage.getSpine().getToc(), null, ncxFile, null, MIMETYPE_NCX, false, false, false);
		// The table of contents file must be first.
		opfPackage.getManifest().getItems().move(0, item);
		log(MessageFormat.format("Using table of contents file {0} for OPS", new Object[] { ncxFile.getName() }), //$NON-NLS-1$
				Severity.VERBOSE, indent);
	}

	private void setup() {
		opfPackage.setVersion(getVersion());
		ncxTOC = NCXFactory.eINSTANCE.createNcx();
		Metadata opfMetadata = OPFFactory.eINSTANCE.createMetadata();
		opfPackage.setMetadata(opfMetadata);
		Guide opfGuide = OPFFactory.eINSTANCE.createGuide();
		opfPackage.setGuide(opfGuide);
		Manifest opfManifest = OPFFactory.eINSTANCE.createManifest();
		opfPackage.setManifest(opfManifest);
		// Create the spine and set a reference to the table of contents
		// item which will be added to the manifest on a later stage.
		Spine opfSpine = OPFFactory.eINSTANCE.createSpine();
		opfSpine.setToc(TABLE_OF_CONTENTS_ID);
		opfPackage.setSpine(opfSpine);

		registerNCXResourceFactory();
		opfPackage.setGenerateTableOfContents(true);
	}

	/**
	 * This method will only validate items that are in the spine, or in reading order.
	 */
	@Override
	protected List<ValidationMessage> validateContents() throws Exception {
		EList<Itemref> spineItems = getSpine().getSpineItems();
		EList<Item> manifestItems = opfPackage.getManifest().getItems();
		ArrayList<ValidationMessage> messages = new ArrayList<ValidationMessage>();
		for (Itemref itemref : spineItems) {
			Item referencedItem = null;
			String id = itemref.getIdref();
			// Find the manifest item that is referenced
			for (Item item : manifestItems) {
				if (item.getId().equals(id)) {
					referencedItem = item;
					break;
				}
			}
			if (referencedItem != null && !referencedItem.isNoToc()) {
				File file = new File(referencedItem.getFile());
				FileReader fr = new FileReader(file);
				messages.addAll(OPS2Validator.validate(new InputSource(fr), referencedItem.getHref()));
			}
		}
		return messages;
	}

	/**
	 * Writes the table of contents file in the specified folder using the NCX format. If a table of contents file has
	 * not been specified an empty one will be created (since it is required to have one). If in addition it has been
	 * specified that the table of contents should be created, the content files will be parsed and a TOC will be
	 * generated.
	 * 
	 * @param oepbsFolder
	 *            the folder to create the NCX file in
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @see {@link #setTableOfContents(File)}
	 */
	@Override
	protected void writeTableOfContents(File oepbsFolder) throws Exception {
		// If a table of contents file has not been specified we must create
		// one. If it has been specified it will be copied.
		if (getItemById(opfPackage.getSpine().getToc()) == null) {
			File ncxFile = new File(oepbsFolder.getAbsolutePath() + File.separator + TOCFILE_NAME);
			ResourceSet resourceSet = new ResourceSetImpl();
			// Register the packages to make it available during loading.
			resourceSet.getPackageRegistry().put(NCXPackage.eNS_URI, NCXPackage.eINSTANCE);
			URI fileURI = URI.createFileURI(ncxFile.getAbsolutePath());
			Resource resource = resourceSet.createResource(fileURI);
			// We've been asked to generate a table of contents using pages
			// contained in the spine.
			if (opfPackage.isGenerateTableOfContents()) {
				generateTableOfContents();
			}
			resource.getContents().add(ncxTOC);
			Map<String, Object> options = new HashMap<String, Object>();
			// NCX requires that we encode using UTF-8
			options.put(XMLResource.OPTION_ENCODING, XML_ENCODING);
			options.put(XMLResource.OPTION_EXTENDED_META_DATA, Boolean.TRUE);
			resource.save(options);
			// Make sure the table of contents file is in the manifest and
			// referenced in the spine. We also want it to be the first element
			// in the manifest.
			Item item = addItem(opfPackage.getSpine().getToc(), null, ncxFile, null, MIMETYPE_NCX, false, false, false);
			opfPackage.getManifest().getItems().move(0, item);
		}
	}

}
