/*******************************************************************************
 * Copyright (c) 2014, 2019 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Leo Dos Santos - initial API and implementation
 *     Pierre-Yves B. <pyvesdev@gmail.com> - Bug 509033 - markdown misses support for ~~strike~~
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.markdown.internal;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.markdown.MarkdownLanguage;
import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.ImageAttributes;
import org.eclipse.mylyn.wikitext.parser.LinkAttributes;
import org.eclipse.mylyn.wikitext.parser.builder.AbstractMarkupDocumentBuilder;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;

/**
 * a document builder that emits Markdown markup
 *
 * @author Leo Dos Santos
 * @see MarkdownLanguage
 * @see MarkdownLanguage#createDocumentBuilder(Writer)
 */
public class MarkdownDocumentBuilder extends AbstractMarkupDocumentBuilder {

	private static final Pattern PATTERN_LINE_BREAK = Pattern.compile("(.*(\r\n|\r|\n)?)?"); //$NON-NLS-1$

	private final Map<String, String> entityToLiteral = new HashMap<String, String>();
	{
		entityToLiteral.put("amp", "&"); //$NON-NLS-1$ //$NON-NLS-2$
		entityToLiteral.put("lt", "<"); //$NON-NLS-1$ //$NON-NLS-2$
		entityToLiteral.put("gt", ">"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private interface MarkdownBlock {

		void lineBreak() throws IOException;

	}

	private class ContentBlock extends NewlineDelimitedBlock implements MarkdownBlock {

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
			MarkdownDocumentBuilder.this.emitContent(c);
		}

		@Override
		public void write(String s) throws IOException {
			MarkdownDocumentBuilder.this.emitContent(s);
		}

		@Override
		public void lineBreak() throws IOException {
			write("  \n"); //$NON-NLS-1$
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
			MarkdownDocumentBuilder.this.emitContent(prefix);
			MarkdownDocumentBuilder.this.emitContent(content);
			MarkdownDocumentBuilder.this.emitContent(suffix);
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

	private class PrefixedLineContentBlock extends ContentBlock {

		PrefixedLineContentBlock(BlockType blockType, String prefix, String suffix, int leadingNewlines,
				int trailingNewlines) {
			super(blockType, prefix, suffix, leadingNewlines, trailingNewlines);
		}

		@Override
		protected void emitContent(String content) throws IOException {
			// break out the block onto its own line if the last character
			// was not a line break or null character literal
			char lastChar = getLastChar();
			if (lastChar != '\n' && lastChar != '\r' && lastChar != '\u0000') {
				MarkdownDocumentBuilder.this.emitContent('\n');
			}

			// split out content by line break
			Matcher matcher = PATTERN_LINE_BREAK.matcher(content);
			while (matcher.find()) {
				// if the line is empty, emit no prefix
				String line = matcher.group(0);
				if (!line.trim().isEmpty()) {
					MarkdownDocumentBuilder.this.emitContent(prefix);
				}
				MarkdownDocumentBuilder.this.emitContent(line);
			}
			// collapse suffix for nested blocks
			if (!content.endsWith(suffix)) {
				MarkdownDocumentBuilder.this.emitContent(suffix);
			}
		}

	}

	private class ListBlock extends ContentBlock {

		private int count = 0;

		ListBlock(BlockType blockType, int leadingNewlines) {
			super(blockType, "", "", leadingNewlines, 1); //$NON-NLS-1$//$NON-NLS-2$
		}

		@Override
		protected void emitContent(String content) throws IOException {
			MarkdownDocumentBuilder.this.emitContent(prefix);
			MarkdownDocumentBuilder.this.emitContent(content);
			if (!content.endsWith("\n\n")) { //$NON-NLS-1$
				MarkdownDocumentBuilder.this.emitContent(suffix);
			}
		}

		protected void addListItem(ListItemBlock item) {
			requireNonNull(item);
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
			String indent = Strings.repeat(" ", prefix.length()); //$NON-NLS-1$

			MarkdownDocumentBuilder.this.emitContent(prefix);
			// split out content by line
			Matcher matcher = PATTERN_LINE_BREAK.matcher(content);
			int lines = 0;
			while (matcher.find()) {
				// indent each line hanging past the initial line item
				String line = matcher.group(0);
				if (lines > 0 && !line.trim().isEmpty()) {
					int indexOfFirstNonSpace = CharMatcher.isNot(' ').indexIn(line);
					if (indexOfFirstNonSpace >= 4) {
						line = Strings.repeat(" ", 4) + line; //$NON-NLS-1$
					} else {
						line = indent + line;
					}
				}
				MarkdownDocumentBuilder.this.emitContent(line);
				lines++;
			}
			// collapse suffix for nested blocks
			if (!content.endsWith(suffix)) {
				MarkdownDocumentBuilder.this.emitContent(suffix);
			}
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
			// [label](http://url.com) or
			// [label](http://url.com "title")
			MarkdownDocumentBuilder.this.emitContent('[');
			MarkdownDocumentBuilder.this.emitContent(content);
			MarkdownDocumentBuilder.this.emitContent(']');

			MarkdownDocumentBuilder.this.emitContent('(');
			MarkdownDocumentBuilder.this.emitContent(attributes.getHref());
			if (!Strings.isNullOrEmpty(attributes.getTitle())) {
				MarkdownDocumentBuilder.this.emitContent(" \""); //$NON-NLS-1$
				MarkdownDocumentBuilder.this.emitContent(attributes.getTitle());
				MarkdownDocumentBuilder.this.emitContent('"');
			}
			MarkdownDocumentBuilder.this.emitContent(')');
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

	public MarkdownDocumentBuilder(Writer out) {
		super(out);
		currentBlock = null;
	}

	@Override
	protected Block computeBlock(BlockType type, Attributes attributes) {
		switch (type) {
		case PARAGRAPH:
			return new ContentBlock(type, "", "", 2, 2); //$NON-NLS-1$ //$NON-NLS-2$
		case QUOTE:
			return new PrefixedLineContentBlock(type, "> ", "", 1, 1); //$NON-NLS-1$ //$NON-NLS-2$
		case BULLETED_LIST:
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
			return new PrefixedLineContentBlock(type, "    ", "", 1, 2); //$NON-NLS-1$ //$NON-NLS-2$
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
			return new ContentBlock("<", ">", 0, 0); //$NON-NLS-1$ //$NON-NLS-2$
		case ITALIC:
		case EMPHASIS:
		case MARK:
			return new ContentBlock("*", "*", 0, 0); //$NON-NLS-1$ //$NON-NLS-2$
		case BOLD:
		case STRONG:
			return new ContentBlock("**", "**", 0, 0); //$NON-NLS-1$ //$NON-NLS-2$
		case CODE:
			return new CodeSpan();
		case DELETED:
			return new ContentBlock("~", "~", 0, 0); //$NON-NLS-1$ //$NON-NLS-2$
		default:
			Logger.getLogger(getClass().getName()).warning("Unexpected block type: " + type); //$NON-NLS-1$
			return new ContentBlock("", "", 0, 0); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	@Override
	protected Block computeHeading(int level, Attributes attributes) {
		return new ContentBlock(computePrefix('#', level) + " ", "", 1, 2); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public void characters(String text) {
		text = escapeAmpersand(text);
		assertOpenBlock();
		try {
			currentBlock.write(text);
		} catch (IOException e) {
			throw new RuntimeException(e);
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
		// ![](/path/to/img.jpg) or
		// ![alt text](path/to/img.jpg "title")
		String altText = ""; //$NON-NLS-1$
		String title = ""; //$NON-NLS-1$
		if (attributes instanceof ImageAttributes) {
			ImageAttributes imageAttr = (ImageAttributes) attributes;
			altText = Strings.nullToEmpty(imageAttr.getAlt());
		}
		if (!Strings.isNullOrEmpty(attributes.getTitle())) {
			title = " \"" + attributes.getTitle() + '"'; //$NON-NLS-1$
		}
		return "![" + altText + "](" + Strings.nullToEmpty(url) + title + ')'; //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public void link(Attributes attributes, String hrefOrHashName, String text) {
		assertOpenBlock();
		LinkAttributes linkAttr = new LinkAttributes();
		linkAttr.setTitle(attributes.getTitle());
		linkAttr.setHref(hrefOrHashName);
		beginSpan(SpanType.LINK, linkAttr);
		characters(text);
		endSpan();
	}

	@Override
	public void imageLink(Attributes linkAttributes, Attributes imageAttributes, String href, String imageUrl) {
		link(linkAttributes, href, computeImage(imageAttributes, imageUrl));
	}

	@Override
	public void acronym(String text, String definition) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void lineBreak() {
		assertOpenBlock();
		try {
			if (currentBlock instanceof MarkdownBlock) {
				((MarkdownBlock) currentBlock).lineBreak();
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
