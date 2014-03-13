/*******************************************************************************
 * Copyright (c) 2014 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.html.core;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.mylyn.internal.wikitext.core.util.css.CssParser;
import org.eclipse.mylyn.internal.wikitext.core.util.css.CssRule;
import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.core.util.XmlStreamWriter;

import com.google.common.collect.Maps;

public class FontElementStrategy extends SpanHtmlElementStrategy {

	private static final class FontElementMatcher implements ElementMatcher<SpanType> {
		@Override
		public boolean matches(SpanType elementType, Attributes attributes) {
			if (elementType == SpanType.SPAN) {
				String cssStyle = attributes.getCssStyle();
				if (cssStyle != null) {
					Iterator<CssRule> rules = new CssParser().createRuleIterator(cssStyle);
					while (rules.hasNext()) {
						CssRule rule = rules.next();
						if (rule.name.equals("color") || rule.name.equals("font-size") || rule.name.equals("font-family")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
							return true;
						}
					}
				}
			}
			return false;

		}
	}

	private static final class FontSpanStrategy implements SpanStrategy {
		private boolean elementOpened = false;

		@Override
		public void beginSpan(DocumentBuilder builder, SpanType type, Attributes attributes) {
			if (builder instanceof HtmlDocumentBuilder) {
				Map<String, String> fontAttributes = null;
				String cssStyle = attributes.getCssStyle();
				if (cssStyle != null) {
					fontAttributes = Maps.newTreeMap();

					Iterator<CssRule> rules = new CssParser().createRuleIterator(cssStyle);
					while (rules.hasNext()) {
						CssRule rule = rules.next();
						if (rule.name.equals("color")) { //$NON-NLS-1$
							fontAttributes.put("color", rule.value); //$NON-NLS-1$
						} else if (rule.name.equals("font-size")) { //$NON-NLS-1$
							fontAttributes.put("size", rule.value); //$NON-NLS-1$
						} else if (rule.name.equals("font-family")) { //$NON-NLS-1$
							fontAttributes.put("face", rule.value); //$NON-NLS-1$
						}
					}
				}
				if (fontAttributes != null && !fontAttributes.isEmpty()) {
					elementOpened = true;

					HtmlDocumentBuilder htmlBuilder = (HtmlDocumentBuilder) builder;
					XmlStreamWriter writer = htmlBuilder.getWriter();
					writer.writeStartElement(htmlBuilder.getHtmlNsUri(), "font"); //$NON-NLS-1$
					for (Entry<String, String> attribute : fontAttributes.entrySet()) {
						writer.writeAttribute(attribute.getKey(), attribute.getValue());
					}
				}
			} else {
				builder.beginSpan(type, attributes);
			}
		}

		@Override
		public void endSpan(DocumentBuilder builder) {
			if (builder instanceof HtmlDocumentBuilder) {
				if (elementOpened) {
					HtmlDocumentBuilder htmlBuilder = (HtmlDocumentBuilder) builder;
					XmlStreamWriter writer = htmlBuilder.getWriter();
					writer.writeEndElement();
				}
			} else {
				builder.endSpan();
			}
		}
	}

	public FontElementStrategy() {
		super(new FontElementMatcher(), new FontSpanStrategy());
	}

	@Override
	public SpanStrategy spanStrategy() {
		return new FontSpanStrategy();
	}
}
