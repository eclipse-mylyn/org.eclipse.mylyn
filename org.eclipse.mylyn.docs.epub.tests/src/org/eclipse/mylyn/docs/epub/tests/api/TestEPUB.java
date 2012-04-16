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

import org.eclipse.emf.common.util.EList;
import org.eclipse.mylyn.docs.epub.core.EPUB;
import org.eclipse.mylyn.docs.epub.core.OPS2Publication;
import org.eclipse.mylyn.docs.epub.core.OPSPublication;
import org.eclipse.mylyn.docs.epub.ocf.Container;
import org.eclipse.mylyn.docs.epub.ocf.RootFile;
import org.eclipse.mylyn.docs.epub.ocf.RootFiles;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Torkild U. Resheim
 */
@SuppressWarnings("nls")
public class TestEPUB extends AbstractTest {

	private final File epubFile = new File("test" + File.separator + "test.epub");

	private final File epubFolder = new File("test" + File.separator + "epub");

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
	 * Test method for {@link org.eclipse.mylyn.docs.epub.core.EPUB#EPUB()}.
	 * <p>
	 * It must be possible to obtain the publication list which must be empty.
	 * </p>
	 */
	@Test
	public final void testEPUB() {
		EPUB epub = new EPUB();
		Assert.assertEquals(true, epub.getOPSPublications().isEmpty());

	}

	/**
	 * Test method for {@link org.eclipse.mylyn.docs.epub.core.EPUB#add(java.io.File, java.lang.String)} .
	 * <ul>
	 * <li>Publication MIME-type shall be correct</li>
	 * <li>Rootfile path shall be correct</li>
	 * <li>Rootfile object shall be correct.</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testAddFileString() throws Exception {
		EPUB epub = new EPUB();
		File drawing = new File("testdata/drawing-100x100.svg");
		epub.add(drawing, "image/svg+xml");
		Container container = epub.getContainer();
		RootFiles rootfiles = container.getRootfiles();
		EList<RootFile> files = rootfiles.getRootfiles();
		Assert.assertEquals(true, files.get(0).getFullPath().equals("SVG+XML/drawing-100x100.svg"));
		Assert.assertEquals(true, files.get(0).getMediaType().equals("image/svg+xml"));
		Assert.assertEquals(true, files.get(0).getPublication() == drawing);
	}

	/**
	 * Test method for {@link org.eclipse.mylyn.docs.epub.core.EPUB#pack(java.io.File)}.
	 * <ul>
	 * <li>Shall throw exception when unknown publication type is added.</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testPackFail() throws Exception {
		EPUB epub = new EPUB();
		File drawing = new File("testdata/drawing-100x100.svg");
		epub.add(drawing, "image/svg+xml");
		Container container = epub.getContainer();
		RootFiles rootfiles = container.getRootfiles();
		EList<RootFile> files = rootfiles.getRootfiles();
		files.get(0).setPublication(null);
		try {
			epub.pack(epubFile);
			fail();
		} catch (Exception e) {
		}
	}

	/**
	 * Test method for
	 * {@link org.eclipse.mylyn.docs.epub.core.EPUB#add(org.eclipse.mylyn.docs.epub.core.OPSPublication)} .
	 * <ul>
	 * <li>Container shall hold more than one OPS publication</li>
	 * <li>OPS structures shall follow naming conventions.</li>
	 * <li>OPS MIME-type shall be correct</li>
	 * <li>Rootfile object shall be correct.</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testAddOPSPublication() throws Exception {
		EPUB epub = new EPUB();
		OPSPublication oebps1 = new OPS2Publication();
		OPSPublication oebps2 = new OPS2Publication();
		epub.add(oebps1);
		epub.add(oebps2);
		Container container = epub.getContainer();
		RootFiles rootfiles = container.getRootfiles();
		EList<RootFile> files = rootfiles.getRootfiles();
		Assert.assertEquals(true, files.get(0).getFullPath().equals("OEBPS/content.opf"));
		Assert.assertEquals(true, files.get(1).getFullPath().equals("OEBPS_1/content.opf"));
		Assert.assertEquals(true, files.get(0).getMediaType().equals("application/oebps-package+xml"));
		Assert.assertEquals(true, files.get(1).getMediaType().equals("application/oebps-package+xml"));
		Assert.assertEquals(true, files.get(0).getPublication() == oebps1);
		Assert.assertEquals(true, files.get(1).getPublication() == oebps2);
	}

	/**
	 * Test method for {@link org.eclipse.mylyn.docs.epub.core.EPUB#getOPSPublications()}.
	 * <p>
	 * One OPS-publication and one SVG drawing are added. Only the OPS-publication shall be returned.
	 * </p>
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testGetOPSPublications() throws Exception {
		EPUB epub = new EPUB();
		OPSPublication oebps = new OPS2Publication();
		epub.add(oebps);
		File drawing = new File("testdata/drawing-100x100.svg");
		epub.add(drawing, "image/svg+xml");
		Assert.assertEquals(1, epub.getOPSPublications().size());
	}

	/**
	 * Test method for {@link org.eclipse.mylyn.docs.epub.core.EPUB#pack(java.io.File)}.
	 * <ul>
	 * <li>Temporary folder shall not exist when job is done.</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testPackFile() throws Exception {
		EPUB epub = new EPUB();
		OPSPublication oebps = new OPS2Publication();
		oebps.addItem(new File("testdata/plain-page.xhtml"));
		epub.add(oebps);
		File drawing = new File("testdata/drawing-100x100.svg");
		epub.add(drawing, "image/svg+xml");
		File tempFolder = epub.pack(epubFile);
		Assert.assertEquals(false, tempFolder.exists());
	}

	/**
	 * Test method for {@link org.eclipse.mylyn.docs.epub.core.EPUB#pack(java.io.File, java.io.File)} .
	 * <ul>
	 * <li>Work folder shall exist when job is done.</li>
	 * <li>Work folder shall contain EPUB artifacts.</li>
	 * <li>Exception shall be thrown if working folder already exist.</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testPackFileFile() throws Exception {
		EPUB epub = new EPUB();
		OPSPublication oebps = new OPS2Publication();
		oebps.addItem(new File("testdata/plain-page.xhtml"));
		epub.add(oebps);
		epub.pack(epubFile, epubFolder);
		// Make sure all required files are present
		File metaFolder = new File(epubFolder.getAbsolutePath() + File.separator + "META-INF");
		Assert.assertEquals(true, metaFolder.exists());
		Assert.assertEquals(true, metaFolder.isDirectory());
		File containerFile = new File(epubFolder.getAbsolutePath() + File.separator + "META-INF" + File.separator
				+ "container.xml");
		Assert.assertEquals(true, containerFile.exists());
		Assert.assertEquals(false, containerFile.isDirectory());
		File oebpsFolder = new File(epubFolder.getAbsolutePath() + File.separator + "OEBPS");
		Assert.assertEquals(true, oebpsFolder.exists());
		Assert.assertEquals(true, oebpsFolder.isDirectory());
	}

	/**
	 * Test method for {@link org.eclipse.mylyn.docs.epub.core.EPUB#pack(java.io.File, java.io.File)} .
	 * <ul>
	 * <li>Exception shall be thrown if working folder already exist.</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testPackFileFileExists() throws Exception {
		File workingFolder = File.createTempFile("epub_", null);
		EPUB epub = new EPUB();
		OPSPublication oebps = new OPS2Publication();
		epub.add(oebps);
		try {
			epub.pack(epubFile, workingFolder);
			fail();
		} catch (Exception e) {
		}
	}

	/**
	 * Test method for {@link org.eclipse.mylyn.docs.epub.core.EPUB#pack(java.io.File)} .
	 * <ul>
	 * <li>Exception shall be thrown if the EPUB is empty.</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testPackMissingPublication() throws Exception {
		EPUB epub = new EPUB();
		try {
			epub.pack(epubFile);
			fail();
		} catch (Exception e) {
		}
	}

	/**
	 * Test method for {@link org.eclipse.mylyn.docs.epub.core.EPUB#unpack(java.io.File)}.
	 * <ul>
	 * <li>Unpacked EPUB shall have the same contents as the packed one.</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testUnpackFile() throws Exception {
		EPUB epub = new EPUB();
		OPSPublication oebps1 = new OPS2Publication();
		OPSPublication oebps2 = new OPS2Publication();
		oebps1.addItem(new File("testdata/plain-page.xhtml"));
		oebps2.addItem(new File("testdata/plain-page.xhtml"));
		epub.add(oebps1);
		epub.add(oebps2);
		epub.pack(epubFile, epubFolder);
		EPUB epub2 = new EPUB();
		epub2.unpack(epubFile);
		Assert.assertEquals(2, epub2.getOPSPublications().size());
	}

	/**
	 * Test method for {@link org.eclipse.mylyn.docs.epub.core.EPUB#unpack(java.io.File, java.io.File)} .
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testUnpackFileFile() throws Exception {
		// First pack the EPUB
		EPUB epub = new EPUB();
		OPSPublication oebps = new OPS2Publication();
		oebps.addItem(new File("testdata/plain-page.xhtml"));
		epub.add(oebps);
		epub.pack(epubFile, epubFolder);
		deleteFolder(epubFolder);

		// Then check for some contents when unpacked
		EPUB epub2 = new EPUB();
		epub2.unpack(epubFile, epubFolder);

		// Make sure all required files are present
		File metaFolder = new File(epubFolder.getAbsolutePath() + File.separator + "META-INF");
		Assert.assertEquals(true, metaFolder.exists());
		Assert.assertEquals(true, metaFolder.isDirectory());
		File containerFile = new File(epubFolder.getAbsolutePath() + File.separator + "META-INF" + File.separator
				+ "container.xml");
		Assert.assertEquals(true, containerFile.exists());
		Assert.assertEquals(false, containerFile.isDirectory());
		File oebpsFolder = new File(epubFolder.getAbsolutePath() + File.separator + "OEBPS");
		Assert.assertEquals(true, oebpsFolder.exists());
		Assert.assertEquals(true, oebpsFolder.isDirectory());
	}

	private class EPUB_OCF_Test extends EPUB {
		public void testReadOCF(File workingFolder) throws IOException {
			readOCF(workingFolder);
		}
	}

	/**
	 * See if the OCF file generated by this tooling can be read.
	 * 
	 * @throws Exception
	 */
	@Test
	public final void testReadOCF_Dogfood() throws Exception {
		File workingFolder = new File("testdata/OCF-Tests/Dogfood");
		EPUB_OCF_Test epub = new EPUB_OCF_Test();
		epub.testReadOCF(workingFolder);
	}

	/**
	 * This case was discovered when testing an EPUB file generated by DocBook Reading the OCF fails with a
	 * java.net.SocketException: Unexpected end of file from server. On closer inspection we can see that the file is
	 * declared as XHTML (which it of course is not). This is probably due to an issue in DocBook XSL 1.76.1
	 * 
	 * @see http://sourceforge.net/tracker/index.php?func=detail&aid=3353537 &group_id=21935&atid=373747.
	 * @throws Exception
	 */
	@Test
	public final void testReadOCF_SocketException() throws Exception {
		File workingFolder = new File("testdata/OCF-Tests/SocketException");
		EPUB_OCF_Test epub = new EPUB_OCF_Test();
		epub.testReadOCF(workingFolder);
	}
}
