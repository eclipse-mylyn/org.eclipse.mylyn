/*******************************************************************************
 * Copyright (c) 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.core.parser.outline;

import junit.framework.TestCase;

import org.eclipse.mylyn.wikitext.textile.core.TextileLanguage;

/**
 * 
 * @author David Green
 * @see OutlineParserTest
 */
public class OutlineItemTest extends TestCase {

	public void testContains() {
		OutlineParser outlineParser = new OutlineParser(new TextileLanguage());
		OutlineItem outline = outlineParser.parse("h1. First Header\n\nh2. First Header First Child\n\nh1. Third Header\n");

		OutlineItem firstHeader = outline.getChildren().get(0);
		OutlineItem secondHeader = outline.getChildren().get(1);
		OutlineItem firstHeaderFirstChild = firstHeader.getChildren().get(0);
		assertTrue(outline.contains(firstHeader));
		assertTrue(outline.contains(secondHeader));
		assertTrue(firstHeader.contains(firstHeader));
		assertTrue(secondHeader.contains(secondHeader));
		assertFalse(firstHeader.contains(secondHeader));
		assertFalse(secondHeader.contains(firstHeader));
		assertFalse(firstHeaderFirstChild.contains(firstHeader));
		assertTrue(firstHeader.contains(firstHeaderFirstChild));
	}

	public void testGetSectionLength() {
		OutlineParser outlineParser = new OutlineParser(new TextileLanguage());
		String markup = "h1. First Header\n\nh2. First Header First Child\n\nh1. Third Header\n";
		OutlineItem outline = outlineParser.parse(markup);
		OutlineItem firstHeader = outline.getChildren().get(0);
		OutlineItem secondHeader = outline.getChildren().get(1);
		OutlineItem firstHeaderFirstChild = firstHeader.getChildren().get(0);

		assertEquals(markup.length(), outline.getSectionLength());
		assertEquals(48, firstHeader.getSectionLength());
		assertEquals(17, secondHeader.getSectionLength());
		assertEquals(30, firstHeaderFirstChild.getSectionLength());
	}
}
