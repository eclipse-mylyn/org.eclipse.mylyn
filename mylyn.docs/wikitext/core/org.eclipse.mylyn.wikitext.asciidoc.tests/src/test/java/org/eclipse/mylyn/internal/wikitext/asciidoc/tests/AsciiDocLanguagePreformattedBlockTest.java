/*******************************************************************************
 * Copyright (c) 2016, 2024 Jeremie Bresson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Jeremie Bresson - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.asciidoc.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

@SuppressWarnings("nls")
public class AsciiDocLanguagePreformattedBlockTest extends AsciiDocLanguageTestBase {

	@Test
	public void testSingleLinePreformattedBlock() {
		String html = parseToHtml("""
				    10 PRINT "Hello World!"

				Some Text""");
		assertEquals("""
				<pre>\
				10 PRINT "Hello World!"</pre>\
				<p>Some Text</p>
				""", html);
	}

	@Test
	public void testMultiLinePreformattedBlock() {
		String html = parseToHtml("""
				    10 PRINT "Hello World!"
				    20 GOTO 10
				""");
		assertEquals("""
				<pre>\
				10 PRINT "Hello World!"
				20 GOTO 10</pre>""", html);
	}

	@Test
	public void testMultiLinePreformattedBlockAndContent() {
		String html = parseToHtml("""
					public static void main(String[] args) {
						System.out.println("Hello World!");
					}

				Some Text""");
		assertEquals("""
				<pre>\
				public static void main(String[] args) {
					System.out.println("Hello World!");
				}</pre>\
				<p>Some Text</p>
				""", html);
	}
}
