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
import org.eclipse.mylyn.wikitext.parser.builder.AbstractMarkupDocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.builder.EntityReferences;

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

		public ContentBlock(BlockType blockType, String prefix, String suffix, int precedingNewlineCount,
				int trailingNewlineCount, boolean emitWhenEmpty) {
			super(blockType, precedingNewlineCount, trailingNewlineCount);
			this.prefix = prefix;
			this.suffix = suffix;
			this.emitWhenEmpty = emitWhenEmpty;
		}

		public ContentBlock(BlockType blockType, String prefix, String suffix, int precedingNewlineCount,
				int trailingNewlineCount) {
			this(blockType, prefix, suffix, precedingNewlineCount, trailingNewlineCount, false);
		}

		public ContentBlock(String prefix, String suffix, int precedingNewlineCount, int trailingNewlineCount) {
			this(null, prefix, suffix, precedingNewlineCount, trailingNewlineCount);
		}

		public ContentBlock(String prefix, String suffix) {
			this(null, prefix, suffix, 0, 0);
		}

		@Override
		public void write(int c) throws IOException {
			CreoleDocumentBuilder.this.emitContent(c);
		}

		@Override
		public void write(String s) throws IOException {
			CreoleDocumentBuilder.this.emitContent(s);
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

	public CreoleDocumentBuilder(Writer out) {
		super(out);
	}

	@Override
	protected Block computeBlock(BlockType type, Attributes attributes) {
		switch (type) {
		case PARAGRAPH:
			return new ContentBlock(type, "", "", 2, 2); //$NON-NLS-1$ //$NON-NLS-2$
		default:
			Logger.getLogger(getClass().getName()).warning("Unexpected block type: " + type); //$NON-NLS-1$
			return new ContentBlock(type, "", "", 2, 2); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	@Override
	protected Block computeSpan(SpanType type, Attributes attributes) {
		switch (type) {
		case ITALIC:
		case EMPHASIS:
		case MARK:
			return new ContentBlock("//", "//"); //$NON-NLS-1$ //$NON-NLS-2$
		case BOLD:
		case STRONG:
			return new ContentBlock("**", "**"); //$NON-NLS-1$ //$NON-NLS-2$
		case DELETED:
			return new ContentBlock("--", "--"); //$NON-NLS-1$ //$NON-NLS-2$
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
		String escapedText = escapeTilde(text);
		assertOpenBlock();
		try {
			currentBlock.write(escapedText);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private String escapeTilde(String text) {
		return text.replace("~", "&tilde;");
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

	@Override
	protected Block createImplicitParagraphBlock() {
		return new ImplicitParagraphBlock();
	}

}
