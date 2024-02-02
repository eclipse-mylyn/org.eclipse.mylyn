/*******************************************************************************
 * Copyright (c) 2016, 2021 Jeremie Bresson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Jeremie Bresson - copied from MarkdownDocumentBuilder and adapted to AsciiDoc
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.asciidoc.internal;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.eclipse.mylyn.wikitext.asciidoc.AsciiDocLanguage;
import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.ImageAttributes;
import org.eclipse.mylyn.wikitext.parser.LinkAttributes;
import org.eclipse.mylyn.wikitext.parser.builder.AbstractMarkupDocumentBuilder;

import com.google.common.base.Strings;

/**
 * a document builder that emits AsciiDoc markup
 *
 * @see AsciiDocLanguage
 * @see AsciiDocLanguage#createDocumentBuilder(Writer)
 */
public class AsciiDocDocumentBuilder extends AbstractMarkupDocumentBuilder {

	private final Map<String, String> entityToLiteral = new HashMap<>();

	{
		entityToLiteral.put("amp", "&"); //$NON-NLS-1$ //$NON-NLS-2$
		entityToLiteral.put("lt", "<"); //$NON-NLS-1$ //$NON-NLS-2$
		entityToLiteral.put("gt", ">"); //$NON-NLS-1$ //$NON-NLS-2$
		entityToLiteral.put("copy", "(C)"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private interface AsciiDocBlock {

		void lineBreak() throws IOException;

	}

	private class ContentBlock extends NewlineDelimitedBlock implements AsciiDocBlock {

		protected String prefix;

		protected String suffix;

		ContentBlock(BlockType blockType, String prefix, String suffix, int leadingNewlines, int trailingNewlines) {
			super(blockType, leadingNewlines, trailingNewlines);
			this.prefix = prefix;
			this.suffix = suffix;
		}

		ContentBlock(String prefix, String suffix, int leadingNewlines, int trailingNewlines) {
			this(null, prefix, suffix, leadingNewlines, trailingNewlines);
		}

		@Override
		public void write(int c) throws IOException {
			AsciiDocDocumentBuilder.this.emitContent(c);
		}

		@Override
		public void write(String s) throws IOException {
			AsciiDocDocumentBuilder.this.emitContent(s);
		}

		@Override
		public void lineBreak() throws IOException {
			write("\n"); //$NON-NLS-1$
		}

		@Override
		public void open() throws IOException {
			super.open();
			pushWriter(new StringWriter());
		}

		@Override
		public void close() throws IOException {
			Writer thisContent = popWriter();
			String content = thisContent.toString();
			if (content.length() > 0) {
				emitContent(content);
			}
			super.close();
		}

		protected void emitContent(final String content) throws IOException {
			AsciiDocDocumentBuilder.this.emitContent(prefix);
			AsciiDocDocumentBuilder.this.emitContent(content);
			AsciiDocDocumentBuilder.this.emitContent(suffix);
		}

	}

	private class ImplicitParagraphBlock extends ContentBlock {

		ImplicitParagraphBlock() {
			super(BlockType.PARAGRAPH, "", "", 2, 2); //$NON-NLS-1$ //$NON-NLS-2$
		}

		@Override
		protected boolean isImplicitBlock() {
			return true;
		}
	}

	private class ListBlock extends ContentBlock {

		private int count = 0;

		ListBlock(BlockType blockType, int leadingNewlines) {
			super(blockType, "", "", leadingNewlines, 1); //$NON-NLS-1$//$NON-NLS-2$
		}

		@Override
		protected void emitContent(String content) throws IOException {
			AsciiDocDocumentBuilder.this.emitContent(prefix);
			AsciiDocDocumentBuilder.this.emitContent(content);
			if (!content.endsWith("\n\n")) { //$NON-NLS-1$
				AsciiDocDocumentBuilder.this.emitContent(suffix);
			}
		}

		protected void addListItem(ListItemBlock item) {
			Objects.requireNonNull(item);
			count++;
		}

		protected int getCount() {
			return count;
		}

	}

	private class ListItemBlock extends ContentBlock {

		private int count;

		private ListItemBlock(String prefix) {
			super(BlockType.LIST_ITEM, prefix, "", 1, 1); //$NON-NLS-1$
		}

		@Override
		public void open() throws IOException {
			super.open();
			if (getPreviousBlock() instanceof ListBlock) {
				ListBlock list = (ListBlock) getPreviousBlock();
				list.addListItem(this);
				count = list.getCount();
			}
		}

		@Override
		protected void emitContent(String content) throws IOException {
			if (getPreviousBlock().getBlockType() == BlockType.NUMERIC_LIST) {
				prefix = count + ". "; //$NON-NLS-1$
			}
			super.emitContent(content);
		}

	}

	private class LinkBlock extends ContentBlock {

		private final LinkAttributes attributes;

		LinkBlock(LinkAttributes attributes) {
			super("", "", 0, 0); //$NON-NLS-1$ //$NON-NLS-2$
			this.attributes = attributes;
		}

		@Override
		protected void emitContent(String content) throws IOException {
			// link:http://url.com[label]
			AsciiDocDocumentBuilder.this.emitContent("link:"); //$NON-NLS-1$
			AsciiDocDocumentBuilder.this.emitContent(attributes.getHref());
			AsciiDocDocumentBuilder.this.emitContent('[');
			if (content != null) {
				AsciiDocDocumentBuilder.this.emitContent(content);
			}
			AsciiDocDocumentBuilder.this.emitContent(']');
		}

	}

	private class CodeSpan extends ContentBlock {

		private CodeSpan() {
			super("`", "`", 0, 0); //$NON-NLS-1$ //$NON-NLS-2$
		}

		@Override
		protected void emitContent(String content) throws IOException {
			if (content.contains("`")) { //$NON-NLS-1$
				prefix = "`` "; //$NON-NLS-1$
				suffix = " ``"; //$NON-NLS-1$
			}
			super.emitContent(content);
		}

	}

	public AsciiDocDocumentBuilder(Writer out) {
		super(out);
		currentBlock = null;
	}

	@Override
	protected Block computeBlock(BlockType type, Attributes attributes) {
		switch (type) {
			case PARAGRAPH:
				return new ContentBlock(type, "", "", 2, 2); //$NON-NLS-1$ //$NON-NLS-2$
			case QUOTE:
				return new ContentBlock(type, "[quote]\n----\n", "\n----\n", 1, 1); //$NON-NLS-1$ //$NON-NLS-2$
			case BULLETED_LIST:
			case DEFINITION_LIST:
			case NUMERIC_LIST:
				if (currentBlock != null) {
					BlockType currentBlockType = currentBlock.getBlockType();
					if (currentBlockType == BlockType.LIST_ITEM || currentBlockType == BlockType.DEFINITION_ITEM
							|| currentBlockType == BlockType.DEFINITION_TERM) {
						return new ListBlock(type, 1);
					}
				}
				return new ListBlock(type, 2);
			case LIST_ITEM:
				if (computeCurrentListType() == BlockType.NUMERIC_LIST) {
					return new ListItemBlock("1. "); //$NON-NLS-1$
				}
				return new ListItemBlock("* "); //$NON-NLS-1$
			case CODE:
			case PREFORMATTED:
				return new ContentBlock(type, "[listing]\n----\n", "\n----", 1, 2); //$NON-NLS-1$ //$NON-NLS-2$
			case TABLE:
				return new ContentBlock(type, "|===\n", "|===", 1, 1);//$NON-NLS-1$ //$NON-NLS-2$
			case TABLE_ROW:
				return new ContentBlock(type, "", "", 2, 1); //$NON-NLS-1$ //$NON-NLS-2$
			case TABLE_CELL_HEADER:
			case TABLE_CELL_NORMAL:
				return new ContentBlock(type, "|", " ", 0, 0); //$NON-NLS-1$ //$NON-NLS-2$
			case DIV:
				return new ContentBlock(type, "", "", 0, 0); //$NON-NLS-1$ //$NON-NLS-2$
			case DEFINITION_ITEM:
				return new ContentBlock(type, ":: ", "", 0, 1); //$NON-NLS-1$//$NON-NLS-2$
			case DEFINITION_TERM:
				return new ContentBlock(type, "", "", 0, 0); //$NON-NLS-1$//$NON-NLS-2$
			default:
				Logger.getLogger(getClass().getName()).warning("Unexpected block type: " + type); //$NON-NLS-1$
				return new ContentBlock(type, "", "", 2, 2); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	@Override
	protected Block computeSpan(SpanType type, Attributes attributes) {
		switch (type) {
			case LINK:
				if (attributes instanceof LinkAttributes) {
					return new LinkBlock((LinkAttributes) attributes);
				}
				return new ContentBlock("", "", 0, 0); //$NON-NLS-1$ //$NON-NLS-2$
			case ITALIC:
			case EMPHASIS:
				return new ContentBlock("_", "_", 0, 0); //$NON-NLS-1$ //$NON-NLS-2$
			case BOLD:
			case STRONG:
				return new ContentBlock("*", "*", 0, 0); //$NON-NLS-1$ //$NON-NLS-2$
			case CODE:
				return new CodeSpan();
			case SUPERSCRIPT:
				return new ContentBlock("^", "^", 0, 0);
			case SUBSCRIPT:
				return new ContentBlock("~", "~", 0, 0);
			case MARK:
				return new ContentBlock("#", "#", 0, 0);
			case SPAN:
				if (attributes.getCssClass() != null) {
					return new ContentBlock("[" + attributes.getCssClass() + "]#", "#", 0, 0);
				}
				return new ContentBlock("", "", 0, 0);
			default:
				Logger.getLogger(getClass().getName()).warning("Unexpected block type: " + type); //$NON-NLS-1$
				return new ContentBlock("", "", 0, 0); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	@Override
	protected Block computeHeading(int level, Attributes attributes) {
		return new ContentBlock(computePrefix('=', level) + " ", "", 2, 2); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public void characters(String text) {
		if (text != null) {
			text = escapeAmpersand(text);
			assertOpenBlock();
			try {
				currentBlock.write(text);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private String escapeAmpersand(String text) {
		return text.replace("&", "&amp;"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public void entityReference(String entity) {
		assertOpenBlock();
		String literal = entityToLiteral.get(entity);
		if (literal == null) {
			literal = "&" + entity + ";"; //$NON-NLS-1$//$NON-NLS-2$
		}
		try {
			currentBlock.write(literal);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void image(Attributes attributes, String url) {
		assertOpenBlock();
		try {
			currentBlock.write(computeImage(attributes, url));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private String computeImage(Attributes attributes, String url) {
		// image:/path/to/img.jpg[] or
		// image:path/to/img.jpg[alt text]
		String altText = null;
		String title = null;
		if (attributes instanceof ImageAttributes imageAttr) {
			altText = imageAttr.getAlt();
		}
		if (!Strings.isNullOrEmpty(attributes.getTitle())) {
			title = "title=\"" + attributes.getTitle() + '"'; //$NON-NLS-1$
		}

		StringBuilder sb = new StringBuilder();
		sb.append("image:"); //$NON-NLS-1$
		sb.append(Strings.nullToEmpty(url));
		sb.append("["); //$NON-NLS-1$
		sb.append(Arrays.asList(altText, title).stream().filter(Objects::nonNull).collect(Collectors.joining(", "))); //$NON-NLS-1$
		sb.append("]"); //$NON-NLS-1$
		return sb.toString();
	}

	@Override
	public void link(Attributes attributes, String hrefOrHashName, String text) {
		assertOpenBlock();
		LinkAttributes linkAttr = new LinkAttributes();
		linkAttr.setTitle(attributes.getTitle());
		linkAttr.setHref(hrefOrHashName);
		beginSpan(SpanType.LINK, linkAttr);
		if (Strings.isNullOrEmpty(text)) {
			characters(hrefOrHashName);
		} else {
			characters(text);
		}
		endSpan();
	}

	@Override
	public void imageLink(Attributes linkAttributes, Attributes imageAttributes, String href, String imageUrl) {
		link(linkAttributes, href, computeImage(imageAttributes, imageUrl));
	}

	@Override
	public void acronym(String text, String definition) {
		assertOpenBlock();
		characters(text);
		characters("("); //$NON-NLS-1$
		characters(definition);
		characters(")"); //$NON-NLS-1$
	}

	@Override
	public void lineBreak() {
		assertOpenBlock();
		try {
			if (currentBlock instanceof AsciiDocBlock) {
				((AsciiDocBlock) currentBlock).lineBreak();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected Block createImplicitParagraphBlock() {
		return new ImplicitParagraphBlock();
	}

}
