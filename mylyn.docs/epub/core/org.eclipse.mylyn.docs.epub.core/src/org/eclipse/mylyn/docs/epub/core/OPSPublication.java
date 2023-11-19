/*******************************************************************************
 * Copyright (c) 2011-2015 Torkild U. Resheim.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Torkild U. Resheim - initial API and implementation
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
import org.eclipse.mylyn.docs.epub.internal.EPUBXMLHelperImp;
import org.eclipse.mylyn.docs.epub.internal.OPSValidator;
import org.eclipse.mylyn.docs.epub.internal.TOCGenerator;
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
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This type represents one EPUB revision 2.0.1 formatted publication.
 *
 * @author Torkild U. Resheim
 * @see http://www.idpf.org/doc_library/epub/OPS_2.0.1_draft.htm
 */
public class OPSPublication extends Publication {

	/** MIME type for NCX documents */
	private static final String MIMETYPE_NCX = "application/x-dtbncx+xml"; //$NON-NLS-1$

	private static final String NCX_FILE_SUFFIX = "ncx"; //$NON-NLS-1$

	/** Identifier of the table of contents file */
	private static final String TABLE_OF_CONTENTS_ID = "ncx"; //$NON-NLS-1$

	/** Default name for the table of contents */
	private static final String TOCFILE_NAME = "toc.ncx"; //$NON-NLS-1$

