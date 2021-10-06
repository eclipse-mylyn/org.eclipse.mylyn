/*******************************************************************************
 * Copyright (c) 2011-2014 Torkild U. Resheim.
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.EList;
import org.eclipse.mylyn.docs.epub.core.EPUB;
import org.eclipse.mylyn.docs.epub.core.OPSPublication;
import org.eclipse.mylyn.docs.epub.core.Publication;
import org.eclipse.mylyn.docs.epub.core.ValidationException;
import org.eclipse.mylyn.docs.epub.dc.Contributor;
import org.eclipse.mylyn.docs.epub.dc.Coverage;
import org.eclipse.mylyn.docs.epub.dc.Date;
import org.eclipse.mylyn.docs.epub.dc.Description;
import org.eclipse.mylyn.docs.epub.dc.Identifier;
import org.eclipse.mylyn.docs.epub.dc.Publisher;
import org.eclipse.mylyn.docs.epub.dc.Relation;
import org.eclipse.mylyn.docs.epub.dc.Rights;
import org.eclipse.mylyn.docs.epub.dc.Source;
import org.eclipse.mylyn.docs.epub.dc.Subject;
import org.eclipse.mylyn.docs.epub.dc.Title;
import org.eclipse.mylyn.docs.epub.opf.Item;
import org.eclipse.mylyn.docs.epub.opf.Role;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests features and regressions for all versions of the OPS supporting implementation {@link Publication}.
 *
 * @author Torkild U. Resheim
 */
@SuppressWarnings("nls")
public class TestPublication extends AbstractTest {

