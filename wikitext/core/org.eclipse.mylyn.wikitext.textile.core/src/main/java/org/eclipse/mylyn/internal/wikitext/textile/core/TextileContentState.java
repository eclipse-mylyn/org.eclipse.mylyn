/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.textile.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.internal.wikitext.textile.core.block.FootnoteBlock;
import org.eclipse.mylyn.wikitext.core.parser.markup.ContentState;

/**
 * Override the default content state so that we can support named links.
 * 
 * @author David Green
 */
public class TextileContentState extends ContentState {
	private static final Pattern NAMED_LINK_PATTERN = Pattern.compile("\\[(\\S+)\\]([a-zA-Z]{3,5}:\\S+)", //$NON-NLS-1$
			Pattern.MULTILINE);

	private final Map<String, String> nameToUrl = new HashMap<String, String>();

	private Set<String> footnoteNumbers;

	@Override
	protected void setMarkupContent(String markupContent) {
		super.setMarkupContent(markupContent);
		preprocessContent(markupContent);
	}

	private void preprocessContent(String markupContent) {
		// look for named links
		Matcher matcher = NAMED_LINK_PATTERN.matcher(markupContent);
		while (matcher.find()) {
			String name = matcher.group(1);
			String href = matcher.group(2);
			nameToUrl.put(name, href);
		}
	}

	public String getNamedLinkUrl(String name) {
		return nameToUrl.get(name);
	}

	/**
	 * the known footnote numbers, or null if there are none
	 */
	public Set<String> getFootnoteNumbers() {
		return footnoteNumbers;
	}

	/**
	 * the known footnote numbers, or null if there are none
	 */
	public void setFootnoteNumbers(Set<String> footnoteNumbers) {
		this.footnoteNumbers = footnoteNumbers;
	}

	/**
	 * @param footnoteNumber
	 *            the number of the footnote, eg: <code>1</code>, or <code>12</code>
	 * @see FootnoteBlock
	 */
	public void footnoteBlockDetected(String footnoteNumber) {
		if (footnoteNumbers == null) {
			footnoteNumbers = new HashSet<String>();
		}
		footnoteNumbers.add(footnoteNumber);
	}

	public boolean hasFootnoteNumber(String footnoteNumber) {
		return footnoteNumbers != null && footnoteNumbers.contains(footnoteNumber);
	}
}
