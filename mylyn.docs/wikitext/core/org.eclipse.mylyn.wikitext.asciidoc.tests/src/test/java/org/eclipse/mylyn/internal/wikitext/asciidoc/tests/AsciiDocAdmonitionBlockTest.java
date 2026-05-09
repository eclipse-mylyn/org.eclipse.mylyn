/*******************************************************************************
 * Copyright (c) 2026 Johannes Kepler University Linz
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alois Zoitl - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.asciidoc.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings("nls")
class AsciiDocAdmonitionBlockTest extends AsciiDocLanguageTestBase {

	private static Stream<Arguments> admonitionProvider() {
		return Stream.of(
				Arguments.of("TIP", "Tip"), //
				Arguments.of("NOTE", "Note"), //
				Arguments.of("WARNING", "Warning"), //
				Arguments.of("IMPORTANT", "Important"), //
				Arguments.of("CAUTION", "Caution") //
				);
	}

	@ParameterizedTest(name = "Basic admonition block test (title, single line content): {0}")
	@MethodSource("admonitionProvider")
	void fullBlockWithOneLine(String type, String label) {
		String input = String.format("""
				[%s]
				.This is the headline of a %s
				====
				This is a simple single line content for an admonition block.
				====
				""", type, label);

		String html = parseToHtml(input);

		String expected = String.format("""
				<div class="admonitionblock %s"><table><tr><td class="icon"><div class="title">%s</div></td>
				<td class="content">
				<div class="title">This is the headline of a %s</div>
				<div class="paragraph">
				<p>This is a simple single line content for an admonition block.</p>
				</div></td></tr></table></div>
				""", type.toLowerCase(), label, label);

		assertEquals(expected.trim(), html.trim());
	}

	@ParameterizedTest(name = "Admonition block test with no title: {0}")
	@MethodSource("admonitionProvider")
	void blockWithNoTitle(String type, String label) {
		String input = String.format("""
				[%s]
				====
				This is a simple single line content for an admonition block.
				====
				""", type, label);

		String html = parseToHtml(input);

		String expected = String.format("""
				<div class="admonitionblock %s"><table><tr><td class="icon"><div class="title">%s</div></td>
				<td class="content">
				<div class="paragraph">
				<p>This is a simple single line content for an admonition block.</p>
				</div></td></tr></table></div>
				""", type.toLowerCase(), label, label);

		assertEquals(expected.trim(), html.trim());
	}

	@Test
	void multiParapgraphAdmonitionBlock() {
		String input = """
				[NOTE]
				====
				This is the first pragraph as content of an admonition block.

				This is the second pragraph as content of an admonition block.
				====
				""";

		String html = parseToHtml(input);

		String expected = """
				<div class="admonitionblock note"><table><tr><td class="icon"><div class="title">Note</div></td>
				<td class="content">
				<div class="paragraph">
				<p>This is the first pragraph as content of an admonition block.</p>
				<p>This is the second pragraph as content of an admonition block.</p>
				</div></td></tr></table></div>
				""";

		assertEquals(expected.trim(), html.trim());
	}

	@Test
	void listConentAdmonitionBlock() {
		String input = """
				[NOTE]
				====
				This is the first pragraph as content of an admonition block.

				. first line of a numbered list
				. second line of a numbered list
				. third line of a numbered list

				This is the second pragraph as content of an admonition block.

				And here another one!
				====
				""";

		String html = parseToHtml(input);

		String expected = """
				<div class="admonitionblock note"><table><tr><td class="icon"><div class="title">Note</div></td>
				<td class="content">
				<div class="paragraph">
				<p>This is the first pragraph as content of an admonition block.</p>
				<ol style="list-style-type:decimal;"><li>first line of a numbered list</li><li>second line of a numbered list</li><li>third line of a numbered list</li></ol><p>This is the second pragraph as content of an admonition block.</p>
				<p>And here another one!</p>
				</div></td></tr></table></div>
				""";

		assertEquals(expected.trim(), html.trim());
	}

	@Test
	void nestedListConentAdmonitionBlock() {
		String input = """
				[NOTE]
				====
				some text above
				. one
				. two
				.* nested unorderd list
				.** nested in nested
				.* another nested unordered list item
				.*. subordered list
				.*. subordered list item
				. three
				some text below.
				====
				""";

		String html = parseToHtml(input);

		String expected = """
				<div class="admonitionblock note"><table><tr><td class="icon"><div class="title">Note</div></td>
				<td class="content">
				<div class="paragraph">
				<p>some text above</p>
				<ol style="list-style-type:decimal;"><li>one</li><li>two<ul><li>nested unorderd list<ul><li>nested in nested</li></ul></li><li>another nested unordered list item<ol style="list-style-type:lower-alpha;"><li>subordered list</li><li>subordered list item</li></ol></li></ul></li><li>three some text below.</li></ol></div></td></tr></table></div>
				""";

		assertEquals(expected.trim(), html.trim());
	}

}
