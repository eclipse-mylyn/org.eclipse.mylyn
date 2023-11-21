/*******************************************************************************
 * Copyright (c) 2007, 2011 David Green and others.
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

package org.eclipse.mylyn.wikitext.core.parser.markup.token;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.parser.markup.token.ImpliedHyperlinkReplacementToken;
import org.junit.Before;
import org.junit.Test;

/**
 * tests for {@link ImpliedHyperlinkReplacementToken}
 *
 * @author David Green
 * @see ImpliedHyperlinkReplacementToken
 */
public class ImpliedHyperlinkReplacementTokenTest {

	private Pattern pattern;

	private ImpliedHyperlinkReplacementToken token;

	@Before
	public void setUp() throws Exception {
		token = new ImpliedHyperlinkReplacementToken();
		pattern = Pattern.compile(token.getPattern(0));
	}

	@Test
	public void testGroupCount() {
		assertEquals(token.getPatternGroupCount(), pattern.matcher("").groupCount());
	}

	@Test
	public void testSimple() {
		String url = "http://www.eclipse.org";
		testFind(url, 0, url.length());
	}

	@Test
	public void testSimpleAtNonZeroOffset() {
		String url = " http://www.eclipse.org ";
		testFind(url, 1, url.length() - 2);
	}

	@Test
	public void testSimpleEndingWithDot() {
		String url = "http://www.eclipse.org.";
		testFind(url, 0, url.length() - 1);
	}

	@Test
	public void testSimpleEndingWithComma() {
		String url = "http://www.eclipse.org,";
		testFind(url, 0, url.length() - 1);
	}

	@Test
	public void testSimpleEndingWithParen() {
		String url = "http://www.eclipse.org)";
		testFind(url, 0, url.length() - 1);
	}

	@Test
	public void testUrlWithParams() {
		String url = "http://www.eclipse.org/wiki?one=two&three=four";
		testFind(url, 0, url.length());
	}

	@Test
	public void testUrlWithPercentHex() {
		String url = "http://www.eclipse.org/%20/bar";
		testFind(url, 0, url.length());
	}

	@Test
	public void testUrlWithSpace() {
		String url = "http://www.eclipse.org/+/bar";
		testFind(url, 0, url.length());
	}

	@Test
	public void testUrlWithHttps() {
		String url = "https://www.eclipse.org/";
		testFind(url, 0, url.length());
	}

	@Test
	public void testUrlWithHash() {
		String url = "http://www.eclipse.org/#anchor";
		testFind(url, 0, url.length());
	}

	private void testFind(String url, int offset, int length) {
		Matcher matcher = pattern.matcher(url);
		if (matcher.find()) {
			int start = matcher.start(1);
			int end = matcher.end(1);
			assertEquals(offset, start);
			assertEquals(length, end - start);
		} else {
			fail("expected to find url at offset " + offset);
		}
	}

}
