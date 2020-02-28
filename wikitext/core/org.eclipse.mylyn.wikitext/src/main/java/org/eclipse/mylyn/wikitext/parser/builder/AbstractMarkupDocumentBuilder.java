/*******************************************************************************
 * Copyright (c) 2011, 2015 Tasktop Technologies
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.parser.builder;

import java.io.IOException;
import java.io.Writer;
import java.util.Stack;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;

/**
 * Provides default functionality for document builders that emit lightweight wiki markup.
 *
 * @author David Green
 * @since 3.0
 */
public abstract class AbstractMarkupDocumentBuilder extends DocumentBuilder {

	/**
	 * Represents a block or section of the document. By default blocks have no content.
	 */
	protected abstract class Block {
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

		/**
		 *
		 */
		protected boolean isImplicitBlock() {
			return false;
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
			emitContent(suffix);
			super.close();
		}
	}

	/**
	 * A block that is delimited by newlines.
	 */
	protected class NewlineDelimitedBlock extends Block {

		private final int precedingNewlineCount;

		private final int trailingNewlineCount;

		public NewlineDelimitedBlock(BlockType blockType, int precedingNewlineCount, int trailingNewlineCount) {
			super(blockType);
			this.precedingNewlineCount = precedingNewlineCount;
			this.trailingNewlineCount = trailingNewlineCount;
		}

		@Override
		public void open() throws IOException {
			super.open();
			emitDelimiter(precedingNewlineCount);
		}

		private void emitDelimiter(int newlineCount) throws IOException {
			if (getLastChar() != 0) {
				int delimiterSize = newlineCount - getTrailingNewlineCount();
				for (int x = delimiterSize; x > 0; --x) {
					emitContent('\n');
				}
			}
		}

		@Override
		public void close() throws IOException {
			emitDelimiter(trailingNewlineCount);
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
			emitContent(normalizeWhitespace(c));
		}

		@Override
		public void write(String s) throws IOException {
			hasContent = true;
			emitContent(normalizeWhitespace(s));
		}

		@Override
		public void close() throws IOException {
			if (hasContent) {
				emitContent("\n\n"); //$NON-NLS-1$
			}
			super.close();
		}

		/**
		 *
		 */
		protected int normalizeWhitespace(int c) {
			return AbstractMarkupDocumentBuilder.this.normalizeWhitespace(c);
		}

		/**
		 *
		 */
		protected String normalizeWhitespace(String s) {
			return AbstractMarkupDocumentBuilder.this.normalizeWhitespace(s);
		}

		@Override
		protected boolean isImplicitBlock() {
			return true;
		}
	}

	protected Block currentBlock;

	private Stack<MarkupWriter> writerState;

	private MarkupWriter writer;

	private boolean adjacentSeparatorRequired = false;

	private static class MarkupWriter extends Writer {

		private final Writer delegate;

		private char lastChar;

		private int trailingNewlineCount;

		private int characterCount;

		public MarkupWriter(Writer delegate) {
			this.delegate = delegate;
		}

		@Override
		public void write(char[] cbuf, int off, int len) throws IOException {
			if (len <= 0) {
				return;
			}
			delegate.write(cbuf, off, len);
			characterCount += len;
			int lastCharIndex = off + len - 1;
			lastChar = cbuf[lastCharIndex];
			int newlineCount = countTailingNewlines(cbuf, off, len);
			if (newlineCount == len) {
				trailingNewlineCount += newlineCount;
			} else {
				trailingNewlineCount = newlineCount;
			}
		}

		private int countTailingNewlines(char[] cbuf, int off, int len) {
			int newlineCount = 0;
			for (int x = off + len - 1; x >= off; --x) {
				char c = cbuf[x];
				if (c != '\n') {
					break;
				}
				++newlineCount;
			}
			return newlineCount;
		}

		/**
		 * get the last character that was written to the writer, or 0 if no character has been written.
		 */
		public char getLastChar() {
			return lastChar;
		}

		public int getTrailingNewlineCount() {
			return trailingNewlineCount;
		}

		public int getCharacterCount() {
			return characterCount;
		}

		@Override
		public void flush() throws IOException {
			delegate.flush();
		}

		@Override
		public void close() throws IOException {
			delegate.close();
		}

		public Writer getDelegate() {
			return delegate;
		}

	}

	protected AbstractMarkupDocumentBuilder(final Writer out) {
		this.writer = new MarkupWriter(out);
	}

	protected void emitContent(int c) throws IOException {
		maybeInsertAdjacentWhitespace(c);
		writer.write(c);
	}

