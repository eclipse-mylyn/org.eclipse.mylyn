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
package org.eclipse.mylyn.docs.epub.tests.api;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.FeatureMapUtil.FeatureEList;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;
import org.eclipse.mylyn.docs.epub.core.EPUB;
import org.eclipse.mylyn.docs.epub.core.OPS2Publication;
import org.eclipse.mylyn.docs.epub.core.OPSPublication;
import org.eclipse.mylyn.docs.epub.core.ValidationMessage;
import org.eclipse.mylyn.docs.epub.core.ValidationMessage.Severity;
import org.eclipse.mylyn.docs.epub.ncx.Meta;
import org.eclipse.mylyn.docs.epub.ncx.NavPoint;
import org.eclipse.mylyn.docs.epub.ncx.Ncx;
import org.eclipse.mylyn.docs.epub.opf.Item;
import org.junit.Test;

/**
 * Tests features and regressions specific to the OPS 2.0.1 supporting implementation {@link OPS2Publication}.
 * 
 * @author Torkild U. Resheim
 */
@SuppressWarnings("nls")
public class TestOPS2Publication extends AbstractTest {

	private static final EStructuralFeature TEXT = XMLTypePackage.eINSTANCE.getXMLTypeDocumentRoot_Text();

	private final static String TOCFILE_ID = "3063f615-672e-4083-911a-65c5ff245e75";

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
	 * Test method for {@link org.eclipse.mylyn.docs.epub.core.OPS2Publication#generateTableOfContents()} .
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
		epub.pack(epubFile);
		Assert.assertTrue(oebps.getTableOfContents() != null);
		Assert.assertTrue(oebps.getTableOfContents() instanceof Ncx);
		Ncx ncx = (Ncx) oebps.getTableOfContents();
		NavPoint h1_1 = ncx.getNavMap().getNavPoints().get(0);
		NavPoint h1_2 = ncx.getNavMap().getNavPoints().get(1);
		Assert.assertEquals("First item", getText(h1_1));
		Assert.assertEquals("Second item", getText(h1_2));
		epubFile.delete();
	}

	/**
	 * Test method for {@link org.eclipse.mylyn.docs.epub.core.OPS2Publication#generateTableOfContents()} .
	 * <ul>
	 * <li>Table of contents shall exist but be empty if not otherwise specified.</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	public final void testGenerateEmptyTableOfContents() throws Exception {
		epub.add(oebps);
		oebps.addItem(new File("testdata/plain-page.xhtml"));
		oebps.setGenerateToc(false);
		epub.pack(epubFile);
		Assert.assertTrue(oebps.getTableOfContents() != null);
		Assert.assertTrue(oebps.getTableOfContents() instanceof Ncx);
	}

	/**
	 * Test method for {@link org.eclipse.mylyn.docs.epub.core.OPS2Publication#getTableOfContents()} .
	 * <ul>
	 * <li>There shall be a table of contents, even if empty.</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testGetTableOfContents() throws Exception {
		epub.add(oebps);
		Assert.assertTrue(oebps.getTableOfContents() != null);
		Assert.assertTrue(oebps.getTableOfContents() instanceof Ncx);
	}

	/**
	 * Test method for {@link org.eclipse.mylyn.docs.epub.core.OPS2Publication#readTableOfContents(java.io.File)} .
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testReadTableOfContents() throws Exception {
		epub.add(oebps);
		oebps.addItem(new File("testdata/plain-page.xhtml"));
		epub.pack(epubFile);

		EPUB epub_in = new EPUB();
		epub_in.unpack(epubFile, epubFolder);
		OPSPublication oebps_in = epub_in.getOPSPublications().get(0);
		Assert.assertTrue(oebps_in.getTableOfContents() != null);
		Assert.assertTrue(oebps_in.getTableOfContents() instanceof Ncx);
		Ncx ncx = (Ncx) oebps_in.getTableOfContents();
		NavPoint h1_1 = ncx.getNavMap().getNavPoints().get(0);
		NavPoint h1_2 = ncx.getNavMap().getNavPoints().get(1);
		Assert.assertEquals("First item", getText(h1_1));
		Assert.assertEquals("Second item", getText(h1_2));
	}

	/**
	 * Test method for {@link org.eclipse.mylyn.docs.epub.core.OPS2Publication#setTableOfContents(java.io.File)} .
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testSetTableOfContents() throws Exception {
		oebps.setTableOfContents(new File("testdata/toc.ncx"));
		epub.add(oebps);
		oebps.addItem(new File("testdata/plain-page.xhtml"));
		epub.pack(epubFile);
		EPUB epub_in = new EPUB();
		epub_in.unpack(epubFile, epubFolder);
		OPSPublication oebps_in = epub_in.getOPSPublications().get(0);
		Assert.assertTrue(oebps_in.getTableOfContents() != null);
		Assert.assertTrue(oebps_in.getTableOfContents() instanceof Ncx);
		Ncx ncx = (Ncx) oebps_in.getTableOfContents();
		NavPoint h1_1 = ncx.getNavMap().getNavPoints().get(0);
		NavPoint h1_2 = ncx.getNavMap().getNavPoints().get(1);
		Assert.assertEquals("First item", getText(h1_1));
		Assert.assertEquals("Second item", getText(h1_2));
		Meta meta = ncx.getHead().getMetas().get(0);
		String id = getText(meta);
		// The UUID for the NCX file should be different if it comes from
		// another NCX than the one specified.
		Assert.assertTrue(TOCFILE_ID.equals(id));

	}

	/**
	 * Test method for {@link org.eclipse.mylyn.docs.epub.core.OPS2Publication#validateContents()} .
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
		epub.pack(epubFile);
		Assert.assertEquals(1, oebps.getValidationMessages().size());
		ValidationMessage msg = oebps.getValidationMessages().get(0);
		Assert.assertEquals(Severity.WARNING, msg.getSeverity());
		Assert.assertTrue(msg.getMessage().startsWith("Element \"bad\""));
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
		epub.pack(epubFile);
		// Two XHTML files, one with a warning. One CSS file and the NCX.
		Assert.assertEquals(4, oebps.getOpfPackage().getManifest().getItems().size());
		// Should be exactly two warning.
		Assert.assertEquals(1, oebps.getValidationMessages().size());
		epubFile.delete();
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
		epub.pack(epubFile);
		ValidationMessage msg = oebps.getValidationMessages().get(0);
		Assert.assertEquals(Severity.WARNING, msg.getSeverity());
		Assert.assertEquals(
				true,
				msg.getMessage().equals(
						"Item \"illegal-type.html\" is not a core media type and does not specify a fallback item."));
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
		epub.pack(epubFile);
		ValidationMessage msg = oebps.getValidationMessages().get(0);
		Assert.assertEquals(Severity.WARNING, msg.getSeverity());
		Assert.assertEquals(
				true,
				msg.getMessage()
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
		epub.pack(epubFile);
		Assert.assertEquals(3, oebps.getOpfPackage().getManifest().getItems().size());
		Assert.assertEquals(1, oebps.getValidationMessages().size());
		ValidationMessage msg = oebps.getValidationMessages().get(0);
		Assert.assertEquals(Severity.WARNING, msg.getSeverity());
		Assert.assertEquals(
				true,
				msg.getMessage()
						.equals("Item \"illegal-type.html\" is not a core media type but a legal fallback item has been specified."));
	}

	private class EPUB_NCX_Test extends OPS2Publication {
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
