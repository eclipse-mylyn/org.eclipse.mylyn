/*******************************************************************************
 * Copyright (c) 2007, 2016 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     Max Rydahl Andersen - Bug 474084
 *     Patrik Suzzi <psuzzi@gmail.com> - Bug 474084
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.asciidoc.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.mylyn.wikitext.asciidoc.internal.util.LanguageSupport;
import org.eclipse.mylyn.wikitext.parser.markup.Block;
import org.eclipse.mylyn.wikitext.parser.markup.ContentState;
import org.eclipse.mylyn.wikitext.parser.markup.IdGenerationStrategy;
import org.eclipse.mylyn.wikitext.parser.markup.Processor;

/**
 * Extended {@link ContentState content state} to provide additional AsciiDoc information to {@link Block blocks} and other {@link Processor
 * processors}
 */
public class AsciiDocContentState extends ContentState {

	public static final String ATTRIBUTE_IDPREFIX = "idprefix"; //$NON-NLS-1$

	public static final String IDPREFIX_DEFAULT_VALUE = "_"; //$NON-NLS-1$

	public static final String ATTRIBUTE_IDSEPARATOR = "idseparator"; //$NON-NLS-1$

	public static final String IDSEPARATOR_DEFAULT_VALUE = "_";//$NON-NLS-1$

	public static final String ATTRIBUTE_IMAGESDIR = "imagesdir"; //$NON-NLS-1$

	public static final String ATTRIBUTE_LEVELOFFSET = "leveloffset"; //$NON-NLS-1$

	// latest title provided via .<optional title> syntax
	private String lastTitle;

	private String lastPropertiesText;

	private boolean heading1Present;

	private final Map<String, String> attributes = new HashMap<>();

	public void setLastTitle(String text) {
		lastTitle = text;
	}

	/**
	 * @return last title provided via [{@code .<optional title>} syntax
	 */
	public String getLastTitle() {
		return lastTitle;
	}

	public void setLastPropertiesText(String text) {
		lastPropertiesText = text;
	}

	public Map<String, String> getLastProperties(List<String> positionalParameters) {
		return LanguageSupport.parseFormattingProperties(lastPropertiesText, positionalParameters);
	}

	public boolean isAttributeDefined(String attrName) {
		return attributes.containsKey(attrName);
	}

	public String getAttribute(String attrName) {
		return attributes.get(attrName);
	}

	public String getAttributeOrValue(String attrName, String valueIfNull) {
		String value = getAttribute(attrName);
		if (value != null) {
			return value;
		}
		return valueIfNull;
	}

	public void putAttribute(String attrName, String value) {
		attributes.put(attrName, value);
		IdGenerationStrategy generationStrategy = getIdGenerator().getGenerationStrategy();
		if (generationStrategy instanceof AsciiDocIdGenerationStrategy) {
			if (ATTRIBUTE_IDPREFIX.equals(attrName)) {
				((AsciiDocIdGenerationStrategy) generationStrategy).setIdPrefix(value);
			} else if (ATTRIBUTE_IDSEPARATOR.equals(attrName)) {
				((AsciiDocIdGenerationStrategy) generationStrategy).setIdSeparator(value);
			}
		}
	}

	public void removeAttribute(String attrName) {
		attributes.remove(attrName);
		IdGenerationStrategy generationStrategy = getIdGenerator().getGenerationStrategy();
		if (generationStrategy instanceof AsciiDocIdGenerationStrategy) {
			if (ATTRIBUTE_IDPREFIX.equals(attrName)) {
				((AsciiDocIdGenerationStrategy) generationStrategy)
						.setIdPrefix(AsciiDocContentState.IDPREFIX_DEFAULT_VALUE);
			} else if (ATTRIBUTE_IDSEPARATOR.equals(attrName)) {
				((AsciiDocIdGenerationStrategy) generationStrategy)
						.setIdSeparator(AsciiDocContentState.IDSEPARATOR_DEFAULT_VALUE);
			}
		}
	}

	public boolean isHeading1Present() {
		return heading1Present;
	}

	public void setHeading1Present(boolean heading1Present) {
		this.heading1Present = heading1Present;
	}
}
