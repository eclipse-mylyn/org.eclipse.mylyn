/*******************************************************************************
 * Copyright (c) 2013, 2024 Frank Becker and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Frank Becker - initial API and implementation
 *     ArSysOp - ongoing support
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.commons.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;

import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
public class HtmlStreamTokenizerTest {

	@Test
	public void testDivSelfTerminatingNoSpace() throws IOException, ParseException {
		HtmlStreamTokenizer htmlStreamTokenizer = new HtmlStreamTokenizer(new StringReader("<div/>"), null);
		HtmlStreamTokenizer.Token token = htmlStreamTokenizer.nextToken();
		assertEquals("div", ((HtmlTag) token.getValue()).getTagName());
		assertTrue(((HtmlTag) token.getValue()).isSelfTerminating());
	}

	@Test
	public void testDivSelfTerminatingLeadingSpace() throws IOException, ParseException {
		HtmlStreamTokenizer htmlStreamTokenizer = new HtmlStreamTokenizer(new StringReader("<div />"), null);
		HtmlStreamTokenizer.Token token = htmlStreamTokenizer.nextToken();
		assertEquals("div", ((HtmlTag) token.getValue()).getTagName());
		assertTrue(((HtmlTag) token.getValue()).isSelfTerminating());
	}

	@Test
	public void testDivSelfTerminatingLeadingSpacePendingSpace() throws IOException, ParseException {
		HtmlStreamTokenizer htmlStreamTokenizer = new HtmlStreamTokenizer(new StringReader("<div / >"), null);
		HtmlStreamTokenizer.Token token = htmlStreamTokenizer.nextToken();
		assertEquals("div", ((HtmlTag) token.getValue()).getTagName());
		assertTrue(((HtmlTag) token.getValue()).isSelfTerminating());
	}

	@Test
	public void testDivSelfTerminatingPendingSpace() throws IOException, ParseException {
		HtmlStreamTokenizer htmlStreamTokenizer = new HtmlStreamTokenizer(new StringReader("<div/ >"), null);
		HtmlStreamTokenizer.Token token = htmlStreamTokenizer.nextToken();
		assertEquals("div", ((HtmlTag) token.getValue()).getTagName());
		assertTrue(((HtmlTag) token.getValue()).isSelfTerminating());
	}

	@Test
	public void testImgSelfTerminatingNoSpace() throws IOException, ParseException {
		HtmlStreamTokenizer htmlStreamTokenizer = new HtmlStreamTokenizer(new StringReader("<img src=\"test.png\"/>"),
				null);
		HtmlStreamTokenizer.Token token = htmlStreamTokenizer.nextToken();
		assertEquals("img", ((HtmlTag) token.getValue()).getTagName());
		assertTrue(((HtmlTag) token.getValue()).isSelfTerminating());
	}

	@Test
	public void testImgSelfTerminatingLeadingSpace() throws IOException, ParseException {
		HtmlStreamTokenizer htmlStreamTokenizer = new HtmlStreamTokenizer(new StringReader("<img src=\"test.png\" />"),
				null);
		HtmlStreamTokenizer.Token token = htmlStreamTokenizer.nextToken();
		assertEquals("img", ((HtmlTag) token.getValue()).getTagName());
		assertTrue(((HtmlTag) token.getValue()).isSelfTerminating());
	}

	@Test
	public void testImgSelfTerminatingLeadingSpacePendingSpace() throws IOException, ParseException {
		HtmlStreamTokenizer htmlStreamTokenizer = new HtmlStreamTokenizer(new StringReader("<img src=\"test.png\" / >"),
				null);
		HtmlStreamTokenizer.Token token = htmlStreamTokenizer.nextToken();
		assertEquals("img", ((HtmlTag) token.getValue()).getTagName());
		assertTrue(((HtmlTag) token.getValue()).isSelfTerminating());
	}

	@Test
	public void testImgSelfTerminatingPendingSpace() throws IOException, ParseException {
		HtmlStreamTokenizer htmlStreamTokenizer = new HtmlStreamTokenizer(new StringReader("<img src=\"test.png\"/ >"),
				null);
		HtmlStreamTokenizer.Token token = htmlStreamTokenizer.nextToken();
		assertEquals("img", ((HtmlTag) token.getValue()).getTagName());
		assertTrue(((HtmlTag) token.getValue()).isSelfTerminating());
	}
}
