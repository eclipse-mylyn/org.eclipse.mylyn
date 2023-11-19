/*******************************************************************************
 * Copyright (c) 2012, 2013 Stefan Seelmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.markdown.internal.util;

import org.eclipse.mylyn.wikitext.parser.markup.Block;

/**
 * Markup blocks that require additional context in order to decide if they can start with a markup line.
 * 
 * @author Stefan Seelmann
 */
public interface ReadAheadBlock extends Cloneable {

	/**
	 * Indicate if the block can start with the given markup line at the provided offset. The
	 * <code>lookAheadReader</code> can be used to ask for more context.
	 * 
	 * @param line
	 *            the line of markup to test
	 * @param lineOffset
	 *            the offset at which the block should start processing
	 * @param lookAheadReader
	 *            the look ahead reader to ask for more context
	 * @return true if the provided markup consists of a valid starting point for the block
	 * @see Block#canStart(String, int)
	 */
	boolean canStart(String line, int lineOffset, LookAheadReader lookAheadReader);

}
