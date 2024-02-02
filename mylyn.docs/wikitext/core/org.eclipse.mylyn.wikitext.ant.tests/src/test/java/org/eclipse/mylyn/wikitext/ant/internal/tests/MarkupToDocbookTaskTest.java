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
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.ant.internal.MarkupToDocbookTask;
import org.junit.Before;
import org.junit.Test;

public class MarkupToDocbookTaskTest extends AbstractTestAntTask {

	private MarkupToDocbookTask task;

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		task = new MarkupToDocbookTask();
		task.setMarkupLanguage(languageName);
	}

	@Test
	public void testSimpleOutput() throws IOException {
		File markup = createSimpleTextileMarkup();
		task.setFile(markup);
		task.execute();

		listFiles();

		File docbookFile = new File(markup.getParentFile(), "markup.xml");
		assertTrue(docbookFile.exists() && docbookFile.isFile());

		String content = getContent(docbookFile);
//

		assertTrue(content.contains("<book"));
		assertTrue(content.contains("</book>"));
		assertTrue(content.contains("<title>markup</title>"));
		assertTrue(Pattern.compile(
				"<chapter id=\"FirstHeading\">\\s*<title>First Heading</title>\\s*<para>some content</para>\\s*</chapter>",
				Pattern.MULTILINE).matcher(content).find());
		assertTrue(Pattern.compile(
				"<chapter id=\"SecondHeading\">\\s*<title>Second Heading</title>\\s*<para>some more content</para>\\s*</chapter>",
				Pattern.MULTILINE).matcher(content).find());

	}

	@Test
	public void testSimpleOutputAlternateTitle() throws IOException {
		File markup = createSimpleTextileMarkup();
		task.setFile(markup);
		task.setBookTitle("Alternate Title");
		task.execute();

		listFiles();

		File docbookFile = new File(markup.getParentFile(), "markup.xml");
		assertTrue(docbookFile.exists() && docbookFile.isFile());

		String content = getContent(docbookFile);
//

		assertTrue(content.contains("<book"));
		assertTrue(content.contains("</book>"));
		assertTrue(content.contains("<title>Alternate Title</title>"));
		assertTrue(Pattern.compile(
				"<chapter id=\"FirstHeading\">\\s*<title>First Heading</title>\\s*<para>some content</para>\\s*</chapter>",
				Pattern.MULTILINE).matcher(content).find());
		assertTrue(Pattern.compile(
				"<chapter id=\"SecondHeading\">\\s*<title>Second Heading</title>\\s*<para>some more content</para>\\s*</chapter>",
				Pattern.MULTILINE).matcher(content).find());

	}

	protected File createSimpleTextileMarkup() throws IOException {
		File markupFile = new File(tempFolder, "markup.textile");
		PrintWriter writer = new PrintWriter(new FileWriter(markupFile));
		try (writer) {
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

	@Test
	public void testTaskdef() {
		assertEquals(MarkupToDocbookTask.class.getName(), loadTaskdefBundle().getString("wikitext-to-docbook"));
	}
}
