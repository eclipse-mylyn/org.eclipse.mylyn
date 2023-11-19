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
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl;
import org.eclipse.mylyn.docs.epub.core.ILogger.Severity;
import org.eclipse.mylyn.docs.epub.internal.EPUBFileUtil;
import org.eclipse.mylyn.docs.epub.ocf.Container;
import org.eclipse.mylyn.docs.epub.ocf.OCFFactory;
import org.eclipse.mylyn.docs.epub.ocf.OCFPackage;
import org.eclipse.mylyn.docs.epub.ocf.RootFile;
import org.eclipse.mylyn.docs.epub.ocf.RootFiles;
import org.eclipse.mylyn.docs.epub.ocf.util.OCFResourceImpl;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

/**
 * Represents one EPUB file. One or more publications can be added and will be a part of the distribution when packed.
 * <p>
 * The simplest usage of this API may look like the following:
 * </p>
 *
 * <pre>
 * EPUB epub = new EPUB();
 * OPSPublication oebps = new OPSPublication();
 * oebps.addItem(new File(&quot;chapter.xhtml&quot;));
 * epub.add(oebps);
 * epub.pack(new File(&quot;book.epub&quot;));
 * </pre>
 * <p>
 * This will create a new EPUB instance and an OPS (which is the typical content of an EPUB) with one chapter. The OPS
 * will have one chapter with contents from <b>chapter.xhtml</b> and the final result is an EPUB named <b>book.epub</b>.
 * </p>
 *
 * @author Torkild U. Resheim
 * @see http://www.idpf.org/doc_library/epub/OPS_2.0.1_draft.htm
 * @see http://www.idpf.org/epub/301/spec/epub-publications.html
 */
public class EPUB {

	/**
	 * @since 3.0
	 */
	public enum PublicationVersion {
		/** Unsupported or undetected publication version. */
		UNKNOWN,
		/** Open Publication Structure (OPS) 2.0.1 */
		V2,
		/** EPUB Publications 3.0.1 */
		V3
	}

	/**
	 * SAX parser for detecting the version of an OEBPS contained within an EPUB.
	 */
	private class VersionDetector extends DefaultHandler2 {