	private Publication oebps;

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		oebps = new OPSPublication(logger);
	}

	/**
	 * Test method for
	 * {@link org.eclipse.mylyn.docs.epub.core.Publication#addContributor(java.lang.String, java.util.Locale, java.lang.String, org.eclipse.mylyn.docs.epub.opf.Role, java.lang.String)}
	 * .
	 */
	@Test
	public final void testAddContributor() {
		Contributor contributor = oebps.addContributor("Nomen Nescio");
		contributor.setRole(Role.AUTHOR);

		oebps.addContributor(null, null, "Nomen Nescio", Role.AUTHOR, null);
	}

	/**
	 * Test method for
	 * {@link org.eclipse.mylyn.docs.epub.core.Publication#addCoverage(java.lang.String, java.util.Locale, java.lang.String)}
	 * .
	 */
	@Test
	public final void testAddCoverage() {
		oebps.addCoverage("Coverage", Locale.CANADA_FRENCH, "My Coverage");
		oebps.addCoverage(null, Locale.CANADA_FRENCH, "My Coverage");
		oebps.addCoverage(null, null, "My Coverage");
		EList<Coverage> Coverages = oebps.getPackage().getMetadata().getCoverages();
		assertEquals("Coverage", Coverages.get(0).getId());
		assertEquals("fr_CA", Coverages.get(0).getLang());
		assertEquals("My Coverage", getText(Coverages.get(0)));
		assertNull(Coverages.get(1).getId());
		assertEquals("fr_CA", Coverages.get(1).getLang());
		assertEquals("My Coverage", getText(Coverages.get(1)));
		assertNull(Coverages.get(2).getId());
		assertNull(Coverages.get(2).getLang());
		assertEquals("My Coverage", getText(Coverages.get(2)));
		try {
			oebps.addCoverage(null, null, null);
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	/**
	 * Test method for
	 * {@link org.eclipse.mylyn.docs.epub.core.Publication#addCreator(java.lang.String, java.util.Locale, java.lang.String, org.eclipse.mylyn.docs.epub.opf.Role, java.lang.String)}
	 * .
	 */
	@Test
	public final void testAddCreator() {
		// TODO
	}

	/**
	 * Test method for
	 * {@link org.eclipse.mylyn.docs.epub.core.Publication#addDate(java.lang.String, java.util.Date, java.lang.String)}
	 * .
	 */
	@Test
	public final void testAddDateStringDateString() {
		// TODO
	}

	/**
	 * Test method for
	 * {@link org.eclipse.mylyn.docs.epub.core.Publication#addDate(java.lang.String, java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public final void testAddDateStringStringString() {
		oebps.addDate(null, "1969", null);
		oebps.addDate(null, "1969-03", null);
		oebps.addDate(null, "1969-03-14", null);
		oebps.addDate(null, "1969-03-14", "event");
		EList<Date> dates = oebps.getPackage().getMetadata().getDates();
		assertEquals("1969", getText(dates.get(0)));
		assertEquals("1969-03", getText(dates.get(1)));
		assertEquals("1969-03-14", getText(dates.get(2)));
		assertEquals("event", dates.get(3).getEvent());
		try {
			oebps.addDate(null, (String) null, null);
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	/**
	 * Test method for
	 * {@link org.eclipse.mylyn.docs.epub.core.Publication#addDescription(java.lang.String, java.util.Locale, java.lang.String)}
	 * .
	 */
	@Test
	public final void testAddDescription() {
		oebps.addDescription("Description", Locale.CANADA_FRENCH, "My Description");
		oebps.addDescription(null, Locale.CANADA_FRENCH, "My Description");
		oebps.addDescription(null, null, "My Description");
		EList<Description> Descriptions = oebps.getPackage().getMetadata().getDescriptions();
		assertEquals("Description", Descriptions.get(0).getId());
		assertEquals("fr_CA", Descriptions.get(0).getLang());
		assertEquals("My Description", getText(Descriptions.get(0)));
		assertNull(Descriptions.get(1).getId());
		assertEquals("fr_CA", Descriptions.get(1).getLang());
		assertEquals("My Description", getText(Descriptions.get(1)));
		assertNull(Descriptions.get(2).getId());
		assertNull(Descriptions.get(2).getLang());
		assertEquals("My Description", getText(Descriptions.get(2)));
		try {
			oebps.addDescription(null, null, null);
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	/**
	 * Test method for
	 * {@link org.eclipse.mylyn.docs.epub.core.Publication#addFormat(java.lang.String, java.lang.String)} .
	 */
	@Test
	public final void testAddFormat() {
		oebps.addDescription("Description", Locale.CANADA_FRENCH, "My Description");
		oebps.addDescription(null, Locale.CANADA_FRENCH, "My Description");
		oebps.addDescription(null, null, "My Description");
		EList<Description> Descriptions = oebps.getPackage().getMetadata().getDescriptions();
		assertEquals("Description", Descriptions.get(0).getId());
		assertEquals("fr_CA", Descriptions.get(0).getLang());
		assertEquals("My Description", getText(Descriptions.get(0)));
		assertNull(Descriptions.get(1).getId());
		assertEquals("fr_CA", Descriptions.get(1).getLang());
		assertEquals("My Description", getText(Descriptions.get(1)));
		assertNull(Descriptions.get(2).getId());
		assertNull(Descriptions.get(2).getLang());
		assertEquals("My Description", getText(Descriptions.get(2)));
		try {
			oebps.addDescription(null, null, null);
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	/**
	 * Test method for
	 * {@link org.eclipse.mylyn.docs.epub.core.Publication#addIdentifier(java.lang.String, java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public final void testAddIdentifier() {
		oebps.addIdentifier("Identifier", "ID", "My Identifier");
		oebps.addIdentifier(null, "ID", "My Identifier");
		oebps.addIdentifier(null, null, "My Identifier");
		EList<Identifier> Identifiers = oebps.getPackage().getMetadata().getIdentifiers();
		assertEquals("Identifier", Identifiers.get(0).getId());
		assertEquals("ID", Identifiers.get(0).getScheme());
		assertEquals("My Identifier", getText(Identifiers.get(0)));
		assertEquals(null, Identifiers.get(1).getId());
		assertEquals("ID", Identifiers.get(1).getScheme());
		assertEquals("My Identifier", getText(Identifiers.get(1)));
		assertEquals(null, Identifiers.get(2).getId());
		assertEquals(null, Identifiers.get(2).getScheme());
		assertEquals("My Identifier", getText(Identifiers.get(2)));
		try {
			oebps.addIdentifier(null, null, null);
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	/**
	 * Test method for {@link org.eclipse.mylyn.docs.epub.core.Publication#addItem(java.io.File)} .
	 */
	@Test
	public final void testAddItemFile() {
		// TODO
	}

	/**
	 * Test method for
	 * {@link org.eclipse.mylyn.docs.epub.core.Publication#addItem(java.lang.String, java.util.Locale, java.io.File, java.lang.String, java.lang.String, boolean, boolean, boolean)}
	 * .
	 */
	@Test
	public final void testAddItemStringLocaleFileStringStringBooleanBooleanBoolean() {
		// TODO
	}

	/**
	 * Test method for
	 * {@link org.eclipse.mylyn.docs.epub.core.Publication#addLanguage(java.lang.String, java.lang.String)} .
	 */
	@Test
	public final void testAddLanguage() {
		oebps.addLanguage(null, "no");
		oebps.addLanguage("id", "no");
		assertEquals("no", getText(oebps.getPackage().getMetadata().getLanguages().get(0)));
		assertEquals("id", oebps.getPackage().getMetadata().getLanguages().get(1).getId());
		try {
			oebps.addLanguage(null, null);
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	/**
	 * Test method for
	 * {@link org.eclipse.mylyn.docs.epub.core.Publication#addPublisher(java.lang.String, java.util.Locale, java.lang.String)}
	 * .
	 */
	@Test
	public final void testAddPublisher() {
		oebps.addPublisher("Publisher", Locale.CANADA_FRENCH, "My Publisher");
		oebps.addPublisher(null, Locale.CANADA_FRENCH, "My Publisher");
		oebps.addPublisher(null, null, "My Publisher");
		EList<Publisher> Publishers = oebps.getPackage().getMetadata().getPublishers();
		assertEquals("Publisher", Publishers.get(0).getId());
		assertEquals("fr_CA", Publishers.get(0).getLang());
		assertEquals("My Publisher", getText(Publishers.get(0)));
		assertEquals(null, Publishers.get(1).getId());
		assertEquals("fr_CA", Publishers.get(1).getLang());
		assertEquals("My Publisher", getText(Publishers.get(1)));
		assertEquals(null, Publishers.get(2).getId());
		assertEquals(null, Publishers.get(2).getLang());
		assertEquals("My Publisher", getText(Publishers.get(2)));
		try {
			oebps.addPublisher(null, null, null);
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	/**
	 * Test method for
	 * {@link org.eclipse.mylyn.docs.epub.core.Publication#addReference(java.lang.String, java.lang.String, org.eclipse.mylyn.docs.epub.opf.Type)}
	 * .
	 */
	@Test
	public final void testAddReference() {
		// TODO
	}

	/**
	 * Test method for
	 * {@link org.eclipse.mylyn.docs.epub.core.Publication#addRelation(java.lang.String, java.util.Locale, java.lang.String)}
	 * .
	 */
	@Test
	public final void testAddRelation() {
		oebps.addRelation("Relation", Locale.CANADA_FRENCH, "My Relation");
		oebps.addRelation(null, Locale.CANADA_FRENCH, "My Relation");
		oebps.addRelation(null, null, "My Relation");
		EList<Relation> Relations = oebps.getPackage().getMetadata().getRelations();
		assertEquals("Relation", Relations.get(0).getId());
		assertEquals("fr_CA", Relations.get(0).getLang());
		assertEquals("My Relation", getText(Relations.get(0)));
		assertEquals(null, Relations.get(1).getId());
		assertEquals("fr_CA", Relations.get(1).getLang());
		assertEquals("My Relation", getText(Relations.get(1)));
		assertEquals(null, Relations.get(2).getId());
		assertEquals(null, Relations.get(2).getLang());
		assertEquals("My Relation", getText(Relations.get(2)));
		try {
			oebps.addRelation(null, null, null);
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	/**
	 * Test method for
	 * {@link org.eclipse.mylyn.docs.epub.core.Publication#addRights(java.lang.String, java.util.Locale, java.lang.String)}
	 * .
	 */
	@Test
	public final void testAddRights() {
		oebps.addRights("Rights", Locale.CANADA_FRENCH, "My Rights");
		oebps.addRights(null, Locale.CANADA_FRENCH, "My Rights");
		oebps.addRights(null, null, "My Rights");
		EList<Rights> Rightss = oebps.getPackage().getMetadata().getRights();
		assertEquals("Rights", Rightss.get(0).getId());
		assertEquals("fr_CA", Rightss.get(0).getLang());
		assertEquals("My Rights", getText(Rightss.get(0)));
		assertEquals(null, Rightss.get(1).getId());
		assertEquals("fr_CA", Rightss.get(1).getLang());
		assertEquals("My Rights", getText(Rightss.get(1)));
		assertEquals(null, Rightss.get(2).getId());
		assertEquals(null, Rightss.get(2).getLang());
		assertEquals("My Rights", getText(Rightss.get(2)));
		try {
			oebps.addRights(null, null, null);
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	/**
	 * Test method for
	 * {@link org.eclipse.mylyn.docs.epub.core.Publication#addSource(java.lang.String, java.util.Locale, java.lang.String)}
	 * .
	 */
	@Test
	public final void testAddSource() {
		oebps.addSource("Source", Locale.CANADA_FRENCH, "My Source");
		oebps.addSource(null, Locale.CANADA_FRENCH, "My Source");
		oebps.addSource(null, null, "My Source");
		EList<Source> Sources = oebps.getPackage().getMetadata().getSources();
		assertEquals("Source", Sources.get(0).getId());
		assertEquals("fr_CA", Sources.get(0).getLang());
		assertEquals("My Source", getText(Sources.get(0)));
		assertEquals(null, Sources.get(1).getId());
		assertEquals("fr_CA", Sources.get(1).getLang());
		assertEquals("My Source", getText(Sources.get(1)));
		assertEquals(null, Sources.get(2).getId());
		assertEquals(null, Sources.get(2).getLang());
		assertEquals("My Source", getText(Sources.get(2)));
		// An IllegalArgumentException is expected
		try {
			oebps.addSource(null, null, null);
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	/**
	 * Test method for
	 * {@link org.eclipse.mylyn.docs.epub.core.Publication#addSubject(java.lang.String, java.util.Locale, java.lang.String)}
	 * .
	 */
	@Test
	public final void testAddSubject() {
		oebps.addSubject("Subject", Locale.CANADA_FRENCH, "My Subject");
		oebps.addSubject(null, Locale.CANADA_FRENCH, "My Subject");
		oebps.addSubject(null, null, "My Subject");
		EList<Subject> subjects = oebps.getPackage().getMetadata().getSubjects();
		assertEquals("Subject", subjects.get(0).getId());
		assertEquals("fr_CA", subjects.get(0).getLang());
		assertEquals("My Subject", getText(subjects.get(0)));
		assertEquals(null, subjects.get(1).getId());
		assertEquals("fr_CA", subjects.get(1).getLang());
		assertEquals("My Subject", getText(subjects.get(1)));
		assertEquals(null, subjects.get(2).getId());
		assertEquals(null, subjects.get(2).getLang());
		assertEquals("My Subject", getText(subjects.get(2)));
		try {
			oebps.addSubject(null, null, null);
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	/**
	 * Test method for
	 * {@link org.eclipse.mylyn.docs.epub.core.Publication#addTitle(java.lang.String, java.util.Locale, java.lang.String)}
	 * .
	 */
	@Test
	public final void testAddTitle() {
		oebps.addTitle("Title", Locale.CANADA_FRENCH, "My Title");
		oebps.addTitle(null, Locale.CANADA_FRENCH, "My Title");
		oebps.addTitle(null, null, "My Title");
		EList<Title> titles = oebps.getPackage().getMetadata().getTitles();
		assertEquals("Title", titles.get(0).getId());
		assertEquals("fr_CA", titles.get(0).getLang());
		assertEquals("My Title", getText(titles.get(0)));
		assertEquals(null, titles.get(1).getId());
		assertEquals("fr_CA", titles.get(1).getLang());
		assertEquals("My Title", getText(titles.get(1)));
		assertEquals(null, titles.get(2).getId());
		assertEquals(null, titles.get(2).getLang());
		assertEquals("My Title", getText(titles.get(2)));
		try {
			oebps.addTitle(null, null, null);
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	/**
	 * Test method for {@link org.eclipse.mylyn.docs.epub.core.Publication#addType(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public final void testAddType() {
		oebps.addType("Type", "My Type");
		oebps.addType(null, "My Type");
		EList<org.eclipse.mylyn.docs.epub.dc.Type> Types = oebps.getPackage().getMetadata().getTypes();
		assertEquals("Type", Types.get(0).getId());
		assertEquals("My Type", getText(Types.get(0)));
		assertEquals(null, Types.get(1).getId());
		assertEquals("My Type", getText(Types.get(1)));
		try {
			oebps.addType(null, null);
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	/**
	 * Test method for {@link org.eclipse.mylyn.docs.epub.core.Publication#generateTableOfContents()} .
	 */
	@Test
	public final void testGenerateTableOfContents() {
		// TODO
	}

	/**
	 * Test method for {@link org.eclipse.mylyn.docs.epub.core.Publication#getIdentifier()}.
	 */
	@Test
	public final void testGetIdentifier() {
		// TODO
	}

	/**
	 * Test method for {@link org.eclipse.mylyn.docs.epub.core.Publication#getItemById(java.lang.String)} .
	 */
	@Test
	public final void testGetItemById() {
		// TODO
	}

	/**
	 * Test method for {@link org.eclipse.mylyn.docs.epub.core.Publication#getItemsByMIMEType(java.lang.String)} .
	 */
	@Test
	public final void testGetItemsByMIMEType() {
		Item i_in_1 = oebps.addItem(new File("testdata/images/subfolder-drawing.svg"));
		Item i_in_2 = oebps.addItem(new File("testdata/plain-page.xhtml"));
		List<Item> i_out_1 = oebps.getItemsByMIMEType("image/svg+xml");
		assertEquals(1, i_out_1.size());
		assertEquals(i_in_1, i_out_1.get(0));
		List<Item> i_out_2 = oebps.getItemsByMIMEType("application/xhtml+xml");
		assertEquals(1, i_out_2.size());
		assertEquals(i_in_2, i_out_2.get(0));
	}

	/**
	 * Test method for {@link org.eclipse.mylyn.docs.epub.core.Publication#getPackage()}.
	 */
	@Test
	public final void testGetOpfPackage() {
		// TODO
	}

	/**
	 * Test method for {@link org.eclipse.mylyn.docs.epub.core.Publication#getRootFolder()}.
	 */
	@Test
	public final void testGetRootFolder() {
		// TODO
	}

	/**
	 * Test method for {@link org.eclipse.mylyn.docs.epub.core.Publication#getSpine()}.
	 */
	@Test
	public final void testGetSpine() {
		// TODO
	}

	/**
	 * Test method for {@link org.eclipse.mylyn.docs.epub.core.Publication#getTableOfContents()} .
	 */
	@Test
	public final void testGetTableOfContents() {
		// TODO
	}

	/**
	 * Test method for {@link org.eclipse.mylyn.docs.epub.core.Publication#getValidationMessages()} .
	 */
	@Test
	public final void testGetValidationMessages() {
		// TODO
	}

	/**
	 * Test method for {@link org.eclipse.mylyn.docs.epub.core.Publication#pack(java.io.File)}. An EPUB where only a
	 * single page has been added shall be packed without issues
	 *
	 * @throws Exception
	 */

	/* Bug 454932 - fix or remove failing EPUB test
	@Test
	public final void testPack_EPUB2() throws Exception {
		oebps.addItem(new File("testdata/plain-page.xhtml"));
		epub.add(oebps);
		epub.pack(epubFile);
		oebps.validateMetadata();
		Report report = new DefaultReportImpl(epubFile.toString());
		EpubCheck checker = new EpubCheck(epubFile, report);
		System.out.println("Validating plain EPUB 2.0.1 file");
		System.out.println("Using version " + EpubCheck.version() + " of EpubCheck.");
		Assert.assertTrue(checker.validate());
	}
	 */
	/**
	 * Test method for {@link org.eclipse.mylyn.docs.epub.core.Publication#pack(java.io.File)}. An EPUB with no content
	 * shall fail when packed.
	 *
	 * @throws Exception
	 */
	@Test
	public final void testPack_Empty() throws Exception {
		epub.add(new OPSPublication());
		try {
			epubFile.delete();
			epub.pack(epubFile);
			fail();
		} catch (ValidationException e) {
		}
	}

	/**
	 * Test method for {@link org.eclipse.mylyn.docs.epub.core.Publication#readTableOfContents(java.io.File)} .
	 */
	@Test
	public final void testReadTableOfContents() {
		// TODO
	}

	/**
	 * Test method for {@link org.eclipse.mylyn.docs.epub.core.Publication#setGenerateToc(boolean)} .
	 */
	@Test
	public final void testSetGenerateToc() {
		// TODO
	}

	/**
	 * Test method for {@link org.eclipse.mylyn.docs.epub.core.Publication#setIdentifierId(java.lang.String)} .
	 */
	@Test
	public final void testSetIdentifierId() {
		// TODO
	}

	/**
	 * Test method for {@link org.eclipse.mylyn.docs.epub.core.Publication#setIncludeReferencedResources(boolean)}. This
	 * is determining whether or not the referenced resources has been picked up and included in the resulting EPUB.
	 * Also handles <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=375795">bug 375795</a>: [epub][patch]
	 * Automatic inclusion of referenced resources fail on anchor references
	 *
	 * @throws Exception
	 */
	@Test
	public final void testSetIncludeReferencedResources() throws Exception {
		oebps.setIncludeReferencedResources(true);
		oebps.addItem(new File("testdata/plain-page_link.xhtml"));
		epub.add(oebps);
		// Included resources will only be added when we pack
		epub.pack(epubFile);

		EPUB epub2 = new EPUB();
		epub2.unpack(epubFile, epubFolder);
		Publication oebps = epub2.getOPSPublications().get(0);
		File root = oebps.getRootFolder();
		File svg = new File(root.getAbsolutePath() + File.separator + "drawing.svg");
		Assert.assertTrue(svg.exists());
		File svg2 = new File(
				root.getAbsolutePath() + File.separator + "images" + File.separator + "subfolder-drawing.svg");
		Assert.assertTrue(svg2.exists());
		File html = new File(root.getAbsolutePath() + File.separator + "plain-page_no-header.xhtml");
		Assert.assertTrue(html.exists());
		File html2 = new File(root.getAbsolutePath() + File.separator + "plain-page.xhtml");
		Assert.assertTrue(html2.exists());
		// The manifest shall only contain the items we have linked to in addition to the toc.ncx and the file that we
		// started from -- a total of six files.
		assertEquals(6, oebps.getPackage().getManifest().getItems().size());

	}

	/**
	 * Test method for <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=380016">bug 380016</a>: Reference scanner
	 * should also include referenced CSS style sheets
	 *
	 * @throws Exception
	 */
	@Test
	public final void test_Bug380016() throws Exception {
		oebps.setIncludeReferencedResources(true);
		oebps.addItem(new File("testdata/OPF-Tests/Bug_380016/chapter.xhtml"));
		epub.add(oebps);
		epub.pack(epubFile);
		EPUB epub2 = new EPUB();
		epub2.unpack(epubFile, epubFolder);
		Publication oebps = epub2.getOPSPublications().get(0);
		File root = oebps.getRootFolder();
		File svg = new File(root.getAbsolutePath() + File.separator + "style.css");
		Assert.assertTrue(svg.exists());
	}

	/**
	 * Test method for <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=360701">bug 360701</a>: [epub] Automatic
	 * inclusion of referenced resources don't work for WikiText generated HTML.
	 *
	 * @throws Exception
	 */
	@Test
	public final void test_Bug360701() throws Exception {
		oebps.setIncludeReferencedResources(true);
		// This page is similar to what WikiText would generate
		oebps.addItem(new File("testdata/plain-page_link.html"));
		epub.add(oebps);
		// Included resources will only be added when we pack
		epub.pack(epubFile);

		EPUB epub2 = new EPUB();
		epub2.unpack(epubFile, epubFolder);
		Publication oebps = epub2.getOPSPublications().get(0);
		File root = oebps.getRootFolder();
		File svg = new File(root.getAbsolutePath() + File.separator + "drawing.svg");
		Assert.assertTrue(svg.exists());
		File svg2 = new File(
				root.getAbsolutePath() + File.separator + "images" + File.separator + "subfolder-drawing.svg");
		Assert.assertTrue(svg2.exists());
		File html = new File(root.getAbsolutePath() + File.separator + "plain-page_no-header.html");
		Assert.assertTrue(html.exists());

	}

	/**
	 * Test method for <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=373052">bug 373052</a>: [epub] Reference
	 * scanner does not handle absolute paths
	 *
	 * @throws Exception
	 */
	@Test
	public final void test_Bug373052() throws Exception {
		// We need to link to a absolute file so we create a temporary HTML file
		// in which we have the link.m
		File htmlFile = File.createTempFile("temp", ".xhtml");
		File svgFile = new File("testdata/drawing.svg");

		FileWriter fw = new FileWriter(htmlFile);
		// A proper declaration must be added or the file type cannot be
		// correctly detected.
		fw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		fw.write("<!DOCTYPE html\n"
				+ "  PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">");
		fw.write("<html xmlns=\"http://www.w3.org/1999/xhtml\"><body>");
		fw.write("<img src=\"" + svgFile.getAbsolutePath() + "\"/>");
		fw.write("</body></html>");
		fw.close();

		oebps.setIncludeReferencedResources(true);
		oebps.addItem(htmlFile);
		epub.add(oebps);
		epub.pack(epubFile);

		htmlFile.delete();

		EPUB epub2 = new EPUB();
		epub2.unpack(epubFile, epubFolder);
		Publication oebps = epub2.getOPSPublications().get(0);
		File root = oebps.getRootFolder();
		File svg = new File(root.getAbsolutePath() + File.separator + "drawing.svg");
		Assert.assertTrue(svg.exists());

	}

	/**
	 * Test method for <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=376312">bug 376312</a>: [epub] Automatic
	 * inclusion of detected resources may add the same resource twice or more
	 * <p>
	 * File A references file C as do file B. File C references file A. Before the fix there would be two instances of
	 * file C.
	 * </p>
	 *
	 * @throws Exception
	 */
	@Test
	public final void test_Bug376312() throws Exception {
		oebps.setIncludeReferencedResources(true);
		oebps.addItem(new File("testdata/circular/file-a.xhtml"));
		oebps.addItem(new File("testdata/circular/file-b.xhtml"));
		epub.add(oebps);
		epub.pack(epubFile);
		EList<Item> items = oebps.getPackage().getManifest().getItems();
		// File A, B, C and the NCX
		assertEquals(4, items.size());
	}

	/**
	 * Test method for {@link org.eclipse.mylyn.docs.epub.core.Publication#setTableOfContents(java.io.File)} .
	 *
	 * @see TestOPSPublication#testSetTableOfContents()
	 */
	@Test
	public final void testSetTableOfContents() {
		// Handled by subclass test.
	}

	/**
	 * Test method for {@link org.eclipse.mylyn.docs.epub.core.Publication#unpack(java.io.File)} .
	 */
	@Test
	public final void testUnpack() {
		// TODO
	}

	/**
	 * Test method for {@link org.eclipse.mylyn.docs.epub.core.Publication#validateContents()} .
	 */
	@Test
	public final void testValidateContents() {
		// TODO
	}

	/**
	 * Test method for {@link org.eclipse.mylyn.docs.epub.core.Publication#validateMetadata()} .
	 */
	@Test
	public final void testValidateMetadata() {
		// TODO
	}

	/**
	 * Test method for {@link org.eclipse.mylyn.docs.epub.core.Publication#writeTableOfContents(java.io.File)} .
	 */
	@Test
	public final void testWriteTableOfContents() {
		// TODO
	}

	private class EPUB_OPF_Test extends OPSPublication {
		public void testReadOPF(File rootFile) throws IOException {
			readOPF(rootFile);
		}
	}

	/**
	 * See if the OPF file generated by this tooling can be read.
	 *
	 * @throws Exception
	 */
	@Test
	public final void testReadOCF_Dogfood() throws Exception {
		File rootFile = new File("testdata/OPF-Tests/Dogfood/content.opf");
		EPUB_OPF_Test epub = new EPUB_OPF_Test();
		epub.testReadOPF(rootFile);
	}

	/**
	 * This case was discovered when testing an EPUB file generated by DocBook Reading the OPF fails with a
	 * java.net.SocketException: Unexpected end of file from server. On closer inspection we can see that the file is
	 * declared as XHTML (which it of course is not). This is probably due to an issue in DocBook XSL 1.76.1
	 *
	 * @see http://sourceforge.net/tracker/index.php?func=detail&aid=3353537 &group_id=21935&atid=373747.
	 * @throws Exception
	 */
	@Test
	public final void testReadOCF_SocketException() throws Exception {
		File rootFile = new File("testdata/OPF-Tests/SocketException/content.opf");
		EPUB_OPF_Test oebps = new EPUB_OPF_Test();
		oebps.testReadOPF(rootFile);
	}

	/**
	 * Test method for <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=380729">bug 380729</a>: Allow reference
	 * elements to have "other." types
	 *
	 * @throws Exception
	 */
	@Test
	public final void test_Bug380729() throws Exception {
		// Validation is normally not performed when loading content. However
		// the previous implementation of the reference type was an
		// enumeration so it would fail when attempting to set it and there
		// was no matching item. Hence this code should now pass as the OPF
		// contains previously invalid values.
		File rootFile = new File("testdata/OPF-Tests/Bug_380729/content.opf");
		EPUB_OPF_Test oebps = new EPUB_OPF_Test();
		oebps.testReadOPF(rootFile);

		// This is required for validation
		oebps.addSubject(null, null, "Required subject");

		// Validate that "cover" and "other.ms-coverpage" already read from
		// the OPF file is OK.
		List<Diagnostic> problems = oebps.validateMetadata();
		assertEquals(0, problems.size());

		// Add illegal reference type and see that we get an error
		oebps.addReference("cover-page.xhtml", "cover", "invalid");
		problems = oebps.validateMetadata();
		assertEquals(1, problems.size());
	}
}
