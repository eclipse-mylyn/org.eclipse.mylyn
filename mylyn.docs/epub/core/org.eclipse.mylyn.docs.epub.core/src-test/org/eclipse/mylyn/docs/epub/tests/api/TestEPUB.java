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
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.emf.common.util.EList;
import org.eclipse.mylyn.docs.epub.core.EPUB;
import org.eclipse.mylyn.docs.epub.core.OPSPublication;
import org.eclipse.mylyn.docs.epub.core.Publication;
import org.eclipse.mylyn.docs.epub.ocf.Container;
import org.eclipse.mylyn.docs.epub.ocf.RootFile;
import org.eclipse.mylyn.docs.epub.ocf.RootFiles;
import org.junit.Test;

/**
 * @author Torkild U. Resheim
 */
@SuppressWarnings("nls")
public class TestEPUB extends AbstractTest {

	/**
	 * Test method for {@link org.eclipse.mylyn.docs.epub.core.EPUB#EPUB()}.
	 * <p>
	 * It must be possible to obtain the publication list which must be empty.
	 * </p>
	 */
	@Test
	public final void testEPUB() {
		EPUB epub = new EPUB();
		assertEquals(true, epub.getOPSPublications().isEmpty());
	}

	/**
	 * Verify that we can open a basic EPUB file.
	 *
	 * @throws Exception
	 */
	@Test
	public final void testEPUB3() throws Exception {
		EPUB epub = new EPUB();
		File epub_3 = new File("testdata/epub/basic_3.epub");
		assertEquals(true, epub.isEPUB(epub_3));
		epub.unpack(epub_3);
		assertEquals(1, epub.getOPSPublications().size());
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
		File drawing = new File("testdata/drawing.svg");
		epub.add(drawing, "image/svg+xml");
		Container container = epub.getContainer();
		RootFiles rootfiles = container.getRootfiles();
		EList<RootFile> files = rootfiles.getRootfiles();
		assertEquals(true, files.get(0).getFullPath().equals("SVG+XML" + File.separator + "drawing.svg"));
		assertEquals(true, files.get(0).getMediaType().equals("image/svg+xml"));
		assertEquals(true, files.get(0).getPublication() == drawing);
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
		File drawing = new File("testdata/drawing.svg");
		epub.add(drawing, "image/svg+xml");
		Container container = epub.getContainer();
		RootFiles rootfiles = container.getRootfiles();
		EList<RootFile> files = rootfiles.getRootfiles();
		files.get(0).setPublication(null);
		try {
			epub.pack(getEpubFile());
			fail();
		} catch (Exception e) {
		}
	}

