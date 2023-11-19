/*******************************************************************************
 * Copyright (c) 2013 David Green and others.
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

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.parser.ListAttributes;
import org.junit.Before;
import org.junit.Test;

/**
 * @author David Green
 * @see TextileDocumentBuilder
 */
public class TextileDocumentBuilderDefinitionListTest {

	private TextileDocumentBuilder builder;

	private StringWriter out;

	@Before
	public void initBuilder() {
		out = new StringWriter();
		builder = new TextileDocumentBuilder(out);
	}

	@Test
	public void simple() {
		builder.beginDocument();
		beginDl();

		dtDd("a term", "a definition");

		endDl();
		builder.endDocument();

		assertEquals("- a term := a definition\n", out.toString());
	}

	@Test
	public void twoEntries() {
		builder.beginDocument();
		beginDl();

		dtDd("a term", "a definition");
		dtDd("term2", "definition2");

		endDl();
		builder.endDocument();

		assertEquals("- a term := a definition\n- term2 := definition2\n", out.toString());
	}

	@Test
	public void multiLineEntry() {
		builder.beginDocument();
		beginDl();

		dt("a multi line entry");

		beginDd();
		builder.characters("line one");
		builder.lineBreak();
		builder.characters("line two");
		endDd();

		endDl();
		builder.endDocument();

		assertEquals("- a multi line entry := line one\nline two =:\n", out.toString());
	}

	protected void dtDd(String term, String definition) {
		dt(term);

		beginDd();
		builder.characters(definition);
		endDd();
	}

	protected void dt(String term) {
		beginDt();
		builder.characters(term);
		endDt();
	}

	private void endDd() {
		builder.endBlock();
	}

	private void beginDd() {
		builder.beginBlock(BlockType.DEFINITION_ITEM, new Attributes());
	}

	private void endDt() {
		builder.endBlock();
	}

	private void beginDt() {
		builder.beginBlock(BlockType.DEFINITION_TERM, new Attributes());
	}

	protected void endDl() {
		builder.endBlock();
	}

	protected void beginDl() {
		builder.beginBlock(BlockType.DEFINITION_LIST, new ListAttributes());
	}
}
