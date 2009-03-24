/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.core.parser.markup;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;

/**
 * A markup block that may span multiple lines.
 * 
 * Implements {@link Cloneable} for the template design pattern.
 * 
 * @author David Green
 */
public abstract class Block extends Processor implements Cloneable {
	private boolean closed;

	public Block() {
	}

	/**
	 * Process the given line of markup starting at the provided offset.
	 * 
	 * @param line
	 *            the markup line to process
	 * @param offset
	 *            the offset at which to start processing
	 * 
	 * @return a non-negative integer to indicate that processing of the block completed before the end of the line, or
	 *         -1 if the entire line was processed.
	 */
	public int processLine(String line, int offset) {
		getState().setLineCharacterOffset(offset);
		return processLineContent(line, offset);
	}

	/**
	 * Process the given line of markup starting at the provided offset.
	 * 
	 * @param line
	 *            the markup line to process
	 * @param offset
	 *            the offset at which to start processing
	 * 
	 * @return a non-negative integer to indicate that processing of the block completed before the end of the line, or
	 *         -1 if the entire line was processed.
	 */
	protected abstract int processLineContent(String line, int offset);

	/**
	 * Indicate if the block can start with the given markup line at the provided offset. Calling this method may cause
	 * the block to have state which is propagated when {@link #clone() cloning} and consumed in
	 * {@link #processLine(String, int, int)}. Calling this method must cause any previous state to be reset. Note that
	 * it is valid for block implementations to refuse to start at non-zero offsets.
	 * 
	 * Implementations must be able to handle this method without having the {@link Processor processor state}
	 * initialized.
	 * 
	 * @param line
	 *            the line of markup to test
	 * @param lineOffset
	 *            the offset at which the block should start processing
	 * 
	 * @return true if the provided markup consists of a valid starting point for the block
	 */
	public abstract boolean canStart(String line, int lineOffset);

	/**
	 * Indicate if block nesting should begin. Called after {@link #processLineContent(String, int)}.
	 * 
	 * @return true if nesting should start, otherwise false.
	 * 
	 * @see #findCloseOffset(String, int)
	 * @since 1.1
	 */
	public boolean beginNesting() {
		return false;
	}

	/**
	 * Indicate if the block can close on the given line at the given offset. blocks that implement a nesting protocol
	 * must implement this method.
	 * 
	 * @param line
	 *            the line of content
	 * @param lineOffset
	 *            the 0-based offset into the line
	 * 
	 * @return the 0-based offset where the close will occur, or -1 if the block should not close on this line.
	 * 
	 * @see #beginNesting()
	 * @since 1.1
	 */
	public int findCloseOffset(String line, int lineOffset) {
		return -1;
	}

	/**
	 * Indicate if the current block is closed
	 */
	public boolean isClosed() {
		return closed;
	}

	/**
	 * Cause the block to be closed. If the block is going from the open to the closed state, then the block must cause
	 * the closed state to be propagated to the {@link DocumentBuilder builder} if necessary.
	 * 
	 * @param closed
	 */
	public void setClosed(boolean closed) {
		this.closed = closed;
	}

	/**
	 * Clone the block including its state. Cloning is generally used after the {@link #canStart(String, int)} method is
	 * called in order to implement the Template design pattern.
	 */
	@Override
	public Block clone() {
		return (Block) super.clone();
	}

}
