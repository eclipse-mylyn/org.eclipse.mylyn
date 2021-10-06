/*******************************************************************************
 * Copyright (c) 2018 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Kevin de Vlaming - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.creole.tests.documentbuilder;

import static org.junit.Assert.assertEquals;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;
import org.junit.Test;

/**
 * @see http://www.wikicreole.org/wiki/Elements
 * @author Kevin de Vlaming
 */
public class CreoleDocumentBuilderBlockTest extends AbstractCreoleDocumentBuilderTest {
	@Test
	public void testParagraph() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("A paragraph ends when a blank line begins!");
		builder.endBlock();
		builder.endDocument();
		assertMarkup("A paragraph ends when a blank line begins!\n\n");
	}

	@Test
	public void testParagraphConsecutive() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("Paragraph 1");
		builder.endBlock();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("Paragraph 2");
		builder.endBlock();
		builder.endDocument();
		assertMarkup("Paragraph 1\n\nParagraph 2\n\n");
	}

	@Test
	public void testParagraphWithStrongEmphasis() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("some ");
		builder.beginSpan(SpanType.STRONG, new Attributes());
		builder.characters("strong");
		builder.endSpan();
		builder.characters(" and ");
		builder.beginSpan(SpanType.EMPHASIS, new Attributes());
		builder.characters("emphasis");
		builder.endSpan();
		builder.endBlock();
		builder.endDocument();
		assertMarkup("some **strong** and //emphasis//\n\n");
	}

	@Test
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

	@Test
	public void testSpanImplicitParagraph() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("Paragraph");
		builder.endBlock();
		builder.beginSpan(SpanType.ITALIC, new Attributes());
		builder.characters("Implicit");
		builder.endSpan();
		builder.characters(" paragraph");
		builder.endDocument();
		assertMarkup("Paragraph\n\n//Implicit// paragraph\n\n");
	}

	@Test
	public void testImplicitParagraphWithSpan() {
		builder.beginDocument();
		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.characters("text1");
		builder.endSpan();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("text2");
		builder.endBlock();
		builder.endDocument();
		assertMarkup("**text1**\n\ntext2\n\n");
	}

	@Test
	public void testSpanOpensImplicitParagraph() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("text");
		builder.endBlock();
		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.characters("bold");
		builder.endSpan();
		builder.characters(" text");
		builder.endDocument();
		assertMarkup("text\n\n**bold** text\n\n");
	}

	@Test
	public void testBlockCode() {
		builder.beginDocument();
		builder.beginBlock(BlockType.CODE, new Attributes());
		builder.characters("text\n\nmore text");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("{{{\ntext\n\nmore text\n}}}\n\n", markup);
	}

	@Test
	public void testCodeBlockWithLineBreaks() {
		builder.beginDocument();
		builder.beginBlock(BlockType.CODE, new Attributes());
		builder.characters("line 1");
		builder.lineBreak();
		builder.characters("line 2");
		builder.lineBreak();
		builder.lineBreak();
		builder.characters("line 3");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("{{{\nline 1\\\\line 2\\\\\\\\line 3\n}}}\n\n", markup);
	}

	@Test
	public void testCodeBlockCurlyBraceContent() {
		assertCodeBlock("{{{\n{something}\n}}}\n\n", "{something}");
	}

	@Test
	public void testCodeBlockSquareBraceContent() {
		assertCodeBlock("{{{\n[something]\n}}}\n\n", "[something]");
	}

	@Test
	public void testPreformattedBlockWithLineBreaks() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PREFORMATTED, new Attributes());
		builder.characters("line 1");
		builder.lineBreak();
		builder.characters("line 2");
		builder.lineBreak();
		builder.lineBreak();
		builder.characters("line 3");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("{{{\nline 1\\\\line 2\\\\\\\\line 3\n}}}\n\n", markup);
	}

	@Test
	public void testPreformattedBlockWithCurlyBraceContent() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PREFORMATTED, new Attributes());
		builder.characters("{somecontent}");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("{{{\n{somecontent}\n}}}\n\n", markup);
	}

	@Test
	public void testParagraphFollowingExtendedBlockCode() {
		builder.beginDocument();
		builder.beginBlock(BlockType.CODE, new Attributes());
		builder.characters("text\n\nmore text");
		builder.endBlock();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("text");
		builder.endBlock();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("text2");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("{{{\ntext\n\nmore text\n}}}\n\ntext\n\ntext2\n\n", markup);
	}

	@Test
	public void testEmptyBlock() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.endBlock();
		builder.endDocument();
		assertMarkup("");
	}

	@Test
	public void testUnsupportedBlock() {
		builder.beginDocument();
		builder.beginBlock(BlockType.FOOTNOTE, new Attributes());
		builder.characters("unsupported");
		builder.endBlock();
		builder.endDocument();
		assertMarkup("unsupported\n\n");
	}

	private void assertCodeBlock(String expected, String content) {
		builder.beginDocument();
		builder.beginBlock(BlockType.CODE, new Attributes());
		builder.characters(content);
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals(expected, markup);
	}

}