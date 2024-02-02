/*******************************************************************************
 * Copyright (c) 2016, 2024 Jeremie Bresson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Jeremie Bresson - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.mediawiki.tests;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import org.eclipse.mylyn.wikitext.mediawiki.WikiTemplateResolver;

/**
 * Extension of the {@link WikiTemplateResolver} for test purposes. You should set some content with {@link #setServerContent(Map)} before
 * using this class in your tests.
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
		requireNonNull(serverContent);
		this.serverContent = serverContent;
	}

	@Override
	protected String readContent(URL pathUrl) throws IOException {
		requireNonNull(serverContent, "Please specify some server content for the tests.");
		String key = pathUrl.toString();
		checkState(serverContent.containsKey(key), "Server content not found for key: %s", key);
		return serverContent.get(key);
	}

}
