package org.eclipse.mylyn.wikitext.textile.core;

import java.io.IOException;

import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;

import junit.framework.TestCase;

public class BugzillaTextileLanguageTest extends TestCase {


	private MarkupParser parser;

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
		assertEquals(new TextileLanguage().getName(),bugzillaTextileLanguage.getExtendsLanguage());
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
}
