/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.ui.viewer;

import java.util.Iterator;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.wikitext.ui.viewer.CssStyleManager.CssRule;
import org.eclipse.mylyn.wikitext.tests.HeadRequired;
import org.eclipse.swt.graphics.RGB;

/**
 * 
 * 
 * @author David Green
 */
@HeadRequired
public class CssStyleManagerTest extends TestCase {

	private CssStyleManager cssStyleManager;

	@Override
	public void setUp() {
		cssStyleManager = new CssStyleManager();
	}

	public void testColorToRgb() {
		Integer white = CssStyleManager.cssColorRgb("white");
		assertNotNull(white);
		Integer white2 = CssStyleManager.cssColorRgb("White");
		assertNotNull(white2);

		Integer white3 = CssStyleManager.cssColorRgb("#FFFFFF");
		assertNotNull(white3);

		Integer white4 = CssStyleManager.cssColorRgb("rgb(255,255,255)");
		assertNotNull(white4);

		assertEquals(white, white2);
		assertEquals(white, white3);
		assertEquals(white, white4);
	}

	public void testProcessCssStyles() {
		FontState defaultState = new FontState();
		defaultState.size = 12;
		FontState state = new FontState();
		cssStyleManager.processCssStyles(state, defaultState,
				"font-size: 14px;color: rgb(3,3,3);font-style: italic bold;text-decoration: underline; background-color: blue;");

		assertEquals(14.0f, state.size);
		assertEquals(new RGB(0, 0, 255), state.background);
		assertEquals(new RGB(3, 3, 3), state.foreground);
		assertTrue(state.isBold());
		assertTrue(state.isItalic());
		assertTrue(state.isUnderline());
	}

	public void testProcessCssStylesNoStyles() {
		FontState defaultState = new FontState();
		defaultState.size = 12;
		FontState state = new FontState();

		assertEquals(0.0f, state.size);
		assertEquals(null, state.background);
		assertEquals(null, state.foreground);
		assertFalse(state.isBold());
		assertFalse(state.isItalic());
		assertFalse(state.isUnderline());
		assertFalse(state.isFixedWidth());
		assertFalse(state.isStrikethrough());
		assertFalse(state.isSubscript());
		assertFalse(state.isSuperscript());

		assertEquals(new FontState(), state);

		cssStyleManager.processCssStyles(state, defaultState, "");

		assertEquals(0.0f, state.size);
		assertEquals(null, state.background);
		assertEquals(null, state.foreground);
		assertFalse(state.isBold());
		assertFalse(state.isItalic());
		assertFalse(state.isUnderline());
		assertFalse(state.isFixedWidth());
		assertFalse(state.isStrikethrough());
		assertFalse(state.isSubscript());
		assertFalse(state.isSuperscript());

		assertEquals(new FontState(), state);
	}

	public void testDetectStyles() {
		String css = "a: b and more; c: d ; e: fg; h i: j";
		String[] expectedRuleNames = new String[] { "a", "c", "e", "i" };
		String[] expectedRuleValues = new String[] { "b and more", "d", "fg", "j" };
		Iterator<CssRule> ruleIterator = cssStyleManager.createRuleIterator(css);
		int count = 0;
		while (ruleIterator.hasNext()) {
			CssRule rule = ruleIterator.next();
			++count;
			assertEquals(expectedRuleNames[count - 1], rule.name);
			assertEquals(expectedRuleValues[count - 1], rule.value);
		}
		assertEquals(expectedRuleNames.length, count);
	}
}
