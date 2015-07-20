/*******************************************************************************
 * Copyright (c) 2015 Jeremie Bresson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jeremie Bresson - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.mediawiki.core.tasks;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.tools.ant.Project;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;

/**
 * Extension of the {@link WikiToDocTask} for test purposes.
 */
class TestWikiToDocTask extends WikiToDocTask {

	private Map<String, String> serverContent;

	public TestWikiToDocTask() {
		setProject(new Project());
	}

	/**
	 * Key value map used to simulate the server content during the tests.
	 *
	 * @param serverContent
	 *            keys are URLs as String, values are the page content corresponding to the URL
	 */
	public void setServerContent(Map<String, String> serverContent) {
		this.serverContent = serverContent;
	}

	@Override
	protected Reader createInputReader(URL pathUrl) throws IOException {
		if (serverContent == null) {
			throw new IllegalStateException(
					"Please specify some server content used during the tests. See: TestWikiToDocTask.setServerContent(Map<String, String>)");
		}
		String key = pathUrl.toString();
		if (!serverContent.containsKey(key)) {
			throw new IllegalStateException("Please define a server content used during the tests for the key: " + key);
		}
		String pageContent = serverContent.get(key);
		return new StringReader(pageContent);
	}

	@Override
	protected Set<String> fetchImages(MarkupLanguage markupLanguage, Path path) {
		return Collections.emptySet();
	}
}
