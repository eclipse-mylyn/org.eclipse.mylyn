/*******************************************************************************
 * Copyright (c) 2011 Torkild U. Resheim.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Torkild U. Resheim - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.docs.epub.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Assert;

import org.eclipse.mylyn.docs.epub.core.EPUB;
import org.eclipse.mylyn.docs.epub.core.OPSPublication;
import org.eclipse.mylyn.docs.epub.opf.Role;
import org.eclipse.mylyn.docs.epub.opf.Type;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.adobe.epubcheck.api.EpubCheck;

/**
 * 
 * @author Torkild U. Resheim
 * 
 */
public class TestAPI {

	private File testRoot;

	private File packingFolder;

	private void delete(File f) throws IOException {
		if (f.isDirectory()) {
			for (File c : f.listFiles())
				delete(c);
		}

		if (f.exists() && !f.delete())
			throw new FileNotFoundException("Failed to delete file: " + f);
	}

	private boolean fileExists(String filename) {
		String path = packingFolder.getAbsolutePath() + File.separator
				+ "OEBPS" + File.separator + filename;
		File file = new File(path);
		return file.exists();
	}

	private File getFile(String path) throws URISyntaxException {
		return new File(path);
	}

	/**
	 * Reads the OPF into a DOM for further analysis.
	 * 
	 * @return the DOM document element
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private Element readOPF() throws ParserConfigurationException,
			SAXException, IOException {
		File fXmlFile = new File(packingFolder.getAbsolutePath()
				+ File.separator + "OEBPS" + File.separator + "content.opf");
		Assert.assertEquals(true, fXmlFile.exists());
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();
		return doc.getDocumentElement();
	}

	@Before
	public void setUp() throws Exception {
		packingFolder = getFile("test/api/pack");
		testRoot = getFile("test/api/");
		delete(testRoot);
	}

	@After
	public void tearDown() {

	}

	/**
	 * Tests node attributes.
	 * 
	 * @param node
	 *            the node to test
	 * @param ids
	 *            expected identifiers
	 * @param values
	 *            expected values
	 */
	private void testAttributes(Node node, String[] ids, String[] values) {
		Assert.assertEquals(ids.length, values.length);
		Assert.assertEquals(
				"Wrong number of attributes in '" + node.getNodeName() + "'",
				ids.length, node.getAttributes().getLength());
		for (int x = 0; x < ids.length; x++) {
			Assert.assertEquals("No such node '" + ids[x] + "'", true,
					node.getAttributes().getNamedItem(ids[x]) != null);
			Assert.assertEquals(values[x],
					node.getAttributes().getNamedItem(ids[x]).getNodeValue());
		}
	}

	/**
	 * Creates a new simple EPUB and tests whether it can be validated using
	 * "epubcheck".
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPackSimpleEPUB() throws Exception {
		File epubfile = new File(testRoot.getAbsolutePath() + File.separator + "simple.epub");
		createSimpleEPUB(epubfile);
		EpubCheck checker = new EpubCheck(epubfile);
		Assert.assertTrue(checker.validate());
	}

	/**
	 * Creates a very simple EPUB file with only required metadata added and one
	 * single page.
	 * 
	 * @return the new EPUB file
	 * @throws URISyntaxException
	 * @throws Exception
	 */
	private OPSPublication createSimpleEPUB(File epubfile) throws URISyntaxException, Exception {
		OPSPublication ops = OPSPublication.getVersion2Instance();
		ops.addTitle(null, null, "Mylyn Docs Test EPUB");
		ops.addSubject(null, null, "Testing");
		ops.addItem(getFile("testdata/plain-page.xhtml"));
		ops.setGenerateToc(true);
		EPUB epub = new EPUB();
		epub.add(ops);
		epub.pack(epubfile, packingFolder);
		return ops;
	}

