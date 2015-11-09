/*******************************************************************************
 * Copyright (c) 2015, 2016 Patrik Suzzi and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Patrik Suzzi - Bug 481670 - [asciidoc] support for lists
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.asciidoc.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests for the AsciiDoc ListBlock elements.
 *
 * @author Patrik Suzzi
 */
public class AsciiDocLanguageListTest extends AsciiDocLanguageTestBase {

	static final String BR = System.getProperty("line.separator");

	static String TEXT_UL = "* level 1" + BR + "** level 2" + BR + "*** level 3" + BR + "**** level 4" + BR
			+ "***** level 5" + BR + "* level 1" + BR;

	static String TEXT_UL_HTML = "<ul><li> level 1<ul><li> level 2<ul><li> level 3<ul><li> level 4<ul><li> level 5</li></ul></li></ul></li></ul></li></ul></li><li> level 1</li></ul>";

	static String TEXT_OL = ". level 1" + BR + ".. level 2" + BR + "... level 3" + BR + ".... level 4" + BR
			+ "..... level 5" + BR + ". level 1" + BR;

	static String TEXT_OL_HTML = "<ol><li> level 1<ol><li> level 2<ol><li> level 3<ol><li> level 4<ol><li> level 5</li></ol></li></ol></li></ol></li></ol></li><li> level 1</li></ol>";

	static String TEXT_MIXL = ". level 1" + BR + "** level 2" + BR + "... level 3" + BR + "**** level 4" + BR
			+ "..... level 5" + BR + "* level 1" + BR;

	static String TEXT_MIXL_HTML = "<ol><li> level 1<ul><li> level 2<ol><li> level 3<ul><li> level 4<ol><li> level 5</li></ol></li></ul></li></ol></li></ul></li></ol><ul><li> level 1</li></ul>";

	static String TEXT_MIX_2 = "** level2 (non-zero start)" + BR + "*** level 3" + BR + ".... level 4, 1st" + BR
			+ ".... level 4, 2nd" + BR;

	static String TEXT_MIX_2_HTML = "<ul><li><ul><li> level2 (non-zero start)<ul><li> level 3<ol><li> level 4, 1st</li><li> level 4, 2nd</li></ol></li></ul></li></ul></li></ul>";

	@Test
	public void testUnorderedList() {
		String html = parseToHtml(TEXT_UL);
		assertEquals("Unordered List parsing", html.trim(), TEXT_UL_HTML.trim());
	}

	@Test
	public void testOrderedList() {
		String html = parseToHtml(TEXT_OL);
		assertEquals("Ordered List parsing", html.trim(), TEXT_OL_HTML.trim());
	}

	@Test
	public void testMixedList() {
		String html = parseToHtml(TEXT_MIXL);
		assertEquals("Mixed List parsing", html.trim(), TEXT_MIXL_HTML.trim());
	}

	@Test
	public void testMixedList2() {
		// mixed ordered and unordered with starting element at 2nd level
		String html = parseToHtml(TEXT_MIX_2);
		assertEquals("Mixed List 2 parsing", html.trim(), TEXT_MIX_2_HTML.trim());
	}

}
