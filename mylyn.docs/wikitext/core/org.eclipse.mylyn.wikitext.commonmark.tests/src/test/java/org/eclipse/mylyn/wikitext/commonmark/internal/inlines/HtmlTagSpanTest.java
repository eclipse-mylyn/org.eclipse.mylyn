/*******************************************************************************
 * Copyright (c) 2015, 2024 David Green and others.
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
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.commonmark.internal.inlines;

import static org.eclipse.mylyn.wikitext.commonmark.internal.inlines.Cursors.createCursor;

import org.junit.Test;

public class HtmlTagSpanTest extends AbstractSourceSpanTest {

	public HtmlTagSpanTest() {
		super(new HtmlTagSpan());
	}

	@Test
	public void htmlTags() {
		assertNoInline(createCursor("<one"));
		assertNoInline(createCursor("< one>"));
		assertNoInline(createCursor("<0one>"));
		assertNoInline(createCursor("<!-- -- -->"));
		assertNoInline(createCursor("<!--->"));
		assertNoInline(createCursor("<!--> -->"));
		assertNoInline(createCursor("<!---> -->"));
		assertInline(HtmlTag.class, 0, 5, createCursor("<one>"));
		assertInline(HtmlTag.class, 0, 5, createCursor("<one> two"));
		assertInline(HtmlTag.class, 0, 22, createCursor("<onetwo three=\"four\"/>"));
		assertInline(HtmlTag.class, 0, 25, createCursor("<onetwo three = 'four' />"));
		assertInline(HtmlTag.class, 0, 33, createCursor("<onetwo three = 'four' selected/>"));
		assertInline(HtmlTag.class, 0, 33, createCursor("<onetwo three = 'four' selected/>"));
		assertInline(HtmlTag.class, 0, 4, createCursor("<a/><b2/>"));
		assertInline(HtmlTag.class, 4, 5, createCursor("<a/><b2/>", 4));
		assertInline(HtmlTag.class, 0, 15, createCursor("<a foo=\"bar\" />"));
		assertInline(HtmlTag.class, 0, 27, createCursor("<a bam = 'baz <em>\"</em>'/>"));
		assertInline(HtmlTag.class, 0, 14, createCursor("<a \n_boolean/>"));
		assertInline(HtmlTag.class, 0, 17, createCursor("<a b=\"c\"\n d='e'/>"));
		assertInline(HtmlTag.class, 0, 20, createCursor("<a zoop:33=zoop:33/>"));
		assertInline(HtmlTag.class, 0, 4, createCursor("</a>"));
		assertInline(HtmlTag.class, 0, 11, createCursor("<!-- c> -->"));
		assertInline(HtmlTag.class, 0, 10, createCursor("<!-- - -->"));
		assertInline(HtmlTag.class, 0, 8, createCursor("<? pi ?>"));
		assertInline(HtmlTag.class, 0, 16, createCursor("<!DECL one two >"));
		assertInline(HtmlTag.class, 0, 12, createCursor("<![CDATA[]]>"));
		assertInline(HtmlTag.class, 0, 13, createCursor("<![CDATA[\n]]>"));
		assertInline(HtmlTag.class, 0, 17, createCursor("<![CDATA[<foo>]]>"));
	}
}
