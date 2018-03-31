/*******************************************************************************
 * Copyright (c) 2015 David Green and others.
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

package org.eclipse.mylyn.wikitext.textile.internal.block;

import org.eclipse.mylyn.wikitext.parser.markup.Block;

public class NotextileTagBlock extends Block {

	private static final String NOTEXTILE_OPEN_TAG = "<notextile>"; //$NON-NLS-1$

	private static final String NOTEXTILE_CLOSE_TAG = "</notextile>"; //$NON-NLS-1$

	private int blockLineCount = 0;

	@Override
	protected int processLineContent(String line, int offset) {
		if (blockLineCount == 0) {
			offset += NOTEXTILE_OPEN_TAG.length();
		}
		++blockLineCount;
		if (offset >= line.length()) {
			if (blockLineCount > 1) {
				builder.characters("\n"); //$NON-NLS-1$
			}
			return -1;
		}
		int indexOfCloseTag = line.indexOf(NOTEXTILE_CLOSE_TAG, offset);
		if (indexOfCloseTag >= 0) {
			builder.charactersUnescaped(line.substring(offset, indexOfCloseTag));
			setClosed(true);
			return indexOfCloseTag + NOTEXTILE_CLOSE_TAG.length();
		}
		builder.charactersUnescaped(line.substring(offset));
		if (blockLineCount > 1) {
			builder.characters("\n"); //$NON-NLS-1$
		}
		return -1;
	}

	@Override
	public boolean canStart(String line, int lineOffset) {
		return line.substring(lineOffset).startsWith(NOTEXTILE_OPEN_TAG);
	}

}
