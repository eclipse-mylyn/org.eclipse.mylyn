/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
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

package org.eclipse.mylyn.wikitext.textile.internal;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;

import org.junit.Test;

import com.google.common.base.CharMatcher;

public class TextileDocumentBuilderEntityReferenceTest {

	@Test
	public void apostrophe() {
		assertEntityReference("’", "#8217");
	}

	@Test
	public void invalidEnitity() {
		assertEntityReference("&not_an_entity;", "not_an_entity");
	}

	@Test
	public void copy() {
		assertEntityReference("(c)", "copy");
	}

	@Test
	public void copyrightCharacter() {
		assertCharacter("(c)", "\u00A9");
	}

	@Test
	public void regCharacter() {
		assertCharacter("(r)", "\u00AE");
	}

	@Test
	public void nonBreakingSpaceCharacter() {
		assertCharacter("", "\u00A0");
	}

	@Test
	public void nbsp() {
		assertEntityReference(" ", "nbsp");
		assertEntityReference(" ", "#160");
	}

	@Test
	public void commonEntities() {
		assertEntityReference("\"", "quot");
		assertEntityReference("&", "amp");
		assertEntityReference("<", "lt");
		assertEntityReference(">", "gt");
		assertEntityReference("(t)", "#8482");
		assertEntityReference("\uu20ac", "euro");
	}

	private void assertCharacter(String expected, String character) {
		StringWriter out = new StringWriter();
		TextileDocumentBuilder builder = new TextileDocumentBuilder(out);
		builder.beginDocument();
		builder.characters("a " + character + " test");
		builder.endDocument();

		String markup = out.toString();
		String expectedSequence = CharMatcher.whitespace().trimAndCollapseFrom("a " + expected + " test", ' ');
		assertEquals(expectedSequence + "\n\n", markup);
	}

	private void assertEntityReference(String expected, String entity) {
		StringWriter out = new StringWriter();
		TextileDocumentBuilder builder = new TextileDocumentBuilder(out);
		builder.beginDocument();
		builder.entityReference(entity);
		builder.endDocument();

		String markup = out.toString();
		assertEquals(expected + "\n\n", markup);
	}

}
