/*******************************************************************************
 * Copyright (c) 2013 Stefan Seelmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.markdown.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser for link definitions in Markdown.
 *
 * @author Stefan Seelmann
 */
public class LinkDefinitionParser {

	private static final String ID_REGEX = " {0,3}(\\[([^]]+?)\\]\\:)"; //$NON-NLS-1$

	private static final String URL_REGEX = "\\s+(?=[<]?(([^>\\s]+)))(?:<\\3>|\\3)"; //$NON-NLS-1$

	private static final String TITLE_REGEX = "(?:\\s+[\"'\\(](.*?)[\"'\\)])?"; //$NON-NLS-1$

	public static final Pattern LINK_DEFINITION_PATTERN = Pattern.compile(ID_REGEX + URL_REGEX + TITLE_REGEX);

	private Map<String, LinkDefinition> linkDefinitions;

	public void parse(String markupContent) {
		linkDefinitions = new HashMap<>();
		Matcher matcher = LINK_DEFINITION_PATTERN.matcher(markupContent);
		while (matcher.find()) {
			String id = matcher.group(2);
			String url = matcher.group(3);
			String title = matcher.group(5);
			int offset = matcher.start(1);
			int length = matcher.end() - offset;
			linkDefinitions.put(id.toLowerCase(), new LinkDefinition(id, url, title, offset, length));
		}
	}

	public LinkDefinition getLinkDefinition(String id) {
		return linkDefinitions.get(id.toLowerCase());
	}

	public Map<String, LinkDefinition> getLinkDefinitions() {
		return linkDefinitions;
	}

}
