/*******************************************************************************
 * Copyright (c) 2007, 2024 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.ant.internal.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.eclipse.mylyn.wikitext.ant.internal.MarkupToDitaTask;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("nls")
public class MarkupToDitaTaskTest extends AbstractTestAntTask {

	private MarkupToDitaTask ditaTask;

	private File topicsFolder;

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();

		ditaTask = new MarkupToDitaTask();
		ditaTask.setMarkupLanguage(languageName);

		topicsFolder = new File(tempFolder, ditaTask.getTopicFolder());
		topicsFolder.mkdirs();
	}

	private File createSimpleTextileMarkup() throws IOException {
		File markupFile = new File(tempFolder, "markup.textile");
		try (PrintWriter writer = new PrintWriter(new FileWriter(markupFile))) {
			writer.println("h1. First Heading");
			writer.println();
			writer.println("some content");
			writer.println();
			writer.println("h1. Second Heading");
			writer.println();
			writer.println("some more content");
		}
		return markupFile;
	}

	private File createTextileMarkupWithXref() throws IOException {
		return createTextileMarkupWithXref(1);
	}

	private File createTextileMarkupWithXref(int headingLevel) throws IOException {
		File markupFile = new File(tempFolder, "markup.textile");
		try (PrintWriter writer = new PrintWriter(new FileWriter(markupFile))) {
			writer.println("h" + headingLevel + "(#Id1). First Heading");
			writer.println();
			writer.println("some content with a \"ref to 2\":#Id2");
			writer.println();
			writer.println("h" + headingLevel + "(#Id2). Second Heading");
			writer.println();
			writer.println("some more content with with a \"ref to 1\":#Id1 and a \"ref to 2\":#Id2");
		}
		return markupFile;
	}

	private File createSimpleTextileMarkupWithFQNHeadings() throws IOException {
		File markupFile = new File(tempFolder, "markup.textile");
		try (PrintWriter writer = new PrintWriter(new FileWriter(markupFile))) {
			writer.println("h1. " + MarkupToDitaTaskTest.class.getName());
			writer.println();
			writer.println("some content");
			writer.println();
			writer.println("h1. Second Heading");
			writer.println();
			writer.println("some more content");
		}
		return markupFile;
	}

	@Test
	public void testCreatesMapbook() throws IOException {
		File markupFile = createSimpleTextileMarkup();
		ditaTask.setBookTitle("Sample Title");
		ditaTask.setFile(markupFile);

		ditaTask.execute();

		listFiles();

		File ditamapFile = new File(tempFolder, "markup.ditamap");
		assertTrue(ditamapFile.exists());
		File firstHeadingFile = new File(topicsFolder, "FirstHeading.dita");
		assertTrue(firstHeadingFile.exists());
		File secondHeadingFile = new File(topicsFolder, "SecondHeading.dita");
		assertTrue(secondHeadingFile.exists());

		String ditamapContent = getContent(ditamapFile);
		assertTrue(ditamapContent.contains("<bookmap>"));
		assertTrue(ditamapContent.contains("<chapter href=\"topics/FirstHeading.dita\" navtitle=\"First Heading\"/>"));
		assertTrue(
				ditamapContent.contains("<chapter href=\"topics/SecondHeading.dita\" navtitle=\"Second Heading\"/>"));

		String firstTopicContent = getContent(firstHeadingFile);
//		<?xml version='1.0' ?><!DOCTYPE topic PUBLIC "-//OASIS//DTD DITA 1.1 Topic//EN" "http://docs.oasis-open.org/dita/v1.1/OS/dtd/topic.dtd">
//		<topic id="FirstHeading">
//			<title>First Heading</title>
//			<body>
//				<p>some content</p>
//			</body>
//		</topic>
		assertTrue(firstTopicContent.contains("<topic id=\"FirstHeading\">"));
		assertTrue(firstTopicContent.contains("<title>First Heading</title>"));
		assertTrue(firstTopicContent.contains("<p>some content</p>"));
	}

	@Test
	public void testCreatesMapbookNoFormatting() throws IOException {
		File markupFile = createSimpleTextileMarkup();
		ditaTask.setBookTitle("Sample Title");
		ditaTask.setFile(markupFile);
		ditaTask.setFormatting(false);

		ditaTask.execute();

		listFiles();

		File ditamapFile = new File(tempFolder, "markup.ditamap");
		assertTrue(ditamapFile.exists());
		File firstHeadingFile = new File(topicsFolder, "FirstHeading.dita");
		assertTrue(firstHeadingFile.exists());
		File secondHeadingFile = new File(topicsFolder, "SecondHeading.dita");
		assertTrue(secondHeadingFile.exists());

		String ditamapContent = getContent(ditamapFile);

		assertTrue(ditamapContent.contains(
				"<bookmap><title>Sample Title</title><chapter href=\"topics/FirstHeading.dita\" navtitle=\"First Heading\"/><chapter href=\"topics/SecondHeading.dita\" navtitle=\"Second Heading\"/></bookmap>"));

		String firstTopicContent = getContent(firstHeadingFile);
//		<?xml version='1.0' ?><!DOCTYPE topic PUBLIC "-//OASIS//DTD DITA 1.1 Topic//EN" "http://docs.oasis-open.org/dita/v1.1/OS/dtd/topic.dtd">
//		<topic id="FirstHeading">
//			<title>First Heading</title>
//			<body>
//				<p>some content</p>
//			</body>
//		</topic>

		assertTrue(firstTopicContent.contains(
				"<topic id=\"FirstHeading\"><title>First Heading</title><body><p>some content</p></body></topic>"));
	}

	@Test
	public void testCreatesSingleTopic() throws IOException {
		File markupFile = createSimpleTextileMarkup();
		ditaTask.setBookTitle("Sample Title");
		ditaTask.setFile(markupFile);
		ditaTask.setFilenameFormat("$1.dita");
		ditaTask.setTopicStrategy(MarkupToDitaTask.BreakStrategy.NONE);

		ditaTask.execute();

		listFiles();

		File ditamapFile = new File(tempFolder, "markup.ditamap");
		assertFalse(ditamapFile.exists());
		File firstHeadingFile = new File(tempFolder, "markup.dita");
		assertTrue(firstHeadingFile.exists());

		String firstTopicContent = getContent(firstHeadingFile);

		assertTrue(firstTopicContent.contains("<topic id=\"FirstHeading\">"));
		assertTrue(firstTopicContent.contains("<title>First Heading</title>"));
		assertTrue(firstTopicContent.contains("<p>some content</p>"));
		assertTrue(firstTopicContent.contains("<topic id=\"SecondHeading\">"));
		assertTrue(firstTopicContent.contains("<title>Second Heading</title>"));
		assertTrue(firstTopicContent.contains("<p>some more content</p>"));
	}

	@Test
	public void testCreatesSingleTopicNoFormatting() throws IOException {
		File markupFile = createSimpleTextileMarkup();
		ditaTask.setBookTitle("Sample Title");
		ditaTask.setFile(markupFile);
		ditaTask.setFilenameFormat("$1.dita");
		ditaTask.setTopicStrategy(MarkupToDitaTask.BreakStrategy.NONE);
		ditaTask.setFormatting(false);

		ditaTask.execute();

		listFiles();

		File ditamapFile = new File(tempFolder, "markup.ditamap");
		assertFalse(ditamapFile.exists());
		File firstHeadingFile = new File(tempFolder, "markup.dita");
		assertTrue(firstHeadingFile.exists());

		String firstTopicContent = getContent(firstHeadingFile);

		assertTrue(firstTopicContent.contains(
				"<topic><title>Sample Title</title><topic id=\"FirstHeading\"><title>First Heading</title><body><p>some content</p></body></topic><topic id=\"SecondHeading\"><title>Second Heading</title><body><p>some more content</p></body></topic></topic>"));
	}

	@Test
	public void testMapbookXRef() throws IOException {
		File markupFile = createTextileMarkupWithXref();
		ditaTask.setBookTitle("Sample Title");
		ditaTask.setFile(markupFile);

		ditaTask.execute();

		listFiles();

		File firstHeadingFile = new File(topicsFolder, "Id1.dita");
		assertTrue(firstHeadingFile.exists());
		File secondHeadingFile = new File(topicsFolder, "Id2.dita");
		assertTrue(secondHeadingFile.exists());

		String firstTopicContent = getContent(firstHeadingFile);
		String secondTopicContent = getContent(secondHeadingFile);

		assertTrue(firstTopicContent.contains("<xref href=\"Id2.dita#Id2\">ref to 2</xref>"));
		assertTrue(secondTopicContent.contains("<xref href=\"Id1.dita#Id1\">ref to 1</xref>"));
		assertTrue(secondTopicContent.contains("<xref href=\"#Id2\">ref to 2</xref>"));
	}

	@Test
	public void testMapbookXRef_H2() throws IOException {
		File markupFile = createTextileMarkupWithXref(2);
		ditaTask.setBookTitle("Sample Title");
		ditaTask.setFile(markupFile);
		ditaTask.setTopicStrategy(MarkupToDitaTask.BreakStrategy.FIRST);

		ditaTask.execute();

		listFiles();

		File firstHeadingFile = new File(topicsFolder, "Id1.dita");
		assertTrue(firstHeadingFile.exists());
		File secondHeadingFile = new File(topicsFolder, "Id2.dita");
		assertTrue(secondHeadingFile.exists());

		String firstTopicContent = getContent(firstHeadingFile);
		String secondTopicContent = getContent(secondHeadingFile);

		assertTrue(firstTopicContent.contains("<xref href=\"Id2.dita#Id2\">ref to 2</xref>"));
		assertTrue(secondTopicContent.contains("<xref href=\"Id1.dita#Id1\">ref to 1</xref>"));
		assertTrue(secondTopicContent.contains("<xref href=\"#Id2\">ref to 2</xref>"));
	}

	@Test
	public void testCreatesSingleTopicXref() throws IOException {
		File markupFile = createTextileMarkupWithXref();
		ditaTask.setBookTitle("Sample Title");
		ditaTask.setFile(markupFile);
		ditaTask.setFilenameFormat("$1.dita");
		ditaTask.setTopicStrategy(MarkupToDitaTask.BreakStrategy.NONE);

		ditaTask.execute();

		listFiles();

		File ditamapFile = new File(tempFolder, "markup.ditamap");
		assertFalse(ditamapFile.exists());
		File firstHeadingFile = new File(tempFolder, "markup.dita");
		assertTrue(firstHeadingFile.exists());

		String firstTopicContent = getContent(firstHeadingFile);

		assertTrue(firstTopicContent.contains("<topic id=\"Id1\">"));
		assertTrue(firstTopicContent.contains("<title>First Heading</title>"));
		assertTrue(firstTopicContent.contains("<xref href=\"#Id2\">ref to 2</xref>"));
		assertTrue(firstTopicContent.contains("<topic id=\"Id2\">"));
		assertTrue(firstTopicContent.contains("<title>Second Heading</title>"));
		assertTrue(firstTopicContent.contains("<xref href=\"#Id1\">ref to 1</xref>"));
	}

	@Test
	public void testCreatesSingleTopicXref_HL2() throws IOException {
		File markupFile = createTextileMarkupWithXref(2);
		ditaTask.setBookTitle("Sample Title");
		ditaTask.setFile(markupFile);
		ditaTask.setFilenameFormat("$1.dita");
		ditaTask.setTopicStrategy(MarkupToDitaTask.BreakStrategy.NONE);

		ditaTask.execute();

		listFiles();

		File ditamapFile = new File(tempFolder, "markup.ditamap");
		assertFalse(ditamapFile.exists());
		File firstHeadingFile = new File(tempFolder, "markup.dita");
		assertTrue(firstHeadingFile.exists());

		String firstTopicContent = getContent(firstHeadingFile);

		assertTrue(firstTopicContent.contains("<topic id=\"Id1\">"));
		assertTrue(firstTopicContent.contains("<title>First Heading</title>"));
		assertTrue(firstTopicContent.contains("<xref href=\"#Id2\">ref to 2</xref>"));
		assertTrue(firstTopicContent.contains("<topic id=\"Id2\">"));
		assertTrue(firstTopicContent.contains("<title>Second Heading</title>"));
		assertTrue(firstTopicContent.contains("<xref href=\"#Id1\">ref to 1</xref>"));
	}

	/**
	 * test for bug 269147
	 */
	@Test
	public void testFileNaming() throws IOException {
		File markupFile = createSimpleTextileMarkupWithFQNHeadings();
		ditaTask.setBookTitle("Sample Title");
		ditaTask.setFile(markupFile);

		ditaTask.execute();

		listFiles();

		File ditamapFile = new File(tempFolder, "markup.ditamap");
		assertTrue(ditamapFile.exists());
		File firstHeadingFile = new File(topicsFolder, MarkupToDitaTaskTest.class.getName() + ".dita");
		assertTrue(firstHeadingFile.exists());
		File secondHeadingFile = new File(topicsFolder, "SecondHeading.dita");
		assertTrue(secondHeadingFile.exists());

		String ditamapContent = getContent(ditamapFile);
		assertTrue(ditamapContent.contains("<bookmap>"));
		assertTrue(ditamapContent.contains("<chapter href=\"topics/" + MarkupToDitaTaskTest.class.getName()
				+ ".dita\" navtitle=\"" + MarkupToDitaTaskTest.class.getName() + "\"/>"));
		assertTrue(
				ditamapContent.contains("<chapter href=\"topics/SecondHeading.dita\" navtitle=\"Second Heading\"/>"));

		String firstTopicContent = getContent(firstHeadingFile);
//		<?xml version='1.0' ?><!DOCTYPE topic PUBLIC "-//OASIS//DTD DITA 1.1 Topic//EN" "http://docs.oasis-open.org/dita/v1.1/OS/dtd/topic.dtd">
//		<topic id="FirstHeading">
//			<title>First Heading</title>
//			<body>
//				<p>some content</p>
//			</body>
//		</topic>
		assertTrue(firstTopicContent.contains("<topic id=\"" + MarkupToDitaTaskTest.class.getName() + "\">"));
		assertTrue(firstTopicContent.contains("<title>" + MarkupToDitaTaskTest.class.getName() + "</title>"));
		assertTrue(firstTopicContent.contains("<p>some content</p>"));
	}

	@Test
	public void testTaskdef() {
		assertEquals(MarkupToDitaTask.class.getName(), loadTaskdefBundle().getString("wikitext-to-dita"));
	}

}
