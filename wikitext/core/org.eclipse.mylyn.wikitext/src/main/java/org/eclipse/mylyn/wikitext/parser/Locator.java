/*******************************************************************************
 * Copyright (c) 2007, 2011 David Green and others.
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
package org.eclipse.mylyn.wikitext.parser;

/**
 * An interface that provides information about the location of the current parser activity. Note that parsers may make
 * a best-effort attempt at determining the location.
 *
 * @author David Green
 * @since 3.0
 */
public interface Locator {
	/**
	 * get the 1-based number of the current line.
	 *
	 * @return the line number or -1 if unknown
	 */
	public int getLineNumber();

	/**
	 * get the 0-based character offset of the current line from the start of the document
	 *
	 * @return the offset or -1 if unknown
	 */
	public int getLineDocumentOffset();

	/**
	 * get the 0-based character offset of the current character from the start of the document. Equivalent to
	 * <code>getLineDocumentOffset()+getLineCharacterOffset()</code>
	 */
	public int getDocumentOffset();

	/**
	 * get the length of the current line in characters, not including the line terminator
	 */
	public int getLineLength();

	/**
	 * get the 0-based offset of the current character in the current line
	 */
	public int getLineCharacterOffset();

	/**
	 * Get the 0-based offset of the end of the current line segment being processed, exclusive. Generally a phrase
	 * modifier starts at {@link #getLineCharacterOffset()} and ends on the character preceding this offset,
	 * <code>[s,e)</code> where s is the start and e is the end.
	 */
	public int getLineSegmentEndOffset();
}
