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

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.builder.AbstractMarkupDocumentBuilder;
import org.eclipse.mylyn.wikitext.markdown.core.MarkdownLanguage;

/**
 * a document builder that emits Markdown markup
 *
 * @author Leo Dos Santos
 * @see MarkdownLanguage
 * @see MarkdownLanguage#createDocumentBuilder(Writer)
 */
public class MarkdownDocumentBuilder extends AbstractMarkupDocumentBuilder {

	private interface MarkdownBlock {

		void lineBreak() throws IOException;

	}

	private class ContentBlock extends Block implements MarkdownBlock {

		private final String prefix;

		private final String suffix;

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

	public MarkdownDocumentBuilder(Writer out) {
		super(out);
	}

	@Override
	protected Block computeBlock(BlockType type, Attributes attributes) {
		switch (type) {
		case PARAGRAPH:
			return new ContentBlock(type, "", "\n\n"); //$NON-NLS-1$ //$NON-NLS-2$
		default:
			Logger.getLogger(getClass().getName()).warning("Unexpected block type: " + type); //$NON-NLS-1$
			return new ContentBlock(type, "", ""); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	@Override
	protected Block computeSpan(SpanType type, Attributes attributes) {
		throw new UnsupportedOperationException();
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
		throw new UnsupportedOperationException();
	}

	@Override
	public void link(Attributes attributes, String hrefOrHashName, String text) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void imageLink(Attributes linkAttributes, Attributes imageAttributes, String href, String imageUrl) {
		throw new UnsupportedOperationException();
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