	/**
	 * Test method for {@link org.eclipse.mylyn.docs.epub.core.EPUB#add(org.eclipse.mylyn.docs.epub.core.Publication)} .
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
		Publication oebps1 = new OPSPublication();
		Publication oebps2 = new OPSPublication();
		epub.add(oebps1);
		epub.add(oebps2);
		Container container = epub.getContainer();
		RootFiles rootfiles = container.getRootfiles();
		EList<RootFile> files = rootfiles.getRootfiles();
		assertEquals(true, files.get(0).getFullPath().equals("OEBPS/content.opf"));
		assertEquals(true, files.get(1).getFullPath().equals("OEBPS_1/content.opf"));
		assertEquals(true, files.get(0).getMediaType().equals("application/oebps-package+xml"));
		assertEquals(true, files.get(1).getMediaType().equals("application/oebps-package+xml"));
		assertEquals(true, files.get(0).getPublication() == oebps1);
		assertEquals(true, files.get(1).getPublication() == oebps2);
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
		Publication oebps = new OPSPublication();
		epub.add(oebps);
		File drawing = new File("testdata/drawing.svg");
		epub.add(drawing, "image/svg+xml");
		assertEquals(1, epub.getOPSPublications().size());
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
		Publication oebps = new OPSPublication();
		oebps.addItem(new File("testdata/plain-page.xhtml"));
		epub.add(oebps);
		File drawing = new File("testdata/drawing.svg");
		epub.add(drawing, "image/svg+xml");
		File tempFolder = epub.pack(getEpubFile());
		assertEquals(false, tempFolder.exists());
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
		Publication oebps = new OPSPublication();
		oebps.addItem(new File("testdata/plain-page.xhtml"));
		epub.add(oebps);
		epub.pack(getEpubFile(), getEpubFolder());
		// Make sure all required files are present
		File metaFolder = new File(getEpubFolder().getAbsolutePath() + File.separator + "META-INF");
		assertEquals(true, metaFolder.exists());
		assertEquals(true, metaFolder.isDirectory());
		File containerFile = new File(
				getEpubFolder().getAbsolutePath() + File.separator + "META-INF" + File.separator + "container.xml");
		assertEquals(true, containerFile.exists());
		assertEquals(false, containerFile.isDirectory());
		File oebpsFolder = new File(getEpubFolder().getAbsolutePath() + File.separator + "OEBPS");
		assertEquals(true, oebpsFolder.exists());
		assertEquals(true, oebpsFolder.isDirectory());
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
		Publication oebps = new OPSPublication();
		epub.add(oebps);
		try {
			epub.pack(getEpubFile(), workingFolder);
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
		try {
			epub.pack(getEpubFile());
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
		Publication oebps = new OPSPublication();
		oebps.addItem(new File("testdata/plain-page.xhtml"));
		epub.add(oebps);
		epub.pack(getEpubFile(), getEpubFolder());
		EPUB epub2 = new EPUB();
		epub2.unpack(getEpubFile());
		assertEquals(1, epub2.getOPSPublications().size());
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
		Publication oebps = new OPSPublication();
		oebps.addItem(new File("testdata/plain-page.xhtml"));
		epub.add(oebps);
		epub.pack(getEpubFile(), getEpubFolder());
		deleteFolder(getEpubFolder());

		// Then check for some contents when unpacked
		EPUB epub2 = new EPUB();
		epub2.unpack(getEpubFile(), getEpubFolder());

		// Make sure all required files are present
		File metaFolder = new File(getEpubFolder().getAbsolutePath() + File.separator + "META-INF");
		assertEquals(true, metaFolder.exists());
		assertEquals(true, metaFolder.isDirectory());
		File containerFile = new File(
				getEpubFolder().getAbsolutePath() + File.separator + "META-INF" + File.separator + "container.xml");
		assertEquals(true, containerFile.exists());
		assertEquals(false, containerFile.isDirectory());
		File oebpsFolder = new File(getEpubFolder().getAbsolutePath() + File.separator + "OEBPS");
		assertEquals(true, oebpsFolder.exists());
		assertEquals(true, oebpsFolder.isDirectory());
	}

	private class EPUB_OCF_Test extends EPUB {
		public void testReadOCF(File workingFolder) throws IOException {
			readOCF(workingFolder);
		}
	}

	/**
	 * See if the OCF file generated by this tooling can be read. As of bug 378800 elements are no longer prefixed with
	 * "ocf", however both are allowed and tested for.
	 *
	 * @throws Exception
	 */
	@Test
	public final void testReadOCF_Dogfood() throws Exception {
		EPUB_OCF_Test epub = new EPUB_OCF_Test();

		File workingFolder = new File("testdata/OCF-Tests/Dogfood");
		epub.testReadOCF(workingFolder);

		workingFolder = new File("testdata/OCF-Tests/Bug_378800");
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

	/**
	 * Test method for <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=377705">bug 377705</a>: [epub] Fail
	 * gracefully when opening an unsupported file
	 * <p>
	 * When attempting to open a file that is not an EPUB the tooling shall reply by throwing an
	 * {@link IllegalArgumentException}.
	 * </p>
	 *
	 * @throws Exception
	 */
	@Test
	public final void test_Bug377705() throws Exception {
		EPUB epub = new EPUB();
		File drawing = new File("testdata/drawing.svg");
		File epub_2 = new File("testdata/epub/basic_2.epub");
		assertEquals(false, epub.isEPUB(drawing));
		assertEquals(true, epub.isEPUB(epub_2));
		try {
			epub.unpack(drawing);
			fail();
		} catch (IllegalArgumentException e) {
		}
		try {
			epub.unpack(epub_2);
		} catch (Exception e) {
			fail();
		}
	}

	/**
	 * Test method for <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=378800">bug 378800</a>: [epub] Remove
	 * "ocf" prefix from elements in container.xml
	 *
	 * @throws Exception
	 */
	@Test
	public final void test_Bug378800() throws Exception {
		EPUB epub = new EPUB();
		Publication oebps = new OPSPublication();
		oebps.addItem(new File("testdata/plain-page.xhtml"));
		epub.add(oebps);
		epub.pack(getEpubFile(), getEpubFolder());
		File metaFolder = new File(getEpubFolder().getAbsolutePath() + File.separator + "META-INF"); //$NON-NLS-1$
		File containerFile = new File(metaFolder.getAbsolutePath() + File.separator + "container.xml"); //$NON-NLS-1$
		BufferedReader br = new BufferedReader(new FileReader(containerFile));
		String in = null;
		boolean ok = false;
		while ((in = br.readLine()) != null) {
			// as opposed to "<ocf:container "
			if (in.startsWith("<container ")) {
				ok = true;
			}
		}
		br.close();
		assertEquals(true, ok);
	}

	/**
	 * Verifies that an EPUB can be unpacked, modified and repacked.
	 *
	 * @throws Exception
	 */
	@Test
	public final void testRepack_AddItem() throws Exception {
		File folder = File.createTempFile("epub_", null);
		folder.delete();
		folder.mkdirs();
		File epubFile = new File("testdata/epub/basic_2.epub");
		File epubFile2 = new File(folder, "repacked.epub");
		epub.unpack(epubFile, folder);
		Publication publication = epub.getOPSPublications().get(0);
		// remove the existing table of contents from the spine
		publication.removeItemById(publication.getSpine().getToc());
		// make sure a new one is generated
		publication.setGenerateToc(true);
		// add a new item to the spine
		publication.addItem(new File("testdata/plain-page_link.xhtml"));
		epub.pack(epubFile2, folder);
	}

}