	/** List of core media types as specified in http://idpf.org/epub/20/spec/OPS_2.0.1_draft.htm#Section1.3.7 */
	private static final String[] CORE_MEDIA_TYPES = new String[] { "image/gif", "image/jpeg", "image/png", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			"image/svg+xml", "application/xhtml+xml", "application/x-dtbook+xml", "text/css", "application/xml", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			"text/x-oeb1-document", "text/x-oeb1-css", "application/x-dtbncx+xml" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	/** The table of contents */
	private Ncx ncxTOC;

	/**
	 * Creates a new EPUB.
	 *
	 * @since 2.0
	 */
	public OPSPublication() {
		super();
		setup();
	}

	/**
	 * Creates a new EPUB logging all event to the specified logger.
	 *
	 * @since 2.0
	 */
	public OPSPublication(ILogger logger) {
		super(logger);
		setup();
	}

	/**
	 * Adds a new EPUB 2 meta item to the publication.
	 *
	 * @param name
	 *            name of the item
	 * @param value
	 *            content of the item
	 * @return the new meta
	 */
	public org.eclipse.mylyn.docs.epub.opf.Meta addMeta(String name, String value) {
		if (value == null) {
			throw new IllegalArgumentException("A value must be specified"); //$NON-NLS-1$
		}
		if (name == null) {
			throw new IllegalArgumentException("A name must be specified"); //$NON-NLS-1$
		}
		org.eclipse.mylyn.docs.epub.opf.Meta opf = OPFFactory.eINSTANCE.createMeta();
		opf.setName(name);
		opf.setContent(value);
		opfPackage.getMetadata().getMetas().add(opf);
		return opf;
	}

	/**
	 * This mechanism will traverse the spine of the publication (which is representing the reading order) and parse
	 * each file for information that can be used to assemble a table of contents. Only XHTML type of files will be
	 * taken into consideration.
	 *
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	@Override
	protected void generateTableOfContents() throws ParserConfigurationException, SAXException, IOException {
		log(Messages.getString("OPS2Publication.0"), Severity.INFO, indent++); //$NON-NLS-1$
		Meta meta = NCXFactory.eINSTANCE.createMeta();
		meta.setName("dtb:uid"); //$NON-NLS-1$
		meta.setContent(getIdentifier().getMixed().getValue(0).toString());
		ncxTOC.getHead().getMetas().add(meta);
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
			if (referencedItem != null && !referencedItem.isNoToc()
					&& referencedItem.getMedia_type().equals(MIMETYPE_XHTML)) {
				File file = new File(referencedItem.getFile());
				FileInputStream fis = new FileInputStream(file);
				log(MessageFormat.format(Messages.getString("OPS2Publication.1"), referencedItem.getHref()), //$NON-NLS-1$
						Severity.VERBOSE, indent);
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

	private boolean isLegalType(Item item) {
		boolean legal = false;
		for (String type : CORE_MEDIA_TYPES) {
			if (item.getMedia_type().equals(type)) {
				legal = true;
			}
		}
		return legal;
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

	/**
	 * Convenience method for adding a cover to the publication. This method will make sure the required actions are
	 * taken to provide a cover page for all reading systems.
	 *
	 * @param image
	 *            the cover image (jpeg, png, svg or gif)
	 * @param title
	 *            title of the cover page
	 */
	@Override
	public void setCover(File image, String title) {
		// Add the cover image to the manifest
		Item item = addItem(COVER_IMAGE_ID, null, image, null, null, false, false, true);
		item.setTitle(title);
		// Point to the cover using a meta tag
		addMeta(COVER_ID, COVER_IMAGE_ID);
		opfPackage.setGenerateCoverHTML(true);
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
		configureNCX();
		// Create the required metadata element
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
	 * Creates and configures a table of contents.
	 */
	private void configureNCX() {
		ncxTOC = NCXFactory.eINSTANCE.createNcx();
		// Set the required version attribute
		ncxTOC.setVersion("2005-1"); //$NON-NLS-1$
		// Create the required head element
		Head head = NCXFactory.eINSTANCE.createHead();
		ncxTOC.setHead(head);
		// Create the required title element
		DocTitle docTitle = NCXFactory.eINSTANCE.createDocTitle();
		Text text = NCXFactory.eINSTANCE.createText();
		FeatureMapUtil.addText(text.getMixed(), "Table of contents"); //$NON-NLS-1$
		docTitle.setText(text);
		ncxTOC.setDocTitle(docTitle);
		// Create the required navigation map element
		NavMap navMap = NCXFactory.eINSTANCE.createNavMap();
		ncxTOC.setNavMap(navMap);
	}

	/**
	 * Validates all XHTML items in the manifest. The following rules are observed:
	 * <ul>
	 * <li>The item must be a core media type. If not it must have a fallback item which must exist and be of a core
	 * media type. Otherwise an error is added to the list of messages</li>
	 * <li>XHTML file content must be in the preferred vocabulary. Warnings are added when this is not the case.</li>
	 * </ul>
	 *
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	@Override
	protected List<ValidationMessage> validateContents()
			throws ParserConfigurationException, SAXException, IOException {
		EList<Item> manifestItems = opfPackage.getManifest().getItems();
		ArrayList<ValidationMessage> messages = new ArrayList<ValidationMessage>();
		for (Item item : manifestItems) {
			// if the "file" attribute is not set we probably have an item
			// that is in the model because we're repacking an EPUB. We'll try
			// to make it easier on the user by figuring out the path to the
			// file and fail only if the file does not exist.
			if (item.getFile() == null) {
				File rootFolder = getRootFolder();
				String href = item.getHref();
				File file = new File(rootFolder, href);
				if (!file.exists()) {
					messages.add(new ValidationMessage(ValidationMessage.Severity.ERROR,
							MessageFormat.format(Messages.getString("OPSPublication.7"), item.getHref()))); //$NON-NLS-1$
				}
				item.setFile(file.toString());
			}
			if (!isLegalType(item)) {
				Item fallback = getItemById(item.getFallback());
				if (fallback == null) {
					messages.add(new ValidationMessage(ValidationMessage.Severity.WARNING,
							MessageFormat.format(Messages.getString("OPS2Publication.13"), //$NON-NLS-1$
									item.getHref())));
				} else if (!isLegalType(fallback)) {
					messages.add(new ValidationMessage(ValidationMessage.Severity.WARNING,
							MessageFormat.format(Messages.getString("OPS2Publication.14"), //$NON-NLS-1$
									item.getHref())));
				} else {
					messages.add(new ValidationMessage(ValidationMessage.Severity.WARNING,
							MessageFormat.format(Messages.getString("OPS2Publication.15"), //$NON-NLS-1$
									item.getHref())));
				}
			}
			// Validate the XHTML items to see if they contain illegal attributes and elements
			if (item.getMedia_type().equals(MIMETYPE_XHTML)) {
				File file = new File(item.getFile());
				FileReader fr = new FileReader(file);
				messages.addAll(OPSValidator.validate(new InputSource(fr), item.getHref()));
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
	protected void writeTableOfContents(File oepbsFolder)
			throws IOException, ParserConfigurationException, SAXException {
		// If a table of contents file has not been specified we must create
		// one. If it has been specified it will be copied.
		if (getItemById(opfPackage.getSpine().getToc()) == null) {
			configureNCX();
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
