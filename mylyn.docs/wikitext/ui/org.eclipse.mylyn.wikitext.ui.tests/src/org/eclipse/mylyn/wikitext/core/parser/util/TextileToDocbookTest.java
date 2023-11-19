/*******************************************************************************
 * Copyright (c) 2007, 2011 David Green and others.
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.parser.util.MarkupToDocbook;
import org.eclipse.mylyn.wikitext.textile.TextileLanguage;
import org.eclipse.mylyn.wikitext.toolkit.TestResources;
import org.junit.Before;
import org.junit.Test;

/**
 * @author David Green
 * @author Peter Friese bug 273355 Support image scaling for Textile -> DocBook
 */
public class TextileToDocbookTest {

	private MarkupToDocbook textileToDocbook;

	@Before
	public void setUp() {
		textileToDocbook = new MarkupToDocbook();
		textileToDocbook.setMarkupLanguage(new TextileLanguage());
	}

	@Test
	public void testHeader() throws Exception {
		textileToDocbook.setBookTitle("Test");
		String book = textileToDocbook.parse("h1. title1\n\nContent para 1\n\nh1. title2\n\nMore content\n");
		assertContent(book);
	}

	@Test
	public void testMultipleNestedElements() throws Exception {
		textileToDocbook.setBookTitle("Test");
		String book = textileToDocbook.parse("h1. title1\n\nContent para 1\n* a\n* list of\n* items\n");
		assertContent(book);
	}

	@Test
	public void testNestedHeaders() throws Exception {
		textileToDocbook.setBookTitle("Test");
		String book = textileToDocbook
				.parse("h1. title1\n\nContent para 1\nh2. title2\n\nContent para2\n\nh1. title3\n\npara3");

		assertTrue(Pattern.compile("</chapter>\\s*<chapter", Pattern.MULTILINE).matcher(book).find());
	}

	@Test
	public void testHeaderWithAcronym() throws Exception {
		textileToDocbook.setBookTitle("Test");
		String book = textileToDocbook
				.parse("h1. TBA(To Be Announced) plus more content in the header\n\nContent para 1");

		assertTrue(Pattern.compile("<glossterm>TBA</glossterm>\\s*plus more", Pattern.MULTILINE).matcher(book).find());
		assertTrue(Pattern.compile("<glossterm>TBA</glossterm>\\s*<glossdef>", Pattern.MULTILINE).matcher(book).find());
		assertTrue(Pattern.compile("<glossdef>\\s*<para>To Be Announced", Pattern.MULTILINE).matcher(book).find());
	}

	@Test
	public void testBlockCode() throws Exception {
		textileToDocbook.setBookTitle("Test");
		String book = textileToDocbook.parse("h1. A\n\nsome content\n\nbc. public class Foo {\n}\n");

		assertTrue(Pattern.compile("<literallayout>\\s*<code>", Pattern.MULTILINE).matcher(book).find());
	}

	@Test
	public void testBlockCodeExtended() throws Exception {
		textileToDocbook.setBookTitle("Test");
		String book = textileToDocbook
				.parse("h1. A\n\nsome content\n\nbc.. \npublic class Foo {\n}\n\n\nsome other content");

		assertTrue(Pattern.compile("<literallayout>\\s*<code>", Pattern.MULTILINE).matcher(book).find());
	}

	@Test
	public void testFootnotes() throws Exception {
		textileToDocbook.setBookTitle("Test");
		String book = textileToDocbook.parse("something[1] with a footnote\n\nfn1. the footnote text");

		Matcher matcher = Pattern.compile("<link\\s*linkend=\"(___fn.*?)\">", Pattern.MULTILINE).matcher(book);
		assertTrue(matcher.find());

		String linkend = matcher.group(1);
		assertNotNull(linkend);

		assertTrue(Pattern
				.compile("<para\\s*id=\"" + Pattern.quote(linkend) + "\"\\s+role=\"footnote\">", Pattern.MULTILINE)
				.matcher(book)
				.find());
	}

	@Test
	public void testImageScaling() throws Exception {
		textileToDocbook.setBookTitle("Test");
		String book = textileToDocbook.parse("Here comes an !{width:80%}imageUrl! with more text");

		Matcher matcher = Pattern.compile("<imagedata .* scale=\"80\"/>", Pattern.MULTILINE).matcher(book);
		assertTrue(matcher.find());
	}

	@Test
	public void testImageWidth() throws Exception {
		textileToDocbook.setBookTitle("Test");
		String book = textileToDocbook.parse("Here comes an !{width:80px}imageUrl! with more text");

		Matcher matcher = Pattern.compile("<imagedata .* width=\"80px\"/>", Pattern.MULTILINE).matcher(book);
		assertTrue(matcher.find());
	}

	@Test
	public void testImageWidthDepth() throws Exception {
		textileToDocbook.setBookTitle("Test");
		String book = textileToDocbook.parse("Here comes an !{width:80px;height:90px;}imageUrl! with more text");

		Matcher matcher = Pattern.compile("<imagedata .* width=\"80px\"\\s.*?depth=\"90px\"/>", Pattern.MULTILINE)
				.matcher(book);
		assertTrue(matcher.find());
	}

	private void assertContent(String book) {
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		String resourceName = MessageFormat.format("{0}_{1}.xml", TextileToDocbookTest.class.getSimpleName(), testName);
		assertEquals(TestResources.load(TextileToDocbookTest.class, resourceName), book);
	}
}
