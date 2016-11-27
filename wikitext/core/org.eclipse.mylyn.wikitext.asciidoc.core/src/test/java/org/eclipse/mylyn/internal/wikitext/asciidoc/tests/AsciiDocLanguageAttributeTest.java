/*******************************************************************************
 * Copyright (c) 2012 Max Rydahl Andersen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Max Rydahl Andersen - copied from markdown to get base for asciidoc
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.asciidoc.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests for asciidoc overview and miscellaneous.
 *
 * @author Max Rydahl Andersen
 */
public class AsciiDocLanguageAttributeTest extends AsciiDocLanguageTestBase {

	
	@Test
	public void basicKeyValueAttribute() {
		String html = parseToHtml(":attr: 42\nThe answer is: {attr}");
		assertEquals("<p>The answer is: 42</p>\n", html);
	}
	
	@Test
	public void ignoreEscapedReference() {
		String html = parseToHtml(":attr: 42\nThe answer is: \\{attr} or {attr}");
		assertEquals("<p>The answer is: {attr} or 42</p>\n", html);
	}
	
	@Test
	public void attributeWithFormatting() {
		String html = parseToHtml(":boldy: *Stronged*\nIs this {boldy}");
		assertEquals("<p>Is this <strong>Stronged</strong></p>\n", html);	
	}
	
	@Test
	public void attributeMidSentence() {
		String html = parseToHtml(":number: three\nIs {number} higher ?");
		assertEquals("<p>Is three higher ?</p>\n", html);	
	}
}