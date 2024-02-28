/*******************************************************************************
 * Copyright (c) 2009, 2024 David Green and others.
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

package org.eclipse.mylyn.wikitext.parser.css.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

import org.eclipse.mylyn.wikitext.parser.css.Block;
import org.eclipse.mylyn.wikitext.parser.css.CompositeSelector;
import org.eclipse.mylyn.wikitext.parser.css.CssClassSelector;
import org.eclipse.mylyn.wikitext.parser.css.CssParser;
import org.eclipse.mylyn.wikitext.parser.css.CssRule;
import org.eclipse.mylyn.wikitext.parser.css.DescendantSelector;
import org.eclipse.mylyn.wikitext.parser.css.IdSelector;
import org.eclipse.mylyn.wikitext.parser.css.NameSelector;
import org.eclipse.mylyn.wikitext.parser.css.Selector;
import org.eclipse.mylyn.wikitext.parser.css.Stylesheet;
import org.junit.Before;
import org.junit.Test;

/**
 * @author David Green
 */
@SuppressWarnings("nls")
public class CssParserTest {
	private CssParser parser;

	@Before
	public void setUp() throws Exception {
		parser = new CssParser();
	}

	@Test
	public void testDetectStyles() {
		String css = "a: b and more; c: d ; e: fg; h i: j";
		String[] expectedRuleNames = { "a", "c", "e", "i" };
		String[] expectedRuleValues = { "b and more", "d", "fg", "j" };
		Iterator<CssRule> ruleIterator = parser.createRuleIterator(css);
		int count = 0;
		while (ruleIterator.hasNext()) {
			CssRule rule = ruleIterator.next();
			++count;
			assertEquals(expectedRuleNames[count - 1], rule.name);
			assertEquals(expectedRuleValues[count - 1], rule.value);
		}
		assertEquals(expectedRuleNames.length, count);
	}

	@Test
	public void testSimple() throws IOException {

		Stylesheet stylesheet = parser.parse(readFully(CssParserTest.class.getSimpleName() + "_0.css"));
		assertNotNull(stylesheet);
		assertEquals(25, stylesheet.getBlocks().size());
	}

	private String readFully(String resourceName) throws IOException {
		try (InputStream stream = CssParserTest.class.getResourceAsStream(resourceName);
				Reader reader = new InputStreamReader(stream)) {
			int i;
			StringWriter writer = new StringWriter();
			while ((i = reader.read()) != -1) {
				writer.write(i);
			}
			return writer.toString();
		}
	}

	@Test
	public void testSimpleId() {
		Selector selector = parser.parseSelector("#id");
		assertNotNull(selector);

		assertEquals(IdSelector.class, selector.getClass());
		assertEquals("id", ((IdSelector) selector).getId());
	}

	@Test
	public void testSimpleClass() {
		Selector selector = parser.parseSelector(".className");
		assertNotNull(selector);

		assertEquals(CssClassSelector.class, selector.getClass());
		assertEquals("className", ((CssClassSelector) selector).getCssClass());
	}

	@Test
	public void testSimpleElement() {
		Selector selector = parser.parseSelector("body");
		assertNotNull(selector);

		assertEquals(NameSelector.class, selector.getClass());
		assertEquals("body", ((NameSelector) selector).getName());
	}

	@Test
	public void testNumericElementName() {
		Selector selector = parser.parseSelector("h1");
		assertNotNull(selector);

		assertEquals(NameSelector.class, selector.getClass());
		assertEquals("h1", ((NameSelector) selector).getName());
	}

	@Test
	public void testCompoundIdElement() {
		Selector selector = parser.parseSelector("#foo a");
		assertNotNull(selector);

		assertEquals(CompositeSelector.class, selector.getClass());

		CompositeSelector compositeSelector = (CompositeSelector) selector;

		assertTrue(compositeSelector.isAnd());
		assertEquals(2, compositeSelector.getComponents().size());

		Selector firstComponent = compositeSelector.getComponents().get(0);
		assertEquals(DescendantSelector.class, firstComponent.getClass());

		Selector ancestorSelector = ((DescendantSelector) firstComponent).getAncestorSelector();
		assertEquals(IdSelector.class, ancestorSelector.getClass());
		assertEquals("foo", ((IdSelector) ancestorSelector).getId());

		Selector secondComponent = compositeSelector.getComponents().get(1);
		assertEquals(NameSelector.class, secondComponent.getClass());
		assertEquals("a", ((NameSelector) secondComponent).getName());
	}

