/*******************************************************************************
 * Copyright (c) 2012 Max Rydahl Andersen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Max Rydahl Andersen - copied from markdown to get base for asciidoc
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
}