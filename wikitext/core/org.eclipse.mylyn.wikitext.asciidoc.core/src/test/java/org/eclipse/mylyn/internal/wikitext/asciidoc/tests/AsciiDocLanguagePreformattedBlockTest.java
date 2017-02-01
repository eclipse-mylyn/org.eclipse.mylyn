/*******************************************************************************
 * Copyright (c) 2016 Jeremie Bresson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jeremie Bresson - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.asciidoc.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AsciiDocLanguagePreformattedBlockTest extends AsciiDocLanguageTestBase {

	@Test
	public void testSingleLinePreformattedBlock() {
		String html = parseToHtml("" //
				+ "    10 PRINT \"Hello World!\"\n" //
				+ "\n" //
				+ "Some Text");
		assertEquals("<pre>" //
				+ "10 PRINT \"Hello World!\"<br/>" //
				+ "</pre>" //
				+ "<p>Some Text</p>\n", html);
	}

	@Test
	public void testMultiLinePreformattedBlock() {
		String html = parseToHtml("" //
				+ "    10 PRINT \"Hello World!\"\n" //
				+ "    20 GOTO 10\n");
		assertEquals("<pre>" //
				+ "10 PRINT \"Hello World!\"<br/>" //
				+ "20 GOTO 10<br/>" //
				+ "</pre>", html);
	}

	@Test
	public void testMultiLinePreformattedBlockAndContent() {
		String html = parseToHtml("" //
				+ "\tpublic static void main(String[] args) {\n" //
				+ "\t\tSystem.out.println(\"Hello World!\");\n" //
				+ "\t}\n" //
				+ "\n" //
				+ "Some Text");
		assertEquals("<pre>" //
				+ "public static void main(String[] args) {<br/>" //
				+ "\tSystem.out.println(\"Hello World!\");<br/>" //
				+ "}<br/>" //
				+ "</pre>" //
				+ "<p>Some Text</p>\n", html);
	}
}