		private String versionString;

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes)
				throws SAXException {
			if (qName.equals("opf:package") || qName.equals("package")) {//$NON-NLS-1$ //$NON-NLS-2$
				versionString = attributes.getValue("version"); //$NON-NLS-1$
			}
		}
	}

	/** EPUB MIME type */
	public static final String MIMETYPE_EPUB = "application/epub+zip"; //$NON-NLS-1$

	/** OEBPS (OPS+OPF) MIME type */
	private static final String MIMETYPE_OEBPS = "application/oebps-package+xml"; //$NON-NLS-1$

	/** The encoding to use for the OCF */
	private static final String OCF_FILE_ENCODING = "UTF-8"; //$NON-NLS-1$

	/** Suffix for OCF files */
	private static final String OCF_FILE_SUFFIX = "xml"; //$NON-NLS-1$

	/** Version of the OCF specification used */
	private static final String OCF_VERSION = "1.0"; //$NON-NLS-1$

	private ILogger logger;

	/** The container holding all the publications */
	private Container ocfContainer;

	/**
	 * Creates a new <b>empty</b> instance of an EPUB. Use {@link #add(Publication)} and {@link #pack(File)} to add
	 * publications and ready the EPUB for distribution.
	 */
	public EPUB() {
		ocfContainer = OCFFactory.eINSTANCE.createContainer();
		RootFiles rootFiles = OCFFactory.eINSTANCE.createRootFiles();
		ocfContainer.setRootfiles(rootFiles);
		ocfContainer.setVersion(OCF_VERSION);
		registerOCFResourceFactory();
	}

	public EPUB(ILogger logger) {
		this();
		this.logger = logger;
	}

	/**
	 * Adds a new publication (or root file) to the EPUB. Use {@link #add(Publication)} when adding an OEBPS
	 * publication.
	 * <p>
	 * Note that while an {@link EPUB} can technically contain multiple instances of an {@link Publication}, in practice
	 * reading systems does not support this.
	 * </p>
	 *
	 * @param file
	 *            the publication to add
	 * @param type
	 *            the MIME type of the publication
	 * @see #add(Publication)
	 */
	public void add(File file, String type) {
		String name = type.substring(type.lastIndexOf('/') + 1, type.length()).toUpperCase();
		RootFiles rootFiles = ocfContainer.getRootfiles();
		int count = rootFiles.getRootfiles().size();
		if (count >= 1) {
			log("Multiple root files is unsupported by most reading systems!", Severity.WARNING); //$NON-NLS-1$
		}
		String rootFileName = count > 0 ? name + "_" + count : name; //$NON-NLS-1$
		rootFileName += File.separator + file.getName();
		RootFile rootFile = OCFFactory.eINSTANCE.createRootFile();
		rootFile.setFullPath(rootFileName);
		rootFile.setMediaType(type);
		rootFile.setPublication(file);
		rootFiles.getRootfiles().add(rootFile);
		log(MessageFormat.format(Messages.getString("EPUB.1"), rootFile.getFullPath(), //$NON-NLS-1$
				rootFile.getMediaType()), Severity.VERBOSE);
	}

	/**
	 * Adds a new OEBPS publication to the EPUB. Use {@link #add(File, String)} to add other types of content.
	 * <p>
	 * Note that while an {@link EPUB} can technically contain multiple instances of an {@link Publication}, in practice
	 * reading systems does not support this.
	 * </p>
	 *
	 * @param oebps
	 *            the publication to add.
	 * @since 2.0
	 */
	public void add(Publication oebps) {
		RootFiles rootFiles = ocfContainer.getRootfiles();
		int count = rootFiles.getRootfiles().size();
		if (count >= 1) {
			log("Multiple root files is unsupported by most reading systems!", Severity.WARNING); //$NON-NLS-1$
		}
		String rootFileName = count > 0 ? "OEBPS_" + count : "OEBPS"; //$NON-NLS-1$ //$NON-NLS-2$
		rootFileName += "/content.opf"; //$NON-NLS-1$
		RootFile rootFile = OCFFactory.eINSTANCE.createRootFile();
		rootFile.setFullPath(rootFileName);
		rootFile.setMediaType(MIMETYPE_OEBPS);
		rootFile.setPublication(oebps);
		rootFiles.getRootfiles().add(rootFile);
		log(MessageFormat.format(Messages.getString("EPUB.0"), rootFile.getFullPath(), //$NON-NLS-1$
				rootFile.getMediaType()), Severity.VERBOSE);
	}

	/**
	 * Utility method for deleting a folder recursively.
	 *
	 * @param folder
	 *            the folder to delete
	 */
	private void deleteFolder(File folder) {
		if (folder.isDirectory()) {
			String[] children = folder.list();
			for (String element : children) {
				deleteFolder(new File(folder, element));
			}
		}
		folder.delete();
	}

	/**
	 * Returns the container instance of the EPUB.
	 *
	 * @return the container instance
	 */
	public Container getContainer() {
		return ocfContainer;
	}

	/**
	 * Returns a list of all <i>OPS publications</i> contained within the EPUB. Publications in unsupported versions
	 * will not be returned. However their existence can still be determined by looking at the
	 * {@link Container#getRootfiles()} result.
	 *
	 * @return a list of all OPS publications
	 * @see {@link #getContainer()} for obtaining the root file container
	 */
	public List<Publication> getOPSPublications() {
		ArrayList<Publication> publications = new ArrayList<Publication>();
		EList<RootFile> rootFiles = ocfContainer.getRootfiles().getRootfiles();
		for (RootFile rootFile : rootFiles) {
			if (rootFile.getMediaType().equals(MIMETYPE_OEBPS)) {
				// May be null if the publications is in an unsupported format.
				if (rootFile.getPublication() != null) {
					publications.add((Publication) rootFile.getPublication());
				}
			}
		}
		return publications;
	}

	/**
	 * Use to check whether or not the specified file is in a supported format and can be opened as an EPUB. If it's not
	 * an EPUB <code>false</code> will be returned. Note that this methods does not test the contents of the EPUB which
	 * may or may not contain unsupported root files.
	 *
	 * @param epubFile
	 *            the target EPUB file
	 * @return <code>true</code> if the file can be opened
	 * @throws IOException
	 */
	public boolean isEPUB(File epubFile) throws IOException {
		String mimeType = EPUBFileUtil.getMimeType(epubFile);
		if (mimeType.equals(MIMETYPE_EPUB)) {
			return isEPUB(new FileInputStream(epubFile));
		}
		return false;
	}

	private static final int BUFFERSIZE = 2048;

	/**
	 * Used to verify that the given {@link InputStream} contents is an EPUB. As per specification the first entry in
	 * the file must be named "mimetype" and contain the string <i>application/epub+zip</i>. Further verification is not
	 * done at this stage.
	 *
	 * @param inputStream
	 *            the EPUB input stream
	 * @return <code>true</code> if the file is an EPUB file
	 * @throws IOException
	 */
	public static boolean isEPUB(InputStream inputStream) throws IOException {
		ZipInputStream in = new ZipInputStream(inputStream);
		try {
			byte[] buf = new byte[BUFFERSIZE];
			ZipEntry entry = null;
			if ((entry = in.getNextEntry()) != null) {
				String entryName = entry.getName();
				if (entryName.equals("mimetype")) { //$NON-NLS-1$
					String type = new String();
					while ((in.read(buf, 0, BUFFERSIZE)) > 0) {
						type = type + new String(buf);
					}
					if (type.trim().equals(EPUB.MIMETYPE_EPUB)) {
						return true;
					}
				}
			}
		} catch (IOException e) {
			return false;
		} finally {
			in.close();
		}
		return false;
	}

	/**
	 * Determines the publication version of the root file.
	 *
	 * @param rootFile
	 *            the root file
	 * @return the publication version
	 */
	private PublicationVersion readPublicationVersion(File rootFile) {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			VersionDetector vd = new VersionDetector();
			SAXParser parser = factory.newSAXParser();
			parser.parse(rootFile, vd);
			if (vd.versionString == null) {
				return PublicationVersion.UNKNOWN;
			}
			String[] segments = vd.versionString.split("\\."); //$NON-NLS-1$
			if (segments[0].equals("2") && segments[1].equals("0")) { //$NON-NLS-1$ //$NON-NLS-2$
				return PublicationVersion.V2;
			} else if (segments[0].equals("3") && segments[1].equals("0")) { //$NON-NLS-1$ //$NON-NLS-2$
				return PublicationVersion.V3;
			} else {
				return PublicationVersion.UNKNOWN;
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			return PublicationVersion.UNKNOWN;
		}
	}

	private void log(String message, Severity severity) {
		if (logger != null) {
			logger.log(message, severity);
		}
	}

	/**
	 * Assembles the EPUB file using a temporary working folder. The folder will be deleted as soon as the assembly has
	 * completed.
	 *
	 * @param epubFile
	 *            the target EPUB file
	 * @throws Exception
	 */
	public File pack(File epubFile) throws Exception {
		File workingFolder = File.createTempFile("epub_", null); //$NON-NLS-1$
		if (workingFolder.delete() && workingFolder.mkdirs()) {
			pack(epubFile, workingFolder);
		}
		deleteFolder(workingFolder);
		return workingFolder;
	}

	/**
	 * Assembles the EPUB file using the specified working folder. The contents of the working folder will <b>not</b> be
	 * removed when the operation has completed. If the temporary data is not interesting, use {@link #pack(File)}
	 * instead.
	 *
	 * @param epubFile
	 *            the target EPUB file
	 * @param rootFolder
	 *            the root folder holding all the EPUB contents
	 * @throws Exception
	 * @see {@link #pack(File)}
	 */
	public void pack(File epubFile, File rootFolder) throws Exception {
		if (ocfContainer.getRootfiles().getRootfiles().isEmpty()) {
			throw new ValidationException("EPUB does not contain any publications"); //$NON-NLS-1$
		}
		rootFolder.mkdirs();
		if (rootFolder.isDirectory() || rootFolder.mkdirs()) {
			writeOCF(rootFolder);
			EList<RootFile> publications = ocfContainer.getRootfiles().getRootfiles();
			log(MessageFormat.format(Messages.getString("EPUB.2"), epubFile.getAbsolutePath()), Severity.INFO); //$NON-NLS-1$
			for (RootFile rootFile : publications) {
				Object publication = rootFile.getPublication();
				File root = new File(rootFolder.getAbsolutePath() + File.separator + rootFile.getFullPath());
				if (publication instanceof Publication) {
					((Publication) publication).pack(root);
				} else {
					if (rootFile.getPublication() instanceof File) {
						EPUBFileUtil.copy((File) rootFile.getPublication(), root);
					} else {
						throw new IllegalArgumentException("Unknown publication type in root file"); //$NON-NLS-1$
					}
				}
			}
			EPUBFileUtil.zip(epubFile, rootFolder);
			log(MessageFormat.format(Messages.getString("EPUB.3"), //$NON-NLS-1$
					publications.size()), Severity.INFO);
		} else {
			throw new IOException("Could not create working folder in " + rootFolder.getAbsolutePath()); //$NON-NLS-1$
		}
	}

	/**
	 * Reads the <i>Open Container Format (OCF)</i> formatted list of contents of this EPUB. The result of this
	 * operation is placed in the {@link #ocfContainer} instance.
	 *
	 * @param rootFolder
	 *            the folder where the EPUB was unpacked
	 * @throws IOException
	 * @see {@link #unpack(File)}
	 * @see {@link #unpack(File, File)}
	 * @see <a href="http://idpf.org/epub/30/spec/epub30-ocf.html">EPUB3 OCF specification</a>
	 * @see <a href="http://idpf.org/epub/20/spec/OCF_2.0.1_draft.doc">EPUB2 OCF specification</a>
	 */
	protected void readOCF(File rootFolder) throws IOException {
		// These file names are listed in the OCF specification and must not be
		// changed.
		File metaFolder = new File(rootFolder.getAbsolutePath() + File.separator + "META-INF"); //$NON-NLS-1$
		File containerFile = new File(metaFolder.getAbsolutePath() + File.separator + "container.xml"); //$NON-NLS-1$
		ResourceSet resourceSet = new ResourceSetImpl();
		URI fileURI = URI.createFileURI(containerFile.getAbsolutePath());
		Resource resource = resourceSet.createResource(fileURI);
		resource.load(null);
		ocfContainer = (Container) resource.getContents().get(0);
	}

	/**
	 * Registers a new resource factory for OCF data structures. This is normally done through Eclipse extension points
	 * but we also need to be able to create this factory without the Eclipse runtime.
	 */
	private void registerOCFResourceFactory() {
		// Register package so that it is available even without the Eclipse
		// runtime
		@SuppressWarnings("unused")
		OCFPackage packageInstance = OCFPackage.eINSTANCE;

		// Register the file suffix
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(OCF_FILE_SUFFIX,
				new XMLResourceFactoryImpl() {

					@Override
					public Resource createResource(URI uri) {
						OCFResourceImpl xmiResource = new OCFResourceImpl(uri);
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
						saveOptions.put(XMLResource.OPTION_ENCODING, OCF_FILE_ENCODING);
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
	 * Unpacks the EPUB file to a temporary location and populates the data model with the content.
	 *
	 * @param epubFile
	 *            the EPUB file to unpack
	 * @return the location when the EPUB is unpacked
	 * @throws Exception
	 * @see {@link #unpack(File, File)}
	 */
	public File unpack(File epubFile) throws Exception {
		File workingFolder = File.createTempFile("epub_", null); //$NON-NLS-1$
		workingFolder.deleteOnExit(); // XXX: Avoid using deleteOnExit()
		if (workingFolder.delete() && workingFolder.mkdirs()) {
			unpack(epubFile, workingFolder);
		}
		return workingFolder;
	}

	/**
	 * Unpacks the given EPUB file into the specified destination and populates the data model with the content. Note
	 * that when the destination folder already exists or is empty the file EPUB will not be unpacked or verified, but
	 * the contents of the destination will be treated as an already unpacked EPUB. If this behaviour is not desired one
	 * should take steps to delete the folder prior to unpacking.
	 * <p>
	 * When performing the unpacking, the modification date of the destination folder will be set to the modification
	 * date of the source EPUB. Additionally the contents of the EPUB will retain the original modification date if set.
	 * </p>
	 * <p>
	 * Multiple OPS root files in the publication will populate the OCF container instance with one {@link Publication}
	 * for each as expected. The contents of the data model starting with the OCF container will be replaced. If the
	 * publication is in an unsupported version it will not be added to the data model.
	 * </p>
	 *
	 * @param epubFile
	 *            the EPUB file to unpack
	 * @param rootFolder
	 *            the destination folder
	 * @throws Exception
	 * @see {@link #unpack(File)} when destination is not interesting
	 * @see {@link #getContainer()} to obtain the container instance
	 * @see {@link #getOPSPublications()} to get a list of all contained OPS publications
	 */
	public void unpack(File epubFile, File rootFolder) throws Exception {
		if (!isEPUB(epubFile)) {
			throw new IllegalArgumentException(MessageFormat.format("{0} is not an EPUB file", epubFile)); //$NON-NLS-1$
		}
		if (!rootFolder.exists() || rootFolder.list().length == 0) {
			EPUBFileUtil.unzip(epubFile, rootFolder);
		}
		readOCF(rootFolder);
		EList<RootFile> rootFiles = ocfContainer.getRootfiles().getRootfiles();
		for (RootFile rootFile : rootFiles) {
			if (rootFile.getMediaType().equals(MIMETYPE_OEBPS)) {
				File root = new File(rootFolder.getAbsolutePath() + File.separator + rootFile.getFullPath());
				switch (readPublicationVersion(root)) {
				case V2:
					Publication ops2 = Publication.getVersion2Instance(logger);
					ops2.unpack(root);
					rootFile.setPublication(ops2);
					break;
				case V3:
					Publication ops3 = Publication.getVersion3Instance();
					ops3.unpack(root);
					rootFile.setPublication(ops3);
					break;
				default:
					log(MessageFormat.format("Unsupported OEBPS version in root file {0}", rootFile.getFullPath()), //$NON-NLS-1$
							Severity.WARNING);
					break;
				}
			}
		}
	}

	/**
	 * Creates a new folder named META-INF and writes the required (as per the OPS specification) <b>container.xml</b>
	 * in that folder. This is part of the packing procedure.
	 *
	 * @param rootFolder
	 *            the root folder
	 * @see <a href="http://idpf.org/epub/30/spec/epub30-ocf.html">EPUB3 OCF specification</a>
	 * @see <a href="http://idpf.org/epub/20/spec/OCF_2.0.1_draft.doc">EPUB2 OCF specification</a>
	 */
	private void writeOCF(File rootFolder) throws IOException {
		File metaFolder = new File(rootFolder.getAbsolutePath() + File.separator + "META-INF"); //$NON-NLS-1$
		if (metaFolder.mkdir()) {
			File containerFile = new File(metaFolder.getAbsolutePath() + File.separator + "container.xml"); //$NON-NLS-1$
			ResourceSet resourceSet = new ResourceSetImpl();
			// Register the packages to make it available during loading.
			URI fileURI = URI.createFileURI(containerFile.getAbsolutePath());
			Resource resource = resourceSet.createResource(fileURI);
			resource.getContents().add(ocfContainer);
			resource.save(null);
		}
	}
}
