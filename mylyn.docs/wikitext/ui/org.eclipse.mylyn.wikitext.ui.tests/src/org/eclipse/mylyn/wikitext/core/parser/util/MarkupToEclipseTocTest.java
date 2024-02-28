/*******************************************************************************
 * Copyright (c) 2007, 2013 David Green and others.
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
package org.eclipse.mylyn.wikitext.core.parser.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.mylyn.wikitext.parser.util.MarkupToEclipseToc;
import org.eclipse.mylyn.wikitext.textile.TextileLanguage;
import org.eclipse.mylyn.wikitext.toolkit.TestResources;
import org.junit.Before;
import org.junit.Test;

/**
 * @author David Green
 */
@SuppressWarnings("nls")
public class MarkupToEclipseTocTest {

	private MarkupToEclipseToc markupToEclipseToc;

	@Before
	public void setUp() {
		markupToEclipseToc = new MarkupToEclipseToc();
		markupToEclipseToc.setMarkupLanguage(new TextileLanguage());
	}

	public void basic() throws Exception {
		markupToEclipseToc.setBookTitle("Test");
		markupToEclipseToc.setHtmlFile("Test.html");
		String toc = markupToEclipseToc.parse(
				"h1. title1\n\nContent para 1\n\nh1. title2\n\nMore content\n\nh2. Nested title\n\nnested content");

		assertEqualsResource("basic.xml", toc);
	}

	@Test
	public void testCopyrightNotice() {
		markupToEclipseToc.setCopyrightNotice("Copyright (c) 2012 David Green");
		String toc = markupToEclipseToc.parse("h1. title");

		assertTrue("content: " + toc, toc.contains("<!-- Copyright (c) 2012 David Green -->"));
	}

	@Test
	public void testEmitAnchorsDefaultFalse() {
		assertEquals(-1, markupToEclipseToc.getAnchorLevel());
	}

	@Test
	public void testEmitAnchorsLevel0() {
		markupToEclipseToc.setBookTitle("Test");
		markupToEclipseToc.setHtmlFile("Test.html");
		markupToEclipseToc.setAnchorLevel(0);
		String toc = markupToEclipseToc.parse("h1. Top");

		assertEqualsResource("testEmitAnchorsLevel0.xml", toc);
	}

	@Test
	public void testEmitAnchorsLevel1() {
		markupToEclipseToc.setBookTitle("Test");
		markupToEclipseToc.setHtmlFile("Test.html");
		markupToEclipseToc.setAnchorLevel(1);
		String toc = markupToEclipseToc.parse("h1. First\n\nh2. Second\n\nh1. Third");

		assertEqualsResource("testEmitAnchorsLevel1.xml", toc);
	}

	private void assertEqualsResource(String resourceName, String actualValue) {
		String expectedValue = TestResources.load(MarkupToEclipseTocTest.class,
				MarkupToEclipseTocTest.class.getSimpleName() + "." + resourceName);
		assertEquals(expectedValue, actualValue);
	}
}
