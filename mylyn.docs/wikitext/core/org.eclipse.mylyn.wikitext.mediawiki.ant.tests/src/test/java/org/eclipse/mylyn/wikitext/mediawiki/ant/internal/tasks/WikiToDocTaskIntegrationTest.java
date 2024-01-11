/*******************************************************************************
 * Copyright (c) 2016, 2024 Tasktop Technologies and others.
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

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.util.Collections;

import org.apache.tools.ant.Project;
import org.eclipse.mylyn.wikitext.mediawiki.ant.internal.tasks.WikiToDocTask.Path;
import org.eclipse.mylyn.wikitext.toolkit.TestResources;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class WikiToDocTaskIntegrationTest {

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Rule
	public final TemporaryFolder serverTemporaryFolder = new TemporaryFolder();

	private TestWikiToDocTask task;

	@Before
	public void before() {
		task = new TestWikiToDocTask();
		task.setDest(temporaryFolder.getRoot());
		task.setProject(new Project());
	}

	@Test
	public void processPageWithManyImages() throws Exception {
		MediaWikiMockFixture mediaWikiMockFixture = new MediaWikiMockFixture(serverTemporaryFolder.getRoot());
		Files.createTempFile("image", "");

		task.setServerContent(
				Collections.singletonMap("https://wiki.eclipse.org/index.php?title=Some%2FMy_Page&action=raw",
						TestResources.load(this.getClass(), "WikiToDocTaskIntegrationTest.mediawiki")));

		mediaWikiMockFixture.createImageFiles();

		task.setImageServerContent(mediaWikiMockFixture.createImageServerContent("https"));

		task.setWikiBaseUrl("https://wiki.eclipse.org");
		task.setPrependImagePrefix("images");
		task.setFormatOutput(true);
		task.setGenerateUnifiedToc(false);

		Path path = new Path();
		path.setTitle("Some - My Page!");
		String wikiPageName = "Some/My_Page";
		path.setName(wikiPageName);
		path.setGenerateToc(true);

		task.setPaths(Collections.singletonList(path));
		task.execute();

		File wikiPageFolder = new File(temporaryFolder.getRoot(), wikiPageName);

		assertTrue(wikiPageFolder.exists());
		mediaWikiMockFixture.assertImageFiles(new File(wikiPageFolder, "images"));
	}
}
