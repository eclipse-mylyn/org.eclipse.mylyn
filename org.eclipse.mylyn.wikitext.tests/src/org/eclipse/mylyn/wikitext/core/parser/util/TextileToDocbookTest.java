/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.core.parser.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.eclipse.mylyn.wikitext.textile.core.TextileLanguage;

/**
 * 
 * 
 * @author David Green
 */
public class TextileToDocbookTest extends TestCase {

	private MarkupToDocbook textileToDocbook;

	@Override
	public void setUp() {
		textileToDocbook = new MarkupToDocbook();
		textileToDocbook.setMarkupLanguage(new TextileLanguage());
	}

	public void testHeader() throws Exception {
		textileToDocbook.setBookTitle("Test");
		String book = textileToDocbook.parse("h1. title1\n\nContent para 1\n\nh1. title2\n\nMore content\n");

		System.out.println("Book: " + book);
	}

	public void testMultipleNestedElements() throws Exception {
		textileToDocbook.setBookTitle("Test");
		String book = textileToDocbook.parse("h1. title1\n\nContent para 1\n* a\n* list of\n* items\n");

		System.out.println("Book: " + book);
	}

	public void testNestedHeaders() throws Exception {
		textileToDocbook.setBookTitle("Test");
		String book = textileToDocbook.parse("h1. title1\n\nContent para 1\nh2. title2\n\nContent para2\n\nh1. title3\n\npara3");

		System.out.println("Book: " + book);

		assertTrue(Pattern.compile("</chapter>\\s*<chapter", Pattern.MULTILINE).matcher(book).find());
	}

	public void testHeaderWithAcronym() throws Exception {
		textileToDocbook.setBookTitle("Test");
		String book = textileToDocbook.parse("h1. TBA(To Be Announced) plus more content in the header\n\nContent para 1");

		System.out.println("Book: " + book);

		assertTrue(Pattern.compile("<glossterm>TBA</glossterm>\\s*plus more", Pattern.MULTILINE).matcher(book).find());
		assertTrue(Pattern.compile("<glossterm>TBA</glossterm>\\s*<glossdef>", Pattern.MULTILINE).matcher(book).find());
		assertTrue(Pattern.compile("<glossdef>\\s*<para>To Be Announced", Pattern.MULTILINE).matcher(book).find());
	}

	public void testBlockCode() throws Exception {
		textileToDocbook.setBookTitle("Test");
		String book = textileToDocbook.parse("h1. A\n\nsome content\n\nbc. public class Foo {\n}\n");

		System.out.println("Book: " + book);

		assertTrue(Pattern.compile("<literallayout>\\s*<code>", Pattern.MULTILINE).matcher(book).find());
	}

	public void testBlockCodeExtended() throws Exception {
		textileToDocbook.setBookTitle("Test");
		String book = textileToDocbook.parse("h1. A\n\nsome content\n\nbc.. \npublic class Foo {\n}\n\n\nsome other content");

		System.out.println("Book: " + book);

		assertTrue(Pattern.compile("<literallayout>\\s*<code>", Pattern.MULTILINE).matcher(book).find());
	}

	public void testFootnotes() throws Exception {
		textileToDocbook.setBookTitle("Test");
		String book = textileToDocbook.parse("something[1] with a footnote\n\nfn1. the footnote text");

		System.out.println("Book: " + book);

		Matcher matcher = Pattern.compile("<link\\s*linkend=\"(fn.*?)\">", Pattern.MULTILINE).matcher(book);
		assertTrue(matcher.find());

		String linkend = matcher.group(1);
		assertNotNull(linkend);

		assertTrue(Pattern.compile("<para\\s*id=\"" + Pattern.quote(linkend) + "\">", Pattern.MULTILINE)
				.matcher(book)
				.find());
	}
}
