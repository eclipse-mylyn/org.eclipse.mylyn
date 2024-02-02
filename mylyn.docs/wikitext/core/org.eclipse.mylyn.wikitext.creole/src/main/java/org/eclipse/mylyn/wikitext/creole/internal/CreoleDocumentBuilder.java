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

package org.eclipse.mylyn.wikitext.creole.internal;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.logging.Logger;

import org.eclipse.mylyn.wikitext.creole.CreoleLanguage;
import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.ImageAttributes;
import org.eclipse.mylyn.wikitext.parser.LinkAttributes;
import org.eclipse.mylyn.wikitext.parser.builder.AbstractMarkupDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.builder.EntityReferences;

import com.google.common.base.Strings;

/**
 * a document builder that emits Creole markup
 *
 * @author Kevin de Vlaming
 * @see CreoleLanguageLanguage
 * @see CreoleLanguage#createDocumentBuilder(Writer)
 */
public class CreoleDocumentBuilder extends AbstractMarkupDocumentBuilder {

	private interface CreoleBlock {

		void lineBreak() throws IOException;

	}

	private class ContentBlock extends NewlineDelimitedBlock implements CreoleBlock {

		protected String prefix;

		protected String suffix;

		private final boolean emitWhenEmpty;

		private final boolean escaping;

		public ContentBlock(BlockType blockType, String prefix, String suffix, int precedingNewlineCount,
				int trailingNewlineCount, boolean emitWhenEmpty, boolean escaping) {
			super(blockType, precedingNewlineCount, trailingNewlineCount);
			this.prefix = prefix;
			this.suffix = suffix;
			this.emitWhenEmpty = emitWhenEmpty;
			this.escaping = escaping;
		}

		public ContentBlock(BlockType blockType, String prefix, String suffix, int precedingNewlineCount,
				int trailingNewlineCount) {
			this(blockType, prefix, suffix, precedingNewlineCount, trailingNewlineCount, false, true);
		}

		public ContentBlock(String prefix, String suffix, int precedingNewlineCount, int trailingNewlineCount) {
			this(null, prefix, suffix, precedingNewlineCount, trailingNewlineCount);
		}

		public ContentBlock(String prefix, String suffix) {
			this(null, prefix, suffix, 0, 0);
		}

		@Override
		public void write(int c) throws IOException {
			if (escaping) {
				CreoleDocumentBuilder.this.emitEscapedContent(c);
			} else {
				CreoleDocumentBuilder.this.emitContent(c);
			}
		}

		@Override
		public void write(String s) throws IOException {
			if (escaping) {
				CreoleDocumentBuilder.this.emitEscapedContent(s);
			} else {
				CreoleDocumentBuilder.this.emitContent(s);
			}
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
			if (emitWhenEmpty || content.length() > 0) {
				emitContent(content);
			}
			super.close();
		}

		protected void emitContent(final String content) throws IOException {
			CreoleDocumentBuilder.this.emitContent(prefix);
			CreoleDocumentBuilder.this.emitContent(content);
			CreoleDocumentBuilder.this.emitContent(suffix);
		}

