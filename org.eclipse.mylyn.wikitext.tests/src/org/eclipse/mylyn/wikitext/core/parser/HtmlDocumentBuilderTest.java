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
package org.eclipse.mylyn.wikitext.core.parser;

import java.io.File;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;

import junit.framework.TestCase;

import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.textile.core.TextileLanguage;

/**
 * 
 * 
 * @author David Green
 */
public class HtmlDocumentBuilderTest extends TestCase {

	private MarkupParser parser;

	private StringWriter out;

	private HtmlDocumentBuilder builder;

	@Override
	public void setUp() {
		parser = new MarkupParser();
		parser.setMarkupLanaguage(new TextileLanguage());
		out = new StringWriter();
		builder = new HtmlDocumentBuilder(out);
		parser.setBuilder(builder);
	}

	public void testRelativeUrlWithBase() throws URISyntaxException {
		builder.setBase(new URI("http://www.foo.bar/baz"));
		parser.parse("\"An URL\":foo/bar.html");
		String html = out.toString();
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<a href=\"http://www.foo.bar/baz/foo/bar.html\">An URL</a>"));
	}

	public void testAbsoluteUrlWithBase() throws URISyntaxException {
		builder.setBase(new URI("http://www.foo.bar/baz"));
		parser.parse("\"An URL\":http://www.baz.ca/foo/bar.html");
		String html = out.toString();
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<a href=\"http://www.baz.ca/foo/bar.html\">An URL</a>"));
	}

	public void testRelativeUrlWithFileBase() throws URISyntaxException {
		builder.setBase(new File("/base/2/with space/").toURI());
		parser.parse("\"An URL\":foo/bar.html");
		String html = out.toString();
		System.out.println("HTML: \n" + html);
		assertTrue(html.contains("<a href=\"file:/base/2/with%20space/foo/bar.html\">An URL</a>"));
	}
}
