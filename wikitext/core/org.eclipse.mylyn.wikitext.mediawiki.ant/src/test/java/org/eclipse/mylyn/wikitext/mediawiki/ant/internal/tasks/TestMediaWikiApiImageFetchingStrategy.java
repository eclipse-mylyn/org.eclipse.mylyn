/*******************************************************************************
 * Copyright (c) 2017, 2021 Jeremie Bresson and others.
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

import static java.util.Objects.requireNonNull;

import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.Map;

public class TestMediaWikiApiImageFetchingStrategy extends MediaWikiApiImageFetchingStrategy {

	private final Map<String, String> serverContent;

	public TestMediaWikiApiImageFetchingStrategy(Map<String, String> serverContent) {
		requireNonNull(serverContent,
				"Please specify some server content for images used during the tests. See: TestMediaWikiApiImageFetchingStrategy#serverContent");
		this.serverContent = Map.copyOf(serverContent);
	}

	@Override
	protected Reader createInputReader(URL apiUrl) {
		String key = apiUrl.toString();
		if (!serverContent.containsKey(key)) {
			throw new IllegalStateException("Please define a server content used during the tests for the key: " + key);
		}
		String pageContent = serverContent.get(key);
		return new StringReader(pageContent);
	}
}
