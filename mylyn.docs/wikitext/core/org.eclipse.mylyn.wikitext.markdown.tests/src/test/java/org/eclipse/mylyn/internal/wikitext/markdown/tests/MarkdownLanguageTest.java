/*******************************************************************************
 * Copyright (c) 2012, 2024 Stefan Seelmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *     Alexander Nyßen - tests for fenced code blocks
 *                       tests for inline links
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.markdown.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;

import org.eclipse.mylyn.wikitext.markdown.MarkdownLanguage;
import org.eclipse.mylyn.wikitext.markdown.internal.GfmIdGenerationStrategy;
import org.eclipse.mylyn.wikitext.markdown.internal.MarkdownDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.markup.AbstractMarkupLanguage;
import org.eclipse.mylyn.wikitext.parser.markup.IdGenerationStrategy;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.util.ServiceLocator;
import org.junit.Test;

/**
 * http://daringfireball.net/projects/markdown/syntax
 *
 * @author Stefan Seelmann
 */
@SuppressWarnings("nls")
public class MarkdownLanguageTest extends MarkdownLanguageTestBase {
	@Test
	public void testDiscoverable() {
		MarkupLanguage language = ServiceLocator.getInstance().getMarkupLanguage("Markdown");
		assertNotNull(language);
		assertTrue(language instanceof MarkdownLanguage);
	}

	@Test
	public void testFullExample() {

		StringBuilder text = new StringBuilder();
		text.append("Header 1\n");
		text.append("======\n");
		text.append("\n");
		text.append("Lorem ipsum **dolor** sit amet, \n");
		text.append("consetetur adipisici elit.\n");
		text.append("\n");
		text.append("***\n");
		text.append("\n");
		text.append("## Header 2\n");
		text.append("\n");
		text.append("> Blockquote\n");
		text.append("\n");
		text.append("    Code block\n");
		text.append("    Continued\n");
		text.append("\n");
		text.append("~~~\n");
		text.append("  Fenced Code block (Tildes)\n");
		text.append("  Continued\n");
		text.append("~~~\n");
		text.append("\n");
		text.append("```\n");
		text.append("  Fenced Code block (Backticks)\n");
		text.append("  Continued\n");
		text.append("```\n");
		text.append("\n");
		text.append("**Some formatted text with an embedded anchor to [Header2](#Header-2 \"Header2 Title\")**.");
		text.append("\n");
		text.append("* List item 1\n");
		text.append("* List item 2\n");
		text.append("\n");
		text.append("I get 10 times more traffic from [Google] [1]  than from [Yahoo][] or [MSN] [].\n");
		text.append("\n");
		text.append("  [1]:     http://google.com/        \"Google\"\n");
		text.append("  [YAHOO]: http://search.yahoo.com/  'Yahoo Search'\n");
		text.append("  [msn]:   http://search.msn.com/    (MSN Search)\n");
		text.append("\n");
		text.append("More text.\n");

		String html = parseToHtml(text.toString());

		assertTrue(html.contains("<h1 id=\"header-1\">Header 1"));
		assertTrue(html.contains("<p>Lorem ipsum"));
		assertTrue(html.contains("<strong>dolor"));
		assertTrue(html.contains("<hr/>"));
		assertTrue(html.contains("<h2 id=\"header-2\">Header 2<"));
		assertTrue(html.contains("<blockquote><p>Blockquote"));
		assertTrue(html.contains("<pre><code>Code block"));
		assertTrue(html.contains("<pre><code>  Fenced Code block (Tildes)"));
		assertTrue(html.contains("<pre><code>  Fenced Code block (Backticks)"));
		assertTrue(html.contains(
				"<strong>Some formatted text with an embedded anchor to <a href=\"#Header-2\" title=\"Header2 Title\">Header2</a></strong>"));
		assertTrue(html.contains("<ul>"));
		assertTrue(html.contains("<li>List item 1</li>"));
		assertTrue(html.contains("<li>List item 2</li>"));
		assertTrue(html.contains("<a href=\"http://google.com/\" title=\"Google\">Google</a>"));
		assertTrue(html.contains("<a href=\"http://search.yahoo.com/\" title=\"Yahoo Search\">Yahoo</a>"));
		assertTrue(html.contains("<a href=\"http://search.msn.com/\" title=\"MSN Search\">MSN</a>"));
		assertFalse(html.contains("[1]"));
		assertFalse(html.contains("[YAHOO]"));
		assertFalse(html.contains("[msn]"));
		assertTrue(html.contains("<p>More text.</p>"));
	}

	@Test
	public void testPreserveHtmlEntities() {
		StringBuilder text = new StringBuilder();
		text.append("AT&T and\n\n");
		text.append("AT&amp;T again\n\n");

		String html = parseToHtml(text.toString());

		assertTrue(html.contains("<p>AT&amp;T and</p>"));
		assertTrue(html.contains("<p>AT&amp;T again</p>"));
	}

	@Test
	public void testBacktickWithLang() {
		StringBuilder text = new StringBuilder();
		text.append("```java\n");
		text.append("new String();\n");
		text.append("```");

		String html = parseToHtml(text.toString());

		assertEquals("<pre class=\"language-java\"><code class=\"language-java\">new String();</code></pre>", html);
	}

	@Test
	public void testCreateDocumentBuilder() {
		AbstractMarkupLanguage lang = new MarkdownLanguage();
		DocumentBuilder builder = lang.createDocumentBuilder(new StringWriter());
		assertNotNull(builder);
		assertTrue(builder instanceof MarkdownDocumentBuilder);
	}

	@Test
	public void testIdGenerationStrategy() {
		IdGenerationStrategy strategy = new MarkdownLanguage().getIdGenerationStrategy();
		assertNotNull(strategy);
		assertEquals(GfmIdGenerationStrategy.class, strategy.getClass());
	}

}
