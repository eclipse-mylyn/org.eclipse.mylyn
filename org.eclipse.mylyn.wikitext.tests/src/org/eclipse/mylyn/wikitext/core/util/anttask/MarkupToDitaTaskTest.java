/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.core.util.anttask;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.eclipse.mylyn.wikitext.textile.core.TextileLanguage;

public class MarkupToDitaTaskTest extends AbstractTestAntTask {

	private MarkupToDitaTask ditaTask;

	private File topicsFolder;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		ditaTask = new MarkupToDitaTask();
		ditaTask.setMarkupLanguage(TextileLanguage.class.getName());

		topicsFolder = new File(tempFolder, ditaTask.getTopicFolder());
		topicsFolder.mkdirs();
	}

	private File createSimpleTextileMarkup() throws IOException {
		File markupFile = new File(tempFolder, "markup.textile");
		PrintWriter writer = new PrintWriter(new FileWriter(markupFile));
		try {
			writer.println("h1. First Heading");
			writer.println();
			writer.println("some content");
			writer.println();
			writer.println("h1. Second Heading");
			writer.println();
			writer.println("some more content");
		} finally {
			writer.close();
		}
		return markupFile;
	}

	private File createTextileMarkupWithXref() throws IOException {
		return createTextileMarkupWithXref(1);
	}

	private File createTextileMarkupWithXref(int headingLevel) throws IOException {
		File markupFile = new File(tempFolder, "markup.textile");
		PrintWriter writer = new PrintWriter(new FileWriter(markupFile));
		try {
			writer.println("h" + headingLevel + "(#Id1). First Heading");
			writer.println();
			writer.println("some content with a \"ref to 2\":#Id2");
			writer.println();
			writer.println("h" + headingLevel + "(#Id2). Second Heading");
			writer.println();
			writer.println("some more content with with a \"ref to 1\":#Id1 and a \"ref to 2\":#Id2");
		} finally {
			writer.close();
		}
		return markupFile;
	}

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
		assertTrue(ditamapContent.contains("<chapter href=\"topics/SecondHeading.dita\" navtitle=\"Second Heading\"/>"));

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

		System.out.println(firstTopicContent);

		assertTrue(firstTopicContent.contains("<topic id=\"FirstHeading\">"));
		assertTrue(firstTopicContent.contains("<title>First Heading</title>"));
		assertTrue(firstTopicContent.contains("<p>some content</p>"));
		assertTrue(firstTopicContent.contains("<topic id=\"SecondHeading\">"));
		assertTrue(firstTopicContent.contains("<title>Second Heading</title>"));
		assertTrue(firstTopicContent.contains("<p>some more content</p>"));
	}

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

		System.out.println(firstTopicContent);
		System.out.println(secondTopicContent);

		assertTrue(firstTopicContent.contains("<xref href=\"Id2.dita#Id2\">ref to 2</xref>"));
		assertTrue(secondTopicContent.contains("<xref href=\"Id1.dita#Id1\">ref to 1</xref>"));
		assertTrue(secondTopicContent.contains("<xref href=\"#Id2\">ref to 2</xref>"));
	}

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

		System.out.println(firstTopicContent);

		assertTrue(firstTopicContent.contains("<topic id=\"Id1\">"));
		assertTrue(firstTopicContent.contains("<title>First Heading</title>"));
		assertTrue(firstTopicContent.contains("<xref href=\"#Id2\">ref to 2</xref>"));
		assertTrue(firstTopicContent.contains("<topic id=\"Id2\">"));
		assertTrue(firstTopicContent.contains("<title>Second Heading</title>"));
		assertTrue(firstTopicContent.contains("<xref href=\"#Id1\">ref to 1</xref>"));
	}

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

		System.out.println(firstTopicContent);

		assertTrue(firstTopicContent.contains("<topic id=\"Id1\">"));
		assertTrue(firstTopicContent.contains("<title>First Heading</title>"));
		assertTrue(firstTopicContent.contains("<xref href=\"#Id2\">ref to 2</xref>"));
		assertTrue(firstTopicContent.contains("<topic id=\"Id2\">"));
		assertTrue(firstTopicContent.contains("<title>Second Heading</title>"));
		assertTrue(firstTopicContent.contains("<xref href=\"#Id1\">ref to 1</xref>"));
	}

}
