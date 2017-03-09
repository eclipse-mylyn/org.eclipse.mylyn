/*******************************************************************************
 * Copyright (c) 2016 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.mediawiki.ant.internal.tasks;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.apache.tools.ant.Project;
import org.eclipse.mylyn.wikitext.mediawiki.ant.internal.tasks.WikiToDocTask.Path;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

@Ignore // bug 513425
public class WikiToDocTaskIntegrationTest {

	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	private WikiToDocTask task;

	@Before
	public void before() throws IOException {
		task = new WikiToDocTask();
		task.setDest(temporaryFolder.getRoot());
		task.setProject(new Project());
	}

	@Test
	public void processPageWithManyImages() {
		task.setWikiBaseUrl("https://wiki.eclipse.org");
		task.setPrependImagePrefix("images");
		task.setFormatOutput(true);
		task.setGenerateUnifiedToc(false);

		Path path = new Path();
		path.setTitle("Linux Tools Project - User Guide");
		String wikiPageName = "Linux_Tools_Project/Vagrant_Tooling/User_Guide";
		path.setName(wikiPageName);
		path.setGenerateToc(true);

		task.setPaths(Collections.singletonList(path));
		task.execute();

		File wikiPageFolder = new File(temporaryFolder.getRoot(), wikiPageName);
		assertManyImages(wikiPageFolder);
	}

	private void assertManyImages(File wikiPageFolder) {
		assertTrue(wikiPageFolder.exists());

		File imagesFolder = new File(wikiPageFolder, "images");
		WikiPageAssertions.assertManyImages(imagesFolder);
	}
}
