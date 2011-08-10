/*******************************************************************************
 * Copyright (c) 2007, 2010 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.confluence.core.phrase;

import java.text.MessageFormat;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.core.parser.LinkAttributes;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElementProcessor;

/**
 * @author David Green
 */
public class HyperlinkPhraseModifier extends PatternBasedElement {

	private static final Pattern QUALIFIED_HREF_PATTERN = Pattern.compile(
			"(#|([a-z]{2,6}:)).*", Pattern.CASE_INSENSITIVE); //$NON-NLS-1$

	@Override
	protected String getPattern(int groupOffset) {
		return "\\[(?:\\s*([^\\]\\|]+)\\|)?([^\\]]+)\\]"; //$NON-NLS-1$
	}

	@Override
	protected int getPatternGroupCount() {
		return 2;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new HyperlinkPhraseModifierProcessor();
	}

	private static class HyperlinkPhraseModifierProcessor extends PatternBasedElementProcessor {
		@Override
		public void emit() {
			String text = group(1);
			String linkComposite = group(2);
			String[] parts = linkComposite.split("\\s*\\|\\s*"); //$NON-NLS-1$
			if (parts.length == 0) {
				// can happen if linkComposite is ' |', see bug 290434
			} else {
				if (text != null) {
					text = text.trim();
				}
				String href = parts[0];
				if (href != null) {
					href = href.trim();
				}
				String tip = parts.length > 1 ? parts[1] : null;
				if (tip != null) {
					tip = tip.trim();
				}
				if (text == null || text.length() == 0) {
					text = href;
					if (text != null && text.length() > 0 && text.charAt(0) == '#') {
						text = text.substring(1);
					}
					Attributes attributes = new LinkAttributes();
					attributes.setTitle(tip);
					getBuilder().link(attributes, toInternalHref(href), text);
				} else {
					LinkAttributes attributes = new LinkAttributes();
					attributes.setTitle(tip);
					attributes.setHref(toInternalHref(href));
					getBuilder().beginSpan(SpanType.LINK, attributes);

					getMarkupLanguage().emitMarkupLine(parser, state, start(1), text, 0);

					getBuilder().endSpan();
				}
			}
		}

		private String toInternalHref(String href) {
			//	Skip if internal anchor or qualified URL
			if (QUALIFIED_HREF_PATTERN.matcher(href).matches()) {
				return href;
			}

			String internalLinkPattern = getMarkupLanguage().getInternalLinkPattern();

			if (internalLinkPattern != null) {
				return MessageFormat.format(internalLinkPattern, href);
			}
			return href;
		}
	}

}
