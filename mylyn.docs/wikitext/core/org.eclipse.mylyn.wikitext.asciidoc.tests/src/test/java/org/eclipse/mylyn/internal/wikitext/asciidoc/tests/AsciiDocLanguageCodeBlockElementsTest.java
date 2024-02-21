/*******************************************************************************
 * Copyright (c) 2015, 2024 Max Rydahl Andersen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Max Rydahl Andersen - Bug 474084: initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.asciidoc.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests for asciidoc code block elements.
 *
 * @author Max Rydahl Andersen
 */
@SuppressWarnings("nls")
public class AsciiDocLanguageCodeBlockElementsTest extends AsciiDocLanguageTestBase {

	@Test
	public void basicCodeBlock() {
		String html = parseToHtml("""
				----
				10 PRINT "Hello World!"
				20 GOTO 10
				----""");
		assertEquals("""
				<div class="listingblock">\
				<div class="content">\
				<pre class="nowrap">\
				<code class="nowrap">\
				10 PRINT "Hello World!"<br/>\
				20 GOTO 10<br/>\
				</code>\
				</pre>\
				</div></div>""", html);
	}

	@Test
	public void basicCodeBlockWithSpaceAtBegin() {
		String html = parseToHtml("""
				----\s
				10 PRINT "Hello World!"
				20 GOTO 10
				----""");
		assertEquals("""
				<div class="listingblock">\
				<div class="content">\
				<pre class="nowrap">\
				<code class="nowrap">\
				10 PRINT "Hello World!"<br/>\
				20 GOTO 10<br/>\
				</code>\
				</pre>\
				</div></div>""", html);
	}

	@Test
	public void basicCodeBlockWithSpacesAtEnd() {
		String html = parseToHtml("""
				----
				10 PRINT "Hello World!"
				20 GOTO 10
				----\s\s\s\s""");
		assertEquals("""
				<div class="listingblock">\
				<div class="content">\
				<pre class="nowrap">\
				<code class="nowrap">\
				10 PRINT "Hello World!"<br/>\
				20 GOTO 10<br/>\
				</code>\
				</pre>\
				</div></div>""", html);
	}

	@Test
	public void titledCodeBlock() {
		String html = parseToHtml("""
				.Helloworld.bas
				----
				10 PRINT "Hello World!"
				20 GOTO 10
				----""");
		assertEquals("""
				<div class="listingblock">\
				<div class="title">Helloworld.bas</div>\
				<div class="content">\
				<pre class="nowrap">\
				<code class="nowrap">\
				10 PRINT "Hello World!"<br/>\
				20 GOTO 10<br/>\
				</code>\
				</pre>\
				</div></div>""", html);
	}

	@Test
	public void sourceCodeBlock() {
		String html = parseToHtml("""
				[source, java]
				----
				System.out.println("Hello World!");
				----""");
		assertEquals("""
				<div class="listingblock">\
				<div class="content">\
				<pre class="nowrap source-java">\
				<code class="nowrap source-java">\
				System.out.println("Hello World!");<br/>\
				</code>\
				</pre>\
				</div></div>""", html);
	}

	@Test
	public void sourceCodeBlockWithTrailingWhitespaces() {
		String html = parseToHtml("""
				[source, java]\s\s
				----
				System.out.println("Hello World!");
				----""");
		assertEquals("""
				<div class="listingblock">\
				<div class="content">\
				<pre class="nowrap source-java">\
				<code class="nowrap source-java">\
				System.out.println("Hello World!");<br/>\
				</code>\
				</pre>\
				</div></div>""", html);
	}

	@Test
	public void titledCodeWithSourceBlock() {
		String html = parseToHtml("""
				.Helloworld.bas
				[source,basic]
				----
				10 PRINT "Hello World!"
				20 GOTO 10
				----""");
		assertEquals("""
				<div class="listingblock">\
				<div class="title">Helloworld.bas</div>\
				<div class="content">\
				<pre class="nowrap source-basic">\
				<code class="nowrap source-basic">\
				10 PRINT "Hello World!"<br/>\
				20 GOTO 10<br/>\
				</code>\
				</pre>\
				</div></div>""", html);
	}

	@Test
	public void unbalancedCodeBlock() {
		// ascidoctor requires matching start/end blocks
		// http://asciidoctor.org/docs/user-manual/#delimiter-lines
		String html = parseToHtml("""
				----
				10 PRINT "Hello World!"
				20 GOTO 10
				---""");
		assertEquals("""
				<div class="listingblock">\
				<div class="content">\
				<pre class="nowrap">\
				<code class="nowrap">\
				10 PRINT "Hello World!"<br/>\
				20 GOTO 10<br/>\
				---<br/>\
				</code>\
				</pre>\
				</div></div>""", html);

	}
}
