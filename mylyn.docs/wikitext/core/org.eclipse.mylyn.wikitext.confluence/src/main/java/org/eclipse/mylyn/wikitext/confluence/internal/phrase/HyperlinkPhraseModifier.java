/*******************************************************************************
 * Copyright (c) 2007, 2015 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.confluence.internal.phrase;

import java.text.MessageFormat;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.confluence.internal.ConfluenceContentState;
import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.parser.LinkAttributes;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElementProcessor;

/**
 * @author David Green
 */
public class HyperlinkPhraseModifier extends PatternBasedElement {

	private static final Pattern QUALIFIED_HREF_PATTERN = Pattern.compile("(#|([a-z]{2,6}:)).*", //$NON-NLS-1$
			Pattern.CASE_INSENSITIVE);

	private final boolean parseRelativeLinks;

	public HyperlinkPhraseModifier(boolean parseRelativeLinks) {
		this.parseRelativeLinks = parseRelativeLinks;
	}

	@Override
	protected String getPattern(int groupOffset) {
		return "\\[(?:\\s*([^\\[\\]\\|]+)\\|)?([^\\[\\]]+)\\]"; //$NON-NLS-1$
	}

	@Override
	protected int getPatternGroupCount() {
		return 2;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new HyperlinkPhraseModifierProcessor(parseRelativeLinks);
	}

	private static class HyperlinkPhraseModifierProcessor extends PatternBasedElementProcessor {

		private final boolean parseRelativeLinks;

		public HyperlinkPhraseModifierProcessor(boolean parseRelativeLinks) {
			this.parseRelativeLinks = parseRelativeLinks;
		}

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
				if (!shouldEmitAsLink(text, href)) {
					getBuilder().characters(group(0));
					return;
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
					emitLinkText(text);
					getBuilder().endSpan();
				}
			}
		}

		private void emitLinkText(String text) {
			getState().setWithinLink(true);
			getMarkupLanguage().emitMarkupLine(parser, state, start(1), text, 0);
			getState().setWithinLink(false);
		}

		@Override
		public ConfluenceContentState getState() {
			return (ConfluenceContentState) super.getState();
		}

		private boolean shouldEmitAsLink(String text, String href) {
			return parseRelativeLinks || isQualifiedLink(href);
		}

		private String toInternalHref(String href) {
			//	Skip if internal anchor or qualified URL
			if (isQualifiedLink(href)) {
				return href;
			}

			String internalLinkPattern = getMarkupLanguage().getInternalLinkPattern();

			if (internalLinkPattern != null) {
				return MessageFormat.format(internalLinkPattern, href);
			}
			return href;
		}

		private boolean isQualifiedLink(String href) {
			return QUALIFIED_HREF_PATTERN.matcher(href).matches();
		}
	}

}
