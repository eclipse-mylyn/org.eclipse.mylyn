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
 *     Torkild U. Resheim - Handle links when transforming, bug 325006
 *     Jeremie Bresson - Bug 492302
 *******************************************************************************/
package org.eclipse.mylyn.tests.wikitext.parser.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.parser.LinkAttributes;
import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentBuilder.Stylesheet;
import org.eclipse.mylyn.wikitext.textile.TextileLanguage;
import org.junit.Before;
import org.junit.Test;

/**
 * @author David Green
 * @author Torkild U. Resheim
 */
public class HtmlDocumentBuilder2Test {

	private MarkupParser parser;

	private StringWriter out;

	private HtmlDocumentBuilder builder;

	private final Map<File, URL> fileToUrl = new HashMap<>();

	@Before
	public void setUp() {
		parser = new MarkupParser();
		parser.setMarkupLanguage(new TextileLanguage());
		out = new StringWriter();
		builder = new HtmlDocumentBuilder(out) {
			@Override
			protected void checkFileReadable(File file) {
				if (!fileToUrl.containsKey(file)) {
					super.checkFileReadable(file);
				}
			}

			@Override
			protected Reader getReader(File inputFile) throws FileNotFoundException {
				URL url = fileToUrl.get(inputFile);
				if (url != null) {
					try {
						return new InputStreamReader(url.openStream());
					} catch (IOException e) {
						throw new FileNotFoundException(String.format("%s (%s)", inputFile, url));
					}
				}
				return super.getReader(inputFile);
			}
		};
		parser.setBuilder(builder);
	}

	private void assertContainsPattern(String pattern) {
		Pattern p = Pattern.compile(pattern);
		String html = out.toString();

		assertTrue(p.matcher(html).find());
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
		final File file = new File("/base/2/with space/");
		builder.setBase(file.toURI());
		parser.parse("\"An URL\":foo/bar.html");
		String html = out.toString();

		Pattern pattern = Pattern.compile("<a href=\"file:(/[A-Z]{1}:)?/base/2/with%20space/foo/bar.html\">An URL</a>");
		assertTrue(pattern.matcher(html).find());
	}

	@Test
	public void testHtmlFilenameFormat() throws URISyntaxException {
		final File file = new File("/base/2/with space/");
		builder.setHtmlFilenameFormat("$1.html");
		builder.setBase(file.toURI());
		parser.parse("\"An URL\":foo.bar/bar");
		assertContainsPattern("<a href=\"file:(/[A-Z]{1}:)?/base/2/with%20space/foo.bar/bar.html\">An URL</a>");
	}

	@Test
	public void testHtmlFilenameFormat_Image() throws URISyntaxException {
		final File file = new File("/base/2/with space/");
		builder.setHtmlFilenameFormat("$1.html");
		builder.setBase(file.toURI());
		parser.parse("\"An URL\":foo.bar/bar.jpg");
		assertContainsPattern("<a href=\"file:(/[A-Z]{1}:)?/base/2/with%20space/foo.bar/bar.jpg\">An URL</a>");
	}

	@Test
	public void testHtmlFilenameFormat_WithAnchor() throws URISyntaxException {
		final File file = new File("/base/2/with space/");
		builder.setHtmlFilenameFormat("$1.html");
		builder.setBase(file.toURI());
		parser.parse("\"An URL\":foo/bar#bar");
		assertContainsPattern("<a href=\"file:(/[A-Z]{1}:)?/base/2/with%20space/foo/bar.html#bar\">An URL</a>");
	}

	@Test
	public void testHtmlFilenameFormat_Internal() throws URISyntaxException {
		builder.setHtmlFilenameFormat("$1.html");
		parser.parse("\"An URL\":#bar");
		assertContainsPattern("<a href=\"#bar\">An URL</a>");
	}

	@Test
	public void testHtmlFilenameFormat_Directory() throws URISyntaxException {
		builder.setHtmlFilenameFormat("$1.html");
		parser.parse("\"An URL\":one/two/");
		assertContainsPattern("<a href=\"one/two/\">An URL</a>");
	}

	@Test
	public void testHtmlFilenameFormat_Absolute() throws URISyntaxException {
		builder.setHtmlFilenameFormat("$1.html");
		parser.parse("\"An URL\":http://example.com/one/two");
		assertContainsPattern("<a href=\"http://example.com/one/two\">An URL</a>");
	}

