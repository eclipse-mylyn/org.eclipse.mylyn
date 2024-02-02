/*******************************************************************************
 * Copyright (c) 2019, 2024 Fabrizio Iannetti and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Fabrizio Iannetti - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.asciidoc.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests for the Table Of Contents block.
 *
 * @author Fabrizio Iannetti
 */
public class AsciiDocTableOfContentsTest extends AsciiDocLanguageTestBase {

	@Test
	public void testToCDefault() {
		String html = parseToHtml("""
				:toc:\s

				= test =

				Test

				== section 1 ==

				=== section 1.1 ===

				==== section 1.1.1 ====

				"""
		);
		String expectedHtml = """
				<h1 id="header">test</h1>\
				<ol class="toc" style="list-style: none;">\
				<li><a href="#_section_1">section 1</a>\
				<ol style="list-style: none;">\
				<li><a href="#_section_1_1">section 1.1</a>\
				</li>\
				</ol>\
				</li>\
				</ol>\
				<p>Test</p>
				<h2 id="_section_1">section 1</h2>\
				<h3 id="_section_1_1">section 1.1</h3>\
				<h4 id="_section_1_1_1">section 1.1.1</h4>"""; //
		assertEquals("toc property default", expectedHtml.trim(), html.trim());
	}

	@Test
	public void testToCMacro() {
		String html = parseToHtml("""
				:toc: macro

				toc::[]

				= test =

				Test

				== section 1 ==

				=== section 1.1 ===

				==== section 1.1.1 ====

				"""
		);
		String expectedHtml = """
				<ol class="toc" style="list-style: none;">\
				<li><a href="#_section_1">section 1</a>\
				<ol style="list-style: none;">\
				<li><a href="#_section_1_1">section 1.1</a>\
				</li>\
				</ol>\
				</li>\
				</ol>\
				<h1 id="header">test</h1>\
				<p>Test</p>
				<h2 id="_section_1">section 1</h2>\
				<h3 id="_section_1_1">section 1.1</h3>\
				<h4 id="_section_1_1_1">section 1.1.1</h4>"""; //
		assertEquals("toc property default", expectedHtml.trim(), html.trim());
	}

	@Test
	public void testToCFiveLevels() {
		String html = parseToHtml("""
				:toc:\s

				:toclevels: 5

				= test =

				Test

				== section 1 ==

				=== section 1.1 ===

				==== section 1.1.1 ====

				===== section 1.1.1.1 =====

				====== section 1.1.1.1.1 ======

				"""
		);
		String expectedHtml = """
				<h1 id="header">test</h1>\
				<ol class="toc" style="list-style: none;">\
				<li><a href="#_section_1">section 1</a>\
				<ol style="list-style: none;">\
				<li><a href="#_section_1_1">section 1.1</a>\
				<ol style="list-style: none;">\
				<li><a href="#_section_1_1_1">section 1.1.1</a>\
				<ol style="list-style: none;">\
				<li><a href="#_section_1_1_1_1">section 1.1.1.1</a>\
				<ol style="list-style: none;">\
				<li><a href="#_section_1_1_1_1_1">section 1.1.1.1.1</a>\
				</li>\
				</ol>\
				</li>\
				</ol>\
				</li>\
				</ol>\
				</li>\
				</ol>\
				</li>\
				</ol>\
				<p>Test</p>
				<h2 id="_section_1">section 1</h2>\
				<h3 id="_section_1_1">section 1.1</h3>\
				<h4 id="_section_1_1_1">section 1.1.1</h4>\
				<h5 id="_section_1_1_1_1">section 1.1.1.1</h5>\
				<h6 id="_section_1_1_1_1_1">section 1.1.1.1.1</h6>"""; //
		assertEquals("toc property default", expectedHtml.trim(), html.trim());
	}

	@Test
	public void testToCTitle() {
		String html = parseToHtml("""
				:toc:\s

				:toc-title: Contents

				= test =

				Test

				== section 1 ==

				=== section 1.1 ===

				==== section 1.1.1 ====

				"""
		);
		String expectedHtml = """
				<h1 id="header">test</h1>\
				<div class="title">Contents</div>\
				<ol class="toc" style="list-style: none;">\
				<li><a href="#_section_1">section 1</a>\
				<ol style="list-style: none;">\
				<li><a href="#_section_1_1">section 1.1</a>\
				</li>\
				</ol>\
				</li>\
				</ol>\
				<p>Test</p>
				<h2 id="_section_1">section 1</h2>\
				<h3 id="_section_1_1">section 1.1</h3>\
				<h4 id="_section_1_1_1">section 1.1.1</h4>"""; //
		assertEquals("toc property default", expectedHtml.trim(), html.trim());
	}

}
