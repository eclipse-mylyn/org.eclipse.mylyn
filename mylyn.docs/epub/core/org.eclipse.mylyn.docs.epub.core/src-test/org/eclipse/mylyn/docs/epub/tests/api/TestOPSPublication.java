/*******************************************************************************
 * Copyright (c) 2011, 2014 Torkild U. Resheim.
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
package org.eclipse.mylyn.docs.epub.tests.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.FeatureMapUtil.FeatureEList;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;
import org.eclipse.mylyn.docs.epub.core.EPUB;
import org.eclipse.mylyn.docs.epub.core.OPSPublication;
import org.eclipse.mylyn.docs.epub.core.Publication;
import org.eclipse.mylyn.docs.epub.core.ValidationMessage;
import org.eclipse.mylyn.docs.epub.core.ValidationMessage.Severity;
import org.eclipse.mylyn.docs.epub.ncx.Meta;
import org.eclipse.mylyn.docs.epub.ncx.NavPoint;
import org.eclipse.mylyn.docs.epub.ncx.Ncx;
import org.eclipse.mylyn.docs.epub.opf.Item;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests features and regressions specific to the OPS 2.0.1 supporting implementation {@link OPSPublication}.
 *
 * @author Torkild U. Resheim
 */
@SuppressWarnings("nls")
public class TestOPSPublication extends AbstractTest {

	private static final EStructuralFeature TEXT = XMLTypePackage.eINSTANCE.getXMLTypeDocumentRoot_Text();

	private final static String TOCFILE_ID = "3063f615-672e-4083-911a-65c5ff245e75";