	@Test
	public void testHtmlLinkWithNullHref() {
		// Bug 492302
		LinkAttributes attributes = new LinkAttributes();
		attributes.setId("lorem");
		builder.link(attributes, null, "");
		assertContainsPattern("<a id=\"lorem\"></a>");
	}

	@Test
	public void testHtmlBeginSpanWithId() {
		// Bug 492302
		LinkAttributes attributes = new LinkAttributes();
		attributes.setId("lorem");
		builder.beginSpan(SpanType.LINK, attributes);
		builder.endSpan();
		assertContainsPattern("<a id=\"lorem\"></a>");
	}

	@Test
	public void testSetHtmlFilenameFormat() {
		builder.setHtmlFilenameFormat("$1.thtml");
		assertEquals("$1.thtml", builder.getHtmlFilenameFormat());

		builder.setHtmlFilenameFormat(null);
		assertNull(builder.getHtmlFilenameFormat());

		try {
			builder.setHtmlFilenameFormat("$.html");
			fail("expected exception");
		} catch (IllegalArgumentException e) {
			assertNull(builder.getHtmlFilenameFormat());
		}
	}

	@Test
	public void testNoGratuitousWhitespace() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("some para text");
		builder.lineBreak();
		builder.characters("more para text");
		builder.endBlock();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("second para");
		builder.endBlock();
		builder.endDocument();

		String html = out.toString();

