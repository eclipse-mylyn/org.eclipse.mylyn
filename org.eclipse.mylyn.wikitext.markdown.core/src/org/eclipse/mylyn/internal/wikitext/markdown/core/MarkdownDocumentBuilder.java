/*******************************************************************************
 * Copyright (c) 2014 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Leo Dos Santos - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.markdown.core;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.ImageAttributes;
import org.eclipse.mylyn.wikitext.core.parser.LinkAttributes;
import org.eclipse.mylyn.wikitext.core.parser.builder.AbstractMarkupDocumentBuilder;
import org.eclipse.mylyn.wikitext.markdown.core.MarkdownLanguage;

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

	private interface MarkdownBlock {

		void lineBreak() throws IOException;

	}

	private class ContentBlock extends Block implements MarkdownBlock {

		protected final String prefix;

		protected final String suffix;

		ContentBlock(BlockType blockType, String prefix, String suffix) {
			super(blockType);
			this.prefix = prefix;
			this.suffix = suffix;
		}

		ContentBlock(String prefix, String suffix) {
			this(null, prefix, suffix);
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

	private class ImplicitParagraphBlock extends AbstractMarkupDocumentBuilder.ImplicitParagraphBlock implements
			MarkdownBlock {

		@Override
		public void lineBreak() throws IOException {
			MarkdownDocumentBuilder.this.emitContent("  \n"); //$NON-NLS-1$
		}

	}

	private class PrefixedLineContentBlock extends ContentBlock {

		PrefixedLineContentBlock(BlockType blockType, String prefix, String suffix) {
			super(blockType, prefix, suffix);
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

	private class LinkBlock extends ContentBlock {

		private final LinkAttributes attributes;

		LinkBlock(LinkAttributes attributes) {
			super("", ""); //$NON-NLS-1$ //$NON-NLS-2$
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

	public MarkdownDocumentBuilder(Writer out) {
		super(out);
	}

	@Override
	protected Block computeBlock(BlockType type, Attributes attributes) {
		switch (type) {
		case PARAGRAPH:
			return new ContentBlock(type, "", "\n\n"); //$NON-NLS-1$ //$NON-NLS-2$
		case QUOTE:
			return new PrefixedLineContentBlock(type, "> ", "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		default:
			Logger.getLogger(getClass().getName()).warning("Unexpected block type: " + type); //$NON-NLS-1$
			return new ContentBlock(type, "", ""); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	@Override
	protected Block computeSpan(SpanType type, Attributes attributes) {
		switch (type) {
		case LINK:
			if (attributes instanceof LinkAttributes) {
				return new LinkBlock((LinkAttributes) attributes);
			}
		case ITALIC:
		case EMPHASIS:
			return new ContentBlock("*", "*"); //$NON-NLS-1$ //$NON-NLS-2$
		case BOLD:
		case STRONG:
			return new ContentBlock("**", "**"); //$NON-NLS-1$ //$NON-NLS-2$
		default:
			Logger.getLogger(getClass().getName()).warning("Unexpected block type: " + type); //$NON-NLS-1$
			return new ContentBlock("", ""); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	@Override
	protected Block computeHeading(int level, Attributes attributes) {
		checkArgument(level >= 1 && level <= 6);
		return new ContentBlock(new String("######".toCharArray(), 0, level) + " ", "\n\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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
		throw new UnsupportedOperationException();
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
		return "![" + altText + "](" + url + title + ')'; //$NON-NLS-1$ //$NON-NLS-2$
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
			} else {
				currentBlock.write("\n"); //$NON-NLS-1$
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
