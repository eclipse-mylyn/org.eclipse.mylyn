/*******************************************************************************
 * Copyright (c) 2012, 2024 Max Rydahl Andersen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Max Rydahl Andersen - copied from markdown to get base for asciidoc
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.asciidoc.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests for AsciiDoc image links. TODO: All the border="0" is here because wikitext hardcodes it.
 *
 * @author Max Rydahl Andersen
 */
public class AsciiDocLanguageImageTest extends AsciiDocLanguageTestBase {

	@Test
	public void handleAutoAlt() {
		String html = parseToHtml("image:images/sunset.jpg[]");
		assertEquals(
				"<p><span class=\"image\"><img alt=\"sunset\" border=\"0\" src=\"images/sunset.jpg\"/></span></p>\n",
				html);
	}

	@Test
	public void handleAutoAltWithSlashAtEnd() {
		String html = parseToHtml("image:broken/sunset.jpg/[]");
		assertEquals(
				"<p><span class=\"image\"><img alt=\"sunset\" border=\"0\" src=\"broken/sunset.jpg/\"/></span></p>\n",
				html);
	}

	@Test
	public void inlineImageNoTextWithSizes() {
		String html = parseToHtml(
				"You can find image:http://upload.wikimedia.org/wikipedia/commons/3/35/Tux.svg[Linux,25,35] everywhere these days.");
		assertEquals("<p>You can find " //
				+ "<span class=\"image\">" //
				+ "<img height=\"35\" width=\"25\" alt=\"Linux\" border=\"0\" src=\"http://upload.wikimedia.org/wikipedia/commons/3/35/Tux.svg\"/>" //
				+ "</span>" //
				+ " everywhere these days.</p>\n", html);
	}

	@Test
	public void imageNoText() {
		String html = parseToHtml("image::sunset.jpg[]");
		assertEquals("<p>" + "<div class=\"imageblock\">" + "<div class=\"content\">"
				+ "<img alt=\"sunset\" border=\"0\" src=\"sunset.jpg\"/>" + "</div>" + "</div></p>\n", html);

	}

	@Test
	public void imageWithText() {
		String html = parseToHtml("image::sunset.jpg[Sunset]");
		assertEquals(
				"<p>" + "<div class=\"imageblock\">" + "<div class=\"content\">"
						+ "<img alt=\"Sunset\" border=\"0\" src=\"sunset.jpg\"/>" + "</div>" + "</div>" + "</p>\n",
				html);

	}

	@Test
	public void imageWithURL() {
		String html = parseToHtml("image::http://asciidoctor.org/images/octocat.jpg[GitHub mascot]");
		assertEquals("<p>" + "<div class=\"imageblock\">" + "<div class=\"content\">"
				+ "<img alt=\"GitHub mascot\" border=\"0\" src=\"http://asciidoctor.org/images/octocat.jpg\"/>"
				+ "</div>" + "</div>" + "</p>\n", html);

	}

	@Test
	public void imageWithFormatting() {
		String html = parseToHtml("image::sunset.jpg[caption=\"Figure 1: \", title=\"A mountain sunset\", "
				+ "alt=\"Sunset\", width=\"300\", height=\"200\", link=\"http://www.flickr.com/photos/javh/5448336655\"]");
		assertEquals(
				"<p>" + "<div class=\"imageblock\">" + "<div class=\"content\">"
						+ "<a href=\"http://www.flickr.com/photos/javh/5448336655\" class=\"image\">"
						+ "<img height=\"200\" width=\"300\" alt=\"Sunset\" border=\"0\" src=\"sunset.jpg\"/>" + "</a>"
						+ "</div>" + "<div class=\"title\">Figure 1: A mountain sunset</div>" + "</div>" + "</p>\n",
				html);
	}

	@Test
	public void imageWithImageDir() {
		String markup = "" + //
				":imagesdir: img_chap1/\n" + //
				"\n" + //
				"image::cover.png[]\n" + //
				"\n" + //
				":imagesdir: img_chap2\n" + //
				"\n" + //
				"image::cover.png[]\n";
		String html = parseToHtml(markup);

		String expected = "" //
				+ "<p><div class=\"imageblock\"><div class=\"content\"><img alt=\"cover\" border=\"0\" src=\"img_chap1/cover.png\"/></div></div></p>\n"
				+ "<p><div class=\"imageblock\"><div class=\"content\"><img alt=\"cover\" border=\"0\" src=\"img_chap2/cover.png\"/></div></div></p>\n";

		assertEquals(expected, html);
	}

	@Test
	public void urlImageWithImageDir() {
		String markup = "" + //
				":imagesdir: imgs/\n" + //
				"\n" + //
				"image::../cover.png[alt]\n" + //
				"\n" + //
				"image::https://example.com/cover.png[alt]\n" + //
				"\n" + //
				"image::http://www.example.com/cover.png[alt]\n" + //
				"\n" + //
				"image::ftp://me@example.com:20/cover.png[alt]\n" + //
				"\n" + //
				"image::file:///C:/absolute/cover.png[alt]\n" + //
				"\n" + //
				"image::file:///absolute/cover.png[alt]\n";
		String html = parseToHtml(markup);

		String expected = "" //
				+ "<p><div class=\"imageblock\"><div class=\"content\"><img alt=\"alt\" border=\"0\" src=\"imgs/../cover.png\"/></div></div></p>\n"
				+ "<p><div class=\"imageblock\"><div class=\"content\"><img alt=\"alt\" border=\"0\" src=\"https://example.com/cover.png\"/></div></div></p>\n"
				+ "<p><div class=\"imageblock\"><div class=\"content\"><img alt=\"alt\" border=\"0\" src=\"http://www.example.com/cover.png\"/></div></div></p>\n"
				+ "<p><div class=\"imageblock\"><div class=\"content\"><img alt=\"alt\" border=\"0\" src=\"ftp://me@example.com:20/cover.png\"/></div></div></p>\n"
				+ "<p><div class=\"imageblock\"><div class=\"content\"><img alt=\"alt\" border=\"0\" src=\"file:///C:/absolute/cover.png\"/></div></div></p>\n"
				+ "<p><div class=\"imageblock\"><div class=\"content\"><img alt=\"alt\" border=\"0\" src=\"file:///absolute/cover.png\"/></div></div></p>\n";

		assertEquals(expected, html);
	}

	@Test
	public void absolutePathImageWithImageDir() {
		String markup = "" + //
				":imagesdir: imgs/\n" + //
				"\n" + //
				"image::cover.png[alt]\n" + //
				"\n" + //
				"image::/absolute/cover.png[alt]\n" + //
				"\n" + //
				"image::C:/absolute/cover.png[alt]\n" + //
				"\n" + //
				"image::D:\\absolute\\cover.png[alt]\n";
		String html = parseToHtml(markup);

		String expected = "" //
				+ "<p><div class=\"imageblock\"><div class=\"content\"><img alt=\"alt\" border=\"0\" src=\"imgs/cover.png\"/></div></div></p>\n"
				+ "<p><div class=\"imageblock\"><div class=\"content\"><img alt=\"alt\" border=\"0\" src=\"/absolute/cover.png\"/></div></div></p>\n"
				+ "<p><div class=\"imageblock\"><div class=\"content\"><img alt=\"alt\" border=\"0\" src=\"C:/absolute/cover.png\"/></div></div></p>\n"
				+ "<p><div class=\"imageblock\"><div class=\"content\"><img alt=\"alt\" border=\"0\" src=\"D:\\absolute\\cover.png\"/></div></div></p>\n";

		assertEquals(expected, html);
	}
}