	private void maybeInsertAdjacentWhitespace(int c) throws IOException {
		if (adjacentSeparatorRequired) {
			if (!isSeparator(c)) {
				char lastChar = getLastChar();
				if (lastChar != 0 && !isSeparator(lastChar)) {
					writer.write(' ');
				}
			}
			adjacentSeparatorRequired = false;
		}
	}

	/**
	 * Emits the given string
	 *
	 * @param str
	 *            the string to emit, or null
	 * @throws IOException
	 *             if the writer throws an IOException
	 */
	protected void emitContent(String str) throws IOException {
		if (str == null || str.length() == 0) {
			return;
		}
		maybeInsertAdjacentWhitespace(str.charAt(0));
		writer.write(str);
	}

	/**
	 * Indicate that the next content to be emitted requires adjacent {@link #isSeparator(char) separator}. When
	 * invoked, the next call to {@link #emitContent(int)} or {@link #emitContent(String)} will test to see if the
	 * {@link #getLastChar() last character} is a separator character, or if the content to be emitted starts with a
	 * separator. If neither are true, then a single space character is inserted into the content stream. Subsequent
	 * calls to <code>emitContent</code> are not affected.
	 *
	 * @see #clearRequireAdjacentSeparator()
	 */
	protected void requireAdjacentSeparator() {
		adjacentSeparatorRequired = true;
	}

	/**
	 * @see #requireAdjacentSeparator()
	 */
	protected void clearRequireAdjacentSeparator() {
		adjacentSeparatorRequired = false;
	}

	protected boolean isSeparator(int i) {
		char c = (char) i;
		boolean separator = Character.isWhitespace(c);
		if (!separator) {
			switch (c) {
			case ',':
			case '.':
			case '!':
			case '?':
			case ':':
			case ';':
			case ')':
			case '(':
			case '}':
			case '{':
			case '[':
			case ']':
			case '|':
			case '"':
				separator = true;
			}
		}
		return separator;
	}

	@Override
	public void beginDocument() {
		// nothing to do
	}

	@Override
	public void endDocument() {
		flush();
	}

	@Override
	public void flush() {
		while (currentBlock != null) {
			endBlock();
		}
	}

	/**
	 * Subclasses may push a writer in order to intercept emitted content. Calls to this method must be matched by
	 * corresponding calls to {@link #popWriter()}.
	 *
	 * @see #popWriter()
	 */
	protected void pushWriter(Writer writer) {
		if (writerState == null) {
			writerState = new Stack<MarkupWriter>();
		}
		writerState.push(this.writer);
		this.writer = new MarkupWriter(writer);
	}

	/**
	 * @see #pushWriter(Writer)
	 */
	protected Writer popWriter() {
		if (writerState == null || writerState.isEmpty()) {
			throw new IllegalStateException();
		}
		MarkupWriter markupWriter = writer;
		writer = writerState.pop();
		return markupWriter.getDelegate();
	}

	/**
	 * get the last character that was emitted, or 0 if no character has been written.
	 */
	protected char getLastChar() {
		char c = writer.getLastChar();
		if (c == 0 && writerState != null) {
			for (int x = writerState.size() - 1; c == 0 && x >= 0; --x) {
				c = writerState.get(x).getLastChar();
			}
		}
		return c;
	}

	/**
	 * Provides the number of trailing newlines that were emitted, or 0 if no trailing newlines were written.
	 */
	protected int getTrailingNewlineCount() {
		int count = writer.getTrailingNewlineCount();
		if (writer.getCharacterCount() == count && writerState != null) {
			for (int x = writerState.size() - 1; x >= 0; --x) {
				MarkupWriter markupWriter = writerState.get(x);
				int trailingNewlineCount = markupWriter.getTrailingNewlineCount();
				count += trailingNewlineCount;
				if (markupWriter.getCharacterCount() > trailingNewlineCount) {
					break;
				}
			}
		}
		return count;
	}

	@Override
	public void beginBlock(BlockType type, Attributes attributes) {
		try {
			if (currentBlock != null && currentBlock.isImplicitBlock()) {
				currentBlock.close();
				currentBlock = null;
			}
			Block block = computeBlock(type, attributes);
			block.open();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected abstract Block computeBlock(BlockType type, Attributes attributes);

	@Override
	public void beginSpan(SpanType type, Attributes attributes) {
		assertOpenBlock();
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
		closeCurrentBlock();
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
		closeCurrentBlock();
	}

	private void closeCurrentBlock() {
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
			Block block = createImplicitParagraphBlock();
			try {
				block.open();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
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

	/**
	 * Creates paragraph blocks in cases where content is emitted but no block is currently open. Subclasses may
	 * override to alter the default paragraph block implementation.
	 */
	protected Block createImplicitParagraphBlock() {
		return new ImplicitParagraphBlock();
	}

}
