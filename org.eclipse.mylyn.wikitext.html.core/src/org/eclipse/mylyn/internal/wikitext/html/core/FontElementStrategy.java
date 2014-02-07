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

import org.eclipse.mylyn.internal.wikitext.core.util.css.CssParser;
import org.eclipse.mylyn.internal.wikitext.core.util.css.CssRule;
import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.core.util.XmlStreamWriter;

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
						if (rule.name.equals("color") || rule.name.equals("font-size")) { //$NON-NLS-1$ //$NON-NLS-2$
							return true;
						}
					}
				}
			}
			return false;

		}
	}

	private static final class FontSpanStrategy implements SpanStrategy {
		@Override
		public void beginSpan(DocumentBuilder builder, SpanType type, Attributes attributes) {
			if (builder instanceof HtmlDocumentBuilder) {
				HtmlDocumentBuilder htmlBuilder = (HtmlDocumentBuilder) builder;
				XmlStreamWriter writer = htmlBuilder.getWriter();
				writer.writeStartElement(htmlBuilder.getHtmlNsUri(), "font"); //$NON-NLS-1$
				String cssStyle = attributes.getCssStyle();
				if (cssStyle != null) {
					Iterator<CssRule> rules = new CssParser().createRuleIterator(cssStyle);
					while (rules.hasNext()) {
						CssRule rule = rules.next();
						if (rule.name.equals("color")) { //$NON-NLS-1$
							writer.writeAttribute("color", rule.value); //$NON-NLS-1$
						} else if (rule.name.equals("font-size")) { //$NON-NLS-1$
							writer.writeAttribute("size", rule.value); //$NON-NLS-1$
						}
					}
				}
			} else {
				builder.beginSpan(type, attributes);
			}
		}

		@Override
		public void endSpan(DocumentBuilder builder) {
			if (builder instanceof HtmlDocumentBuilder) {
				HtmlDocumentBuilder htmlBuilder = (HtmlDocumentBuilder) builder;
				XmlStreamWriter writer = htmlBuilder.getWriter();
				writer.writeEndElement();
			} else {
				builder.endSpan();
			}
		}
	}

	public FontElementStrategy() {
		super(new FontElementMatcher(), new FontSpanStrategy());
	}
}
