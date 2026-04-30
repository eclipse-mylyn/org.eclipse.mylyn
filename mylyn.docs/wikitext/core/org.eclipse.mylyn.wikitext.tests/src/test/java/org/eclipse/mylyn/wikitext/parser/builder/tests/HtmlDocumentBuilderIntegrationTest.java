/*******************************************************************************
 * Copyright (c) 2007, 2024 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     ArSysOp - ongoing support
 *     See git history
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.parser.builder.tests;


import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.textile.TextileLanguage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author David Green
 */
@SuppressWarnings({ "nls", "restriction" })
public class HtmlDocumentBuilderIntegrationTest {

	private MarkupParser parser;

	private StringWriter out;

	private HtmlDocumentBuilder builder;

	@BeforeEach
	void setUp() {
		parser = new MarkupParser();
		parser.setMarkupLanguage(new TextileLanguage());
		out = new StringWriter();
		builder = new HtmlDocumentBuilder(out);
		parser.setBuilder(builder);
	}

	@Test
	public void testRelativeUrlWithBase() throws URISyntaxException {
		builder.setBase(new URI("http://www.foo.bar/baz"));
		parser.parse("\"An URL\":foo/bar.html");
		String html = out.toString();

		assertTrue(html.contains("<a href=\"http://www.foo.bar/baz/foo/bar.html\">An URL</a>"));
	}

	@Test
	public void testAbsoluteUrlWithBase() throws URISyntaxException {
		builder.setBase(new URI("http://www.foo.bar/baz"));
		parser.parse("\"An URL\":http://www.baz.ca/foo/bar.html");
		String html = out.toString();

		assertTrue(html.contains("<a href=\"http://www.baz.ca/foo/bar.html\">An URL</a>"));
	}

	@Test
	public void testRelativeUrlWithFileBase() throws URISyntaxException {
		URI uri = new File("/base/2/with space/").toURI();
		builder.setBase(uri);
		parser.parse("\"An URL\":foo/bar.html");
		String html = out.toString();

		String expected = "<a href=\"" + uri.toString() + "/foo/bar.html\">An URL</a>";
		assertTrue(html.contains(expected), "Expected " + expected);
	}

	@Test
	public void testCopyrightNotice() {
		builder.setCopyrightNotice("Copyright notice here");
		parser.parse("content");
		String html = out.toString();

		String expected = "<!-- Copyright notice here -->";
		assertTrue(html.contains(expected), "Expected " + expected + " but received " + html);
		String metaExpected = "<meta name=\"copyright\" content=\"Copyright notice here\"/>";
		assertTrue(html.contains(metaExpected), "Expected " + metaExpected + " but received " + html);
	}

	@Test
	public void testCopyrightNoticeFormatted() {
		try (StringWriter out = new StringWriter()) {
			builder = new HtmlDocumentBuilder(out, true);
			parser.setBuilder(builder);

			builder.setCopyrightNotice("Copyright notice here");
			parser.parse("content");
			String html = out.toString();

			String expected = "<!-- Copyright notice here -->";
			assertTrue(html.startsWith(
					"""
					<?xml version='1.0' encoding='utf-8' ?>
					<!-- Copyright notice here -->
					<html"""), "Expected " + expected + " but received\n" + html);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
