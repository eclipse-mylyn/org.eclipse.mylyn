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

package org.eclipse.mylyn.internal.wikitext.ui.util.css;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

/**
 * 
 * @author David Green
 */
public class CssParserTest extends TestCase {
	private CssParser parser;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		parser = new CssParser();
	}

	public void testDetectStyles() {
		String css = "a: b and more; c: d ; e: fg; h i: j";
		String[] expectedRuleNames = new String[] { "a", "c", "e", "i" };
		String[] expectedRuleValues = new String[] { "b and more", "d", "fg", "j" };
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

	public void testSimple() throws IOException {

		Stylesheet stylesheet = parser.parse(readFully(CssParserTest.class.getSimpleName() + "_0.css"));
		assertNotNull(stylesheet);
		assertEquals(25, stylesheet.getBlocks().size());
	}

	private String readFully(String resourceName) throws IOException {
		InputStream stream = CssParserTest.class.getResourceAsStream(resourceName);
		try {
			Reader reader = new InputStreamReader(stream);
			int i;
			StringWriter writer = new StringWriter();
			while ((i = reader.read()) != -1) {
				writer.write(i);
			}
			return writer.toString();
		} finally {
			stream.close();
		}
	}

	public void testSimpleId() {
		Selector selector = parser.parseSelector("#id");
		assertNotNull(selector);
		System.out.println(selector);
		assertEquals(IdSelector.class, selector.getClass());
		assertEquals("id", ((IdSelector) selector).getId());
	}

	public void testSimpleClass() {
		Selector selector = parser.parseSelector(".className");
		assertNotNull(selector);
		System.out.println(selector);
		assertEquals(CssClassSelector.class, selector.getClass());
		assertEquals("className", ((CssClassSelector) selector).getCssClass());
	}

	public void testSimpleElement() {
		Selector selector = parser.parseSelector("body");
		assertNotNull(selector);
		System.out.println(selector);
		assertEquals(NameSelector.class, selector.getClass());
		assertEquals("body", ((NameSelector) selector).getName());
	}

	public void testNumericElementName() {
		Selector selector = parser.parseSelector("h1");
		assertNotNull(selector);
		System.out.println("testNumericElementName: " + selector);
		assertEquals(NameSelector.class, selector.getClass());
		assertEquals("h1", ((NameSelector) selector).getName());
	}

	public void testCompoundIdElement() {
		Selector selector = parser.parseSelector("#foo a");
		assertNotNull(selector);
		System.out.println("testCompoundIdElement: " + selector);
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

	public void testCompoundIdElement2() {
		Selector selector = parser.parseSelector("#foo, a");
		assertNotNull(selector);
		System.out.println(selector);
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

	public void testCompoundElementWithJoin() {
		Selector selector = parser.parseSelector("table tr");
		assertNotNull(selector);
		System.out.println(selector);
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

	public void testCompoundElementUsingDot() {
		Selector selector = parser.parseSelector("table.summaryTable");
		assertNotNull(selector);
		System.out.println(selector);
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

	public void testCompoundElementUsingDotAndJoin() {
		Selector selector = parser.parseSelector("table.summaryTable tr.a1");
		assertNotNull(selector);
		System.out.println("testCompoundElementUsingDotAndJoin: " + selector);
		assertEquals(CompositeSelector.class, selector.getClass());

		CompositeSelector compositeSelector = (CompositeSelector) selector;

		assertTrue(compositeSelector.isAnd());
		assertEquals(2, compositeSelector.getComponents().size());

	}

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
