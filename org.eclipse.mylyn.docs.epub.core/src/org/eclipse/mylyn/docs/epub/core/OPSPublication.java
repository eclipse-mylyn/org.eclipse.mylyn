/*******************************************************************************
 * Copyright (c) 2011 Torkild U. Resheim.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 * Torkild U. Resheim - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.docs.epub.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.emf.ecore.util.EcoreValidator;
import org.eclipse.emf.ecore.util.FeatureMapUtil;
import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl;
import org.eclipse.mylyn.docs.epub.dc.Contributor;
import org.eclipse.mylyn.docs.epub.dc.Coverage;
import org.eclipse.mylyn.docs.epub.dc.Creator;
import org.eclipse.mylyn.docs.epub.dc.DCFactory;
import org.eclipse.mylyn.docs.epub.dc.DCType;
import org.eclipse.mylyn.docs.epub.dc.Date;
import org.eclipse.mylyn.docs.epub.dc.Description;
import org.eclipse.mylyn.docs.epub.dc.Format;
import org.eclipse.mylyn.docs.epub.dc.Identifier;
import org.eclipse.mylyn.docs.epub.dc.Language;
import org.eclipse.mylyn.docs.epub.dc.LocalizedDCType;
import org.eclipse.mylyn.docs.epub.dc.Publisher;
import org.eclipse.mylyn.docs.epub.dc.Relation;
import org.eclipse.mylyn.docs.epub.dc.Rights;
import org.eclipse.mylyn.docs.epub.dc.Source;
import org.eclipse.mylyn.docs.epub.dc.Subject;
import org.eclipse.mylyn.docs.epub.dc.Title;
import org.eclipse.mylyn.docs.epub.opf.Item;
import org.eclipse.mylyn.docs.epub.opf.Itemref;
import org.eclipse.mylyn.docs.epub.opf.OPFFactory;
import org.eclipse.mylyn.docs.epub.opf.OPFPackage;
import org.eclipse.mylyn.docs.epub.opf.Package;
import org.eclipse.mylyn.docs.epub.opf.Reference;
import org.eclipse.mylyn.docs.epub.opf.Role;
import org.eclipse.mylyn.docs.epub.opf.Spine;
import org.eclipse.mylyn.docs.epub.opf.Type;
import org.eclipse.mylyn.docs.epub.opf.util.OPFResourceImpl;
import org.eclipse.mylyn.internal.docs.epub.core.EPUBFileUtil;
import org.eclipse.mylyn.internal.docs.epub.core.EPUBXMLHelperImp;
import org.eclipse.mylyn.internal.docs.epub.core.ReferenceScanner;
import org.xml.sax.SAXException;

/**
 * This type represents one <i>OPS publication</i>. This includes the <i>OPF
 * package document</i> and <i>OPS content documents</i>. It maintains a data
 * structure representing the entire publication and API for building it.
 * <p>
 * Please note that this API is provisional and should not yet be used to build
 * applications.
 * </p>
 * 
 * @author Torkild U. Resheim
 */
public abstract class OPSPublication {
	// Rules of engagement:
	// * Keep all data in the model, use "transient" for temporary properties
	// * Do not actually do anything before the final assemble

	/** Default identifier for the cover page */
	private static final String COVER_ID = "cover";

	/** Publication identifier for the cover image item */
	public static final String COVER_IMAGE_ID = "cover-image";

	protected static final String CREATION_DATE_ID = "creation";

	public final static String MIMETYPE_CSS = "text/css";

	public static final String MIMETYPE_EPUB = "application/epub+zip";

	public static final String MIMETYPE_XHTML = "application/xhtml+xml";

	private static final String OPF_FILE_SUFFIX = "opf";

	protected static final String UUID_SCHEME = "uuid";

	/** The encoding to use in XML files */
	protected static final String XML_ENCODING = "UTF-8";

	/**
	 * Returns an EPUB version 2.0.1 instance.
	 * 
	 * @return an EPUB instance
	 */
	public static OPSPublication getVersion2Instance() {
		return new OPS2Publication();
	}

	/** List of validation messages */
	public List<ValidationMessage> messages;

	/** The root model element */
	protected Package opfPackage;

	/** The root folder TODO: Move to opfPackage */
	private File rootFolder;

	protected OPSPublication() {
		opfPackage = OPFFactory.eINSTANCE.createPackage();
		registerOPFResourceFactory();
	}

