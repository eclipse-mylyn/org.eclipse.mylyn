/*******************************************************************************
 * Copyright (c) 2006, 2024 IBM Corporation and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     IBM Corporation - initial API and implementation
 *     ArSysOp - ongoing support
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.commons.tests.core;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.eclipse.mylyn.internal.commons.core.Html2TextReader;

import junit.framework.TestCase;

/**
 * <p>
 * Based on {@link org.eclipse.jface.text.tests.Html2TextReaderTest}.
 * </p>
 * we add the following Test: <br>
 * &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; testSymbolLt, <br>
 * &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; testSymbolGt, <br>
 * &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; testSymbolNbsp, <br>
 * &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; testSymbolCirc, <br>
 * &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; testSymbolTilde, <br>
 * &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; testSymbolQuot, <br>
 * &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; testSymbolAElig, <br>
 * &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; testSymbolNotInEntityLookup
 */

@SuppressWarnings("nls")
public class Html2TextReaderTest extends TestCase {

	private static final boolean DEBUG = false;

	private static final String LD = System.getProperty("line.separator", "\n"); //$NON-NLS-1$ //$NON-NLS-2$

	private void verify(String input, String expectedOutput) throws IOException {

		Reader reader = new StringReader(input);

		try (Html2TextReader htmlReader = new Html2TextReader(reader)) {
			String result = htmlReader.getString();

			if (DEBUG) {
				System.out.println("<" + result + "/>");
			}

			assertEquals(expectedOutput, result);
		}
	}

	public void test0() throws IOException {

		String string = "<code>3<5<code>";

		String expected = "3<5";

		verify(string, expected);

	}

	public void test1() throws IOException {

		String string = "<dl><dt>@author</dt><dd>Foo Bar</dd></dl>";

		String expected = LD + "@author" + LD + "\tFoo Bar" + LD;

		verify(string, expected);

	}

	public void test2() throws IOException {
		String string = "<code>3>5<code>";
		String expected = "3>5";
		verify(string, expected);
	}

	public void test3() throws IOException {
		String string = "<a href= \"<p>this is only a string - not a tag<p>\">text</a>";
		String expected = "text";
		verify(string, expected);
	}

	public void test4() throws IOException {
		String string = "<html><body text=\"#000000\" bgcolor=\"#FFFF88\"><font size=-1><h5>void p.Bb.fes()</h5><p><dl><dt>Parameters:</dt><dd><b>i</b> fred or <code>null</code></dd></dl></font></body></html>";
		String expected = "void p.Bb.fes()" + LD + LD + LD + "Parameters:" + LD + "\ti fred or null" + LD;
		verify(string, expected);
	}

	public void test5() throws IOException {
		String string = "<code>1<2<3<4</code>";
		String expected = "1<2<3<4";
		verify(string, expected);
	}

	public void test6() throws IOException {
		//test for bug 19070
		String string = "<p>Something.<p>Something more.";
		String expected = LD + "Something." + LD + "Something more.";
		verify(string, expected);
	}

	public void testComments() throws Exception {
		String string = "<!-- begin-user-doc -->no comment<!-- end-user-doc -->";
		String expected = "no comment";
		verify(string, expected);
	}

	public void testSymbolLt() throws IOException {
		String string = "&lt;";
		String expected = "<";
		verify(string, expected);
	}

	public void testSymbolGt() throws IOException {
		String string = "&gt;";
		String expected = ">";
		verify(string, expected);
	}

	public void testSymbolNbsp() throws IOException {
		String string = "a&nbsp;b";
		String expected = "a b";
		verify(string, expected);
	}

	public void testSymbolAmp() throws IOException {
		String string = "&amp;";
		String expected = "&";
		verify(string, expected);
	}

	public void testSymbolCirc() throws IOException {
		String string = "&circ;";
		String expected = "^";
		verify(string, expected);
	}

	public void testSymbolTilde() throws IOException {
		String string = "&tilde;";
		String expected = "~";
		verify(string, expected);
	}

	public void testSymbolQuot() throws IOException {
		String string = "&quot;";
		String expected = "\"";
		verify(string, expected);
	}

	public void testSymbolAElig() throws IOException {
		String string = "&AElig;";
		String expected = "&AElig;";
		verify(string, expected);
	}

	public void testSymbolNotInEntityLookup() throws IOException {
		String string = "&auml;";
		String expected = "&auml;";
		verify(string, expected);
	}

}