		@Override
		public void lineBreak() throws IOException {
			write("\\\\"); //$NON-NLS-1$
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

	private class LinkBlock extends ContentBlock {

		private final LinkAttributes attributes;

		LinkBlock(LinkAttributes attributes) {
			super(null, "", "", 0, 0, true, true);
			this.attributes = attributes;
		}

		@Override
		protected void emitContent(String content) throws IOException {
			// [[http://url.com|label]]
			CreoleDocumentBuilder.this.emitContent("[[");
			if (!Strings.isNullOrEmpty(attributes.getHref())) {
				CreoleDocumentBuilder.this.emitContent(attributes.getHref());

			}
			if (!Strings.isNullOrEmpty(attributes.getHref()) && !Strings.isNullOrEmpty(content)) {
				CreoleDocumentBuilder.this.emitContent('|');
			}
			if (!Strings.isNullOrEmpty(content)) {
				CreoleDocumentBuilder.this.emitContent(content);
			}
			CreoleDocumentBuilder.this.emitContent("]]");
		}

	}

	private class TableCellBlock extends ContentBlock {

		public TableCellBlock(BlockType blockType) {
			super(blockType, blockType == BlockType.TABLE_CELL_NORMAL ? "|" : "|=", "", 0, 0, true, true);
		}

		@Override
		protected void emitContent(String content) throws IOException {
			if (Strings.isNullOrEmpty(content) || content.trim().isEmpty()) {
				content = " "; //$NON-NLS-1$
			}
			super.emitContent(content);
		}
	}

	public CreoleDocumentBuilder(Writer out) {
		super(out);
	}

	@Override
	protected Block computeBlock(BlockType type, Attributes attributes) {
		return switch (type) {
			case BULLETED_LIST, NUMERIC_LIST -> new NewlineDelimitedBlock(type, doubleNewlineDelimiterCount(), 1);
			case LIST_ITEM -> {
				char prefixChar = computeCurrentListType() == BlockType.NUMERIC_LIST ? '#' : '*';
				yield new ContentBlock(type, computePrefix(prefixChar, computeListLevel()) + " ", "", 1, 1);
			}
			case PARAGRAPH -> new ContentBlock(type, "", "", 2, 2); //$NON-NLS-1$ //$NON-NLS-2$
			case PREFORMATTED, CODE -> new ContentBlock(type, "{{{\n", "\n}}}", 2, 2, false, false); //$NON-NLS-1$ //$NON-NLS-2$
			case TABLE -> new SuffixBlock(type, "\n"); //$NON-NLS-1$
			case TABLE_CELL_HEADER, TABLE_CELL_NORMAL -> new TableCellBlock(type);
			case TABLE_ROW -> new SuffixBlock(type, "|\n"); //$NON-NLS-1$
			default -> {
				Logger.getLogger(getClass().getName()).warning("Unexpected block type: " + type); //$NON-NLS-1$
				yield new ContentBlock(type, "", "", 2, 2); //$NON-NLS-1$ //$NON-NLS-2$
			}
		};
	}

	private int doubleNewlineDelimiterCount() {
		if (currentBlock != null) {
			BlockType currentBlockType = currentBlock.getBlockType();
			if (currentBlockType == BlockType.LIST_ITEM || currentBlockType == BlockType.BULLETED_LIST
					|| currentBlockType == BlockType.NUMERIC_LIST) {
				return 1;
			}
		}
		return 2;
	}

	@Override
	protected Block computeSpan(SpanType type, Attributes attributes) {
		switch (type) {
			case LINK:
				if (attributes instanceof LinkAttributes) {
					return new LinkBlock((LinkAttributes) attributes);
				}
				return new ContentBlock("[[", "]]", 0, 0);
			case ITALIC:
			case EMPHASIS:
			case MARK:
				return new ContentBlock("//", "//"); //$NON-NLS-1$ //$NON-NLS-2$
			case BOLD:
			case STRONG:
				return new ContentBlock("**", "**"); //$NON-NLS-1$ //$NON-NLS-2$
			case DELETED:
				return new ContentBlock("--", "--"); //$NON-NLS-1$ //$NON-NLS-2$
			case CODE:
				return new ContentBlock(null, "{{{", "}}}", 0, 0, false, false); //$NON-NLS-1$ //$NON-NLS-2$
			case UNDERLINED:
				return new ContentBlock("__", "__"); //$NON-NLS-1$ //$NON-NLS-2$
			default:
				Logger.getLogger(getClass().getName()).warning("Unexpected span type: " + type); //$NON-NLS-1$
				return new ContentBlock("", "", 2, 2); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	@Override
	protected Block computeHeading(int level, Attributes attributes) {
		return new ContentBlock(computePrefix('=', level) + " ", "", 1, 2); //$NON-NLS-1$

	}

	@Override
	public void characters(String text) {
		assertOpenBlock();
		try {
			currentBlock.write(text);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void entityReference(String entity) {
		assertOpenBlock();
		String literal = EntityReferences.instance().equivalentString(entity);
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
		if (url != null) {
			assertOpenBlock();
			try {
				Block imageBlock = new ContentBlock(null, "{{", "}}", 0, 0, false, false);
				imageBlock.open();
				imageBlock.write(url);
				writeImageAttributes(imageBlock, attributes);
				imageBlock.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private void writeImageAttributes(Block imageBlock, Attributes attributes) throws IOException {
		if (attributes instanceof ImageAttributes) {
			if (!Strings.isNullOrEmpty(((ImageAttributes) attributes).getAlt())) {
				imageBlock.write('|');
				imageBlock.write(((ImageAttributes) attributes).getAlt());
			} else if (!Strings.isNullOrEmpty(((ImageAttributes) attributes).getTitle())) {
				imageBlock.write('|');
				imageBlock.write(((ImageAttributes) attributes).getTitle());
			}
		}
	}

	@Override
	public void link(Attributes attributes, String hrefOrHashName, String text) {
		assertOpenBlock();
		LinkAttributes linkAttributes = new LinkAttributes();
		linkAttributes.setHref(hrefOrHashName);
		beginSpan(SpanType.LINK, linkAttributes);
		characters(text);
		endSpan();
	}

	@Override
	public void imageLink(Attributes linkAttributes, Attributes imageAttributes, String href, String imageUrl) {
		String altText = "";
		if (imageAttributes instanceof ImageAttributes
				&& !Strings.isNullOrEmpty(((ImageAttributes) imageAttributes).getAlt())) {
			altText = ((ImageAttributes) imageAttributes).getAlt();
		}
		link(linkAttributes, href, "{{" + Strings.nullToEmpty(imageUrl) + "}}" + altText);
	}

	@Override
	public void acronym(String text, String definition) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void lineBreak() {
		assertOpenBlock();
		try {
			if (currentBlock instanceof CreoleBlock) {
				((CreoleBlock) currentBlock).lineBreak();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void horizontalRule() {
		assertOpenBlock();
		String horizontalRule = "\n----\n"; //$NON-NLS-1$
		try {
			currentBlock.write(horizontalRule);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	void emitEscapedContent(String s) throws IOException {
		if (s != null) {
			for (int x = 0; x < s.length(); ++x) {
				emitEscapedContent(s.charAt(x));
			}
		}
	}

	void emitEscapedContent(int c) throws IOException {
		if (c == '~' || c == '*' || c == '#' || c == '|' || c == '=') {
			super.emitContent('~');
		}
		super.emitContent(c);
	}

	@Override
	protected Block createImplicitParagraphBlock() {
		return new ImplicitParagraphBlock();
	}

}