	/**
	 * Adds data to the publication that we always want to be present.
	 * <ul>
	 * <li>The creation date.</li>
	 * <li><i>Eclipse Mylyn Docs project</i> as contributor redactor role.</li>
	 * <li>A unique identifier if none has been specified.</li>
	 * <li>A empty description if none has been specified.</li>
	 * <li>Language "English" if none has been specified.</li>
	 * <li>A dummy title if none has been specified.</li>
	 * <li>The publication format if none has been specified.</li>
	 * </ul>
	 */
	private void addCompulsoryData() {
		// Creation date is always when we build
		addDate(null, new java.util.Date(System.currentTimeMillis()), CREATION_DATE_ID);
		// Make it clear where the tooling comes from
		addContributor(null, null, "Eclipse Mylyn Docs project", Role.REDACTOR, null);
		// Generate an unique identifier
		if (getIdentifier() == null) {
			addIdentifier(UUID_SCHEME, "uuid", "urn:uuid" + UUID.randomUUID().toString());
			setIdentifierId(UUID_SCHEME);
		}
		// Add empty subject
		if (opfPackage.getMetadata().getSubjects().isEmpty()) {
			addSubject(null, null, "");
		}
		// Add English language
		if (opfPackage.getMetadata().getLanguages().isEmpty()) {
			addLanguage(null, Locale.ENGLISH.toString());
		}
		// Add dummy title
		if (opfPackage.getMetadata().getTitles().isEmpty()) {
			addTitle(null, null, "No title specified");
		}
		// Set the publication format
		if (opfPackage.getMetadata().getFormats().isEmpty()) {
			addFormat(null, MIMETYPE_EPUB);
		}
	}

	/**
	 * Specifies a new contributor for the publication.
	 * 
	 * @param id
	 *            an identifier or <code>null</code>
	 * @param name
	 *            name of the creator
	 * @param role
	 *            the role or <code>null</code>
	 * @param fileAs
	 *            name to file the creator under or <code>null</code>
	 * @param lang
	 *            the language code or <code>null</code>
	 * @return the new creator
	 */
	public Contributor addContributor(String id, Locale lang, String name, Role role, String fileAs) {
		Contributor dc = DCFactory.eINSTANCE.createContributor();
		setDcLocalized(dc, id, lang, name);
		if (role != null) {
			dc.setRole(role);
		}
		if (fileAs != null) {
			dc.setFileAs(fileAs);
		}
		opfPackage.getMetadata().getContributors().add(dc);
		return dc;
	}

	/**
	 * Specifies a new &quot;coverage&quot; for the publication.
	 * 
	 * @param id
	 *            an identifier or <code>null</code>
	 * @param lang
	 *            the language code or <code>null</code>
	 * @param value
	 *            value of the item
	 * @return the new coverage
	 */
	public Coverage addCoverage(String id, Locale lang, String value) {
		if (value == null) {
			throw new IllegalArgumentException("A value must be specified");
		}
		Coverage dc = DCFactory.eINSTANCE.createCoverage();
		setDcLocalized(dc, id, lang, value);
		opfPackage.getMetadata().getCoverages().add(dc);
		return dc;
	}

	/**
	 * Specifies a new creator for the publication.
	 * 
	 * @param id
	 *            a unique identifier or <code>null</code>
	 * @param lang
	 *            the language code or <code>null</code>
	 * @param name
	 *            name of the creator
	 * @param role
	 *            the role or <code>null</code>
	 * @param fileAs
	 *            name to file the creator under or <code>null</code>
	 * @return the new creator
	 */
	public Creator addCreator(String id, Locale lang, String name, Role role, String fileAs) {
		Creator dc = DCFactory.eINSTANCE.createCreator();
		setDcLocalized(dc, id, lang, name);
		if (role != null) {
			dc.setRole(role);
		}
		if (fileAs != null) {
			dc.setFileAs(fileAs);
		}
		opfPackage.getMetadata().getCreators().add(dc);
		return dc;
	}