	/**
	 * See if all contributors are serialised properly.
	 * <ul>
	 * <li>Wrong number of attributes due to EMF default value handling.</li>
	 * <li>Unexpected attribute names and or values.</li>
	 * <li>Wrong element value.</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSerializationContributors() throws Exception {
		OPSPublication ops = OPSPublication.getVersion2Instance();
		File epubfile = new File(testRoot.getAbsolutePath() + File.separator + "simple.epub");
		Role[] roles = Role.values();
		for (Role role : roles) {
			ops.addContributor(role.getLiteral(), Locale.ENGLISH,
					"Nomen Nescio", role, "Nescio, Nomen");
		}
		EPUB epub = new EPUB();
		epub.add(ops);
		ops.addItem(new File("testdata/plain-page.xhtml"));
		epub.pack(epubfile, packingFolder);
		Element doc = readOPF();
		Node guide = doc.getElementsByTagName("opf:metadata").item(0);
		Node node = guide.getFirstChild(); // Discard first TEXT node
		node = discard(node); // title
		node = discard(node); // subject
		String[] ids = new String[] { "id", "xml:lang", "opf:role",
				"opf:file-as" };
		for (Role role : roles) {
			node = node.getNextSibling();
			String[] values = new String[] { role.getLiteral(),
					Locale.ENGLISH.getLanguage(),
					role.getLiteral(), "Nescio, Nomen" };
			assertNode(node, "dc:contributor", "Nomen Nescio");
			testAttributes(node, ids, values);
			node = node.getNextSibling(); // Discard next TEXT node
		}
	}

	private Node discard(Node node) {
		node = node.getNextSibling(); // Discard element node
		node = node.getNextSibling(); // Discard TEXT node
		return node;
	}

	private void assertNode(Node node, String name, String value) {
		Assert.assertEquals(name, node.getNodeName());
		if (value != null) {
			Assert.assertEquals(value, node.getFirstChild().getNodeValue());
		}
	}


	/**
	 * Checks the contents of the OPF when nothing has been added to the
	 * publication.
	 * <ul>
	 * <li>There should be a table of contents</li>
	 * <li>The metadata, manifest, spine and guide elements should exist.</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSerializationEmpty() throws Exception {
		OPSPublication ops = OPSPublication.getVersion2Instance();
		File epubfile = new File(testRoot.getAbsolutePath() + File.separator + "simple.epub");
		EPUB epub = new EPUB();
		epub.add(ops);
		ops.addItem(new File("testdata/plain-page.xhtml"));
		epub.pack(epubfile, packingFolder);
		Element doc = readOPF();
		Assert.assertEquals("opf:package", doc.getNodeName());
		NodeList nl = doc.getChildNodes();
		// Text nodes in between
		Assert.assertEquals("opf:metadata", nl.item(1).getNodeName());
		Assert.assertEquals("opf:manifest", nl.item(3).getNodeName());
		Assert.assertEquals("opf:spine", nl.item(5).getNodeName());
		Assert.assertEquals("opf:guide", nl.item(7).getNodeName());

		// Table of contents
		Node toc = nl.item(3).getFirstChild().getNextSibling();
		Assert.assertEquals("opf:item", toc.getNodeName());
		Assert.assertEquals("ncx", toc.getAttributes().getNamedItem("id")
				.getNodeValue());
		Assert.assertEquals("toc.ncx", toc.getAttributes().getNamedItem("href")
				.getNodeValue());
		Assert.assertEquals("application/x-dtbncx+xml", toc.getAttributes()
				.getNamedItem("media-type")
				.getNodeValue());
		Assert.assertEquals(true, fileExists("toc.ncx"));
		Node spine = nl.item(5);
		Assert.assertEquals("ncx", spine.getAttributes().getNamedItem("toc")
				.getNodeValue());
		doc = null;
	}

	/**
	 * See if all references are serialised properly.
	 * <ul>
	 * <li>Wrong number of attributes due to EMF default value handling.</li>
	 * <li>Unexpected attribute names and or values</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSerializationReferences() throws Exception {
		OPSPublication ops = OPSPublication.getVersion2Instance();
		File epubfile = new File(testRoot.getAbsolutePath() + File.separator + "simple.epub");
		Type[] types = Type.values();
		for (Type type : types) {
			ops.addReference(type.getLiteral() + ".xhtml", type.getName(),
					type);
		}
		EPUB epub = new EPUB();
		ops.addItem(new File("testdata/plain-page.xhtml"));
		epub.add(ops);
		epub.pack(epubfile, packingFolder);
		Element doc = readOPF();
		Node guide = doc.getElementsByTagName("opf:guide").item(0);
		Node ref = guide.getFirstChild(); // Discard first TEXT node
		String[] ids = new String[] { "title", "href", "type" };
		for (Type type : types) {
			ref = ref.getNextSibling();
			// The should be exactly three attributes
			Assert.assertEquals(
					"Wrong number of attributes in '" + type.getLiteral() + "'",
					3,
					ref.getAttributes().getLength());
			String[] values = new String[] { type.getName(),
					type.getLiteral() + ".xhtml", type.getLiteral() };
			testAttributes(ref, ids, values);
			ref = ref.getNextSibling();
		}
		doc = null;
	}

}
