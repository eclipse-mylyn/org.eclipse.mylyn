/*******************************************************************************
 * Copyright (c) 2013, 2024 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.maven.internal;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.UUID;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentBuilder.Stylesheet;
import org.eclipse.mylyn.wikitext.parser.util.MarkupToEclipseToc;
import org.eclipse.mylyn.wikitext.splitter.SplitOutlineItem;
import org.eclipse.mylyn.wikitext.splitter.SplittingHtmlDocumentBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;

@SuppressWarnings("nls")
public class MarkupToEclipseHelpMojoTest {

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private MarkupToEclipseHelpMojo markupToEclipseHelp;

	@Before
	public void setup() {
		markupToEclipseHelp = new MarkupToEclipseHelpMojo();
		markupToEclipseHelp.outputFolder = temporaryFolder.getRoot();
		markupToEclipseHelp.sourceFolder = calculateSourceFolder();
	}

	private File calculateSourceFolder() {
		URL resource = MarkupToEclipseHelpMojoTest.class.getResource("/test.textile");
		requireNonNull(resource);
		checkState(resource.getProtocol().equals("file"), "Expecting resource to have the file protocol: %s", resource);
		String path = resource.getPath();
		File file = new File(path);
		checkState(file.exists(), "Expecting file to exist: %s", file);
		checkState(file.isFile(), "Expecting file to be a file: %s", file);
		return file.getParentFile();
	}

	@Test
	public void ensureFolderExistsNotFolder() {
		File folder = mock(File.class);
		doReturn(true).when(folder).exists();
		doReturn(false).when(folder).isDirectory();
		BuildFailureException bfe = assertThrows(BuildFailureException.class,
				() -> markupToEclipseHelp.ensureFolderExists("test", folder, false));
		assertTrue(bfe.getMessage().contains("test exists but is not a folder"));
	}

	@Test
	public void ensureFolderExistsMissingNoCreate() {
		File folder = mock(File.class);
		BuildFailureException bfe = assertThrows(BuildFailureException.class,
				() -> markupToEclipseHelp.ensureFolderExists("test", folder, false));
		assertTrue(bfe.getMessage().contains("test does not exist"));
	}

	@Test
	public void ensureFolderExistsNothingToDo() {
		File folder = mock(File.class);
		doReturn(true).when(folder).exists();
		doReturn(true).when(folder).isDirectory();
		markupToEclipseHelp.ensureFolderExists("test", folder, false);
		verify(folder).exists();
		verify(folder).isDirectory();
		verifyNoMoreInteractions(folder);
	}

	@Test
	public void ensureFolderExistsMissingCreate() {
		File folder = mock(File.class);
		doReturn(true).when(folder).mkdirs();
		markupToEclipseHelp.ensureFolderExists("test", folder, true);
		verify(folder).exists();
		verify(folder).mkdirs();
		verifyNoMoreInteractions(folder);
	}

	@Test
	public void ensureFolderExistsMissingCreateFails() {
		File folder = mock(File.class);
		BuildFailureException bfe = assertThrows(BuildFailureException.class,
				() -> markupToEclipseHelp.ensureFolderExists("test", folder, true));
		assertTrue(bfe.getMessage().contains("Cannot create"));
	}

	@Test
	public void execute() throws MojoExecutionException, MojoFailureException {
		markupToEclipseHelp.multipleOutputFiles = true;
		markupToEclipseHelp.title = "Test This";
		markupToEclipseHelp.execute();

		assertTrue(computeOutputFile("test.html").exists());
		assertTrue(computeOutputFile("test-toc.xml").exists());
		assertTrue(computeOutputFile("Top-Level-Heading-2.html").exists());
		assertTrue(computeOutputFile("Top-Level-Heading-3.html").exists());
		assertTrue(computeOutputFile("images/wikitext-32.gif").exists());
		assertFalse(computeOutputFile("test.textile").exists());

		assertHasContent("test.html", "<title>Test This</title>");
		assertHasContent("test.html", "<h1 id=\"TestFile\">Test File</h1>");
		assertHasContent("test-toc.xml", "<toc topic=\"test.html\" label=\"Test This\">");
		assertHasContent("test-toc.xml", "<topic href=\"Top-Level-Heading-2.html\" label=\"Top Level Heading 2\">");
		assertHasContent("Top-Level-Heading-2.html", "<h1 id=\"TopLevelHeading2\">Top Level Heading 2</h1>");
		assertHasContent("Top-Level-Heading-3.html", "<h1 id=\"TopLevelHeading3\">Top Level Heading 3</h1>");
	}

	@Test
	public void processNonMarkupFileOverwritesTargetFile() throws IOException {
		URL resource = MarkupToEclipseHelpMojoTest.class.getResource("/test.textile");
		File file = new File(resource.getPath());

		markupToEclipseHelp.process(file, "", null);
		markupToEclipseHelp.process(file, "", null);

		assertTrue(computeOutputFile("test.textile").exists());
		assertHasContent("test.textile", Files.readString(file.toPath(), StandardCharsets.UTF_8));
	}

	@Test
	public void configureStylesheetUrls() {
		markupToEclipseHelp.stylesheetUrls = Arrays.asList("test/foo.css", "bar.css");
		HtmlDocumentBuilder builder = mock(HtmlDocumentBuilder.class);
		markupToEclipseHelp.configureStylesheets(builder, "");
		verify(builder, times(2)).addCssStylesheet(any(Stylesheet.class));
	}

	@Test
	public void configureStylesheetUrlsWithRelativePath() {
		markupToEclipseHelp.stylesheetUrls = Arrays.asList("bar.css");
		HtmlDocumentBuilder builder = mock(HtmlDocumentBuilder.class);
		markupToEclipseHelp.configureStylesheets(builder, "one/two");
		ArgumentCaptor<Stylesheet> captor = ArgumentCaptor.forClass(Stylesheet.class);
		verify(builder).addCssStylesheet(captor.capture());
		assertEquals("../../bar.css", captor.getValue().getUrl());
	}

	@Test
	public void createBuilder() {
		markupToEclipseHelp.copyrightNotice = UUID.randomUUID().toString();
		markupToEclipseHelp.title = UUID.randomUUID().toString();
		markupToEclipseHelp.htmlFilenameFormat = "$1.test.html";
		HtmlDocumentBuilder builder = markupToEclipseHelp.createRootBuilder(new StringWriter(), "test", "");
		assertEquals(markupToEclipseHelp.copyrightNotice, builder.getCopyrightNotice());
		assertEquals(markupToEclipseHelp.title, builder.getTitle());
		assertEquals(markupToEclipseHelp.htmlFilenameFormat, builder.getHtmlFilenameFormat());
	}

	@Test
	public void calculateHelpPrefixRoot() {
		assertNull(markupToEclipseHelp.calculateHelpPrefix(""));
	}

	@Test
	public void calculateHelpPrefix() {
		assertEquals("test/one", markupToEclipseHelp.calculateHelpPrefix("test/one"));
	}

	@Test
	public void calculateHelpPrefixRootWithHelpPrefix() {
		markupToEclipseHelp.helpPrefix = "help";
		assertEquals("help", markupToEclipseHelp.calculateHelpPrefix(""));
	}

	@Test
	public void calculateHelpPrefixWithHelpPrefix() {
		markupToEclipseHelp.helpPrefix = "help";
		assertEquals("help/test/one", markupToEclipseHelp.calculateHelpPrefix("test/one"));
	}

	@Test
	public void calculateHelpPrefixWindowsSeparator() {
		assertEquals("test/one", markupToEclipseHelp.calculateHelpPrefix("test\\one"));
	}

	@Test
	public void createMarkupToEclipseTocCopyrightNotice() {
		File file = mock(File.class);
		doReturn("Test.html").when(file).getName();

		markupToEclipseHelp.copyrightNotice = "Test Copyright";
		MarkupToEclipseToc toc = markupToEclipseHelp.createMarkupToEclipseToc("", file, "Test");
		assertEquals("Test Copyright", toc.getCopyrightNotice());
	}

	@Test
	public void createMarkupToEclipseTocAnchorLevel() {
		File file = mock(File.class);
		doReturn("Test.html").when(file).getName();

		MarkupToEclipseToc toc = markupToEclipseHelp.createMarkupToEclipseToc("", file, "Test");
		assertEquals(0, toc.getAnchorLevel());
		markupToEclipseHelp.tocAnchorLevel = 3;
		toc = markupToEclipseHelp.createMarkupToEclipseToc("", file, "Test");
		assertEquals(3, toc.getAnchorLevel());
	}

	@Test
	public void computeResourcePath() {
		assertEquals("styles/main.css", markupToEclipseHelp.computeResourcePath("styles/main.css", ""));
		assertEquals("../styles/main.css", markupToEclipseHelp.computeResourcePath("styles/main.css", "one"));
		assertEquals("../../styles/main.css", markupToEclipseHelp.computeResourcePath("styles/main.css", "one/two"));
		assertEquals("/styles/main.css", markupToEclipseHelp.computeResourcePath("/styles/main.css", "prefix"));
		assertEquals("http://example.com/main.css",
				markupToEclipseHelp.computeResourcePath("http://example.com/main.css", "prefix"));
	}

	@Test
	public void createSplittingBuilderWithRelativePath() {
		assertNavigationImagesPath("images", "");
		assertNavigationImagesPath("../../images", "one/two");
	}

	@Test
	public void computeResourcePathInvalidUri() {
		BuildFailureException bfe = assertThrows(BuildFailureException.class,
				() -> markupToEclipseHelp.computeResourcePath(":not valid", ""));
		assertTrue(bfe.getMessage().contains("\":not valid\" is not a valid URI"));
	}

	@Test
	public void embeddedTableOfContents() {
		HtmlDocumentBuilder builder = mock(HtmlDocumentBuilder.class);
		SplitOutlineItem item = mock(SplitOutlineItem.class);
		File htmlOutputFile = mock(File.class);
		SplittingHtmlDocumentBuilder splittingBuilder = markupToEclipseHelp.createSplittingBuilder(builder, item,
				htmlOutputFile, "");
		assertFalse(splittingBuilder.isEmbeddedTableOfContents());
		markupToEclipseHelp.embeddedTableOfContents = true;
		splittingBuilder = markupToEclipseHelp.createSplittingBuilder(builder, item, htmlOutputFile, "");
		assertTrue(splittingBuilder.isEmbeddedTableOfContents());
	}

	private void assertNavigationImagesPath(String expected, String relativePath) {
		HtmlDocumentBuilder builder = mock(HtmlDocumentBuilder.class);
		SplitOutlineItem item = mock(SplitOutlineItem.class);
		SplittingHtmlDocumentBuilder splittingBuilder = markupToEclipseHelp.createSplittingBuilder(builder, item,
				mock(File.class), relativePath);
		assertEquals(expected, splittingBuilder.getNavigationImagePath());
	}

	private void assertHasContent(String path, String expectedContent) {
		File file = computeOutputFile(path);
		assertTrue(file.toString(), file.exists());
		assertTrue(file.toString(), file.isFile());
		try {
			String content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
			assertTrue(String.format("expected %s but got %s", expectedContent, content),
					content.contains(expectedContent));
		} catch (IOException e) {
			throw new IllegalStateException(file.toString(), e);
		}
	}

	private File computeOutputFile(String path) {
		return new File(temporaryFolder.getRoot(), path);
	}

}
