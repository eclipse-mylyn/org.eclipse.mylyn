/*******************************************************************************
 * Copyright (c) 2004, 2008 David Green and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;

import org.junit.Test;

/**
 * @author David Green
 */
public class TracHyperlinkUtilStandaloneTest {

	@Test
	public void testWikiPattern2SinglePositiveMatch() {
		Matcher matcher = TracHyperlinkUtil.wikiPattern2.matcher("a HyperLink there");
		assertTrue(matcher.find());
		assertEquals(matcher.group(0), "HyperLink");
		assertFalse(matcher.find());
	}

	@Test
	public void testWikiPattern2MultiplePositiveMatch() {
		Matcher matcher = TracHyperlinkUtil.wikiPattern2.matcher("a HyperLink there and ThereIsAnother");
		assertTrue(matcher.find());
		assertEquals(matcher.group(0), "HyperLink");
		assertTrue(matcher.find());
		assertEquals(matcher.group(0), "ThereIsAnother");
		assertFalse(matcher.find());
	}

	@Test
	public void testWikiPattern2SingleNegativeMatch() {
		Matcher matcher = TracHyperlinkUtil.wikiPattern2.matcher("no !HyperLink there");
		assertFalse(matcher.find());
	}

	@Test
	public void testWikiPattern2SinglePositiveMatchAtStartOfLine() {
		Matcher matcher = TracHyperlinkUtil.wikiPattern2.matcher("HyperLink there");
		assertTrue(matcher.find());
		assertEquals(matcher.group(0), "HyperLink");
		assertFalse(matcher.find());
	}

	@Test
	public void testWikiPattern2SingleNegativeMatchAtStartOfLine() {
		Matcher matcher = TracHyperlinkUtil.wikiPattern2.matcher("!HyperLink there");
		assertFalse(matcher.find());
	}

	@Test
	public void testWikiPattern2MixedPositiveNegativeMatch() {
		Matcher matcher = TracHyperlinkUtil.wikiPattern2.matcher("a HyperLink there and ThereIsAnother but !NotHere");
		assertTrue(matcher.find());
		assertEquals(matcher.group(0), "HyperLink");
		assertTrue(matcher.find());
		assertEquals(matcher.group(0), "ThereIsAnother");
		assertFalse(matcher.find());
	}

}
