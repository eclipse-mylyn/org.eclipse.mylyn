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

package org.eclipse.mylyn.internal.wikitext.mediawiki.core.ant.tasks;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.tools.ant.Project;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class MediaWikiImageFetcherTest {
	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	private final MediaWikiImageFetcher task = new MediaWikiImageFetcher();

	@Before
	public void before() throws IOException {
		task.setDest(temporaryFolder.getRoot());
		task.setProject(new Project());
	}

	@Test
	public void processPageWithManyImages() throws MalformedURLException {
		task.setUrl(new URL("http://wiki.eclipse.org/"));
		String wikiPageName = "Linux_Tools_Project/Vagrant_Tooling/User_Guide";
		task.setPageName(wikiPageName);

		task.execute();

		WikiPageAssertions.assertManyImages(temporaryFolder.getRoot());
	}

}
