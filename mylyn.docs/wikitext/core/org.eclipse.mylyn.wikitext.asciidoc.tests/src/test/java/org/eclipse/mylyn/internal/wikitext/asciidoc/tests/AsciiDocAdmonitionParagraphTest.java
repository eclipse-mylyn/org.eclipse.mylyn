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

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

@SuppressWarnings("nls")
public class AsciiDocAdmonitionParagraphTest extends AsciiDocLanguageTestBase {

	static final Collection<String[]> SIMPLE_TEST_DATA = Arrays.asList(new String[][] { { "TIP", "Tip" },
		{ "NOTE", "Note" }, { "WARNING", "Warning" }, { "IMPORTANT", "Important" }, { "CAUTION", "Caution" } });

	@Test
	public void paragraphWithOneLine() {

		for (String[] test : SIMPLE_TEST_DATA) {
			String type = test[0];
			String label = test[1];

			String html = parseToHtml(type + ": This is a " + type.toLowerCase() + "!");

			String expected = String.format("""
					<div class="admonitionblock %s"><table><tr><td class="icon"><div class="title">%s</div></td>
					<td class="content">
					This is a %s!
					</td></tr></table></div>
					""", type.toLowerCase(), label, type.toLowerCase());

			assertEquals(expected, html);
		}
	}


	@Test
	public void paragraphWithSeveralLines() {
		String html = parseToHtml("""
				TIP: This is a tip!
				Where we have a second line.
				And even a third one.
				""");

		String expected = """
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
