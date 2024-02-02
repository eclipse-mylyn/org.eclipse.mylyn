/*******************************************************************************
 * Copyright (c) 2017 Jeremie Bresson and others.
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

package org.eclipse.mylyn.wikitext.asciidoc.internal;

import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.mylyn.wikitext.asciidoc.AsciiDocLanguage;
import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.markup.IdGenerationStrategy;

public class AsciiDocIdGenerationStrategy extends IdGenerationStrategy {

	private final Set<String> existingIds = new HashSet<>();

	private String idPrefix = AsciiDocContentState.IDPREFIX_DEFAULT_VALUE;

	private String idSeparator = AsciiDocContentState.IDSEPARATOR_DEFAULT_VALUE;

	public void setIdPrefix(String idPrefix) {
		this.idPrefix = idPrefix;
	}

	public void setIdSeparator(String idSeparator) {
		this.idSeparator = idSeparator;
	}

	@Override
	public String generateId(String headingText) {
		String baseId = computeHeadingId(headingText, idPrefix, idSeparator);
		String id = baseId;
		int counter = 2;
		while (existingIds.contains(id)) {
			id = baseId + idSeparator + counter;
			counter = counter + 1;
		}
		existingIds.add(id);
		return id;
	}

	static String computeHeadingId(String text, String idprefix, String idseparator) {
		StringBuilder sb = new StringBuilder();
		if (idprefix != null) {
			sb.append(idprefix);
		}
		String anchor = parseToHtml(text);
		anchor = anchor.trim();
		if (anchor.startsWith("<p>")) { //$NON-NLS-1$
			anchor = anchor.substring(3);
		}
		if (anchor.endsWith("</p>")) { //$NON-NLS-1$
			anchor = anchor.substring(0, anchor.length() - 4);
		}
		anchor = anchor.replaceAll("[^\\w]", " "); //$NON-NLS-1$ //$NON-NLS-2$
		anchor = anchor.trim();
		anchor = anchor.toLowerCase();
		if (idseparator != null) {
			anchor = anchor.replaceAll("\\s+", idseparator); //$NON-NLS-1$
		}
		sb.append(anchor);
		return sb.toString();
	}

	private static String parseToHtml(String markup) {
		MarkupParser localParser = new MarkupParser(new AsciiDocLanguage());
		StringWriter out = new StringWriter();
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(out);
		builder.setEmitAsDocument(false);
		localParser.setBuilder(builder);
		localParser.parse(markup);
		return out.toString();
	}

}
