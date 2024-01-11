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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.mylyn.wikitext.commonmark.internal.Line;
import org.eclipse.mylyn.wikitext.commonmark.internal.TextSegment;
import org.junit.Test;

public class PotentialEmphasisSpanTest extends AbstractSourceSpanTest {

	public PotentialEmphasisSpanTest() {
		super(new PotentialEmphasisSpan());
	}

	@Test
	public void emphasis() {
		assertParseToHtml("* one*", "* one*");
		assertParseToHtml("*one *", "*one *");
		assertParseToHtml("*one", "*one");
		assertParseToHtml("<em>some text</em>", "*some text*");
		assertParseToHtml("<em>some text</em> and more", "*some text* and more");
		assertParseToHtml("<em>some\ntext</em>d", "*some\ntext*d");
		assertParseToHtml("*<em>one</em>", "**one*");
	}

	@Test
	public void strongEmphasis() {
		assertParseToHtml("** one**", "** one**");
		assertParseToHtml("**one **", "**one **");
		assertParseToHtml("**one", "**one");
		assertParseToHtml("<strong>some text</strong>", "**some text**");
		assertParseToHtml("<strong>some text</strong> and more", "**some text** and more");
		assertParseToHtml("<strong>some\ntext</strong>d", "**some\ntext**d");
	}

	@Test
	public void underscoreEmphasis() {
		assertParseToHtml("_ one_", "_ one_");
		assertParseToHtml("_one _", "_one _");
		assertParseToHtml("_one", "_one");
		assertParseToHtml("a_one_", "a_one_");
		assertParseToHtml("_one_a", "_one_a");
		assertParseToHtml("<em>s</em>", "_s_");
		assertParseToHtml("<em>some text</em>", "_some text_");
		assertParseToHtml("<em>some text</em> and more", "_some text_ and more");
		assertParseToHtml("<em>some\ntext</em>", "_some\ntext_");
		assertParseToHtml("<em>some text_a b</em> a", "_some text_a b_ a");
		assertParseToHtml("<em>some text\\_a b</em> a", "_some text\\_a b_ a");
		assertParseToHtml("_<em>one</em>", "__one_");
	}

	@Test
	public void underscoreStrongEmphasis() {
		assertParseToHtml("__ one__", "__ one__");
		assertParseToHtml("__one __", "__one __");
		assertParseToHtml("__one", "__one");
		assertParseToHtml("a__one__", "a__one__");
		assertParseToHtml("__one__a", "__one__a");
		assertParseToHtml("<strong>s</strong>", "__s__");
		assertParseToHtml("<strong>some text</strong>", "__some text__");
		assertParseToHtml("<strong>some text</strong> and more", "__some text__ and more");
		assertParseToHtml("<strong>some\ntext</strong>", "__some\ntext__");
		assertParseToHtml("<strong>some text__a b</strong> a", "__some text__a b__ a");
	}

	@Test
	public void isLeftFlanking() {
		assertLeftFlanking(true, "**a", 0, 2);
		assertLeftFlanking(false, "** a", 0, 2);
		assertLeftFlanking(true, " **a", 1, 2);
		assertLeftFlanking(true, ".**a", 1, 2);
		assertLeftFlanking(false, "**", 0, 2);

		assertLeftFlanking(true, "***abc", 0, 3);
		assertLeftFlanking(true, "  _abc", 2, 1);
		assertLeftFlanking(true, "**\"abc\"", 0, 2);
		assertLeftFlanking(true, " _\"abc\"", 1, 1);

		assertLeftFlanking(false, "abc***", 3, 3);
		assertLeftFlanking(false, "  abc_", 5, 1);
		assertLeftFlanking(false, "\"abc\"**", 5, 2);
		assertLeftFlanking(false, "\"abc\"_", 5, 1);

		assertLeftFlanking(true, "abc**def", 3, 2);
		assertLeftFlanking(true, "\"abc\"_\"def\"", 5, 1);

		assertLeftFlanking(false, "abc *** def", 4, 3);
		assertLeftFlanking(false, "a _ b", 2, 1);
	}

	@Test
	public void isRightFlanking() {
		assertRightFlanking(false, "**", 0, 2);
		assertRightFlanking(true, "a** ", 1, 2);
		assertRightFlanking(true, "a**a", 1, 2);
		assertRightFlanking(true, "a**.", 1, 2);
		assertRightFlanking(true, "a**", 1, 2);

		assertRightFlanking(false, "***abc", 0, 3);
		assertRightFlanking(false, "  _abc", 2, 1);
		assertRightFlanking(false, "**\"abc\"", 0, 2);
		assertRightFlanking(false, " _\"abc\"", 1, 1);

		assertRightFlanking(true, "abc***", 3, 3);
		assertRightFlanking(true, "  abc_", 5, 1);
		assertRightFlanking(true, "\"abc\"**", 5, 2);
		assertRightFlanking(true, "\"abc\"_", 5, 1);

		assertRightFlanking(true, "abc**def", 3, 2);
		assertRightFlanking(true, "\"abc\"_\"def\"", 5, 1);

		assertRightFlanking(false, "abc *** def", 4, 3);
		assertRightFlanking(false, "a _ b", 2, 1);
	}

	private void assertLeftFlanking(boolean expected, String markup, int offset, int length) {
		Cursor cursor = createCursor(markup, offset);
		char delimiter = markup.charAt(offset);
		assertTrue(delimiter == '*' || delimiter == '_');
		assertEquals(expected, new PotentialEmphasisSpan().isLeftFlanking(cursor, length));
	}

	private void assertRightFlanking(boolean expected, String markup, int offset, int length) {
		Cursor cursor = createCursor(markup, offset);
		char delimiter = markup.charAt(offset);
		assertTrue(delimiter == '*' || delimiter == '_');
		assertEquals(expected, new PotentialEmphasisSpan().isRightFlanking(cursor, length));
	}

	private Cursor createCursor(String markup, int offset) {
		TextSegment segment = new TextSegment(List.of(new Line(1, 0, markup)));
		Cursor cursor = new Cursor(segment);
		cursor.advance(offset);
		return cursor;
	}
}