	/**
	 * Adds a new date to the publication. The given instance will be
	 * represented in a format defined by "Date and Time Formats" at
	 * http://www.w3.org/TR/NOTE-datetime and by ISO 8601 on which it is based.
	 * 
	 * @param id
	 *            optional identifier
	 * @param date
	 *            the date
	 * @param event
	 *            the event
	 * @return the new date
	 * @see #addDate(String, String, String)
	 */
	public Date addDate(String id, java.util.Date date, String event) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		TimeZone tz = TimeZone.getTimeZone("UTC");
		df.setTimeZone(tz);
		Date dc = DCFactory.eINSTANCE.createDate();
		setDcCommon(dc, id, df.format(date));
		if (event != null) {
			dc.setEvent(event);
		}
		opfPackage.getMetadata().getDates().add(dc);
		return dc;
	}

	/**
	 * Date of publication, in the format defined by the W3C specification
	 * "<a href="http://www.w3.org/TR/NOTE-datetime">Date and Time Formats</a>"
	 * and by ISO 8601. In particular, dates without times must be represented
	 * in the form YYYY[-MM[-DD]]: a required 4-digit year, an optional 2-digit
	 * month, and if the month is given, an optional 2-digit day of month. The
	 * event attribute is optional, possible values may include: "creation",
	 * "publication", and "modification".
	 * 
	 * @param value
	 *            the date string
	 * @param event
	 *            an optional event description
	 * @return the new date
	 */
	public Date addDate(String id, String value, String event) {
		if (value == null) {
			throw new IllegalArgumentException("A value must be specified");
		}
		Date dc = DCFactory.eINSTANCE.createDate();
		setDcCommon(dc, id, value);
		if (event != null) {
			dc.setEvent(event);
		}
		opfPackage.getMetadata().getDates().add(dc);
		return dc;
	}

	/**
	 * Adds a new description to the publication.
	 * 
	 * @param id
	 *            identifier or <code>null</code>
	 * @param lang
	 *            the language code or <code>null</code>
	 * @param value
	 *            the description text
	 * @return the new description
	 */
	public Description addDescription(String id, Locale lang, String value) {
		if (value == null) {
			throw new IllegalArgumentException("A value must be specified");
		}
		Description dc = DCFactory.eINSTANCE.createDescription();
		setDcLocalized(dc, id, lang, value);
		opfPackage.getMetadata().getDescriptions().add(dc);
		return dc;
	}

	/**
	 * Adds an optional publication format.
	 * 
	 * @param id
	 *            identifier or <code>null</code>
	 * @param value
	 *            the format to add
	 * @return the new format
	 */
	public Format addFormat(String id, String value) {
		Format dc = DCFactory.eINSTANCE.createFormat();
		setDcCommon(dc, id, value);
		opfPackage.getMetadata().getFormats().add(dc);
		return dc;
	}

	/**
	 * Adds a new identifier to the publication.
	 * 
	 * @param id
	 *            the identifier id
	 * @param scheme
	 *            the scheme used for representing the identifier
	 * @param value
	 *            the identifier value
	 * @return the new identifier
	 */
	public Identifier addIdentifier(String id, String scheme, String value) {
		if (value == null) {
			throw new IllegalArgumentException("A value must be specified");
		}
		Identifier dc = DCFactory.eINSTANCE.createIdentifier();
		dc.setId(id);
		dc.setScheme(scheme);
		FeatureMapUtil.addText(dc.getMixed(), value);
		opfPackage.getMetadata().getIdentifiers().add(dc);
		return dc;
	}

	/**
	 * Adds a new item to the manifest using default values for properties not
	 * specified. Same as
	 * <code>addItem(null, null, file, null, null, true, false);</code>.
	 * 
	 * @param file
	 * @return the new item
	 */
	public Item addItem(File file) {
		return addItem(null, null, file, null, null, true, true, false);
	}

	/**
	 * Adds a new item to the manifest. If an identifier is not specified it
	 * will automatically be assigned.
	 * 
	 * <p>
	 * The <i>spine</i> defines the reading order, so the order items are added
	 * and whether or not <i>spine</i> is <code>true</code> does matter. Unless
	 * a table of contents file has been specified it will be generated. All
	 * files that have been added to the spine will be examined unless the
	 * <i>noToc</i> attribute has been set to <code>true</code>.
	 * </p>
	 * 
	 * @param file
	 *            the file to add
	 * @param dest
	 *            the destination sub-folder or <code>null</code>
	 * @param id
	 *            identifier or <code>null</code>
	 * @param type
	 *            MIME file type
	 * @param spine
	 *            whether or not to add the item to the spine
	 * @param linear
	 *            whether or not the item is part of the reading order
	 * @param noToc
	 *            whether or not to include in TOC when automatically generated
	 * @return the new item
	 */
	public Item addItem(String id, Locale lang, File file, String dest, String type, boolean spine, boolean linear,
			boolean noToc) {
		if (file == null || !file.exists()) {
			throw new IllegalArgumentException("\"file\" " + file.getAbsolutePath() + " must exist.");
		}
		if (file.isDirectory()) {
			throw new IllegalArgumentException("\"file\" " + file.getAbsolutePath() + " must not be a directory.");
		}
		Item item = OPFFactory.eINSTANCE.createItem();
		if (type == null) {
			type = EPUBFileUtil.getMimeType(file);
			if (type == null) {
				throw new IllegalArgumentException("Could not automatically determine MIME type for file " + file
						+ ". Please specify the correct value");
			}
		}
		if (id == null) {
			String prefix = "";
			if (!type.equals(MIMETYPE_XHTML)) {
				prefix = (type.indexOf('/')) == -1 ? type : type.substring(0, type.indexOf('/')) + "-";
			}
			id = prefix + file.getName().substring(0, file.getName().lastIndexOf('.'));
		}
		item.setId(id);
		if (dest == null) {
			item.setHref(file.getName());
		} else {
			item.setHref(dest + '/' + file.getName());
		}
		item.setNoToc(noToc);
		item.setMedia_type(type);
		item.setFile(file.getAbsolutePath());
		opfPackage.getManifest().getItems().add(item);
		if (spine) {
			Itemref ref = OPFFactory.eINSTANCE.createItemref();
			if (!linear) {
				ref.setLinear("no");
			}
			ref.setIdref(id);
			getSpine().getSpineItems().add(ref);
		}
		return item;
	}

	/**
	 * Adds a new language specification to the publication.
	 * 
	 * @param id
	 *            identifier or <code>null</code>
	 * @param lang
	 *            the language code or <code>null</code>
	 * @param lang
	 *            the RFC-3066 format of the language code
	 * @return the language instance
	 */
	public Language addLanguage(String id, String value) {
		if (value == null) {
			throw new IllegalArgumentException("A value must be specified");
		}
		Language dc = DCFactory.eINSTANCE.createLanguage();
		setDcCommon(dc, id, value);
		opfPackage.getMetadata().getLanguages().add(dc);
		return dc;
	}

	/**
	 * Adds a new meta item to the publication.
	 * 
	 * @param name
	 *            name of the item
	 * @param value
	 *            content of the item
	 * @return the new meta
	 */
	public org.eclipse.mylyn.docs.epub.opf.Meta addMeta(String name, String value) {
		if (value == null) {
			throw new IllegalArgumentException("A value must be specified");
		}
		if (name == null) {
			throw new IllegalArgumentException("A name must be specified");
		}
		org.eclipse.mylyn.docs.epub.opf.Meta opf = OPFFactory.eINSTANCE.createMeta();
		opf.setName(name);
		opf.setContent(value);
		opfPackage.getMetadata().getMetas().add(opf);
		return opf;
	}

	/**
	 * Adds a new publisher to the publication.
	 * 
	 * @param id
	 *            identifier or <code>null</code>
	 * @param lang
	 *            the language code or <code>null</code>
	 * @param value
	 *            name of the publisher
	 * @return the new publisher
	 */
	public Publisher addPublisher(String id, Locale lang, String value) {
		if (value == null) {
			throw new IllegalArgumentException("A value must be specified");
		}
		Publisher dc = DCFactory.eINSTANCE.createPublisher();
		setDcLocalized(dc, id, lang, value);
		opfPackage.getMetadata().getPublishers().add(dc);
		return dc;
	}

	/**
	 * The structural components of the books are listed in reference elements
	 * contained within the guide element. These components could refer to the
	 * table of contents, list of illustrations, foreword, bibliography, and
	 * many other standard parts of the book. Reading systems are not required
	 * to use the guide element but it is a good idea to use it.
	 * 
	 * @param href
	 *            the item referenced
	 * @param title
	 *            title of the reference
	 * @param value
	 *            type of the reference
	 * @return the reference
	 */
	public Reference addReference(String href, String title, Type value) {
		if (value == null) {
			throw new IllegalArgumentException("A value must be specified");
		}
		if (href == null) {
			throw new IllegalArgumentException("A href must be specified");
		}
		if (title == null) {
			throw new IllegalArgumentException("A title must be specified");
		}
		Reference reference = OPFFactory.eINSTANCE.createReference();
		reference.setHref(href);
		reference.setTitle(title);
		reference.setType(value);
		opfPackage.getGuide().getGuideItems().add(reference);
		return reference;
	}

	/**
	 * Adds a optional <i>relation</i> specification to the publication.
	 * 
	 * @param id
	 *            identifier or <code>null</code>
	 * @param lang
	 *            the language code or <code>null</code>
	 * @param value
	 *            the value of the relation
	 * @return the new relation
	 */
	public Relation addRelation(String id, Locale lang, String value) {
		if (value == null) {
			throw new IllegalArgumentException("A value must be specified");
		}
		Relation dc = DCFactory.eINSTANCE.createRelation();
		setDcLocalized(dc, id, lang, value);
		opfPackage.getMetadata().getRelations().add(dc);
		return dc;
	}

	/**
	 * Adds a optional <i>rights</i> specification to the publication.
	 * 
	 * @param id
	 *            identifier or <code>null</code>
	 * @param lang
	 *            the language code or <code>null</code>
	 * @param value
	 *            the rights text
	 * @return the new rights element
	 */
	public Rights addRights(String id, Locale lang, String value) {
		if (value == null) {
			throw new IllegalArgumentException("A value must be specified");
		}
		Rights dc = DCFactory.eINSTANCE.createRights();
		setDcLocalized(dc, id, lang, value);
		opfPackage.getMetadata().getRights().add(dc);
		return dc;
	}

	/**
	 * Adds a optional <i>source</i> specification to the publication.
	 * 
	 * @param id
	 *            identifier or <code>null</code>
	 * @param lang
	 *            the language code or <code>null</code>
	 * @param value
	 *            the source text
	 * @return the new source element
	 */
	public Source addSource(String id, Locale lang, String value) {
		if (value == null) {
			throw new IllegalArgumentException("A value must be specified");
		}
		Source dc = DCFactory.eINSTANCE.createSource();
		setDcLocalized(dc, id, lang, value);
		opfPackage.getMetadata().getSources().add(dc);
		return dc;
	}

	/**
	 * Adds a required <i>subject</i> specification to the publication.
	 * 
	 * @param id
	 *            identifier or <code>null</code>
	 * @param lang
	 *            the language code or <code>null</code>
	 * @param value
	 *            the subject
	 */
	public Subject addSubject(String id, Locale lang, String value) {
		if (value == null) {
			throw new IllegalArgumentException("A value must be specified");
		}
		Subject dc = DCFactory.eINSTANCE.createSubject();
		setDcLocalized(dc, id, lang, value);
		opfPackage.getMetadata().getSubjects().add(dc);
		return dc;
	}

	/**
	 * Adds a required <i>title</i> specification to the publication.
	 * 
	 * @param id
	 *            identifier or <code>null</code>
	 * @param lang
	 *            the language code or <code>null</code>
	 * @param value
	 *            the new title
	 * @return the new title
	 */
	public Title addTitle(String id, Locale lang, String value) {
		if (value == null) {
			throw new IllegalArgumentException("A value must be specified");
		}
		Title dc = DCFactory.eINSTANCE.createTitle();
		setDcLocalized(dc, id, lang, value);
		opfPackage.getMetadata().getTitles().add(dc);
		return dc;
	}

	/**
	 * Adds a optional <i>type</i> specification to the publication.
	 * 
	 * @param id
	 *            identifier or <code>null</code>
	 * @param type
	 *            the type to add
	 * @return the new type
	 */
	public org.eclipse.mylyn.docs.epub.dc.Type addType(String id, String value) {
		if (value == null) {
			throw new IllegalArgumentException("A value must be specified");
		}
		org.eclipse.mylyn.docs.epub.dc.Type dc = DCFactory.eINSTANCE.createType();
		setDcCommon(dc, id, value);
		opfPackage.getMetadata().getTypes().add(dc);
		return dc;
	}

	/**
	 * Copies all items part of the publication into the OEPBS folder unless the
	 * item in question will be generated.
	 * 
	 * @param rootFolder
	 *            the folder to copy into.
	 * @throws IOException
	 */
	private void copyContent(File rootFolder) throws IOException {
		EList<Item> items = opfPackage.getManifest().getItems();
		for (Item item : items) {
			if (!item.isGenerated()) {
				File source = new File(item.getFile());
				File destination = new File(rootFolder.getAbsolutePath() + File.separator + item.getHref());
				EPUBFileUtil.copy(source, destination);
			}
		}
	}

	/**
	 * Implement to handle generation of table of contents from the items added
	 * to the <i>spine</i>.
	 * 
	 * @throws Exception
	 */
	protected abstract void generateTableOfContents() throws Exception;

	/**
	 * Returns the main identifier of the publication or <code>null</code> if it
	 * could not be determined.
	 * 
	 * @return the main identifier or <code>null</code>
	 */
	public Identifier getIdentifier() {
		EList<Identifier> identifiers = opfPackage.getMetadata().getIdentifiers();
		for (Identifier identifier : identifiers) {
			if (identifier.getId().equals(opfPackage.getUniqueIdentifier())) {
				return identifier;
			}
		}
		return null;
	}

	/**
	 * Locates and returns an item from the manifest corresponding to the given
	 * identifier.
	 * 
	 * @param id
	 *            the identifier
	 * @return the item
	 */
	public Item getItemById(String id) {
		EList<Item> items = opfPackage.getManifest().getItems();
		for (Item item : items) {
			if (item.getId().equals(id)) {
				return item;
			}
		}
		return null;
	}

	/**
	 * Returns a list of all manifest items that have the specified MIME type.
	 * 
	 * @param mimetype
	 *            the MIME type to search for
	 * @return a list of all items
	 */
	public List<Item> getItemsByMIMEType(String mimetype) {
		ArrayList<Item> stylesheets = new ArrayList<Item>();
		EList<Item> items = opfPackage.getManifest().getItems();
		for (Item item : items) {
			if (item.getMedia_type().equals(mimetype)) {
				stylesheets.add(item);
			}
		}
		return stylesheets;
	}

	public Package getOpfPackage() {
		return opfPackage;
	}

	/**
	 * Returns the root folder of the publication. This is the folder where the
	 * OPF file resides. Note that this property will only have a value if this
	 * instance has been populated using an existing publication, such as when
	 * unpacking an EPUB file.
	 * 
	 * @return the root folder or <code>null</code>
	 */
	public File getRootFolder() {
		return rootFolder;
	}

	/**
	 * Returns the publication spine.
	 * 
	 * @return the spine
	 */
	protected Spine getSpine() {
		return opfPackage.getSpine();
	}

	/**
	 * Returns the table of contents for the publication. As the actual
	 * implementation may vary depending on
	 * 
	 * @return the table of contents
	 */
	public abstract Object getTableOfContents();

	/**
	 * Returns a list of validation messages. This list is only populated when
	 * {@link #pack(File)} has taken place.
	 * 
	 * @return a list of validation messages
	 */
	public List<ValidationMessage> getValidationMessages() {
		return messages;
	}

	/**
	 * Iterates over all files in the manifest attempting to determine
	 * referenced resources such as image files and adds these to the manifest.
	 * 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private void includeReferencedResources() throws ParserConfigurationException, SAXException, IOException {
		EList<Item> manifestItems = opfPackage.getManifest().getItems();
		// Compose a list of file references
		HashMap<File, List<File>> references = new HashMap<File, List<File>>();
		for (Item item : manifestItems) {
			// Only parse XHTML-files and files that are not generated
			if (item.getMedia_type().equals(MIMETYPE_XHTML) && !item.isGenerated()) {
				if (item.getSourcePath() != null) {
					File source = new File(item.getSourcePath());
					references.put(source, ReferenceScanner.parse(item));
				} else {
					File source = new File(item.getFile());
					references.put(source, ReferenceScanner.parse(item));
				}
			}
		}
		for (File root : references.keySet()) {
			List<File> files = references.get(root);
			for (File file : files) {
				File relativePath = new File(EPUBFileUtil.getRelativePath(root, file));
				addItem(null, null, file, relativePath.getParent(), null, false, false, false);
			}
		}

	}

	/**
	 * Assembles the OPS publication in a location relative to the root file.
	 * 
	 * @param rootFile
	 *            the root file
	 * @throws Exception
	 */
	void pack(File rootFile) throws Exception {
		if (opfPackage.getSpine().getSpineItems().isEmpty()) {
			throw new IllegalArgumentException("Spine does not contain any items");
		}
		// Note that order is important here. Some of the steps for assembling
		// the EPUB may insert data into the EPUB structure. Hence the OPF must
		// be written last.
		this.rootFolder = rootFile.getAbsoluteFile().getParentFile();
		addCompulsoryData();
		if (rootFolder.isDirectory() || rootFolder.mkdirs()) {
			if (opfPackage.isGenerateCoverHTML()) {
				writeCoverHTML(rootFolder);
			}
			if (opfPackage.isIncludeReferencedResources()) {
				includeReferencedResources();
			}
			copyContent(rootFolder);
			messages = validateContents();
			writeTableOfContents(rootFolder);
			writeOPF(rootFile);
		} else {
			throw new IOException("Could not create OEBPS folder in " + rootFolder.getAbsolutePath());
		}
		validateMetadata();
	}

	/**
	 * Reads the root file.
	 * 
	 * @param rootFile
	 *            the file to read
	 * @throws IOException
	 */
	protected void readOPF(File rootFile) throws IOException {
		ResourceSet resourceSet = new ResourceSetImpl();
		URI fileURI = URI.createFileURI(rootFile.getAbsolutePath());
		Resource resource = resourceSet.createResource(fileURI);
		resource.load(null);
		opfPackage = (Package) resource.getContents().get(0);
	}

	/**
	 * Implement to read the table of contents for the particular OEPBS
	 * implementation.
	 * 
	 * @param tocFile
	 *            the table of contents file
	 * @throws IOException
	 */
	protected abstract void readTableOfContents(File tocFile) throws IOException;

	/**
	 * Registers a new resource factory for OPF data structures. This is
	 * normally done through Eclipse extension points but we also need to be
	 * able to create this factory without the Eclipse runtime.
	 */
	private void registerOPFResourceFactory() {
		// Register package so that it is available even without the Eclipse
		// runtime
		@SuppressWarnings("unused")
		OPFPackage packageInstance = OPFPackage.eINSTANCE;

		// Register the file suffix
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(OPF_FILE_SUFFIX,
				new XMLResourceFactoryImpl() {

					@Override
					public Resource createResource(URI uri) {
						OPFResourceImpl xmiResource = new OPFResourceImpl(uri) {

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
						parserFeatures.put("http://xml.org/sax/features/validation", Boolean.FALSE);
						parserFeatures.put("http://apache.org/xml/features/nonvalidating/load-external-dtd",
								Boolean.FALSE);
						loadOptions.put(XMLResource.OPTION_PARSER_FEATURES, parserFeatures);
						return xmiResource;
					}

				});
	}

	/**
	 * Convenience method for adding a cover to the publication. This method
	 * will make sure the required actions are taken to provide a cover page for
	 * all reading systems. 
	 * 
	 * @param image
	 *            the cover image (jpeg, png, svg or gif)
	 * @param title
	 *            title of the cover page
	 */
	public void setCover(File image, String title) {
		// Add the cover image to the manifest
		Item item = addItem(COVER_IMAGE_ID, null, image, null, null, false, false, true);
		item.setTitle(title);
		// Point to the cover using a meta tag
		addMeta(COVER_ID, COVER_IMAGE_ID);
		opfPackage.setGenerateCoverHTML(true);

	}

	/**
	 * Sets common properties for <i>Dublin Core</i> elements.
	 * 
	 * @param dc
	 *            the Dublin Core element
	 * @param id
	 *            optional identifier
	 * @param value
	 *            value of the element
	 */
	private void setDcCommon(DCType dc, String id, String value) {
		FeatureMapUtil.addText(dc.getMixed(), value);
		if (id != null) {
			dc.setId(id);
		}
	}

	/**
	 * Sets common properties for localized <i>Dublin Core</i> elements.
	 * 
	 * @param dc
	 *            the Dublin Core element
	 * @param id
	 *            optional identifier
	 * @param lang
	 *            language code
	 * @param value
	 *            value of the element
	 */
	private void setDcLocalized(LocalizedDCType dc, String id, Locale lang, String value) {
		setDcCommon(dc, id, value);
		if (lang != null) {
			dc.setLang(lang.toString());
		}
	}

	/**
	 * Specifies whether or not to automatically generate table of contents from
	 * the publication contents. The default is <code>true</code>
	 * 
	 * @param generateToc
	 *            whether or not to generate a table of contents
	 */
	public void setGenerateToc(boolean generateToc) {
		opfPackage.setGenerateTableOfContents(generateToc);
	}

	/**
	 * Specifies the id of the identifier used for the publication.
	 * 
	 * @param identifier_id
	 *            the identifier id
	 * @see #addIdentifier(String, String, String)
	 */
	public void setIdentifierId(String identifier_id) {
		opfPackage.setUniqueIdentifier(identifier_id);
	}

	/**
	 * Specifies whether or not to automatically include resources (files) that
	 * are referenced in the contents. The default is <code>false</code>.
	 * 
	 * @param include
	 *            whether or not automatically include resources
	 */
	public void setIncludeReferencedResources(boolean include) {
		opfPackage.setIncludeReferencedResources(include);
	}

	/**
	 * Specifies a target of contents file for the publication. This is an
	 * alternative to {@link #setGenerateToc(boolean)}.
	 * 
	 * @param tocFile
	 *            the table of contents file
	 */
	public abstract void setTableOfContents(File tocFile);

	/**
	 * Populates the data model with the content from an unpacked EPUB.
	 * 
	 * @param epubFile
	 *            the EPUB file to unpack
	 * @param destination
	 *            the destination folder
	 * @throws Exception
	 */
	void unpack(File rootFile) throws Exception {
		readOPF(rootFile);
		rootFolder = rootFile.getAbsoluteFile().getParentFile();
		String tocId = opfPackage.getSpine().getToc();
		Item tocItem = getItemById(tocId);
		File tocFile = new File(rootFolder.getAbsolutePath() + File.separator + tocItem.getHref());
		readTableOfContents(tocFile);
	}

	/**
	 * Implement to validate contents.
	 * 
	 * @throws Exception
	 */
	protected abstract List<ValidationMessage> validateContents() throws Exception;

	/**
	 * Validates the data model contents.
	 * 
	 * @return a list of EMF diagnostics
	 */
	public List<Diagnostic> validateMetadata() {
		EValidator.Registry.INSTANCE.put(OPFPackage.eINSTANCE, new EcoreValidator());
		BasicDiagnostic diagnostics = new BasicDiagnostic();
		for (EObject eo : opfPackage.eContents()) {
			Map<Object, Object> context = new HashMap<Object, Object>();
			Diagnostician.INSTANCE.validate(eo, diagnostics, context);
		}
		return diagnostics.getChildren();
	}

	/**
	 * Writes a XHTML-file for the cover image. This is added to the publication
	 * and all required references set.
	 * 
	 * @param rootFolder
	 *            the publication root folder
	 * @throws IOException
	 * 
	 */
	private void writeCoverHTML(File rootFolder) throws IOException {
		Item coverImage = getItemById(COVER_IMAGE_ID);
		File coverFile = new File(rootFolder.getAbsolutePath() + File.separator + "cover-page.xhtml");
		if (!coverFile.exists()) {

			try {
				FileWriter fw = new FileWriter(coverFile);
				fw.append("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?>\n");
				fw.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">\n");
				fw.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">\n");
				fw.append("  <head>\n");
				fw.append("    <title>" + coverImage.getTitle() + "</title>\n");
				fw.append("    <style type=\"text/css\">");
				fw.append("      #cover-body {\n");
				fw.append("        margin: 0px;\n");
				fw.append("        text-align: center;\n");
				fw.append("        background-color: #222222;\n");
				fw.append("      }\n");
				fw.append("      #cover-block {\n");
				fw.append("        height: 100%;\n");
				fw.append("        margin-top: 0;\n");
				fw.append("      }\n");
				fw.append("      #cover-image {\n");
				fw.append("        height: 100%;\n");
				fw.append("        text-align: center;\n");
				fw.append("        max-width: 100%;\n");
				fw.append("      }\n");
				fw.append("    </style>\n");
				fw.append("  </head>\n");
				fw.append("  <body id=\"cover-body\">\n");
				fw.append("    <div id=\"cover-block\">\n");
				fw.append("      <img id=\"cover-image\" src=\"" + coverImage.getHref() + "\" alt=\""
						+ coverImage.getTitle() + "\"/>\n");
				fw.append("    </div>\n");
				fw.append("  </body>\n");
				fw.append("</html>\n");
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// Add the cover page item
		Item coverPage = addItem(COVER_ID, null, coverFile, null, MIMETYPE_XHTML, true, false, false);
		coverPage.setGenerated(true);
		addReference(coverPage.getHref(), coverImage.getTitle(), Type.COVER);
		// Move the cover page first in the spine.
		EList<Itemref> spine = opfPackage.getSpine().getSpineItems();
		Itemref cover = null;
		for (Itemref itemref : spine) {
			if (itemref.getIdref().equals(COVER_ID)) {
				cover = itemref;
			}
		}
		if (cover != null) {
			spine.move(0, cover);
		}
	}

	/**
	 * Writes the <b>content.opf</b> file.
	 * 
	 * @param rootFolder
	 *            the folder where to write the file.
	 * @throws IOException
	 */
	private void writeOPF(File opfFile) throws IOException {
		ResourceSet resourceSet = new ResourceSetImpl();
		// Register the packages to make it available during loading.
		URI fileURI = URI.createFileURI(opfFile.getAbsolutePath());
		Resource resource = resourceSet.createResource(fileURI);
		resource.getContents().add(opfPackage);
		resource.save(null);
	}

	/**
	 * Implement to handle writing of the table of contents. Note that this
	 * method should do nothing if the table of contents has already been
	 * specified using {@link #setTableOfContents(File)}.
	 * 
	 * @param rootFolder
	 *            the folder to write in
	 * @throws Exception
	 */
	protected abstract void writeTableOfContents(File rootFolder) throws Exception;
}
