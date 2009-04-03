/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
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

import org.eclipse.mylyn.internal.wikitext.core.util.css.CssParser;
import org.eclipse.mylyn.internal.wikitext.core.util.css.CssRule;
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

	public void processCssStyles(FontState state, FontState parentState, String cssStyles) {
		Iterator<CssRule> ruleIterator = new CssParser().createRuleIterator(cssStyles);
		while (ruleIterator.hasNext()) {
			cssStyleManager.processCssStyles(state, parentState, ruleIterator.next());
		}
	}

	public void testProcessCssStyles() {
		FontState defaultState = new FontState();
		defaultState.size = 12;
		FontState state = new FontState();
		processCssStyles(state, defaultState,
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

		processCssStyles(state, defaultState, "");

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

}
