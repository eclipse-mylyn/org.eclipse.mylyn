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
 *******************************************************************************/

package org.eclipse.mylyn.commons.core;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;

import junit.framework.TestCase;

@SuppressWarnings("nls")
public class HtmlStreamTokenizerTest extends TestCase {

	public void testDivSelfTerminatingNoSpace() throws IOException, ParseException {
		HtmlStreamTokenizer htmlStreamTokenizer = new HtmlStreamTokenizer(new StringReader("<div/>"), null);
		HtmlStreamTokenizer.Token token = htmlStreamTokenizer.nextToken();
		assertEquals("div", ((HtmlTag) token.getValue()).getTagName());
		assertTrue(((HtmlTag) token.getValue()).isSelfTerminating());
	}

	public void testDivSelfTerminatingLeadingSpace() throws IOException, ParseException {
		HtmlStreamTokenizer htmlStreamTokenizer = new HtmlStreamTokenizer(new StringReader("<div />"), null);
		HtmlStreamTokenizer.Token token = htmlStreamTokenizer.nextToken();
		assertEquals("div", ((HtmlTag) token.getValue()).getTagName());
		assertTrue(((HtmlTag) token.getValue()).isSelfTerminating());
	}

	public void testDivSelfTerminatingLeadingSpacePendingSpace() throws IOException, ParseException {
		HtmlStreamTokenizer htmlStreamTokenizer = new HtmlStreamTokenizer(new StringReader("<div / >"), null);
		HtmlStreamTokenizer.Token token = htmlStreamTokenizer.nextToken();
		assertEquals("div", ((HtmlTag) token.getValue()).getTagName());
		assertTrue(((HtmlTag) token.getValue()).isSelfTerminating());
	}

	public void testDivSelfTerminatingPendingSpace() throws IOException, ParseException {
		HtmlStreamTokenizer htmlStreamTokenizer = new HtmlStreamTokenizer(new StringReader("<div/ >"), null);
		HtmlStreamTokenizer.Token token = htmlStreamTokenizer.nextToken();
		assertEquals("div", ((HtmlTag) token.getValue()).getTagName());
		assertTrue(((HtmlTag) token.getValue()).isSelfTerminating());
	}

	public void testImgSelfTerminatingNoSpace() throws IOException, ParseException {
		HtmlStreamTokenizer htmlStreamTokenizer = new HtmlStreamTokenizer(new StringReader("<img src=\"test.png\"/>"),
				null);
		HtmlStreamTokenizer.Token token = htmlStreamTokenizer.nextToken();
		assertEquals("img", ((HtmlTag) token.getValue()).getTagName());
		assertTrue(((HtmlTag) token.getValue()).isSelfTerminating());
	}

	public void testImgSelfTerminatingLeadingSpace() throws IOException, ParseException {
		HtmlStreamTokenizer htmlStreamTokenizer = new HtmlStreamTokenizer(new StringReader("<img src=\"test.png\" />"),
				null);
		HtmlStreamTokenizer.Token token = htmlStreamTokenizer.nextToken();
		assertEquals("img", ((HtmlTag) token.getValue()).getTagName());
		assertTrue(((HtmlTag) token.getValue()).isSelfTerminating());
	}

	public void testImgSelfTerminatingLeadingSpacePendingSpace() throws IOException, ParseException {
		HtmlStreamTokenizer htmlStreamTokenizer = new HtmlStreamTokenizer(new StringReader("<img src=\"test.png\" / >"),
				null);
		HtmlStreamTokenizer.Token token = htmlStreamTokenizer.nextToken();
		assertEquals("img", ((HtmlTag) token.getValue()).getTagName());
		assertTrue(((HtmlTag) token.getValue()).isSelfTerminating());
	}

	public void testImgSelfTerminatingPendingSpace() throws IOException, ParseException {
		HtmlStreamTokenizer htmlStreamTokenizer = new HtmlStreamTokenizer(new StringReader("<img src=\"test.png\"/ >"),
				null);
		HtmlStreamTokenizer.Token token = htmlStreamTokenizer.nextToken();
		assertEquals("img", ((HtmlTag) token.getValue()).getTagName());
		assertTrue(((HtmlTag) token.getValue()).isSelfTerminating());
	}
}
