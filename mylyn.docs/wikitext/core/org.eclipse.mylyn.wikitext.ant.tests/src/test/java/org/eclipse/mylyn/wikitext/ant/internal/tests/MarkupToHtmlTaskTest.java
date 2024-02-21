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
 *     Torkild U. Resheim - Handle links when transforming file based wiki
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.ant.internal.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.ant.internal.MarkupToHtmlTask;
import org.junit.Before;
import org.junit.Test;

/**
 * @author David Green
 * @author Torkild U. Resheim
 */
@SuppressWarnings("nls")
public class MarkupToHtmlTaskTest extends AbstractTestAntTask {

	protected MarkupToHtmlTask task;

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		task = createTask();
		task.setFormatOutput(true);
		task.setMarkupLanguage(languageName);
	}

	protected MarkupToHtmlTask createTask() {
		return new MarkupToHtmlTask();
	}

	@Test
	public void testSimpleOutput() throws IOException {
		File markup = createSimpleTextileMarkup();
		task.setFile(markup);
		task.execute();

		listFiles();

		File htmlFile = new File(markup.getParentFile(), "markup.html");
		assertTrue(htmlFile.exists() && htmlFile.isFile());

		String content = getContent(htmlFile);
//

		assertTrue(content.contains("<html"));
		assertTrue(content.contains("</html>"));
		assertTrue(content.contains("<title>markup</title>"));
		assertTrue(content.contains("<body>"));
		assertTrue(content.contains("</body>"));
	}

	@Test
	public void testSimpleOutputStrictXHTML() throws IOException {
		File markup = createSimpleTextileMarkupWithImage();
		task.setFile(markup);
		task.setXhtmlStrict(true);
		task.execute();

		listFiles();

		File htmlFile = new File(markup.getParentFile(), "markup.html");
		assertTrue(htmlFile.exists() && htmlFile.isFile());

		String content = getContent(htmlFile);

		// verify that alt is present on img tag.
		assertTrue(Pattern.compile("<img.*?alt=\"\"").matcher(content).find());
	}

	@Test
	public void testSimpleOutputAlternateTitle() throws IOException {
		File markup = createSimpleTextileMarkup();
		task.setFile(markup);
		task.setTitle("Alternate Title");
		task.execute();

		listFiles();

		File htmlFile = new File(markup.getParentFile(), "markup.html");
		assertTrue(htmlFile.exists() && htmlFile.isFile());

		String content = getContent(htmlFile);
//

		assertTrue(content.contains("<html"));
		assertTrue(content.contains("</html>"));
		assertTrue(content.contains("<title>Alternate Title</title>"));
		assertTrue(content.contains("<body>"));
		assertTrue(content.contains("</body>"));
	}

	@Test
	public void testMultipleFiles() throws IOException {
		File markup = createSimpleTextileMarkup();
		task.setFile(markup);
		task.setMultipleOutputFiles(true);
		task.execute();

		listFiles();

		File htmlFile = new File(markup.getParentFile(), "markup.html");
		assertTrue(htmlFile.exists() && htmlFile.isFile());

		String content = getContent(htmlFile);
//

		assertTrue(content.contains("<html"));
		assertTrue(content.contains("</html>"));
		assertTrue(content.contains("<title>markup</title>"));
		assertTrue(content.contains("<body>"));
		assertTrue(content.contains("</body>"));
		assertTrue(content.contains("<a href=\"Second-Heading.html\" title=\"Second Heading\">Next</a>"));
		assertTrue(Pattern.compile("<td[^>]*>Second Heading</td>").matcher(content).find());

		File htmlFile2 = new File(markup.getParentFile(), "Second-Heading.html");
		assertTrue(htmlFile2.exists());

		String content2 = getContent(htmlFile2);
//

		assertTrue(content2.contains("<html"));
		assertTrue(content2.contains("</html>"));
		assertTrue(content2.contains("<title>markup - Second Heading</title>"));
		assertTrue(content2.contains("<body>"));
		assertTrue(content2.contains("</body>"));
		assertTrue(content2.contains("<a href=\"markup.html\" title=\"First Heading\">Previous</a>"));
		assertTrue(Pattern.compile("<td[^>]*>First Heading</td>").matcher(content2).find());
	}

	@Test
	public void testMultipleFilesWithCrossReferences() throws IOException {
		File markup = createTextileMarkupFile(
				"h1. Heading One\n\n\"link to two\":#HeadingTwo\n\n\"link to two point one\":#HeadingTwoPointOne\n\nh1. Heading Two\n\nh2. Heading Two Point One\n\n\"link to one\":#HeadingOne\n");
		task.setFile(markup);
		task.setMultipleOutputFiles(true);
		task.execute();

		listFiles();

		File htmlFile = new File(markup.getParentFile(), "markup.html");
		assertTrue(htmlFile.exists() && htmlFile.isFile());

		String content = getContent(htmlFile);

		assertTrue(content.contains("<a href=\"Heading-Two.html#HeadingTwo\">link to two</a>"));
		assertTrue(content.contains("<a href=\"Heading-Two.html#HeadingTwoPointOne\">link to two point one</a>"));

		File htmlFile2 = new File(markup.getParentFile(), "Heading-Two.html");
		assertTrue(htmlFile2.exists());

		String content2 = getContent(htmlFile2);

		assertTrue(content2.contains("<a href=\"markup.html#HeadingOne\">link to one</a>"));
	}

	@Test
	public void testTocFilenameCorrectnessSingleFile() throws IOException {
		File markup = createTextileMarkupFile(
				"{toc}\n\nh1. Heading One\n\nh1. Heading Two\n\nh2. Heading Two Point One");
		task.setFile(markup);
		task.setMultipleOutputFiles(false);
		task.execute();

		listFiles();

		File htmlFile = new File(markup.getParentFile(), "markup.html");
		assertTrue(htmlFile.exists() && htmlFile.isFile());

		String content = getContent(htmlFile);

		assertTrue(content.contains("<a href=\"#HeadingOne\">Heading One</a>"));
		assertTrue(content.contains("<a href=\"#HeadingTwo\">Heading Two</a>"));
	}

	@Test
	public void testTocFilenameCorrectnessMultipleFiles() throws IOException {
		File markup = createTextileMarkupFile(
				"{toc}\n\nh1. Heading One\n\nh1. Heading Two\n\nh2. Heading Two Point One\n\n\"link\":#HeadingOne");
		task.setFile(markup);
		task.setMultipleOutputFiles(true);
		task.execute();

		listFiles();

		File htmlFile = new File(markup.getParentFile(), "markup.html");
		assertTrue(htmlFile.exists() && htmlFile.isFile());

		String content = getContent(htmlFile);

		assertTrue(content.contains("<a href=\"#HeadingOne\">Heading One</a>"));
		assertTrue(content.contains("<a href=\"Heading-Two.html#HeadingTwo\">Heading Two</a>"));
		assertTrue(content.contains("<a href=\"Heading-Two.html#HeadingTwoPointOne\">Heading Two Point One</a>"));

		// navigation
		assertTrue(content.contains("<a href=\"Heading-Two.html\" title=\"Heading Two\">Next</a>"));

		htmlFile = new File(markup.getParentFile(), "Heading-Two.html");
		assertTrue(htmlFile.exists() && htmlFile.isFile());

		content = getContent(htmlFile);

		assertTrue(content.contains("<a href=\"markup.html\" title=\"Heading One\">Previous</a>"));
		assertTrue(content.contains("<a href=\"markup.html#HeadingOne\">link</a>"));
	}

	@Test
	public void testWithJavadocLink() throws IOException {
		File markup = createTextileMarkupFile("\"Test\":@.Test");
		task.setFile(markup);
		task.setJavadocBasePackageName("org.eclipse.mylyn.wikitext");
		task.execute();

		File htmlFile = new File(markup.getParentFile(), "markup.html");
		assertTrue(htmlFile.exists() && htmlFile.isFile());

		String content = getContent(htmlFile);

		assertTrue(content, content
				.contains("<a href=\"index.html?org/eclipse/mylyn/wikitext/Test.html\" target=\"_javadoc\">Test</a>"));
	}

	protected File createSimpleTextileMarkup() throws IOException {
		StringWriter out = new StringWriter();
		PrintWriter writer = new PrintWriter(out);
		try (writer) {
			writer.println("h1. First Heading");
			writer.println();
			writer.println("some content");
			writer.println();
			writer.println("h1. Second Heading");
			writer.println();
			writer.println("some more content");
		}
		return createTextileMarkupFile(out.toString());
	}

	protected File createTextileMarkupFile(String content) throws IOException {
		File markupFile = new File(tempFolder, "markup.textile");
		Writer writer = new FileWriter(markupFile);
		try (writer) {
			writer.write(content);
		}
		return markupFile;
	}

	protected File createSimpleTextileMarkupWithImage() throws IOException {
		File markupFile = new File(tempFolder, "markup.textile");
		try (PrintWriter writer = new PrintWriter(new FileWriter(markupFile))) {
			writer.println("some content with !image.png! an image");
		}
		return markupFile;
	}

	@Test
	public void testTaskdef() {
		assertEquals(MarkupToHtmlTask.class.getName(), loadTaskdefBundle().getString("wikitext-to-html"));
	}

	@Test
	public void testMultipleFiles_LinkToConvertedMarkupDocument() throws IOException {
		File markup = createTextileMarkupFile(
				"h1. Heading One\n\n\"a link\":foo#bar\n\nh1. Heading Two\n\n\"a link\":foo#bar\n");
		task.setFile(markup);
		task.setMultipleOutputFiles(true);
		task.execute();

		listFiles();

		File htmlFile = new File(markup.getParentFile(), "markup.html");
		assertTrue(htmlFile.exists() && htmlFile.isFile());

		String content = getContent(htmlFile);
		//
		assertTrue(content.contains("<a href=\"foo.html#bar\">a link</a>"));

		File htmlFile2 = new File(markup.getParentFile(), "Heading-Two.html");
		assertTrue(htmlFile2.exists());

		String content2 = getContent(htmlFile2);
		//
		assertTrue(content2.contains("<a href=\"foo.html#bar\">a link</a>"));
	}

}