	@Test
	public void testCompoundIdElement2() {
		Selector selector = parser.parseSelector("#foo, a");
		assertNotNull(selector);

		assertEquals(CompositeSelector.class, selector.getClass());

		CompositeSelector compositeSelector = (CompositeSelector) selector;

		assertFalse(compositeSelector.isAnd());
		assertEquals(2, compositeSelector.getComponents().size());

		Selector firstComponent = compositeSelector.getComponents().get(0);
		assertEquals(IdSelector.class, firstComponent.getClass());
		assertEquals("foo", ((IdSelector) firstComponent).getId());

		Selector secondComponent = compositeSelector.getComponents().get(1);
		assertEquals(NameSelector.class, secondComponent.getClass());
		assertEquals("a", ((NameSelector) secondComponent).getName());
	}

	@Test
	public void testCompoundElementWithJoin() {
		Selector selector = parser.parseSelector("table tr");
		assertNotNull(selector);

		assertEquals(CompositeSelector.class, selector.getClass());

		CompositeSelector compositeSelector = (CompositeSelector) selector;

		assertTrue(compositeSelector.isAnd());
		assertEquals(2, compositeSelector.getComponents().size());

		Selector firstComponent = compositeSelector.getComponents().get(0);
		assertEquals(DescendantSelector.class, firstComponent.getClass());

		Selector ancestorSelector = ((DescendantSelector) firstComponent).getAncestorSelector();
		assertEquals(NameSelector.class, ancestorSelector.getClass());
		assertEquals("table", ((NameSelector) ancestorSelector).getName());

		Selector secondComponent = compositeSelector.getComponents().get(1);
		assertEquals(NameSelector.class, secondComponent.getClass());
		assertEquals("tr", ((NameSelector) secondComponent).getName());
	}

	@Test
	public void testCompoundElementUsingDot() {
		Selector selector = parser.parseSelector("table.summaryTable");
		assertNotNull(selector);

		assertEquals(CompositeSelector.class, selector.getClass());

		CompositeSelector compositeSelector = (CompositeSelector) selector;

		assertTrue(compositeSelector.isAnd());
		assertEquals(2, compositeSelector.getComponents().size());

		Selector firstComponent = compositeSelector.getComponents().get(0);
		assertEquals(NameSelector.class, firstComponent.getClass());
		assertEquals("table", ((NameSelector) firstComponent).getName());

		Selector secondComponent = compositeSelector.getComponents().get(1);
		assertEquals(CssClassSelector.class, secondComponent.getClass());
		assertEquals("summaryTable", ((CssClassSelector) secondComponent).getCssClass());
	}

	@Test
	public void testCompoundElementUsingDotAndJoin() {
		Selector selector = parser.parseSelector("table.summaryTable tr.a1");
		assertNotNull(selector);

		assertEquals(CompositeSelector.class, selector.getClass());

		CompositeSelector compositeSelector = (CompositeSelector) selector;

		assertTrue(compositeSelector.isAnd());
		assertEquals(2, compositeSelector.getComponents().size());

	}

	@Test
	public void testComments() {
		Stylesheet stylesheet = parser.parse("tr { /* font-size: 115%; */ font-size: 100%; } /* foo { sdf: sdf; } */");
		List<Block> blocks = stylesheet.getBlocks();
		assertEquals(1, blocks.size());
		Block block = blocks.get(0);
		List<CssRule> rules = block.getRules();
		assertEquals(1, rules.size());
		CssRule rule = rules.get(0);
		assertEquals("font-size", rule.name);
		assertEquals("100%", rule.value);
	}
}
