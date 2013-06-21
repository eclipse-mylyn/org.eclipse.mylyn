/*******************************************************************************
 * Copyright (c) 2012, 2013 Stefan Seelmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.markdown.tests;

import org.eclipse.mylyn.wikitext.tests.TestUtil;

/**
 * http://daringfireball.net/projects/markdown/syntax
 * 
 * @author Stefan Seelmann
 */
public class MarkdownLanguageTest extends MarkdownLanguageTestBase {

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
		TestUtil.println("HTML: " + html);
		assertTrue(html.contains("<h1>Header 1"));
		assertTrue(html.contains("<p>Lorem ipsum"));
		assertTrue(html.contains("<strong>dolor"));
		assertTrue(html.contains("<hr/>"));
		assertTrue(html.contains("<h2>Header 2<"));
		assertTrue(html.contains("<blockquote><p>Blockquote"));
		assertTrue(html.contains("<pre><code>Code block"));
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
}
