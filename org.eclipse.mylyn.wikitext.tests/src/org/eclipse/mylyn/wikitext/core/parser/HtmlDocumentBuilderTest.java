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
package org.eclipse.mylyn.wikitext.core.parser;

import java.io.File;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;

import junit.framework.TestCase;

import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.tests.TestUtil;
import org.eclipse.mylyn.wikitext.textile.core.TextileLanguage;

/**
 * @author David Green
 */
public class HtmlDocumentBuilderTest extends TestCase {

	private MarkupParser parser;

	private StringWriter out;

	private HtmlDocumentBuilder builder;

	@Override
	public void setUp() {
		parser = new MarkupParser();
		parser.setMarkupLanguage(new TextileLanguage());
		out = new StringWriter();
		builder = new HtmlDocumentBuilder(out);
		parser.setBuilder(builder);
	}

	public void testRelativeUrlWithBase() throws URISyntaxException {
		builder.setBase(new URI("http://www.foo.bar/baz"));
		parser.parse("\"An URL\":foo/bar.html");
		String html = out.toString();
		TestUtil.println("HTML: \n" + html);
		assertTrue(html.contains("<a href=\"http://www.foo.bar/baz/foo/bar.html\">An URL</a>"));
	}

	public void testAbsoluteUrlWithBase() throws URISyntaxException {
		builder.setBase(new URI("http://www.foo.bar/baz"));
		parser.parse("\"An URL\":http://www.baz.ca/foo/bar.html");
		String html = out.toString();
		TestUtil.println("HTML: \n" + html);
		assertTrue(html.contains("<a href=\"http://www.baz.ca/foo/bar.html\">An URL</a>"));
	}

	public void testRelativeUrlWithFileBase() throws URISyntaxException {
		URI uri = new File("/base/2/with space/").toURI();
		builder.setBase(uri);
		parser.parse("\"An URL\":foo/bar.html");
		String html = out.toString();
		TestUtil.println("HTML: \n" + html + "\nURI:" + uri);
		String expected = "<a href=\"" + uri.toString() + "/foo/bar.html\">An URL</a>";
		assertTrue("Expected " + expected, html.contains(expected));
	}

	public void testCopyrightNotice() {
		builder.setCopyrightNotice("Copyright notice here");
		parser.parse("content");
		String html = out.toString();
		TestUtil.println("HTML: \n" + html);
		String expected = "<!-- Copyright notice here -->";
		assertTrue("Expected " + expected + " but received " + html, html.contains(expected));
		String metaExpected = "<meta name=\"copyright\" content=\"Copyright notice here\"/>";
		assertTrue("Expected " + metaExpected + " but received " + html, html.contains(metaExpected));
	}

	public void testCopyrightNoticeFormatted() {
		out = new StringWriter();
		builder = new HtmlDocumentBuilder(out, true);
		parser.setBuilder(builder);

		builder.setCopyrightNotice("Copyright notice here");
		parser.parse("content");
		String html = out.toString();
		TestUtil.println("HTML: \n" + html);

		String expected = "<!-- Copyright notice here -->";
		assertTrue("Expected " + expected + " but received " + html,
				html.startsWith("<?xml version='1.0' encoding='utf-8' ?>\n" + "<!-- Copyright notice here -->\n"
						+ "<html"));
	}
}
