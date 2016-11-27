/*******************************************************************************
 * Copyright (c) 2011, 2016 Tasktop Technologies
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.confluence.core;

import java.io.StringWriter;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.core.parser.ImageAttributes;
import org.eclipse.mylyn.wikitext.core.parser.LinkAttributes;

import junit.framework.TestCase;

/**
 * @author David Green
 * @see ConfluenceDocumentBuilder
 */
public class ConfluenceDocumentBuilderTest extends TestCase {

	private DocumentBuilder builder;

	private StringWriter out;

	@Override
	protected void setUp() throws Exception {
		out = new StringWriter();
		builder = new ConfluenceDocumentBuilder(out);
		super.setUp();
	}

	public void testParagraph() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("text\n\nmore text");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("text  more text\n\n", markup);
	}

	public void testParagraphWithCssClass() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes(null, "test", null, null));
		builder.characters("text\n\nmore text");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("text  more text\n\n", markup);
	}

	public void testParagraphWithCssStyle() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes(null, null, "x-test: foo;", null));
		builder.characters("text\n\nmore text");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("text  more text\n\n", markup);
	}

	public void testParagraphWithId() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes("123", null, null, null));
		builder.characters("text\n\nmore text");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("text  more text\n\n", markup);
	}

	public void testParagraphWithIdAndClass() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes("123", "test", null, null));
		builder.characters("text\n\nmore text");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("text  more text\n\n", markup);
	}

	public void testParagraphWithLink() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("text\n\nmore text ");
		final LinkAttributes attributes = new LinkAttributes();
		attributes.setHref("http://example.com/foo+bar/baz.gif");
		builder.beginSpan(SpanType.LINK, attributes);
		builder.characters("baz");
		builder.endSpan();
		builder.characters(" test");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("text  more text [baz | http://example.com/foo+bar/baz.gif] test\n\n", markup);
	}

	public void testBlockCode() {
		builder.beginDocument();
		builder.beginBlock(BlockType.CODE, new Attributes());
		builder.characters("text\n\nmore text");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("{code}text\n\nmore text{code}\n\n\n", markup);
	}

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

		assertEquals("{code}line 1\nline 2\n\nline 3{code}\n\n\n", markup);
	}

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

		assertEquals("{noformat}line 1\nline 2\n\nline 3{noformat}\n\n", markup);
	}

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

		assertEquals("{code}text\n\nmore text{code}\n\n\ntext\n\ntext2\n\n", markup);
	}

	public void testHeading1() {
		builder.beginDocument();
		builder.beginHeading(1, new Attributes());
		builder.characters("text\n\nmore text");
		builder.endHeading();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("text");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("h1. text  more text\n\ntext\n\n", markup);
	}

	public void testHeadingWithLineBreaks() {
		builder.beginDocument();
		builder.beginHeading(1, new Attributes());
		builder.characters("line 1 of heading");
		builder.lineBreak();
		builder.characters("line 2 of heading");
		builder.endHeading();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("text");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("h1. line 1 of heading\\\\line 2 of heading\n\ntext\n\n", markup);
	}

	public void testHeading1_WithNestedMarkup() {
		builder.beginDocument();
		builder.beginHeading(1, new Attributes());
		builder.characters("text ");
		builder.beginSpan(SpanType.EMPHASIS, new Attributes());
		builder.characters("emphasized");
		builder.endSpan();
		builder.endHeading();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("text");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("h1. text _emphasized_\n\ntext\n\n", markup);
	}

	public void testImplicitParagrah() {
		builder.beginDocument();
		builder.characters("text1");
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("text2");
		builder.endBlock();
		builder.characters("text3");
		builder.endDocument();

		String markup = out.toString();

		assertEquals("text1\n\ntext2\n\ntext3\n\n", markup);
	}

	public void testBoldSpanNoWhitespace_spanAtLineStart() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());

		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.characters("text2");
		builder.endSpan();
		builder.characters("text3");

		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("*text2* text3\n\n", markup);
	}

	public void testBoldSpanNoWhitespace_spanAtLineEnd() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());

		builder.characters("text3");
		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.characters("text2");
		builder.endSpan();

		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("text3 *text2*\n\n", markup);
	}

	public void testBoldSpanNoWhitespace_spanMidLine() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());

		builder.characters("text3");
		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.characters("text2");
		builder.endSpan();
		builder.characters("text4");

		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("text3 *text2* text4\n\n", markup);
	}

	public void testBoldSpanNoWhitespace_adjacentSpans() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());

		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.characters("text2");
		builder.endSpan();
		builder.beginSpan(SpanType.ITALIC, new Attributes());
		builder.characters("text3");
		builder.endSpan();

		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("*text2*_text3_\n\n", markup);
	}

	public void testSpanWithAdjacentWhitespace() {
		builder.beginDocument();

		builder.characters("prefix ");

		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.characters("bolded");
		builder.endBlock();

		builder.characters(" suffix");

		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("prefix *bolded* suffix\n\n", markup);
	}

	public void testBoldSpanWithAdjacentPunctuation() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());

		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.characters("text2");
		builder.endSpan();
		builder.characters("!");

		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("*text2*!\n\n", markup);
	}

	public void testEmptySpan() {
		builder.beginDocument();

		builder.characters("prefix ");

		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.endBlock();

		builder.characters(" suffix");

		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("prefix  suffix\n\n", markup);
	}

	public void testMonospaceSpan() {
		builder.beginDocument();

		builder.characters("prefix ");

		builder.beginSpan(SpanType.MONOSPACE, new Attributes());
		builder.characters("test");
		builder.endSpan();

		builder.characters(" suffix");

		builder.endDocument();

		String markup = out.toString();

		assertEquals("prefix {{test}} suffix\n\n", markup);
	}

	public void testLinkSpan() {
		builder.beginDocument();

		builder.characters("prefix ");

		LinkAttributes attributes = new LinkAttributes();
		attributes.setHref("http://example.com/target");
		builder.beginSpan(SpanType.LINK, attributes);
		builder.characters("test");
		builder.endSpan();

		builder.characters(" suffix");

		builder.endDocument();

		String markup = out.toString();

		assertEquals("prefix [test | http://example.com/target] suffix\n\n", markup);
	}

	public void testLinkSpanWithAdjacentSpans() {
		builder.beginDocument();

		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.characters("bold");
		builder.endSpan();

		LinkAttributes attributes = new LinkAttributes();
		attributes.setHref("http://example.com/target");
		builder.beginSpan(SpanType.LINK, attributes);
		builder.characters("test");
		builder.endSpan();

		builder.beginSpan(SpanType.ITALIC, new Attributes());
		builder.characters("italic");
		builder.endSpan();

		builder.endDocument();

		String markup = out.toString();

		assertEquals("*bold*[test | http://example.com/target]_italic_\n\n", markup);
	}

	public void testLinkSpanWithTitle() {
		builder.beginDocument();

		builder.characters("prefix ");

		LinkAttributes attributes = new LinkAttributes();
		attributes.setHref("http://example.com/target");
		attributes.setTitle("Title Words");
		builder.beginSpan(SpanType.LINK, attributes);
		builder.characters("test");
		builder.endSpan();

		builder.characters(" suffix");

		builder.endDocument();

		String markup = out.toString();

		assertEquals("prefix [test | http://example.com/target | Title Words] suffix\n\n", markup);
	}

	public void testLinkSpanNoText() {
		builder.beginDocument();

		builder.characters("prefix ");

		LinkAttributes attributes = new LinkAttributes();
		attributes.setHref("http://example.com/target");
		builder.beginSpan(SpanType.LINK, attributes);
		builder.endSpan();

		builder.characters(" suffix");

		builder.endDocument();

		String markup = out.toString();

		assertEquals("prefix [http://example.com/target] suffix\n\n", markup);
	}

	public void testLinkAlternate() {
		builder.beginDocument();

		builder.characters("prefix ");

		builder.link("#test", "Test 123");

		builder.characters(" suffix");

		builder.endDocument();

		String markup = out.toString();

		assertEquals("prefix [Test 123 | #test] suffix\n\n", markup);
	}

	public void testLinkSpanNoHref() {
		builder.beginDocument();

		builder.characters("prefix ");

		LinkAttributes attributes = new LinkAttributes();
		builder.beginSpan(SpanType.LINK, attributes);
		builder.endSpan();

		builder.characters(" suffix");

		builder.endDocument();

		String markup = out.toString();

		assertEquals("prefix [] suffix\n\n", markup);
	}

	public void testTableWithEmptyCells() {
		assertTableRow("| |content| |\n\n", BlockType.TABLE_CELL_NORMAL);
	}

	public void testTableWithEmptyHeaderCells() {
		assertTableRow("|| ||content|| |\n\n", BlockType.TABLE_CELL_HEADER);
	}

	public void testDivAfterImplicitParagraph() {
		builder.beginDocument();

		builder.characters("test");

		builder.beginBlock(BlockType.DIV, new Attributes());
		builder.characters("more ");
		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.characters("text");
		builder.endSpan();
		builder.endBlock();

		builder.beginBlock(BlockType.NUMERIC_LIST, new Attributes());

		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.characters("text2");
		builder.endSpan();

		builder.endBlock();

		builder.endDocument();

		String markup = out.toString();

		assertEquals("test\n\nmore *text*\n\n# text2\n", markup);
	}

	public void testDivWithinTableCell() {
		builder.beginDocument();

		builder.beginBlock(BlockType.TABLE, new Attributes());

		builder.beginBlock(BlockType.TABLE_ROW, new Attributes());

		builder.beginBlock(BlockType.TABLE_CELL_NORMAL, new Attributes());
		builder.characters("first");
		builder.endBlock();

		builder.beginBlock(BlockType.TABLE_CELL_NORMAL, new Attributes());

		builder.beginBlock(BlockType.DIV, new Attributes());
		builder.characters("content");
		builder.endBlock(); // div
		builder.beginBlock(BlockType.DIV, new Attributes());
		builder.characters("content2");
		builder.endBlock(); // div

		builder.endBlock(); // cell

		builder.endBlock(); // row

		builder.endBlock(); // table

		builder.endDocument();

		String markup = out.toString();

		assertEquals("|first|content content2|\n\n", markup);
	}

	public void testItalics() {
		builder.beginDocument();

		builder.characters("prefix ");

		builder.beginSpan(SpanType.ITALIC, new Attributes());
		builder.characters("italic phrase");
		builder.endBlock();

		builder.characters(" suffix");

		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("prefix _italic phrase_ suffix\n\n", markup);
	}

	public void testEmptyBoldSpan() {
		builder.beginDocument();

		builder.characters("first ");

		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.endSpan();

		builder.characters("second");

		builder.endDocument();

		String markup = out.toString();

		assertEquals("first second\n\n", markup);
	}

	public void testParagraphWithSingleNewline() {
		builder.beginDocument();

		builder.characters("first");
		builder.lineBreak();
		builder.characters("second");

		builder.endDocument();

		String markup = out.toString();

		assertEquals("first\nsecond\n\n", markup);
	}

	public void testParagraphWithMultipleNewlines() {
		builder.beginDocument();

		builder.characters("first");
		builder.lineBreak();
		builder.lineBreak();
		builder.lineBreak();
		builder.characters("second");

		builder.endDocument();

		String markup = out.toString();

		assertEquals("first\n\\\\ \\\\second\n\n", markup);
	}

	public void testListItemWithSingleNewline() {
		builder.beginDocument();

		builder.beginBlock(BlockType.BULLETED_LIST, new Attributes());

		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.characters("first");
		builder.lineBreak();
		builder.characters("second");
		builder.endBlock(); // list item

		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.characters("another");
		builder.endBlock(); // list item

		builder.endBlock(); // list

		builder.endDocument();

		String markup = out.toString();

		assertEquals("* first\\\\second\n* another\n", markup);
	}

	public void testListItemWithMultipleNewlines() {
		builder.beginDocument();

		builder.beginBlock(BlockType.BULLETED_LIST, new Attributes());

		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.characters("first");
		builder.lineBreak();
		builder.lineBreak();
		builder.characters("second");
		builder.endBlock(); // list item

		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.characters("another");
		builder.endBlock(); // list item

		builder.endBlock(); // list

		builder.endDocument();

		String markup = out.toString();

		assertEquals("* first\\\\ \\\\second\n* another\n", markup);
	}

	public void testListWithMultipleItems() {
		builder.beginDocument();

		builder.beginBlock(BlockType.BULLETED_LIST, new Attributes());

		emitListItem("first");
		emitListItem("second");

		builder.endBlock(); // list

		builder.endDocument();

		String markup = out.toString();

		assertEquals("* first\n* second\n", markup);
	}

	private void emitListItem(String text) {
		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.characters(text);
		builder.endBlock(); // list item
	}

	public void testListWithNestedSublist() {
		builder.beginDocument();

		builder.beginBlock(BlockType.BULLETED_LIST, new Attributes());

		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.characters("first");

		builder.beginBlock(BlockType.BULLETED_LIST, new Attributes());
		emitListItem("first.1");
		emitListItem("first.2");
		builder.endBlock(); // list
		builder.endBlock(); // list item

		emitListItem("second");

		builder.endBlock(); // list

		builder.endDocument();

		String markup = out.toString();

		assertEquals("* first\n** first.1\n** first.2\n* second\n", markup);
	}

	public void testListWithNestedSublist2() {
		builder.beginDocument();

		builder.beginBlock(BlockType.BULLETED_LIST, new Attributes());

		emitListItem("first");

		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.characters("second");

		builder.beginBlock(BlockType.BULLETED_LIST, new Attributes());
		emitListItem("second.1");
		builder.endBlock(); // list
		builder.endBlock(); // list item
		builder.endBlock(); // list

		builder.beginBlock(BlockType.NUMERIC_LIST, new Attributes());
		emitListItem("third");

		builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
		builder.characters("fourth");
		builder.beginBlock(BlockType.NUMERIC_LIST, new Attributes());
		emitListItem("fourth.1");
		builder.endBlock(); // list
		builder.endBlock(); // list item

		builder.endBlock(); // list

		builder.endDocument();

		String markup = out.toString();

		assertEquals("* first\n* second\n** second.1\n\n# third\n# fourth\n## fourth.1\n", markup);
	}

	public void testPreformattedWithMultipleNewlines() {
		builder.beginDocument();

		builder.beginBlock(BlockType.PREFORMATTED, new Attributes());
		builder.characters("first");
		builder.lineBreak();
		builder.lineBreak();
		builder.characters("second");
		builder.endBlock(); // pre

		builder.endDocument();

		String markup = out.toString();

		assertEquals("{noformat}first\n\nsecond{noformat}\n\n", markup);
	}

	public void testImplicitParagrahWithSpan() {
		builder.beginDocument();
		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.characters("text1");
		builder.endSpan();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("text2");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("*text1*\n\ntext2\n\n", markup);
	}

	public void testSpanOpensImplicitParagraph() {
		builder.beginDocument();
		builder.beginBlock(BlockType.CODE, new Attributes());
		builder.characters("// some code");
		builder.endBlock();
		builder.beginSpan(SpanType.DELETED, new Attributes());
		builder.characters("redacted");
		builder.endSpan();
		builder.characters(" text");
		builder.endDocument();

		String markup = out.toString();

		assertEquals("{code}// some code{code}\n\n-redacted- text\n\n", markup);
	}

	public void testBlockQuote() {
		builder.beginDocument();
		builder.beginBlock(BlockType.QUOTE, new Attributes());
		builder.characters("block text");
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("{quote}block text{quote}\n\n", markup);
	}

	public void testBlockQuoteContainingMarkup() {
		builder.beginDocument();
		builder.beginBlock(BlockType.QUOTE, new Attributes());
		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.characters("bold");
		builder.endSpan();
		builder.characters("text");
		builder.beginSpan(SpanType.ITALIC, new Attributes());
		builder.characters("italic");
		builder.endSpan();
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("{quote}*bold* text _italic_{quote}\n\n", markup);
	}

	public void testCitationAfterBlockQuote() {
		builder.beginDocument();
		builder.beginBlock(BlockType.QUOTE, new Attributes());
		builder.beginSpan(SpanType.CITATION, new Attributes());
		builder.characters("citation");
		builder.endSpan();
		builder.endBlock();
		builder.beginSpan(SpanType.CITATION, new Attributes());
		builder.characters("citation");
		builder.endSpan();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("{quote}??citation??{quote}\n\n??citation??\n\n", markup);
	}

	public void testImageLink() {
		builder.beginDocument();
		builder.characters("a ");
		builder.imageLink(new LinkAttributes(), new ImageAttributes(), "#foo", "fooImage.png");
		builder.characters(" test");
		builder.endDocument();

		String markup = out.toString();

		assertEquals("a !fooImage.png!:#foo test\n\n", markup);
	}

	public void testImage() {
		builder.beginDocument();
		builder.characters("a ");
		builder.image(new ImageAttributes(), "fooImage.png");
		builder.characters(" test");
		builder.endDocument();

		String markup = out.toString();

		assertEquals("a !fooImage.png! test\n\n", markup);
	}

	public void testImageNoUrl() {
		builder.beginDocument();
		builder.characters("a ");
		builder.image(new ImageAttributes(), null);
		builder.characters(" test");
		builder.endDocument();

		String markup = out.toString();

		assertEquals("a  test\n\n", markup);
	}

	public void testSpanSuperscript() {
		assertSpan("begin ^span text^ end\n\n", SpanType.SUPERSCRIPT);
	}

	public void testSpanSubscript() {
		assertSpan("begin ~span text~ end\n\n", SpanType.SUBSCRIPT);
	}

	public void testSpanBold() {
		assertSpan("begin *span text* end\n\n", SpanType.BOLD);
	}

	public void testSpanCitation() {
		assertSpan("begin ??span text?? end\n\n", SpanType.CITATION);
	}

	public void testSpanCode() {
		assertSpan("begin @span text@ end\n\n", SpanType.CODE);
	}

	public void testSpanDeleted() {
		assertSpan("begin -span text- end\n\n", SpanType.DELETED);
	}

	public void testSpanEmphasis() {
		assertSpan("begin _span text_ end\n\n", SpanType.EMPHASIS);
	}

	public void testSpanInserted() {
		assertSpan("begin +span text+ end\n\n", SpanType.INSERTED);
	}

	public void testSpanItalic() {
		assertSpan("begin _span text_ end\n\n", SpanType.ITALIC);
	}

	public void testSpanMonospace() {
		assertSpan("begin {{span text}} end\n\n", SpanType.MONOSPACE);
	}

	public void testSpanQuote() {
		assertSpan("beginspan textend\n\n", SpanType.QUOTE);
	}

	public void testSpanSpan() {
		assertSpan("beginspan textend\n\n", SpanType.SPAN);
	}

	public void testSpanStrong() {
		assertSpan("begin *span text* end\n\n", SpanType.STRONG);
	}

	public void testSpanUnderlined() {
		assertSpan("begin +span text+ end\n\n", SpanType.UNDERLINED);
	}

	public void testNestedBoldItalic() {
		assertNestedSpan("begin *_span_* end\n\n", SpanType.BOLD, SpanType.ITALIC);
	}

	public void testNestedItalicBold() {
		assertNestedSpan("begin _*span*_ end\n\n", SpanType.ITALIC, SpanType.BOLD);
	}

	public void testNestedDeletedSuperscript() {
		assertNestedSpan("begin -^span^- end\n\n", SpanType.DELETED, SpanType.SUPERSCRIPT);
	}

	public void testNestedSuperscriptDeleted() {
		assertNestedSpan("begin ^-span-^ end\n\n", SpanType.SUPERSCRIPT, SpanType.DELETED);
	}

	public void testNestedUnderlineSubscript() {
		assertNestedSpan("begin +~span~+ end\n\n", SpanType.UNDERLINED, SpanType.SUBSCRIPT);
	}

	public void testNestedSubscriptUnderline() {
		assertNestedSpan("begin ~+span+~ end\n\n", SpanType.SUBSCRIPT, SpanType.UNDERLINED);
	}

	public void testNestedMonospaceCitation() {
		assertNestedSpan("begin {{??span??}} end\n\n", SpanType.MONOSPACE, SpanType.CITATION);
	}

	public void testNestedCitationMonospace() {
		assertNestedSpan("begin ??{{span}}?? end\n\n", SpanType.CITATION, SpanType.MONOSPACE);
	}

	public void testNestedLinkCode() {
		assertNestedSpan("begin %@span@% end\n\n", SpanType.LINK, SpanType.CODE);
	}

	public void testNestedCodeLink() {
		assertNestedSpan("begin @%span%@ end\n\n", SpanType.CODE, SpanType.LINK);
	}

	public void testAdjacentBoldItalic() {
		assertAdjacentSpan("begin *left*_right_ end\n\n", SpanType.BOLD, SpanType.ITALIC);
	}

	public void testAdjacentItalicBold() {
		assertAdjacentSpan("begin _left_*right* end\n\n", SpanType.ITALIC, SpanType.BOLD);
	}

	public void testAdjacentDeletedSuperscript() {
		assertAdjacentSpan("begin -left-^right^ end\n\n", SpanType.DELETED, SpanType.SUPERSCRIPT);
	}

	public void testAdjacentSuperscriptDeleted() {
		assertAdjacentSpan("begin ^left^-right- end\n\n", SpanType.SUPERSCRIPT, SpanType.DELETED);
	}

	public void testAdjacentUnderlineSubscript() {
		assertAdjacentSpan("begin +left+~right~ end\n\n", SpanType.UNDERLINED, SpanType.SUBSCRIPT);
	}

	public void testAdjacentSubscriptUnderline() {
		assertAdjacentSpan("begin ~left~+right+ end\n\n", SpanType.SUBSCRIPT, SpanType.UNDERLINED);
	}

	public void testAdjacentMonospaceCitation() {
		assertAdjacentSpan("begin {{left}}??right?? end\n\n", SpanType.MONOSPACE, SpanType.CITATION);
	}

	public void testAdjacentCitationMonospace() {
		assertAdjacentSpan("begin ??left??{{right}} end\n\n", SpanType.CITATION, SpanType.MONOSPACE);
	}

	public void testAdjacentLinkCode() {
		assertAdjacentSpan("begin %left%@right@ end\n\n", SpanType.LINK, SpanType.CODE);
	}

	public void testAdjacentCodeLink() {
		assertAdjacentSpan("begin @left@%right% end\n\n", SpanType.CODE, SpanType.LINK);
	}

	public void testItalicBoldItalic() {
		builder.beginDocument();
		builder.beginSpan(SpanType.ITALIC, new Attributes());
		builder.characters("italic");
		builder.endSpan();
		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.beginSpan(SpanType.ITALIC, new Attributes());
		builder.characters("bolditalic");
		builder.endSpan();
		builder.endSpan();
		builder.endDocument();

		String markup = out.toString();

		assertEquals("_italic_*_bolditalic_*\n\n", markup);
	}

	private void assertSpan(String expected, SpanType spanType) {
		builder.beginDocument();
		builder.characters("begin");
		builder.beginSpan(spanType, new Attributes());
		builder.characters("span text");
		builder.endSpan();
		builder.characters("end");
		builder.endDocument();

		String markup = out.toString();

		assertEquals(expected, markup);
	}

	private void assertNestedSpan(String expected, SpanType outerSpan, SpanType innerSpan) {
		builder.beginDocument();
		builder.characters("begin");
		builder.beginSpan(outerSpan, new Attributes());
		builder.beginSpan(innerSpan, new Attributes());
		builder.characters("span");
		builder.endSpan();
		builder.endSpan();
		builder.characters("end");
		builder.endDocument();

		String markup = out.toString();

		assertEquals(expected, markup);
	}

	private void assertAdjacentSpan(String expected, SpanType leftSpan, SpanType rightSpan) {
		builder.beginDocument();
		builder.characters("begin");
		builder.beginSpan(leftSpan, new Attributes());
		builder.characters("left");
		builder.endSpan();
		builder.beginSpan(rightSpan, new Attributes());
		builder.characters("right");
		builder.endSpan();
		builder.characters("end");
		builder.endDocument();

		String markup = out.toString();

		assertEquals(expected, markup);
	}

	private void assertTableRow(String expectedMarkup, BlockType cellType) {
		builder.beginDocument();
		builder.beginBlock(BlockType.TABLE, new Attributes());

		builder.beginBlock(BlockType.TABLE_ROW, new Attributes());

		builder.beginBlock(cellType, new Attributes());
		builder.endBlock();

		builder.beginBlock(cellType, new Attributes());
		builder.characters("content");
		builder.endBlock();

		builder.beginBlock(cellType, new Attributes());
		builder.endBlock();

		builder.endBlock();
		builder.endBlock();
		builder.endDocument();

		String markup = out.toString();

		assertEquals(expectedMarkup, markup);
	}
}
