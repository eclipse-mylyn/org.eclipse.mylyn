/*******************************************************************************
 * Copyright (c) 2015 Stefan Seelmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.asciidoc.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Stefan Seelmann
 * @author Max Rydahl Andersen
 */
public class AsciiDocLanguageTest extends AsciiDocLanguageTestBase {

	@Test
	public void fullExample() {

		StringBuilder text = new StringBuilder();
		text.append("Header 2\n");
		text.append("-------\n");
		text.append("\n");
		text.append("Lorem ipsum *dolor* sit amet, \n");
		text.append("\n");
		text.append(":var2: two\n");
		text.append("=== Header 3\n");
		text.append("\n");
		text.append("consetetur _adipisici_ {var2} elit.\n");

		String html = parseToHtml(text.toString());

		assertEquals("<h2>Header 2</h2><p>Lorem ipsum <strong>dolor</strong> sit amet, </p>\n"
				+ "<h3>Header 3</h3><p>consetetur <em>adipisici</em> two elit.</p>\n", html);
	}
}
