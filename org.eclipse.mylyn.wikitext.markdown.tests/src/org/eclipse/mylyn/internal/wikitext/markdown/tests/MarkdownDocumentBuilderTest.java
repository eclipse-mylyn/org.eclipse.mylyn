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
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.core.parser.ImageAttributes;
import org.eclipse.mylyn.wikitext.core.parser.LinkAttributes;
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
		assertMarkup("some **strong** and *emphasis*\n\n");
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

	public void testImplicitParagraphWithStrongEmphasis() {
		builder.beginDocument();
		builder.characters("some ");
		builder.beginSpan(SpanType.STRONG, new Attributes());
		builder.characters("strong");
		builder.endSpan();
		builder.characters(" and ");
		builder.beginSpan(SpanType.EMPHASIS, new Attributes());
		builder.characters("emphasis");
		builder.endSpan();
		builder.endDocument();
		assertMarkup("some **strong** and *emphasis*\n\n");
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

	// span elements - http://daringfireball.net/projects/markdown/syntax#span

	public void testUnsupportedSpan() {
		builder.beginDocument();
		builder.beginSpan(SpanType.UNDERLINED, new Attributes());
		builder.characters("unsupported");
		builder.endSpan();
		builder.endDocument();
		assertMarkup("unsupported");
	}

	public void testEmptySpan() {
		builder.beginDocument();
		builder.characters("prefix");
		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.endSpan();
		builder.characters(" suffix");
		builder.endDocument();
		assertMarkup("prefix suffix\n\n");
	}

	public void testLink() {
		builder.beginDocument();
		builder.characters("This ");
		builder.link("http://example.net/", "link");
		builder.characters(" has no title attribute.");
		builder.endDocument();
		assertMarkup("This [link](http://example.net/) has no title attribute.\n\n");
	}

	public void testLinkWithTitle() {
		builder.beginDocument();
		builder.characters("This is ");
		LinkAttributes attr = new LinkAttributes();
		attr.setTitle("Title");
		builder.link(attr, "http://example.com/", "an example");
		builder.characters(" inline link.");
		builder.endDocument();
		assertMarkup("This is [an example](http://example.com/ \"Title\") inline link.\n\n");
	}

	public void testLinkWithEmptyAttributes() {
		builder.beginDocument();
		builder.characters("This is ");
		builder.link(new Attributes(), "http://example.com/", "an example");
		builder.characters(" inline link.");
		builder.endDocument();
		assertMarkup("This is [an example](http://example.com/) inline link.\n\n");
	}

	public void testLinkImplicitParagraph() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("A paragraph.");
		builder.endBlock();
		builder.link("http://example.com/", "A link");
		builder.characters(" opens an implicit paragraph.");
		builder.endDocument();
		assertMarkup("A paragraph.\n\n[A link](http://example.com/) opens an implicit paragraph.\n\n");
	}

	public void testEmphasis() {
		builder.beginDocument();
		builder.beginSpan(SpanType.EMPHASIS, new Attributes());
		builder.characters("emphasis");
		builder.endSpan();
		builder.endDocument();
		assertMarkup("*emphasis*");
	}

	public void testItalic() {
		builder.beginDocument();
		builder.beginSpan(SpanType.ITALIC, new Attributes());
		builder.characters("italic");
		builder.endSpan();
		builder.endDocument();
		assertMarkup("*italic*");
	}

	public void testStrong() {
		builder.beginDocument();
		builder.beginSpan(SpanType.STRONG, new Attributes());
		builder.characters("strong");
		builder.endSpan();
		builder.endDocument();
		assertMarkup("**strong**");
	}

	public void testBold() {
		builder.beginDocument();
		builder.beginSpan(SpanType.BOLD, new Attributes());
		builder.characters("bold");
		builder.endSpan();
		builder.endDocument();
		assertMarkup("**bold**");
	}

	public void testImage() {
		builder.beginDocument();
		builder.image(new ImageAttributes(), "/path/to/img.jpg");
		builder.endDocument();
		assertMarkup("![](/path/to/img.jpg)\n\n");
	}

	public void testImageWithTitle() {
		builder.beginDocument();
		ImageAttributes attr = new ImageAttributes();
		attr.setAlt("Alt text");
		attr.setTitle("Optional title");
		builder.image(attr, "/path/to/img.jpg");
		builder.endDocument();
		assertMarkup("![Alt text](/path/to/img.jpg \"Optional title\")\n\n");
	}

	public void testImageWithEmptyAttributes() {
		builder.beginDocument();
		builder.image(new Attributes(), "/path/to/img.jpg");
		builder.endDocument();
		assertMarkup("![](/path/to/img.jpg)\n\n");
	}

	public void testImageImplicitParagraph() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("Below is an image:");
		builder.endBlock();
		builder.image(new ImageAttributes(), "/path/to/img.jpg");
		builder.endDocument();
		assertMarkup("Below is an image:\n\n![](/path/to/img.jpg)\n\n");
	}

	public void testImageLink() {
		builder.beginDocument();
		builder.imageLink("http://example.net/", "/path/to/img.jpg");
		builder.endDocument();
		assertMarkup("[![](/path/to/img.jpg)](http://example.net/)");
	}

	public void testImageLinkWithSingleEmptyAttributes() {
		builder.beginDocument();
		builder.imageLink(new Attributes(), "http://example.net/", "/path/to/img.jpg");
		builder.endDocument();
		assertMarkup("[![](/path/to/img.jpg)](http://example.net/)");
	}

	public void testImageLinkWithBothEmptyAttributes() {
		builder.beginDocument();
		builder.imageLink(new Attributes(), new Attributes(), "http://example.net/", "/path/to/img.jpg");
		builder.endDocument();
		assertMarkup("[![](/path/to/img.jpg)](http://example.net/)");
	}

	public void testImageLinkWithImageAttributes() {
		builder.beginDocument();
		ImageAttributes attr = new ImageAttributes();
		attr.setAlt("Alt text");
		attr.setTitle("Optional title");
		builder.imageLink(attr, "http://example.net/", "/path/to/img.jpg");
		builder.endDocument();
		assertMarkup("[![Alt text](/path/to/img.jpg \"Optional title\")](http://example.net/)");
	}

	public void testImageLinkWithLinkAttributes() {
		builder.beginDocument();
		LinkAttributes linkAttr = new LinkAttributes();
		linkAttr.setTitle("Optional link title");
		ImageAttributes imageAttr = new ImageAttributes();
		imageAttr.setAlt("Alt text");
		imageAttr.setTitle("Optional image title");
		builder.imageLink(linkAttr, imageAttr, "http://example.net/", "/path/to/img.jpg");
		builder.endDocument();
		assertMarkup("[![Alt text](/path/to/img.jpg \"Optional image title\")](http://example.net/ \"Optional link title\")");
	}

	private void assertMarkup(String expected) {
		String markup = out.toString();
		TestUtil.println(markup);
		assertEquals(expected, markup);
	}

}
