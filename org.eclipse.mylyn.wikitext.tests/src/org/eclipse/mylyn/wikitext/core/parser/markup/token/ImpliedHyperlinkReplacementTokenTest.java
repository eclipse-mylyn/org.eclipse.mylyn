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

package org.eclipse.mylyn.wikitext.core.parser.markup.token;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

/**
 * tests for {@link ImpliedHyperlinkReplacementToken}
 * 
 * @author David Green
 * 
 * @see ImpliedHyperlinkReplacementToken
 */
public class ImpliedHyperlinkReplacementTokenTest extends TestCase {

	private Pattern pattern;

	private ImpliedHyperlinkReplacementToken token;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		token = new ImpliedHyperlinkReplacementToken();
		pattern = Pattern.compile(token.getPattern(0));
	}

	public void testGroupCount() {
		assertEquals(token.getPatternGroupCount(), pattern.matcher("").groupCount());
	}

	public void testSimple() {
		String url = "http://www.eclipse.org";
		testFind(url, 0, url.length());
	}

	public void testSimpleAtNonZeroOffset() {
		String url = " http://www.eclipse.org ";
		testFind(url, 1, url.length() - 2);
	}

	public void testSimpleEndingWithDot() {
		String url = "http://www.eclipse.org.";
		testFind(url, 0, url.length() - 1);
	}

	public void testSimpleEndingWithComma() {
		String url = "http://www.eclipse.org,";
		testFind(url, 0, url.length() - 1);
	}

	public void testSimpleEndingWithParen() {
		String url = "http://www.eclipse.org)";
		testFind(url, 0, url.length() - 1);
	}

	public void testUrlWithParams() {
		String url = "http://www.eclipse.org/wiki?one=two&three=four";
		testFind(url, 0, url.length());
	}

	public void testUrlWithPercentHex() {
		String url = "http://www.eclipse.org/%20/bar";
		testFind(url, 0, url.length());
	}

	public void testUrlWithSpace() {
		String url = "http://www.eclipse.org/+/bar";
		testFind(url, 0, url.length());
	}

	public void testUrlWithHttps() {
		String url = "https://www.eclipse.org/";
		testFind(url, 0, url.length());
	}

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
