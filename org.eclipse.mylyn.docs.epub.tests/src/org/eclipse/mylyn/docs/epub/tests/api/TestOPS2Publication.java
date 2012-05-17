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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Torkild U. Resheim
 */
@SuppressWarnings("nls")
public class TestOPS2Publication extends AbstractTest {

	private static final EStructuralFeature TEXT = XMLTypePackage.eINSTANCE.getXMLTypeDocumentRoot_Text();

	private final static String TOCFILE_ID = "3063f615-672e-4083-911a-65c5ff245e75";

	private final File epubFile = new File("test" + File.separator + "test.epub");

	private final File epubFolder = new File("test" + File.separator + "epub");

	private boolean deleteFolder(File folder) {
		if (folder.isDirectory()) {
			String[] children = folder.list();
			for (String element : children) {
				boolean ok = deleteFolder(new File(folder, element));
				if (!ok) {
					return false;
				}
			}
		}
		return folder.delete();
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
	 * @throws java.lang.Exception
	 */
	@Override
	@Before
	public void setUp() throws Exception {
		if (epubFile.exists()) {
			epubFile.delete();
		}
		if (epubFolder.exists()) {
			deleteFolder(epubFolder);
		}
		epubFolder.mkdirs();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Override
	@After
	public void tearDown() throws Exception {
		if (epubFolder.exists()) {
			deleteFolder(epubFolder);
		}
		if (epubFile.exists()) {
			epubFile.delete();
		}
	}

	/**
	 * Test method for {@link org.eclipse.mylyn.docs.epub.core.OPS2Publication#generateTableOfContents()} .
	 * <ul>
	 * <li>Table of contents shall be generated from content per default.</li>
	 * <li>Table of contents shall exist but be empty if not otherwise specified.</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testGenerateTableOfContents() throws Exception {
		EPUB epub1 = new EPUB();
		OPSPublication oebps1 = new OPS2Publication();
		epub1.add(oebps1);
		oebps1.addItem(new File("testdata/plain-page.xhtml"));
		epub1.pack(epubFile);
		Assert.assertTrue(oebps1.getTableOfContents() != null);
		Assert.assertTrue(oebps1.getTableOfContents() instanceof Ncx);
		Ncx ncx = (Ncx) oebps1.getTableOfContents();
		NavPoint h1_1 = ncx.getNavMap().getNavPoints().get(0);
		NavPoint h1_2 = ncx.getNavMap().getNavPoints().get(1);
		Assert.assertEquals("First item", getText(h1_1));
		Assert.assertEquals("Second item", getText(h1_2));
		epubFile.delete();

		EPUB epub2 = new EPUB();
		OPSPublication oebps2 = new OPS2Publication();
		epub2.add(oebps2);
		oebps2.addItem(new File("testdata/plain-page.xhtml"));
		oebps2.setGenerateToc(false);
		epub2.pack(epubFile);
		Assert.assertTrue(oebps2.getTableOfContents() != null);
		Assert.assertTrue(oebps2.getTableOfContents() instanceof Ncx);
		Ncx ncx2 = (Ncx) oebps2.getTableOfContents();
		Assert.assertEquals(null, ncx2.getNavMap());
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
		EPUB epub = new EPUB();
		OPSPublication oebps = new OPS2Publication();
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
		EPUB epub_out = new EPUB();
		OPSPublication oebps_out = new OPS2Publication();
		epub_out.add(oebps_out);
		oebps_out.addItem(new File("testdata/plain-page.xhtml"));
		epub_out.pack(epubFile);

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
		EPUB epub_out = new EPUB();
		OPSPublication oebps_out = new OPS2Publication();
		oebps_out.setTableOfContents(new File("testdata/toc.ncx"));
		epub_out.add(oebps_out);
		oebps_out.addItem(new File("testdata/plain-page.xhtml"));
		epub_out.pack(epubFile);
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
		EPUB epub_out = new EPUB();
		OPSPublication oebps_out = new OPS2Publication();
		epub_out.add(oebps_out);
		oebps_out.addItem(new File("testdata/plain-page_warnings.xhtml"));
		epub_out.pack(epubFile);
		Assert.assertEquals(1, oebps_out.getValidationMessages().size());
		ValidationMessage msg = oebps_out.getValidationMessages().get(0);
		Assert.assertEquals(Severity.WARNING, msg.getSeverity());
		Assert.assertEquals("Element \"bad\" is not in OPS Preferred Vocabularies.", msg.getMessage());
	}

	/**
	 * Test method for <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=379052">bug 379052</a>: OPS validator
	 * should handle all XHTML in the manifest
	 * 
	 * @throws Exception
	 */
	@Test
	public final void test_Bug379052() throws Exception {
		EPUB epub_out = new EPUB();
		OPSPublication oebps_out = new OPS2Publication();
		oebps_out.setIncludeReferencedResources(true);
		epub_out.add(oebps_out);
		oebps_out.addItem(new File("testdata/OPF-Tests/Bug_379052/link_warnings.xhtml"));
		epub_out.pack(epubFile);
		// Two XHTML files, one with a warning. One HTML file and the NCX.
		Assert.assertEquals(4, oebps_out.getOpfPackage().getManifest().getItems().size());
		// Should be exactly one warning.
		Assert.assertEquals(1, oebps_out.getValidationMessages().size());
		ValidationMessage msg = oebps_out.getValidationMessages().get(0);
		Assert.assertEquals(Severity.WARNING, msg.getSeverity());
		Assert.assertEquals("Element \"bad\" is not in OPS Preferred Vocabularies.", msg.getMessage());
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
