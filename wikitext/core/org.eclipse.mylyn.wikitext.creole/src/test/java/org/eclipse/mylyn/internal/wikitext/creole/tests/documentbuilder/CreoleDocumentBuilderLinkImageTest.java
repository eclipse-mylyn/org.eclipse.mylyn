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

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.parser.ImageAttributes;
import org.eclipse.mylyn.wikitext.parser.LinkAttributes;
import org.junit.Test;

/**
 * @see http://www.wikicreole.org/wiki/Elements
 * @author Kevin de Vlaming
 */
public class CreoleDocumentBuilderLinkImageTest extends AbstractCreoleDocumentBuilderTest {
	@Test
	public void testLink() {
		builder.beginDocument();
		builder.characters("This ");
		builder.link("http://example.com/", "link");
		builder.characters(" has no title attribute.");
		builder.endDocument();
		assertMarkup("This [[http://example.com/|link]] has no title attribute.\n\n");
	}

	@Test
	public void testLinkWithNoUrl() {
		builder.beginDocument();
		builder.characters("This ");
		builder.link(null, "link");
		builder.characters(" has no url.");
		builder.endDocument();
		assertMarkup("This [[link]] has no url.\n\n");
	}

	@Test
	public void testLinkWithNoLinkText() {
		builder.beginDocument();
		builder.characters("This ");
		builder.link("http://example.com/", null);
		builder.characters(" has no link text.");
		builder.endDocument();
		assertMarkup("This [[http://example.com/]] has no link text.\n\n");
	}

	@Test
	public void testLinkSpanWithNoUrl() {
		builder.beginDocument();
		builder.characters("This ");

		LinkAttributes attributes = new LinkAttributes();
		builder.beginSpan(SpanType.LINK, attributes);
		builder.endSpan();

		builder.characters(" has no url or link text.");
		builder.endDocument();
		assertMarkup("This [[]] has no url or link text.\n\n");
	}

	@Test
	public void testLinkWithTitle() {
		builder.beginDocument();
		builder.characters("This is ");
		LinkAttributes attr = new LinkAttributes();
		attr.setTitle("Title");
		builder.link(attr, "http://example.com/", "inline link");
		builder.characters(" has a title attribute that is ignored.");
		builder.endDocument();
		assertMarkup("This is [[http://example.com/|inline link]] has a title attribute that is ignored.\n\n");
	}

	@Test
	public void testLinkWithEmptyAttributes() {
		builder.beginDocument();
		builder.characters("This is ");
		builder.link(new Attributes(), "http://example.com/", "an example");
		builder.characters(" inline link.");
		builder.endDocument();
		assertMarkup("This is [[http://example.com/|an example]] inline link.\n\n");
	}

	@Test
	public void testLinkImplicitParagraph() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("A paragraph.");
		builder.endBlock();
		builder.link("http://example.com/", "A link");
		builder.characters(" opens an implicit paragraph.");
		builder.endDocument();
		assertMarkup("A paragraph.\n\n[[http://example.com/|A link]] opens an implicit paragraph.\n\n");
	}

	@Test
	public void testLinkSpanEmptyAttributes() {
		builder.beginDocument();
		builder.beginSpan(SpanType.LINK, new Attributes());
		builder.characters("http://example.com");
		builder.endSpan();
		builder.endDocument();
		assertMarkup("[[http://example.com]]\n\n");
	}

	@Test
	public void testImage() {
		builder.beginDocument();
		builder.image(new ImageAttributes(), "/path/to/img.jpg");
		builder.endDocument();
		assertMarkup("{{/path/to/img.jpg}}\n\n");
	}

	@Test
	public void testImageWithNoUrl() {
		builder.beginDocument();
		builder.image(new ImageAttributes(), null);
		builder.endDocument();
		assertMarkup("");
	}

	@Test
	public void testImageWithTitle() {
		builder.beginDocument();
		ImageAttributes attr = new ImageAttributes();
		attr.setAlt("Alt text");
		attr.setTitle("Optional title");
		builder.image(attr, "/path/to/img.jpg");
		builder.endDocument();
		assertMarkup("{{/path/to/img.jpg|Alt text}}\n\n");
	}

	@Test
	public void testImageWithTitleNoAltText() {
		builder.beginDocument();
		ImageAttributes attr = new ImageAttributes();
		attr.setTitle("Optional title");
		builder.image(attr, "/path/to/img.jpg");
		builder.endDocument();
		assertMarkup("{{/path/to/img.jpg|Optional title}}\n\n");
	}

	@Test
	public void testImageWithEmptyAttributes() {
		builder.beginDocument();
		builder.image(new Attributes(), "/path/to/img.jpg");
		builder.endDocument();
		assertMarkup("{{/path/to/img.jpg}}\n\n");
	}

	@Test
	public void testImageImplicitParagraph() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("Below is an image:");
		builder.endBlock();
		builder.image(new ImageAttributes(), "/path/to/img.jpg");
		builder.endDocument();
		assertMarkup("Below is an image:\n\n{{/path/to/img.jpg}}\n\n");
	}

	@Test
	public void testImageLink() {
		builder.beginDocument();
		builder.imageLink("http://example.net/", "/path/to/img.jpg");
		builder.endDocument();
		assertMarkup("[[http://example.net/|{{/path/to/img.jpg}}]]\n\n");
	}

	@Test
	public void testImageLinkWithSingleEmptyAttributes() {
		builder.beginDocument();
		builder.imageLink(new Attributes(), "http://example.net/", "/path/to/img.jpg");
		builder.endDocument();
		assertMarkup("[[http://example.net/|{{/path/to/img.jpg}}]]\n\n");
	}

	@Test
	public void testImageLinkWithBothEmptyAttributes() {
		builder.beginDocument();
		builder.imageLink(new Attributes(), new Attributes(), "http://example.net/", "/path/to/img.jpg");
		builder.endDocument();
		assertMarkup("[[http://example.net/|{{/path/to/img.jpg}}]]\n\n");
	}

	@Test
	public void testImageLinkWithImageAttributes() {
		builder.beginDocument();
		ImageAttributes attr = new ImageAttributes();
		attr.setAlt("Alt text");
		attr.setTitle("Optional title");
		builder.imageLink(attr, "http://example.net/", "/path/to/img.jpg");
		builder.endDocument();
		assertMarkup("[[http://example.net/|{{/path/to/img.jpg}}Alt text]]\n\n");
	}

	@Test
	public void testImageLinkWithLinkAttributes() {
		builder.beginDocument();
		LinkAttributes linkAttr = new LinkAttributes();
		linkAttr.setTitle("Optional link title");
		ImageAttributes imageAttr = new ImageAttributes();
		imageAttr.setAlt("Alt text");
		imageAttr.setTitle("Optional image title");
		builder.imageLink(linkAttr, imageAttr, "http://example.net/", "/path/to/img.jpg");
		builder.endDocument();
		assertMarkup("[[http://example.net/|{{/path/to/img.jpg}}Alt text]]\n\n");
	}
}
