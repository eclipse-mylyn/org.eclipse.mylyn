/*******************************************************************************
 * Copyright (c) 2015, 2024 Max Rydahl Andersen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Max Rydahl Andersen- initial API and implementation
 *     Alexander Fedorov (ArSysOp) - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.asciidoc.internal.token;

import org.eclipse.mylyn.wikitext.parser.LinkAttributes;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElementProcessor;

/**
 * Detects explicit links like: link:http://www.example.com[link text], link:index.html[]
 *
 * @author Max Rydahl Andersen
 */
public class ExplicitLinkReplacementToken extends PatternBasedElement {

	static String EXPLICT_LINK_PATTERN = "(((?:link:\\+\\+)(.*)\\+\\+(\\[(.*?)\\]))|((?:link:)(.*)(\\[(.*?)\\])))"; //$NON-NLS-1$

	@Override
	protected String getPattern(int groupOffset) {
		return EXPLICT_LINK_PATTERN;
	}

	@Override
	protected int getPatternGroupCount() {
		return 9;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new InlineLinkReplacementTokenProcessor();
	}

	private static class InlineLinkReplacementTokenProcessor extends PatternBasedElementProcessor {
		@Override
		public void emit() {
			String href, text;

			if (group(7) != null) { // without ++
				href = group(7);
				text = group(9);
			} else { // with ++
				href = group(3);
				text = group(5);
			}

			LinkAttributes attributes = new LinkAttributes();
			if (text == null) {
				text = href;
			} else if (text.endsWith("^")) { //$NON-NLS-1$
				text = text.substring(0, text.length() - 1);
				attributes.setTarget("_blank"); //$NON-NLS-1$
			}

			builder.link(attributes, href, text);

		}
	}
}
