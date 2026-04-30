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
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.mediawiki.ant.internal.tasks;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.tools.ant.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

@SuppressWarnings("nls")
public class MediaWikiImageFetcherTest {
	@TempDir
	File temporaryFolder;

	@TempDir
	File serverTemporaryFolder;

	private final TestMediaWikiImageFetcher task = new TestMediaWikiImageFetcher();

	@BeforeEach
	public void before() {
		task.setDest(temporaryFolder);
		task.setProject(new Project());
	}

	@Test
	public void processPageWithManyImages() throws IOException, URISyntaxException {
		MediaWikiMockFixture mediaWikiMockFixture = new MediaWikiMockFixture(serverTemporaryFolder);
		mediaWikiMockFixture.createImageFiles();
		task.setImageServerContent(mediaWikiMockFixture.createImageServerContent("http"));

		task.setUrl(new URI("http://wiki.eclipse.org/").toURL());
		String wikiPageName = "Some/My_Page";
		task.setPageName(wikiPageName);

		task.execute();

		mediaWikiMockFixture.assertImageFiles(temporaryFolder);
	}

}
