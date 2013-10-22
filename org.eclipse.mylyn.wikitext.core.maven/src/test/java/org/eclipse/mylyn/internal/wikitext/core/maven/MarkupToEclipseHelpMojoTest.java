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

package org.eclipse.mylyn.internal.wikitext.core.maven;

import static com.google.common.base.Preconditions.checkState;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.UUID;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder.Stylesheet;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

public class MarkupToEclipseHelpMojoTest {

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder() {
		@Override
		public void delete() {
			// ignore
		}
	};

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private MarkupToEclipseHelpMojo markupToEclipseHelp;

	@Before
	public void setup() {
		markupToEclipseHelp = new MarkupToEclipseHelpMojo();
		markupToEclipseHelp.outputFolder = temporaryFolder.getRoot();
		markupToEclipseHelp.sourceFolder = calculateSourceFolder();
	}

	private File calculateSourceFolder() {
		URL resource = MarkupToEclipseHelpMojoTest.class.getResource("/test.textile");
		checkState(resource.getProtocol().equals("file"));
		String path = resource.getPath();
		File file = new File(path);
		checkState(file.exists());
		checkState(file.isFile());
		return file.getParentFile();
	}

	@Test
	public void ensureFolderExistsNotFolder() {
		File folder = mock(File.class);
		doReturn(true).when(folder).exists();
		doReturn(false).when(folder).isDirectory();
		thrown.expect(BuildFailureException.class);
		thrown.expectMessage("test exists but is not a folder");
		markupToEclipseHelp.ensureFolderExists("test", folder, false);
	}

	@Test
	public void ensureFolderExistsMissingNoCreate() {
		File folder = mock(File.class);
		thrown.expect(BuildFailureException.class);
		thrown.expectMessage("test does not exist");
		markupToEclipseHelp.ensureFolderExists("test", folder, false);
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
		thrown.expect(BuildFailureException.class);
		thrown.expectMessage("Cannot create");
		markupToEclipseHelp.ensureFolderExists("test", folder, true);
	}

	@Test
	public void execute() throws MojoExecutionException, MojoFailureException {
		markupToEclipseHelp.multipleOutputFiles = true;
		markupToEclipseHelp.title = "Test This";
		markupToEclipseHelp.execute();

		assertTrue(computeOutputFile("test.html").exists());
		assertTrue(computeOutputFile("Top-Level-Heading-2.html").exists());
		assertTrue(computeOutputFile("Top-Level-Heading-3.html").exists());
		assertTrue(computeOutputFile("images/wikitext-32.gif").exists());
		assertFalse(computeOutputFile("test.textile").exists());

		assertHasContent("test.html", "<title>Test This</title>");
		assertHasContent("test.html", "<h1 id=\"TestFile\">Test File</h1>");
		assertHasContent("Top-Level-Heading-2.html", "<h1 id=\"TopLevelHeading2\">Top Level Heading 2</h1>");
		assertHasContent("Top-Level-Heading-3.html", "<h1 id=\"TopLevelHeading3\">Top Level Heading 3</h1>");
	}

	@Test
	public void configureStylesheetUrls() {
		markupToEclipseHelp.stylesheetUrls = Lists.newArrayList("test/foo.css", "bar.css");
		HtmlDocumentBuilder builder = mock(HtmlDocumentBuilder.class);
		markupToEclipseHelp.configureStylesheets(builder);
		verify(builder, times(2)).addCssStylesheet(any(Stylesheet.class));
	}

	@Test
	public void createBuilder() {
		markupToEclipseHelp.copyrightNotice = UUID.randomUUID().toString();
		markupToEclipseHelp.title = UUID.randomUUID().toString();
		HtmlDocumentBuilder builder = markupToEclipseHelp.createRootBuilder(new StringWriter(), "test");
		assertEquals(markupToEclipseHelp.copyrightNotice, builder.getCopyrightNotice());
		assertEquals(markupToEclipseHelp.title, builder.getTitle());
	}

	private void assertHasContent(String path, String expectedContent) {
		File file = computeOutputFile(path);
		assertTrue(file.toString(), file.exists());
		assertTrue(file.toString(), file.isFile());
		try {
			String content = Files.toString(file, Charsets.UTF_8);
			assertTrue(String.format("expected %s but got %s", expectedContent, content),
					content.contains(expectedContent));
		} catch (IOException e) {
			throw new IllegalStateException(file.toString(), e);
		}
	}

	private File computeOutputFile(String path) {
		return new File(temporaryFolder.getRoot(), path);
	}

	@After
	public void listFiles() {
		listFiles("", temporaryFolder.getRoot());
	}

	private void listFiles(String string, File base) {
		File[] children = base.listFiles();
		if (children != null) {
			for (File child : children) {
				String path = string + "/" + child.getName();
				System.out.println(path);
				if (child.isDirectory()) {
					listFiles(path, child);
				}
			}
		}
	}
}