		assertEquals(-1, html.indexOf('\r'));
		assertEquals(-1, html.indexOf('\n'));
		assertEquals(
				"<?xml version='1.0' encoding='utf-8' ?><html xmlns=\"http://www.w3.org/1999/xhtml\"><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/></head><body><p>some para text<br/>more para text</p><p>second para</p></body></html>",
				html);
	}

	@Test
	public void testCssStylesheetAsLink() {
		builder.addCssStylesheet(new Stylesheet("styles/test.css"));
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("some para text");
		builder.endBlock();
		builder.endDocument();

		String html = out.toString();

		assertTrue(html.contains(
				"<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/><link type=\"text/css\" rel=\"stylesheet\" href=\"styles/test.css\"/></head>"));
	}

	@Test
	public void testCssStylesheetEmbedded() throws Exception {
		URL cssResource = HtmlDocumentBuilder2Test.class.getResource("resources/test.css");
		File cssFile = new File(cssResource.toURI().getPath());

		fileToUrl.put(cssFile, cssResource);

		builder.addCssStylesheet(new Stylesheet(cssFile));
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("some para text");
		builder.endBlock();
		builder.endDocument();

		String html = out.toString();

		html = html.replace("&#xd;", "");

		assertTrue(Pattern.compile(
				"<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/><style type=\"text/css\">\\s*body\\s+\\{\\s+background-image: test-content.png;\\s+\\}\\s*</style></head>",
				Pattern.MULTILINE).matcher(html).find());
	}

	@Test
	public void testDefaultTargetForExternalLinks() throws Exception {
		builder.setDefaultAbsoluteLinkTarget("_external");
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.link("http://www.example.com", "test");
		builder.endBlock();
		builder.endDocument();

		String html = out.toString();

		assertTrue(html.contains("<a href=\"http://www.example.com\" target=\"_external\">test</a>"));
	}

	@Test
	public void testDefaultTargetForExternalLinks2() throws Exception {
		builder.setBase(new URI("http://www.notexample.com"));
		builder.setDefaultAbsoluteLinkTarget("_external");
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.link("http://www.example.com", "test");
		builder.endBlock();
		builder.endDocument();

		String html = out.toString();

		assertTrue(html.contains("<a href=\"http://www.example.com\" target=\"_external\">test</a>"));
	}

	@Test
	public void testDefaultTargetForInternalLinks() throws Exception {
		builder.setDefaultAbsoluteLinkTarget("_external");
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.link("foo", "test");
		builder.endBlock();
		builder.endDocument();

		String html = out.toString();

		assertTrue(html.contains("<a href=\"foo\">test</a>"));
	}

	@Test
	public void testDefaultTargetForInternalLinks2() throws Exception {
		builder.setBase(new URI("http://www.example.com/"));
		builder.setDefaultAbsoluteLinkTarget("_external");
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.link("http://www.example.com/foo.html", "test");
		builder.endBlock();
		builder.endDocument();

		String html = out.toString();

		assertTrue(html.contains("<a href=\"http://www.example.com/foo.html\">test</a>"));
	}

	@Test
	public void testSuppressInlineStyles() throws Exception {
		StringWriter out = new StringWriter();
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(out);
		builder.setUseInlineStyles(false);
		builder.beginDocument();
		builder.beginBlock(BlockType.NOTE, new Attributes());
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("foo");
		builder.endBlock();
		builder.endBlock();
		builder.endDocument();

		String html = out.toString();

		assertTrue(html.contains("<body><div class=\"note\"><p>foo</p></div></body>"));
		assertTrue(html.contains("<style type=\"text/css\">"));
	}

	@Test
	public void testSuppressBuiltInlineStyles() throws Exception {
		StringWriter out = new StringWriter();
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(out);
		builder.setSuppressBuiltInStyles(true);
		builder.beginDocument();
		builder.beginBlock(BlockType.NOTE, new Attributes());
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("foo");
		builder.endBlock();
		builder.endBlock();
		builder.endDocument();

		String html = out.toString();

		assertTrue(html.contains("<body><div class=\"note\"><p>foo</p></div></body>"));
		assertTrue(!html.contains("<style type=\"text/css\">"));
	}

	@Test
	public void testLinkRel() throws Exception {
		// link-specific rel
		StringWriter out = new StringWriter();
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(out);
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		LinkAttributes attributes = new LinkAttributes();
		attributes.setRel("nofollow");
		builder.link(attributes, "http://www.foo.bar", "Foo Bar");
		builder.endBlock();
		builder.endDocument();

		String html = out.toString();

		assertTrue(html.contains("<a href=\"http://www.foo.bar\" rel=\"nofollow\">Foo Bar</a>"));

		// default link rel
		out = new StringWriter();
		builder = new HtmlDocumentBuilder(out);
		builder.setLinkRel("nofollow");
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		attributes = new LinkAttributes();
		builder.link(attributes, "http://www.foo.bar", "Foo Bar");
		builder.endBlock();
		builder.endDocument();

		html = out.toString();

		assertTrue(html.contains("<a href=\"http://www.foo.bar\" rel=\"nofollow\">Foo Bar</a>"));

		// both link-specific and default link ref
		out = new StringWriter();
		builder = new HtmlDocumentBuilder(out);
		builder.setLinkRel("nofollow");
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		attributes = new LinkAttributes();
		attributes.setRel("foobar");
		builder.link(attributes, "http://www.foo.bar", "Foo Bar");
		builder.endBlock();
		builder.endDocument();

		html = out.toString();

		assertTrue(html.contains("<a href=\"http://www.foo.bar\" rel=\"foobar nofollow\">Foo Bar</a>"));

		// no rel at all
		out = new StringWriter();
		builder = new HtmlDocumentBuilder(out);
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		attributes = new LinkAttributes();
		builder.link(attributes, "http://www.foo.bar", "Foo Bar");
		builder.endBlock();
		builder.endDocument();

		html = out.toString();

		assertTrue(html.contains("<a href=\"http://www.foo.bar\">Foo Bar</a>"));
	}

	@Test
	public void testStylesheetWithAttributes() {
		Stylesheet stylesheet = new Stylesheet("a/test.css");
		stylesheet.getAttributes().put("foo", "bar");
		builder.addCssStylesheet(stylesheet);

		builder.beginDocument();
		builder.endDocument();

		String html = out.toString();

		assertTrue(html.contains("<link type=\"text/css\" rel=\"stylesheet\" href=\"a/test.css\" foo=\"bar\"/>"));
	}

	@Test
	public void testStylesheetWithNoAttributes() {
		builder.addCssStylesheet(new Stylesheet("a/test.css"));

		builder.beginDocument();
		builder.endDocument();

		String html = out.toString();

		assertTrue(html.contains("<link type=\"text/css\" rel=\"stylesheet\" href=\"a/test.css\"/>"));
	}

	@Test
	public void testFilterEntityReferences_FiltersKnown() {
		builder.setFilterEntityReferences(true);

		doEntityReferenceTest("yen", "&#165;");
	}

	@Test
	public void testFilterEntityReferences_DisabledFilter() {
		doEntityReferenceTest("yen", "&yen;");
	}

	@Test
	public void testFilterEntityReferences_FiltersUnknown() {
		builder.setFilterEntityReferences(true);

		doEntityReferenceTest("unlikely", "&amp;unlikely;");
	}

	private void doEntityReferenceTest(String entityReference, String expected) {
		builder.beginDocument();
		builder.entityReference(entityReference);
		builder.endDocument();

		String html = out.toString();

		assertTrue(html.contains(expected));
	}
}
