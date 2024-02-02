/*******************************************************************************
 * Copyright (c) 2013, 2014 Tasktop Technologies and others.
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

package org.eclipse.mylyn.wikitext.html;

import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;

import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.parser.HtmlParser;
import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * A {@link MarkupLanguage} for handling HTML.
 * <p>
 * The {@link HtmlLanguage} maps block types as follows:
 * </p>
 * <ul>
 * <li>{@link BlockType#BULLETED_LIST} maps to HTML tag {@code
 *
<ul>
 * }</li>
 * <li>{@link BlockType#CODE} maps to HTML tags <code>&lt;pre>&lt;code></code></li>
 * <li>{@link BlockType#DEFINITION_LIST} maps to HTML tag {@code
 *
<dl>
 * }</li>
 * <li>{@link BlockType#DEFINITION_ITEM} maps to HTML tag {@code
 *
<dd>}</li>
 * <li>{@link BlockType#DEFINITION_TERM} maps to HTML tag {@code
 *
<dt>}</li>
 * <li>{@link BlockType#DIV} maps to HTML tag {@code <div>}</li>
 * <li>{@link BlockType#LIST_ITEM} maps to HTML tag {@code
 *
<li>}</li>
 * <li>{@link BlockType#NUMERIC_LIST} maps to HTML tag {@code
 *
<ol>
 * }</li>
 * <li>{@link BlockType#PARAGRAPH} maps to HTML tag {@code
 *
<p>
 * }</li>
 * <li>{@link BlockType#PREFORMATTED} maps to HTML tag {@code
 *
 *

<pre>
 * }</li>
 * <li>{@link BlockType#QUOTE} maps to HTML tag {@code <blockquote>}</li>
 * <li>{@link BlockType#TABLE} maps to HTML tag {@code
 *
<table>
 * }</li>
 * <li>{@link BlockType#TABLE_CELL_HEADER} maps to HTML tag {@code
 *
<th>}</li>
 * <li>{@link BlockType#TABLE_CELL_NORMAL} maps to HTML tag {@code
 *
<td>}</li>
 * <li>{@link BlockType#TABLE_ROW} maps to HTML tag {@code
 *
<tr>
 * }</li>
 * </ul>
 * <p>
 * The {@link HtmlLanguage} maps span types as follows:
 * </p>
 * <ul>
 * <li>{@link SpanType#BOLD} maps to HTML tag {@code <b>}</li>
 * <li>{@link SpanType#CITATION} maps to HTML tag {@code <cite>}</li>
 * <li>{@link SpanType#CODE} maps to HTML tag {@code <code>}</li>
 * <li>{@link SpanType#DELETED} maps to HTML tag {@code <del>}</li>
 * <li>{@link SpanType#EMPHASIS} maps to HTML tag {@code <em>}</li>
 * <li>{@link SpanType#INSERTED} maps to HTML tag {@code <ins>}</li>
 * <li>{@link SpanType#ITALIC} maps to HTML tag {@code <i>}</li>
 * <li>{@link SpanType#LINK} maps to HTML tag {@code <a>}</li>
 * <li>{@link SpanType#MONOSPACE} maps to HTML tag {@code <tt>}</li>
 * <li>{@link SpanType#QUOTE} maps to HTML tag {@code
 *
<q>}</li>
 * <li>{@link SpanType#SPAN} maps to HTML tag {@code <span>}</li>
 * <li>{@link SpanType#STRONG} maps to HTML tag {@code <strong>}</li>
 * <li>{@link SpanType#SUBSCRIPT} maps to HTML tag {@code <sub>}</li>
 * <li>{@link SpanType#SUPERSCRIPT} maps to HTML tag {@code <sup>}</li>
 * <li>{@link SpanType#UNDERLINED} maps to HTML tag {@code <u>}</li>
 * <li>{@link SpanType#MARK} maps to HTML tag {@mark <u>}</li>
 * </ul>
 * <p>
 * {@link HtmlLanguage} variants created using {@link HtmlLanguageBuilder} may map {@link SpanType} and {@link BlockType} differently.
 * </p>
 *
 * @author david.green
 * @see HtmlParser
 * @see HtmlLanguageBuilder
 * @see #builder()
 * @since 3.0
 */
public class HtmlLanguage extends MarkupLanguage {

	static final String NAME_HTML = "HTML"; //$NON-NLS-1$

	private boolean parseCleansHtml = true;

	public HtmlLanguage() {
		setName(NAME_HTML);
	}

	/**
	 * Indicates if {@link #processContent(MarkupParser, String, boolean) parsing} is run with cleanup rules for HTML. Defaults to
	 * {@code true}.
	 *
	 * @return true if cleanup rules are used when parsing, otherwise false
	 * @see HtmlParser#instanceWithHtmlCleanupRules()
	 */
	public boolean isParseCleansHtml() {
		return parseCleansHtml;
	}

	/**
	 * Set whether {@link #processContent(MarkupParser, String, boolean) parsing} is run with cleanup rules for HTML.
	 *
	 * @param parseCleansHtml
	 *            true if cleanup rules are used when parsing, otherwise false
	 * @see #isParseCleansHtml()
	 * @see HtmlParser#instanceWithHtmlCleanupRules()
	 */
	public void setParseCleansHtml(boolean parseCleansHtml) {
		this.parseCleansHtml = parseCleansHtml;
	}

	@Override
	public void processContent(MarkupParser parser, String markupContent, boolean asDocument) {
		HtmlParser htmlParser = createHtmlParser();
		InputSource source = new InputSource(new StringReader(markupContent));
		try {
			htmlParser.parse(source, parser.getBuilder(), asDocument);
		} catch (IOException | SAXException e) {
			throw new RuntimeException(e);
		}
	}

	HtmlParser createHtmlParser() {
		return parseCleansHtml ? HtmlParser.instanceWithHtmlCleanupRules() : new HtmlParser();
	}

	@Override
	public DocumentBuilder createDocumentBuilder(Writer out, boolean formatting) {
		return new HtmlDocumentBuilder(out, formatting);
	}

	/**
	 * Creates a {@link HtmlLanguageBuilder}.
	 *
	 * @return the new {@link HtmlLanguageBuilder}
	 */
	public static HtmlLanguageBuilder builder() {
		return new HtmlLanguageBuilder();
	}

	@Override
	public HtmlLanguage clone() {
		HtmlLanguage copy = (HtmlLanguage) super.clone();
		copy.setParseCleansHtml(parseCleansHtml);
		return copy;
	}

}
