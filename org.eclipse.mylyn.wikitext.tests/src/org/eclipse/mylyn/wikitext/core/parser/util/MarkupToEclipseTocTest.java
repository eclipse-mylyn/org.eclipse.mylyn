/*******************************************************************************
 * Copyright (c) 2007, 2012 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.core.parser.util;

import junit.framework.TestCase;

import org.eclipse.mylyn.wikitext.tests.TestUtil;
import org.eclipse.mylyn.wikitext.textile.core.TextileLanguage;

/**
 * @author David Green
 */
public class MarkupToEclipseTocTest extends TestCase {

	private MarkupToEclipseToc markupToEclipseToc;

	@Override
	public void setUp() {
		markupToEclipseToc = new MarkupToEclipseToc();
		markupToEclipseToc.setMarkupLanguage(new TextileLanguage());
	}

	public void testHeader() throws Exception {
		markupToEclipseToc.setBookTitle("Test");
		markupToEclipseToc.setHtmlFile("Test.html");
		String toc = markupToEclipseToc.parse("h1. title1\n\nContent para 1\n\nh1. title2\n\nMore content\n\nh2. Nested title\n\nnested content");

		TestUtil.println("Eclipse TOC: " + toc);

		assertEquals("<?xml version='1.0' encoding='utf-8' ?>\n" + "<toc topic=\"Test.html\" label=\"Test\">\n"
				+ "	<topic href=\"Test.html\" label=\"title1\"></topic>\n"
				+ "	<topic href=\"Test.html#title2\" label=\"title2\">\n"
				+ "		<topic href=\"Test.html#Nestedtitle\" label=\"Nested title\"></topic>\n" + "	</topic>\n"
				+ "</toc>", toc);
	}

	public void testCopyrightNotice() {
		markupToEclipseToc.setCopyrightNotice("Copyright (c) 2012 David Green");
		String toc = markupToEclipseToc.parse("h1. title");

		TestUtil.println("TOC: " + toc);

		assertTrue("content: " + toc, toc.contains("<!-- Copyright (c) 2012 David Green -->"));
	}
}
