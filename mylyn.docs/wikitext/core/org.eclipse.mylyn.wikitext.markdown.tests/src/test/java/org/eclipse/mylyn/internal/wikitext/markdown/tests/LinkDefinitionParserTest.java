/*******************************************************************************
 * Copyright (c) 2013, 2024 Stefan Seelmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.markdown.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.eclipse.mylyn.wikitext.markdown.internal.LinkDefinition;
import org.eclipse.mylyn.wikitext.markdown.internal.LinkDefinitionParser;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Stefan Seelmann
 */
public class LinkDefinitionParserTest {

	private LinkDefinitionParser linkDefinitionParser;

	@Before
	public void setUp() {
		linkDefinitionParser = new LinkDefinitionParser();
	}

	@Test
	public void testWithoutTitle() {
		String markup = "[foo]: http://example.com/";
		linkDefinitionParser.parse(markup);

		LinkDefinition linkDefinition = linkDefinitionParser.getLinkDefinition("foo");
		assertNotNull(linkDefinition);
		assertEquals("foo", linkDefinition.getId());
		assertEquals("http://example.com/", linkDefinition.getUrl());
		assertNull(linkDefinition.getTitle());
	}

	@Test
	public void testEmptyTitle() {
		String markup = "[foo]: http://example.com/ \"\"";
		linkDefinitionParser.parse(markup);

		LinkDefinition linkDefinition = linkDefinitionParser.getLinkDefinition("foo");
		assertNotNull(linkDefinition);
		assertEquals("foo", linkDefinition.getId());
		assertEquals("http://example.com/", linkDefinition.getUrl());
		assertEquals("", linkDefinition.getTitle());
	}

	@Test
	public void testDoubleQuotedTitle() {
		String markup = "[foo]: http://example.com/ \"Optional Title Here\"";
		linkDefinitionParser.parse(markup);

		LinkDefinition linkDefinition = linkDefinitionParser.getLinkDefinition("foo");
		assertNotNull(linkDefinition);
		assertEquals("foo", linkDefinition.getId());
		assertEquals("http://example.com/", linkDefinition.getUrl());
		assertEquals("Optional Title Here", linkDefinition.getTitle());
	}

	@Test
	public void testSingleQuotedTitle() {
		String markup = "[foo]: http://example.com/ 'Optional Title Here'";
		linkDefinitionParser.parse(markup);

		LinkDefinition linkDefinition = linkDefinitionParser.getLinkDefinition("foo");
		assertNotNull(linkDefinition);
		assertEquals("foo", linkDefinition.getId());
		assertEquals("http://example.com/", linkDefinition.getUrl());
		assertEquals("Optional Title Here", linkDefinition.getTitle());
	}

	@Test
	public void testParenthesedTitle() {
		String markup = "[foo]: http://example.com/ (Optional Title Here)";
		linkDefinitionParser.parse(markup);

		LinkDefinition linkDefinition = linkDefinitionParser.getLinkDefinition("foo");
		assertNotNull(linkDefinition);
		assertEquals("foo", linkDefinition.getId());
		assertEquals("http://example.com/", linkDefinition.getUrl());
		assertEquals("Optional Title Here", linkDefinition.getTitle());
	}

	@Test
	public void testUrlInAngleBrackets() {
		String markup = "[foo]: <http://example.com/> (Optional Title Here)";
		linkDefinitionParser.parse(markup);

		LinkDefinition linkDefinition = linkDefinitionParser.getLinkDefinition("foo");
		assertNotNull(linkDefinition);
		assertEquals("foo", linkDefinition.getId());
		assertEquals("http://example.com/", linkDefinition.getUrl());
		assertEquals("Optional Title Here", linkDefinition.getTitle());
	}

	@Test
	public void testTitleCanBePutOnNextLine() {
		String markup = "   [foo]: <http://example.com/>\n      (Optional Title Here)";
		linkDefinitionParser.parse(markup);

		LinkDefinition linkDefinition = linkDefinitionParser.getLinkDefinition("foo");
		assertNotNull(linkDefinition);
		assertEquals("foo", linkDefinition.getId());
		assertEquals("http://example.com/", linkDefinition.getUrl());
		assertEquals("Optional Title Here", linkDefinition.getTitle());
	}

	@Test
	public void testMayStartWithUpToThreeSpaces() {
		String markup = "   [foo]: <http://example.com/> (Optional Title Here)";
		linkDefinitionParser.parse(markup);
		LinkDefinition linkDefinition = linkDefinitionParser.getLinkDefinition("foo");
		assertNotNull(linkDefinition);
		assertEquals("foo", linkDefinition.getId());
		assertEquals("http://example.com/", linkDefinition.getUrl());
		assertEquals("Optional Title Here", linkDefinition.getTitle());
	}

	@Test
	public void testMayNotStartWithMoreThanThreeSpaces() {
		String markup = "    [foo]: http://example.com/";
		linkDefinitionParser.parse(markup);
		LinkDefinition linkDefinition = linkDefinitionParser.getLinkDefinition("foo");
		assertNotNull(linkDefinition);
	}

	@Test
	public void testNoMatchInMiddleOfLine() {
		String markup = "Lorem [foo]: http://example.com/ ipsum.";
		linkDefinitionParser.parse(markup);
		LinkDefinition linkDefinition = linkDefinitionParser.getLinkDefinition("foo");
		assertNotNull(linkDefinition);
	}

	@Test
	public void testNotCaseSensitive() {
		String markup = "[foo]: http://example.com/";
		linkDefinitionParser.parse(markup);

		LinkDefinition linkDefinition = linkDefinitionParser.getLinkDefinition("FoO");
		assertNotNull(linkDefinition);
		assertEquals("foo", linkDefinition.getId());
		assertEquals("http://example.com/", linkDefinition.getUrl());
		assertNull(linkDefinition.getTitle());
	}

	@Test
	public void testMultiline() {
		String markup = "aaa\n\n  [foo]: http://foo.com/\n  [bar]: http://bar.com/\n\nbbb";
		linkDefinitionParser.parse(markup);
		LinkDefinition fooLinkDefinition = linkDefinitionParser.getLinkDefinition("foo");
		assertNotNull(fooLinkDefinition);
		assertEquals("foo", fooLinkDefinition.getId());
		assertEquals("http://foo.com/", fooLinkDefinition.getUrl());
		assertNull(fooLinkDefinition.getTitle());
		LinkDefinition barLinkDefinition = linkDefinitionParser.getLinkDefinition("bar");
		assertNotNull(barLinkDefinition);
		assertEquals("bar", barLinkDefinition.getId());
		assertEquals("http://bar.com/", barLinkDefinition.getUrl());
		assertNull(barLinkDefinition.getTitle());
	}
}
