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

package org.eclipse.mylyn.internal.wikitext.mediawiki.core.tasks;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.eclipse.mylyn.internal.wikitext.mediawiki.core.tasks.WikiToDocTask.Stylesheet;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class WikiToDocTaskTest {
	@Rule
	public final TemporaryFolder temporaryFolder = new TemporaryFolder();

	private final WikiToDocTask task = new WikiToDocTask();

	@Before
	public void before() throws IOException {
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
}
