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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@SuppressWarnings("nls")
class AsciiDocAdmonitionParagraphTest extends AsciiDocLanguageTestBase {

	@ParameterizedTest(name = "Admonition online pargraph type: {0}")
	@CsvSource({ //
		"TIP, Tip", //
		"NOTE, Note", //
		"WARNING, Warning", //
		"IMPORTANT, Important", //
		"CAUTION, Caution" //
	})
	void paragraphWithOneLine(String type, String label) {
		String input = type + ": This is a " + type.toLowerCase() + "!";
		String html = parseToHtml(input);

		// Assumes your expected output format follows the same structure
		String expected = String.format("""
				<div class="admonitionblock %s"><table><tr><td class="icon"><div class="title">%s</div></td>
				<td class="content">
				This is a %s!
				</td></tr></table></div>
				""", type.toLowerCase(), label, type.toLowerCase());

		assertEquals(expected.trim(), html.trim());
	}


	@Test
	void paragraphWithSeveralLines() {
		final String html = parseToHtml("""
				TIP: This is a tip!
				Where we have a second line.
				And even a third one.
				""");

		final String expected = """
				<div class="admonitionblock tip"><table><tr><td class="icon"><div class="title">Tip</div></td>
				<td class="content">
				This is a tip!
				Where we have a second line.
				And even a third one.
				</td></tr></table></div>
				""";

		assertEquals(expected, html);
	}

}
