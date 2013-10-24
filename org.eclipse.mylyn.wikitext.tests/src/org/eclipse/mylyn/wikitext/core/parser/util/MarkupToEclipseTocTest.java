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

import java.io.IOException;
import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.mylyn.wikitext.tests.TestUtil;
import org.eclipse.mylyn.wikitext.textile.core.TextileLanguage;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.io.Resources;

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

	public void basic() throws Exception {
		markupToEclipseToc.setBookTitle("Test");
		markupToEclipseToc.setHtmlFile("Test.html");
		String toc = markupToEclipseToc.parse("h1. title1\n\nContent para 1\n\nh1. title2\n\nMore content\n\nh2. Nested title\n\nnested content");

		TestUtil.println("Eclipse TOC: " + toc);

		assertEqualsResource("basic.xml", toc);
	}

	public void testCopyrightNotice() {
		markupToEclipseToc.setCopyrightNotice("Copyright (c) 2012 David Green");
		String toc = markupToEclipseToc.parse("h1. title");

		TestUtil.println("TOC: " + toc);

		assertTrue("content: " + toc, toc.contains("<!-- Copyright (c) 2012 David Green -->"));
	}

	public void testEmitAnchorsDefaultFalse() {
		assertEquals(-1, markupToEclipseToc.getAnchorLevel());
	}

	public void testEmitAnchorsLevel0() {
		markupToEclipseToc.setBookTitle("Test");
		markupToEclipseToc.setHtmlFile("Test.html");
		markupToEclipseToc.setAnchorLevel(0);
		String toc = markupToEclipseToc.parse("h1. Top");

		TestUtil.println("TOC: " + toc);

		assertEqualsResource("testEmitAnchorsLevel0.xml", toc);
	}

	public void testEmitAnchorsLevel1() {
		markupToEclipseToc.setBookTitle("Test");
		markupToEclipseToc.setHtmlFile("Test.html");
		markupToEclipseToc.setAnchorLevel(1);
		String toc = markupToEclipseToc.parse("h1. First\n\nh2. Second\n\nh1. Third");

		TestUtil.println("TOC: " + toc);

		assertEqualsResource("testEmitAnchorsLevel1.xml", toc);
	}

	private void assertEqualsResource(String resourceName, String actualValue) {
		String expectedValue = loadResource(resourceName);
		assertEquals(expectedValue, actualValue);
	}

	private String loadResource(String resourceName) {
		String name = MarkupToEclipseTocTest.class.getSimpleName() + "." + resourceName;
		URL resource = MarkupToEclipseTocTest.class.getResource(name);
		Preconditions.checkState(resource != null, "Cannot load resource %s", name);
		try {
			return Resources.toString(resource, Charsets.UTF_8);
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}
}
