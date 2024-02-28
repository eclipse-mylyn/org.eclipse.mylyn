/*******************************************************************************
 * Copyright (c) 2007, 2024 David Green and others.
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

package org.eclipse.mylyn.wikitext.mediawiki.ant.internal.tasks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.mylyn.wikitext.mediawiki.ant.internal.tasks.WikiToDocTask.Path;
import org.eclipse.mylyn.wikitext.mediawiki.ant.internal.tasks.WikiToDocTask.Stylesheet;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

@SuppressWarnings("nls")
public class WikiToDocTaskTest {

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	private TestWikiToDocTask task;

	@Before
	public void before() {
		task = new TestWikiToDocTask();
		HashMap<String, String> serverContent = new HashMap<>();
		serverContent.put(
				"http://wiki.eclipse.org/api.php?action=query&titles=GEF%2FGEF4%2FCommon&generator=images&prop=imageinfo&iiprop=url&format=xml",
				"<api batchcomplete=\"\"><query><pages></pages></query></api>");
		serverContent.put(
				"http://wiki.eclipse.org/api.php?action=query&titles=Mylyn%2FFAQ&generator=images&prop=imageinfo&iiprop=url&format=xml",
				"<api batchcomplete=\"\"><query><pages></pages></query></api>");
		task.setImageServerContent(serverContent);
		task.setDest(temporaryFolder.getRoot());
	}

	@Test
	public void stylesheetUrlRelative() {
		File dest = task.getDest();
		Stylesheet stylesheet = new Stylesheet();
		stylesheet.setUrl("a/b.css");
		assertEquals("a/b.css", task.createBuilderStylesheet(dest, stylesheet).getUrl());
		assertEquals("../a/b.css", task.createBuilderStylesheet(new File(dest, "styles"), stylesheet).getUrl());
	}

	@Test
	public void stylesheetUrlAbsolute() {
		Stylesheet stylesheet = new Stylesheet();
		stylesheet.setUrl("http://example.com/a/b.css");
		assertEquals("http://example.com/a/b.css", task.createBuilderStylesheet(task.getDest(), stylesheet).getUrl());
	}

	@Ignore
	@Test
	public void testMapPageNameToHref() throws Exception {
		//Bug 467067
		Map<String, String> serverContent = Collections.singletonMap(
				"http://wiki.eclipse.org/index.php?title=GEF%2FGEF4%2FCommon&action=raw",
				"Link to [[GEF/GEF4/Common#IActivatable|IActivatable]] is here\n\n==IActivatable==\nLorem ipsum");

		task.setServerContent(serverContent);
		task.setMarkupLanguage("MediaWiki");
		task.setWikiBaseUrl("http://wiki.eclipse.org");
		task.setNavigationImages(true);
		task.setValidate(true);
		task.setFailOnValidationError(true);
		task.setPrependImagePrefix("images");
		task.setFormatOutput(false);
		task.setDefaultAbsoluteLinkTarget("doc_external");
		task.setHelpPrefix("reference/wiki");
		task.setGenerateUnifiedToc(false);
		Path path = new Path();
		path.setTitle("GEF4 Common Documentation");
		path.setName("GEF/GEF4/Common");
		path.setGenerateToc(true);
		task.setPaths(Collections.singletonList(path));
		task.execute();

		File result = task.computeHtmlOutputFile(path);
		String content = Files.readString(result.toPath(), StandardCharsets.UTF_8);
		assertTrue(content.contains(
				"<p>Link to <a href=\"#IActivatable\" title=\"GEF/GEF4/Common#IActivatable\">IActivatable</a> is here</p>"));
	}

	@Ignore
	@Test
	public void testLink() throws Exception {
		//Bug 444459
		StringBuilder sb = new StringBuilder();
		sb.append("= What is Mylyn? =\n\n");
		sb.append("= Installation =\n\n");
		sb.append("=== Recommended GTK Setup for KDE ===\n\n");
		sb.append("There is a recommended GTK theme to use for KDE.\n\n");
		sb.append("= Task List =\n\n");
		sb.append("== Why are closed tasks not greyed out on Linux? ==\n\n");
		sb.append("See the [[Mylyn/FAQ#Recommended_GTK_Setup_for_KDE|Recommended GTK Setup for KDE]] section.\n\n");

		Map<String, String> serverContent = Collections
				.singletonMap("http://wiki.eclipse.org/index.php?title=Mylyn%2FFAQ&action=raw", sb.toString());

		task.setServerContent(serverContent);
		task.setMarkupLanguage("MediaWiki");
		task.setWikiBaseUrl("http://wiki.eclipse.org");
		task.setValidate(true);
		task.setFailOnValidationError(true);
		task.setPrependImagePrefix("images");
		task.setFormatOutput(true);
		task.setDefaultAbsoluteLinkTarget("mylyn_external");
		task.setTitle("Mylyn");
		task.setGenerateUnifiedToc(false);
		Path path = new Path();
		path.setTitle("Mylyn FAQ");
		path.setName("Mylyn/FAQ");
		path.setGenerateToc(true);
		task.setPaths(Collections.singletonList(path));
		task.execute();

		File mainPage = task.computeHtmlOutputFile(path);
		assertEquals("main page exists", true, mainPage.exists());
		File installationPage = new File(mainPage.getParentFile(), "Installation.html");
		assertEquals("'Installation.html' page exists", true, installationPage.exists());
		File taskListPage = new File(mainPage.getParentFile(), "Task-List.html");
		assertEquals("'Task-List.html' page exists", true, taskListPage.exists());
		String content = Files.readString(taskListPage.toPath(), StandardCharsets.UTF_8);
		assertTrue(content.contains(
				"<a href=\"Installation.html#Recommended_GTK_Setup_for_KDE\" title=\"Mylyn/FAQ#Recommended_GTK_Setup_for_KDE\">Recommended GTK Setup for KDE</a> section."));
	}
}
