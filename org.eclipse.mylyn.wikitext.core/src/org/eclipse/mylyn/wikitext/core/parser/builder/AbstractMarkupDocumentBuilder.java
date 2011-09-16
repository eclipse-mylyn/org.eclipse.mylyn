/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.core.parser.builder;

import java.io.IOException;
import java.io.Writer;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;

/**
 * Provides default functionality for document builders that emit lightweight wiki markup.
 * 
 * @author David Green
 * @since 1.6
 */
public abstract class AbstractMarkupDocumentBuilder extends DocumentBuilder {

	/**
	 * a block or section
	 */
	protected class Block {
		private Block previousBlock;

		private final BlockType blockType;

		protected Block(BlockType blockType) {
			this.blockType = blockType;
		}

		public void write(int c) throws IOException {
		}

		public void write(String s) throws IOException {
		}

		public void open() throws IOException {
			previousBlock = currentBlock;
			currentBlock = this;
		}

		public void close() throws IOException {
			currentBlock = previousBlock;
		}

		public Block getPreviousBlock() {
			return previousBlock;
		}

		public BlockType getBlockType() {
			return blockType;
		}
	}

	/**
	 * a block that outputs a suffix when it's closed
	 */
	protected class SuffixBlock extends Block {
		private final String suffix;

		public SuffixBlock(BlockType blockType, String suffix) {
			super(blockType);
			this.suffix = suffix;
		}

		@Override
		public void close() throws IOException {
			out.write(suffix);
			super.close();
		}
	}

	/**
	 * a block that provides default paragraph functionality, for emitting content when no explicit block has been
	 * opened.
	 */
	protected class ImplicitParagraphBlock extends Block {

		private boolean hasContent = false;

		public ImplicitParagraphBlock() {
			super(BlockType.PARAGRAPH);
		}

		@Override
		public void write(int c) throws IOException {
			hasContent = true;
			out.write(normalizeWhitespace(c));
		}

		@Override
		public void write(String s) throws IOException {
			hasContent = true;
			out.write(normalizeWhitespace(s));
		}

		@Override
		public void close() throws IOException {
			if (hasContent) {
				out.write("\n\n"); //$NON-NLS-1$
			}
			super.close();
		}
	}

	protected Block currentBlock = new ImplicitParagraphBlock();

	protected Writer out;

	protected AbstractMarkupDocumentBuilder(Writer out) {
		this.out = out;
	}

	@Override
	public void beginDocument() {
		// nothing to do
	}

	@Override
	public void endDocument() {
		while (currentBlock != null) {
			endBlock();
		}
	}

	@Override
	public void beginBlock(BlockType type, Attributes attributes) {
		Block block = computeBlock(type, attributes);
		try {
			if (currentBlock instanceof ImplicitParagraphBlock) {
				currentBlock.close();
				currentBlock = null;
			}
			block.open();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected abstract Block computeBlock(BlockType type, Attributes attributes);

	@Override
	public void beginSpan(SpanType type, Attributes attributes) {
		Block block = computeSpan(type, attributes);
		try {
			block.open();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected abstract Block computeSpan(SpanType type, Attributes attributes);

	@Override
	public void endSpan() {
		endBlock();
	}

	protected String computePrefix(char c, int count) {
		String prefix = ""; //$NON-NLS-1$
		while (count-- > 0) {
			prefix += c;
		}
		return prefix;
	}

	protected int computeListLevel() {
		int level = 0;
		Block b = currentBlock;
		while (b != null) {
			if (b.getBlockType() != null) {
				switch (b.getBlockType()) {
				case BULLETED_LIST:
				case NUMERIC_LIST:
				case DEFINITION_LIST:
					++level;
				}
			}
			b = b.getPreviousBlock();
		}
		return level;
	}

	protected BlockType computeCurrentListType() {
		Block b = currentBlock;
		while (b != null) {
			if (b.getBlockType() != null) {
				switch (b.getBlockType()) {
				case BULLETED_LIST:
				case NUMERIC_LIST:
				case DEFINITION_LIST:
					return b.getBlockType();
				}
			}
			b = b.getPreviousBlock();
		}
		return null;
	}

	@Override
	public void endBlock() {
		if (currentBlock != null) {
			try {
				currentBlock.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void beginHeading(int level, Attributes attributes) {
		Block block = computeHeading(level, attributes);
		try {
			block.open();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void endHeading() {
		endBlock();
	}

	protected abstract Block computeHeading(int level, Attributes attributes);

	protected void assertOpenBlock() {
		if (currentBlock == null) {
			currentBlock = new ImplicitParagraphBlock();
		}
	}

	@Override
	public void charactersUnescaped(String literal) {
		characters(literal);
	}

	protected int normalizeWhitespace(int c) {
		if (c == '\r' || c == '\n') {
			c = ' ';
		}
		return c;
	}

	protected String normalizeWhitespace(String s) {
		s = s.replaceAll("(\r|\n)", " "); //$NON-NLS-1$//$NON-NLS-2$
		return s;
	}
}
