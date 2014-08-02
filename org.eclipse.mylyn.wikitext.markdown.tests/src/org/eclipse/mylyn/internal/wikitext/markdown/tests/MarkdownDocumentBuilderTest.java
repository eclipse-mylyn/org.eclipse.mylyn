/*******************************************************************************
 * Copyright (c) 2014 Tasktop Technologies
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Leo Dos Santos - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.markdown.tests;

import java.io.StringWriter;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.wikitext.markdown.core.MarkdownDocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.tests.TestUtil;

/**
 * @see http://daringfireball.net/projects/markdown/syntax
 * @author Leo Dos Santos
 */
public class MarkdownDocumentBuilderTest extends TestCase {

	private DocumentBuilder builder;

	private StringWriter out;

	@Override
	protected void setUp() throws Exception {
		out = new StringWriter();
		builder = new MarkdownDocumentBuilder(out);
	}

	// block elements - http://daringfireball.net/projects/markdown/syntax#block

	public void testEmptyBlock() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.endBlock();
		builder.endDocument();
		assertMarkup("");
	}

	public void testParagraph() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("A paragraph ends when a blank line begins!");
		builder.endBlock();
		builder.endDocument();
		assertMarkup("A paragraph ends when a blank line begins!\n\n");
	}

	public void testImplicitParagraph() {
		builder.beginDocument();
		builder.characters("text1");
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("text2");
		builder.endBlock();
		builder.characters("text3");
		builder.endDocument();
		assertMarkup("text1\n\ntext2\n\ntext3\n\n");
	}

	public void testLineBreak() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("line");
		builder.lineBreak();
		builder.characters("break");
		builder.endBlock();
		builder.endDocument();
		assertMarkup("line  \nbreak\n\n");
	}

	public void testLineBreakImplicitParagraph() {
		builder.beginDocument();
		builder.characters("line");
		builder.lineBreak();
		builder.characters("break");
		builder.endDocument();
		assertMarkup("line  \nbreak\n\n");
	}

	public void testHeadings() {
		builder.beginDocument();
		builder.beginHeading(1, new Attributes());
		builder.characters("This is an H1");
		builder.endHeading();
		builder.beginHeading(2, new Attributes());
		builder.characters("This is an H2");
		builder.endHeading();
		builder.beginHeading(6, new Attributes());
		builder.characters("This is an H6");
		builder.endHeading();
		builder.endDocument();
		assertMarkup("# This is an H1\n\n## This is an H2\n\n###### This is an H6\n\n");
	}

	private void assertMarkup(String expected) {
		String markup = out.toString();
		TestUtil.println(markup);
		assertEquals(expected, markup);
	}

}
