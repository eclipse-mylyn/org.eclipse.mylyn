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

import java.io.IOException;
import java.net.URL;

import org.apache.tools.ant.Project;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

@SuppressWarnings("nls")
public class MediaWikiImageFetcherTest {
	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Rule
	public final TemporaryFolder serverTemporaryFolder = new TemporaryFolder();

	private final TestMediaWikiImageFetcher task = new TestMediaWikiImageFetcher();

	@Before
	public void before() {
		task.setDest(temporaryFolder.getRoot());
		task.setProject(new Project());
	}

	@Test
	public void processPageWithManyImages() throws IOException {
		MediaWikiMockFixture mediaWikiMockFixture = new MediaWikiMockFixture(serverTemporaryFolder.getRoot());
		mediaWikiMockFixture.createImageFiles();
		task.setImageServerContent(mediaWikiMockFixture.createImageServerContent("http"));

		task.setUrl(new URL("http://wiki.eclipse.org/"));
		String wikiPageName = "Some/My_Page";
		task.setPageName(wikiPageName);

		task.execute();

		mediaWikiMockFixture.assertImageFiles(temporaryFolder.getRoot());
	}

}
