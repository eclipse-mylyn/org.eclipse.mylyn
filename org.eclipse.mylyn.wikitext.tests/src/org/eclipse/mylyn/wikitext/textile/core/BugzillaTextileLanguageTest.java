/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.textile.core;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;

/**
 * @author David Green
 */
public class BugzillaTextileLanguageTest extends TestCase {

	private MarkupParser parser;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		initParser();
	}

	private void initParser() throws IOException {
		parser = new MarkupParser();
		parser.setMarkupLanaguage(new BugzillaTextileLanguage());
	}

	public void testName() {
		BugzillaTextileLanguage bugzillaTextileLanguage = new BugzillaTextileLanguage();
		assertNotNull(bugzillaTextileLanguage.getName());
		assertNotNull(bugzillaTextileLanguage.getExtendsLanguage());
		assertFalse(bugzillaTextileLanguage.getName().equals(bugzillaTextileLanguage.getExtendsLanguage()));
		assertEquals(new TextileLanguage().getName(), bugzillaTextileLanguage.getExtendsLanguage());
	}

	public void testQuotedBlock() {
		String html = parser.parseToHtml("One\n\n> Two\n\nThree");
		System.out.println(html);
		assertTrue(html.contains("<body><p>One</p><blockquote><p>> Two</p></blockquote><p>Three</p></body>"));
	}

	public void testQuotedBlock2() {
		String html = parser.parseToHtml("One\n\n> Two\nThree");
		System.out.println(html);
		assertTrue(html.contains("<body><p>One</p><blockquote><p>> Two</p></blockquote><p>Three</p></body>"));
	}

	public void testQuotedBlock3() {
		String html = parser.parseToHtml("One\n> Two\n\nThree");
		System.out.println(html);
		assertTrue(html.contains("<body><p>One</p><blockquote><p>> Two</p></blockquote><p>Three</p></body>"));
	}

	public void testQuotedBlock4() {
		String html = parser.parseToHtml("One\n(In reply to comment #123)\n> Two\n\nThree");
		System.out.println(html);
		assertTrue(html.contains("<body><p>One</p><blockquote><p>(In reply to comment #123)<br/>> Two</p></blockquote><p>Three</p></body>"));
	}

	public void testQuotedBlock5() {
		String html = parser.parseToHtml("One\n > Two\n > Three\nFour");
		System.out.println(html);
		assertTrue(html.contains("<body><p>One</p><blockquote><p> > Two<br/> > Three</p></blockquote><p>Four</p></body>"));
	}

	public void testThisBugHasBeen() {
		String baseMarkup = "*** This bug has been marked as a duplicate of bug 123 ***";

		String html = parser.parseToHtml(baseMarkup);
		System.out.println(html);
		assertTrue(html.contains("<p style=\"color: Blue;\">*** This bug has been marked as a duplicate of bug 123 ***</p>"));

		html = parser.parseToHtml(" \t " + baseMarkup);
		System.out.println(html);
		assertTrue(html.contains("<p style=\"color: Blue;\"> \t *** This bug has been marked as a duplicate of bug 123 ***</p>"));

		html = parser.parseToHtml(baseMarkup + "  ");
		System.out.println(html);
		assertTrue(html.contains("<p style=\"color: Blue;\">*** This bug has been marked as a duplicate of bug 123 ***  </p>"));
	}

	public void testThisBugHasBeenNegativeMatch() {
		String baseMarkup = "*** This bug has been marked as a duplicate of bug 123";

		String html = parser.parseToHtml(baseMarkup);
		System.out.println(html);
		assertTrue(html.contains("<body><ul><li><ul><li><ul><li>This bug has been marked as a duplicate of bug 123"));
	}

	public void testBugFooHasBeenMatch() {
		String markup = "*** Bug 209610 has been marked as a duplicate of this bug. ***";
		String html = parser.parseToHtml(markup);
		System.out.println(html);
		assertTrue(html.contains("<body><p style=\"color: Blue;\">*** Bug 209610 has been marked as a duplicate of this bug. ***</p>"));
	}

	public void testBugFooHasBeenNegativeMatch() {
		String markup = "*** Bug 209610 has been marked as a duplicate of this bug.";
		String html = parser.parseToHtml(markup);
		System.out.println(html);
		assertTrue(html.contains("<body><ul><li><ul><li><ul><li>Bug 209610 has been marked as a duplicate of this bug."));
	}

	public void testXmlEscaping() {
		String html = parser.parseToHtml("some <start>mark</start> up");
		System.out.println(html);
		assertTrue(html.contains("<p>some &lt;start>mark&lt;/start> up</p>"));
	}

	public void testHtmlEscaping() {
		String html = parser.parseToHtml("some <span class=\"s\">mark</span> up");
		System.out.println(html);
		assertTrue(html.contains("<p>some &lt;span class=\"s\">mark&lt;/span> up</p>"));
	}
}