	private OPSPublication oebps;

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		oebps = new OPSPublication(logger);
	}

	/**
	 * Test method for {@link org.eclipse.mylyn.docs.epub.core.Publication#setCover(java.io.File, java.lang.String)} .
	 * <ul>
	 * <li>Cover page SVG shall exist in the unpacked folder</li>
	 * <li>Cover page HTML shall exist in the unpacked folder</li>
	 * </ul>
	 *
	 * @throws Exception
	 */
	@Test
	public final void testSetCover() throws Exception {
		oebps.setCover(new File("testdata/drawing.svg"), "Title");
		oebps.addItem(new File("testdata/plain-page.xhtml"));
		epub.add(oebps);
		epub.pack(getEpubFile());

		EPUB epub2 = new EPUB();
		epub2.unpack(getEpubFile(), getEpubFolder());
		Publication oebps = epub2.getOPSPublications().get(0);
		File root = oebps.getRootFolder();
		File svg = new File(root.getAbsolutePath() + File.separator + "drawing.svg");
		Assert.assertTrue(svg.exists());
		File html = new File(root.getAbsolutePath() + File.separator + "cover-page.xhtml");
		Assert.assertTrue(html.exists());
	}

	@SuppressWarnings("rawtypes")
	public String getText(Object element) {
		if (element instanceof NavPoint) {
			FeatureMap fm = ((NavPoint) element).getNavLabels().get(0).getText().getMixed();
			Object o = fm.get(TEXT, false);
			if (o instanceof FeatureEList) {
				if (((FeatureEList) o).size() > 0) {
					return ((FeatureEList) o).get(0).toString();
				}
			}
		}
		if (element instanceof Meta) {
			Object o = ((Meta) element).getContent();
			return o.toString();
		}
		return "null";
	}

	/**
	 * Test method for {@link org.eclipse.mylyn.docs.epub.core.Publication#addMeta(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public final void testAddMeta() {
		oebps.addMeta("name", "value");
		assertEquals("name", oebps.getPackage().getMetadata().getMetas().get(0).getName());
		assertEquals("value", oebps.getPackage().getMetadata().getMetas().get(0).getContent());
		try {
			oebps.addMeta(null, "value");
			fail();
		} catch (IllegalArgumentException e) {
		}
		try {
			oebps.addMeta("name", null);
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	/**
	 * Test method for {@link org.eclipse.mylyn.docs.epub.core.OPSPublication#generateTableOfContents()} .
	 * <ul>
	 * <li>Table of contents shall be generated from content per default.</li>
	 * </ul>
	 *
	 * @throws Exception
	 */
	@Test
	public final void testGenerateTableOfContents() throws Exception {
		epub.add(oebps);
		oebps.addItem(new File("testdata/plain-page.xhtml"));
		epub.pack(getEpubFile());
		assertTrue(oebps.getTableOfContents() != null);
		assertTrue(oebps.getTableOfContents() instanceof Ncx);
		Ncx ncx = (Ncx) oebps.getTableOfContents();
		NavPoint h1_1 = ncx.getNavMap().getNavPoints().get(0);
		NavPoint h1_2 = ncx.getNavMap().getNavPoints().get(1);
		assertEquals("First item", getText(h1_1));
		assertEquals("Second item", getText(h1_2));
		getEpubFile().delete();
	}

	/**
	 * Test method for {@link org.eclipse.mylyn.docs.epub.core.OPSPublication#generateTableOfContents()} .
	 * <ul>
	 * <li>Table of contents shall exist but be empty if not otherwise specified.</li>
	 * </ul>
	 *
	 * @throws Exception
	 */
	@Test
	public final void testGenerateEmptyTableOfContents() throws Exception {
		epub.add(oebps);
		oebps.addItem(new File("testdata/plain-page.xhtml"));
		oebps.setGenerateToc(false);
		epub.pack(getEpubFile());
		assertTrue(oebps.getTableOfContents() != null);
		assertTrue(oebps.getTableOfContents() instanceof Ncx);
	}

	/**
	 * Test method for {@link org.eclipse.mylyn.docs.epub.core.OPSPublication#getTableOfContents()} .
	 * <ul>
	 * <li>There shall be a table of contents, even if empty.</li>
	 * </ul>
	 *
	 * @throws Exception
	 */
	@Test
	public final void testGetTableOfContents() throws Exception {
		epub.add(oebps);
		assertTrue(oebps.getTableOfContents() != null);
		assertTrue(oebps.getTableOfContents() instanceof Ncx);
	}

	/**
	 * Test method for {@link org.eclipse.mylyn.docs.epub.core.OPSPublication#readTableOfContents(java.io.File)} .
	 *
	 * @throws Exception
	 */
	@Test
	public final void testReadTableOfContents() throws Exception {
		epub.add(oebps);
		oebps.addItem(new File("testdata/plain-page.xhtml"));
		epub.pack(getEpubFile());

		EPUB epub_in = new EPUB();
		epub_in.unpack(getEpubFile(), getEpubFolder());
		Publication oebps_in = epub_in.getOPSPublications().get(0);
		assertTrue(oebps_in.getTableOfContents() != null);
		assertTrue(oebps_in.getTableOfContents() instanceof Ncx);
		Ncx ncx = (Ncx) oebps_in.getTableOfContents();
		NavPoint h1_1 = ncx.getNavMap().getNavPoints().get(0);
		NavPoint h1_2 = ncx.getNavMap().getNavPoints().get(1);
		assertEquals("First item", getText(h1_1));
		assertEquals("Second item", getText(h1_2));
	}

	/**
	 * Test method for {@link org.eclipse.mylyn.docs.epub.core.OPSPublication#setTableOfContents(java.io.File)} .
	 *
	 * @throws Exception
	 */
	@Test
	public final void testSetTableOfContents() throws Exception {
		oebps.setTableOfContents(new File("testdata/toc.ncx"));
		epub.add(oebps);
		oebps.addItem(new File("testdata/plain-page.xhtml"));
		epub.pack(getEpubFile());
		EPUB epub_in = new EPUB();
		epub_in.unpack(getEpubFile(), getEpubFolder());
		Publication oebps_in = epub_in.getOPSPublications().get(0);
		assertTrue(oebps_in.getTableOfContents() != null);
		assertTrue(oebps_in.getTableOfContents() instanceof Ncx);
		Ncx ncx = (Ncx) oebps_in.getTableOfContents();
		NavPoint h1_1 = ncx.getNavMap().getNavPoints().get(0);
		NavPoint h1_2 = ncx.getNavMap().getNavPoints().get(1);
		assertEquals("First item", getText(h1_1));
		assertEquals("Second item", getText(h1_2));
		Meta meta = ncx.getHead().getMetas().get(0);
		String id = getText(meta);
		// The UUID for the NCX file should be different if it comes from
		// another NCX than the one specified.
		assertTrue(TOCFILE_ID.equals(id));

	}

	/**
	 * Test method for {@link org.eclipse.mylyn.docs.epub.core.OPSPublication#validateContents()} .
	 * <ul>
	 * <li>There shall be a warning message</li>
	 * </ul>
	 *
	 * @throws Exception
	 */
	@Test
	public final void testValidateContents() throws Exception {
		epub.add(oebps);
		oebps.addItem(new File("testdata/plain-page_warnings.xhtml"));
		epub.pack(getEpubFile());
		assertEquals(1, oebps.getValidationMessages().size());
		ValidationMessage msg = oebps.getValidationMessages().get(0);
		assertEquals(Severity.WARNING, msg.getSeverity());
		assertTrue(msg.getMessage().startsWith("Element \"bad\""));
	}

	/**
	 * Test method for <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=379052">bug 379052</a>: OPS validator
	 * should handle all XHTML in the manifest
	 * <p>
	 * The test will add one XHTML file that links to another XHTML file that should be validated and one that should
	 * not
	 * </p>
	 *
	 * @throws Exception
	 */
	@Test
	public final void test_Bug379052() throws Exception {
		oebps.setIncludeReferencedResources(true);
		epub.add(oebps);
		oebps.addItem(new File("testdata/OPF-Tests/Bug_379052/chapter-1.xhtml"));
		epub.pack(getEpubFile());
		// Two XHTML files, one with a warning. One CSS file and the NCX.
		assertEquals(4, oebps.getPackage().getManifest().getItems().size());
		// Should be exactly two warning.
		assertEquals(1, oebps.getValidationMessages().size());
		getEpubFile().delete();
	}

	/**
	 * Test method for <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=358671">bug 358671</a>: Add support for
	 * fallback items
	 * <p>
	 * This method tests for the exception that shall be raised when an illegal item has been added.
	 * </p>
	 *
	 * @throws Exception
	 */
	@Test
	public final void test_Bug358671_Illegal_Item() throws Exception {
		epub.add(oebps);
		oebps.addItem(new File("testdata/OPF-Tests/Bug_358671/illegal-type.html"));
		epub.pack(getEpubFile());
		ValidationMessage msg = oebps.getValidationMessages().get(0);
		assertEquals(Severity.WARNING, msg.getSeverity());
		assertEquals(true, msg.getMessage()
				.equals("Item \"illegal-type.html\" is not a core media type and does not specify a fallback item."));
	}

	/**
	 * Test method for <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=358671">bug 358671</a>: Add support for
	 * fallback items
	 * <p>
	 * This method tests for the exception that shall be raised when an illegal item has been added with an illegal
	 * fallback item. Which fallback items that are allowed is specified by the OPS version.
	 * </p>
	 *
	 * @throws Exception
	 */
	@Test
	public final void test_Bug358671_Illegal_Fallback() throws Exception {
		epub.add(oebps);
		Item item = oebps.addItem(new File("testdata/OPF-Tests/Bug_358671/illegal-type.html"));
		item.setFallback("fallback");
		oebps.addItem("fallback", null, new File("testdata/OPF-Tests/Bug_358671/illegal-type.html"), null, null, true,
				true, false);
		epub.pack(getEpubFile());
		ValidationMessage msg = oebps.getValidationMessages().get(0);
		assertEquals(Severity.WARNING, msg.getSeverity());
		assertEquals(true, msg.getMessage()
				.equals("Item \"illegal-type.html\" is not a core media type and specifies a non-core media fallback item."));
	}

	/**
	 * Test method for <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=358671">bug 358671</a>: Add support for
	 * fallback items
	 * <p>
	 * This method tests for the warning that shall be issued when an illegal item has been added with a legal fallback
	 * item.
	 * </p>
	 *
	 * @throws Exception
	 */
	@Test
	public final void test_Bug358671_Legal_Fallback() throws Exception {
		epub.add(oebps);
		Item item = oebps.addItem(new File("testdata/OPF-Tests/Bug_358671/illegal-type.html"));
		item.setFallback("fallback");
		oebps.addItem("fallback", null, new File("testdata/plain-page.xhtml"), null, null, true, true, false);
		epub.pack(getEpubFile());
		assertEquals(3, oebps.getPackage().getManifest().getItems().size());
		assertEquals(1, oebps.getValidationMessages().size());
		ValidationMessage msg = oebps.getValidationMessages().get(0);
		assertEquals(Severity.WARNING, msg.getSeverity());
		assertEquals(true, msg.getMessage()
				.equals("Item \"illegal-type.html\" is not a core media type but a legal fallback item has been specified."));
	}

	private class EPUB_NCX_Test extends OPSPublication {
		public void testReadOCF(File tocFile) throws IOException {
			readTableOfContents(tocFile);
		}
	}

	/**
	 * See if the NCX file generated by this tooling can be read.
	 *
	 * @throws Exception
	 */
	@Test
	public final void testReadNCX_Dogfood() throws Exception {
		File workingFolder = new File("testdata/NCX-Tests/Dogfood/toc.ncx");
		EPUB_NCX_Test epub = new EPUB_NCX_Test();
		epub.testReadOCF(workingFolder);
	}

	/**
	 * This case was discovered when testing an EPUB file generated by DocBook Reading the NCX fails with a
	 * java.net.SocketException: Unexpected end of file from server. On closer inspection we can see that the file is
	 * declared as XHTML (which it of course is not). This is probably due to an issue in DocBook XSL 1.76.1
	 *
	 * @see http://sourceforge.net/tracker/index.php?func=detail&aid=3353537&group_id=21935&atid=373747.
	 * @throws Exception
	 */
	@Test
	public final void testReadNCX_SocketException() throws Exception {
		File workingFolder = new File("testdata/NCX-Tests/SocketException/toc.ncx");
		EPUB_NCX_Test epub = new EPUB_NCX_Test();
		epub.testReadOCF(workingFolder);
	}

}
