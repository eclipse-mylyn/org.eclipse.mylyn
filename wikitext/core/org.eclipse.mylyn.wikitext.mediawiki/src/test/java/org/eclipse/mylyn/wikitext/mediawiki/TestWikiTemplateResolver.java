/*******************************************************************************
 * Copyright (c) 2016 Jeremie Bresson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jeremie Bresson - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.mediawiki;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import org.eclipse.mylyn.wikitext.mediawiki.WikiTemplateResolver;

/**
 * Extension of the {@link WikiTemplateResolver} for test purposes. You should set some content with
 * {@link #setServerContent(Map)} before using this class in your tests.
 */
class TestWikiTemplateResolver extends WikiTemplateResolver {

	private Map<String, String> serverContent;

	/**
	 * Key value map used to simulate the server content during the tests.
	 *
	 * @param serverContent
	 *            keys are URLs as String, values are the page content corresponding to the URL
	 */
	public void setServerContent(Map<String, String> serverContent) {
		checkNotNull(serverContent);
		this.serverContent = serverContent;
	}

	@Override
	protected String readContent(URL pathUrl) throws IOException {
		checkNotNull(serverContent, "Please specify some server content for the tests.");
		String key = pathUrl.toString();
		checkState(serverContent.containsKey(key), "Server content not found for key: %s", key);
		return serverContent.get(key);
	}

}
