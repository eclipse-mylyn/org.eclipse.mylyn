/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.parser.builder;

import static java.text.MessageFormat.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.splitter.DefaultSplittingStrategy;
import org.eclipse.mylyn.wikitext.splitter.SplitOutlineItem;
import org.eclipse.mylyn.wikitext.splitter.SplittingHtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.splitter.SplittingOutlineParser;
import org.eclipse.mylyn.wikitext.textile.TextileLanguage;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.io.Resources;

public class SplittingHtmlDocumentBuilderTest {

	@Rule
	public final TemporaryFolder temporaryFolderRule = new TemporaryFolder();

	private final SplittingHtmlDocumentBuilder builder = new SplittingHtmlDocumentBuilder();

	private HtmlDocumentBuilder rootBuilder;

	private File outputFile;

	@Before
	public void before() throws IOException {
		outputFile = temporaryFolderRule.newFile("index.html");
	}

	@Test
	public void embeddedTableOfContents() throws IOException {
		assertFalse(builder.isEmbeddedTableOfContents());

		generateContents("h1. First\n\nh2. First.1\n\nh1. Second\n\nh2. Second.1\n\ntext", true);

		assertFileContents("embeddedTableOfContents_First.html.txt", outputFile);
		assertFileContents("embeddedTableOfContents_Second.html.txt",
				new File(outputFile.getParentFile(), "Second.html"));
	}

	@Test
	public void headingLinks() throws IOException {
		generateContents(
				"{toc}\n\nh1. First\n\nh2. First.1\n\nh1. Second\n\n\"link to first\":#First and \"link to first.1\":#First.1\n\nh2. Second.1\n\ntext",
				false);

		assertFileContents("headingLinks_First.html.txt", outputFile);
		assertFileContents("headingLinks_Second.html.txt", new File(outputFile.getParentFile(), "Second.html"));
	}

	private void generateContents(String markup, boolean embeddedTableOfContents)
			throws IOException, FileNotFoundException {
		try (OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(outputFile),
				StandardCharsets.UTF_8)) {
			rootBuilder = new HtmlDocumentBuilder(out, true);

			SplittingOutlineParser outlineParser = new SplittingOutlineParser();
			outlineParser.setMarkupLanguage(new TextileLanguage());
			outlineParser.setSplittingStrategy(new DefaultSplittingStrategy());
			SplitOutlineItem outline = outlineParser.parse(markup);
			outline.setSplitTarget(outputFile.getName());

			builder.setEmbeddedTableOfContents(embeddedTableOfContents);
			builder.setRootBuilder(rootBuilder);
			builder.setRootFile(outputFile);
			builder.setFormatting(true);
			builder.setOutline(outline);

			MarkupParser parser = new MarkupParser(new TextileLanguage());
			parser.setBuilder(builder);
			parser.parse(markup);
		}
	}

	private void assertFileContents(String resource, File outputFile) throws IOException {
		String resourcePath = "resources/SplittingHtmlDocumentBuilderTest_" + resource;
		String resourceContents = convertToUnixLineEndings(Resources
				.toString(SplittingHtmlDocumentBuilderTest.class.getResource(resourcePath), StandardCharsets.UTF_8));
		String actualContents = Resources.toString(outputFile.toURI().toURL(), StandardCharsets.UTF_8);
		assertEquals(format("Resource {0} differs", resourcePath), resourceContents, actualContents);
	}

	private String convertToUnixLineEndings(String resource) {
		return resource.replaceAll("\\r\\n?", "\n");
	}
}
