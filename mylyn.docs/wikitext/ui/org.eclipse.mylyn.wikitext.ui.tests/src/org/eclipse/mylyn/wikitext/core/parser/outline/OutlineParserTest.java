/*******************************************************************************
 * Copyright (c) 2007, 2021 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.core.parser.outline;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.eclipse.mylyn.wikitext.mediawiki.MediaWikiLanguage;
import org.eclipse.mylyn.wikitext.parser.outline.OutlineItem;
import org.eclipse.mylyn.wikitext.parser.outline.OutlineParser;
import org.eclipse.mylyn.wikitext.textile.TextileLanguage;
import org.junit.Before;
import org.junit.Test;

/**
 * @author David Green
 */
@SuppressWarnings("nls")
public class OutlineParserTest {

	private OutlineParser outlineParser;

	@Before
	public void setUp() {
		outlineParser = new OutlineParser(new TextileLanguage());
	}

	@Test
	public void testSimple() {
		OutlineItem outline = outlineParser.parse("h1. First Header\n\nh2. Second Header\n\nh1. Third Header\n");

		assertEquals(2, outline.getChildren().size());
		assertEquals(1, outline.getChildren().get(0).getChildren().size());
		assertEquals(0, outline.getChildren().get(1).getChildren().size());
	}

	@Test
	public void testNearestItem() {
		String textile = "h1. First Header\n\nh2. Second Header\n\nh1. Third Header\n";
		OutlineItem outline = outlineParser.parse(textile);

		int idxOfH2 = textile.indexOf("h2. Second");
		assertNotEquals(-1, idxOfH2);

		OutlineItem h2Item = outline.findNearestMatchingOffset(idxOfH2);
		assertNotNull(h2Item);
		assertSame(outline.getChildren().get(0), h2Item.getParent());
		assertEquals(2, h2Item.getLevel());

		OutlineItem h1Item = outline.findNearestMatchingOffset(idxOfH2 - 1);
		assertNotNull(h1Item);
		assertSame(outline.getChildren().get(0), h1Item);
		assertEquals(1, h1Item.getLevel());

		int secondIdxOfH1 = textile.indexOf("h1. Third");
		OutlineItem secondH1Item = outline.findNearestMatchingOffset(secondIdxOfH1);
		assertNotNull(secondH1Item);
		assertSame(outline.getChildren().get(1), secondH1Item);
		assertEquals(1, secondH1Item.getLevel());

		OutlineItem h2Item2 = outline.findNearestMatchingOffset(secondIdxOfH1 - 1);
		assertSame(h2Item, h2Item2);
	}

	@Test
	public void testPrevious() {
		String textile = "h1. First Header\n\nh2. Second Header\n\nh1. Third Header\n";
		OutlineItem outline = outlineParser.parse(textile);

		assertNull(outline.getPrevious());

		assertSame(outline, outline.getChildren().get(0).getPrevious());
		assertSame(outline.getChildren().get(0), outline.getChildren().get(1).getPrevious());
		assertSame(outline.getChildren().get(0), outline.getChildren().get(0).getChildren().get(0).getPrevious());
	}

	@Test
	public void testMoveChildren() {
		String textile = "h1. First Header\n\nh2. Second Header\n\nh1. Third Header\n";
		OutlineItem outline = outlineParser.parse(textile);
		assertEquals(2, outline.getChildren().size());

		OutlineItem outline2 = outlineParser.createRootItem();

		outline2.moveChildren(outline);

		assertEquals(0, outline.getChildren().size());
		assertEquals(2, outline2.getChildren().size());
		assertSame(outline2, outline2.getChildren().get(0).getParent());
		assertSame(outline2, outline2.getChildren().get(1).getParent());

		outline = outlineParser.parse(textile);

		outline.moveChildren(outline2);

		assertEquals(0, outline2.getChildren().size());
		assertEquals(4, outline.getChildren().size());
		assertSame(outline, outline.getChildren().get(0).getParent());
		assertSame(outline, outline.getChildren().get(1).getParent());
		assertSame(outline, outline.getChildren().get(2).getParent());
		assertSame(outline, outline.getChildren().get(3).getParent());

	}

	@Test
	public void testHeadersWithHtmlTags() {
		// bug 374019
		outlineParser = new OutlineParser(new MediaWikiLanguage());

		OutlineItem outline = outlineParser
				.parse("= <span style=\"font-family:monospace\">Heading Text</span> =\n\n text");

		assertEquals(1, outline.getChildren().size());

		OutlineItem headingItem = outline.getChildren().get(0);

		assertEquals("Heading_Text", headingItem.getId());
		assertEquals("Heading Text", headingItem.getLabel());
	}
}
