/*******************************************************************************
 * Copyright (c) 2016, 2021 Jeremie Bresson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Jeremie Bresson - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.mediawiki.ant.internal.tasks;

import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.Map;

import org.apache.tools.ant.Project;

/**
 * Extension of the {@link WikiToDocTask} for test purposes.
 */
class TestWikiToDocTask extends WikiToDocTask {

	private Map<String, String> serverContent;

	private Map<String, String> imageServerContent;

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

	public void setImageServerContent(Map<String, String> imageServerContent) {
		this.imageServerContent = Map.copyOf(imageServerContent);
	}

	@Override
	protected Reader createInputReader(URL pathUrl) {
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
	protected MediaWikiApiImageFetchingStrategy createImageFetchingStrategy() {
		return new TestMediaWikiApiImageFetchingStrategy(imageServerContent);
	}
}
