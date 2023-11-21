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
package org.eclipse.mylyn.wikitext.util;

import org.eclipse.mylyn.wikitext.parser.Locator;

/**
 * @author David Green
 * @since 3.0
 */
public class LocatorImpl implements Locator {

	private final int documentOffset;

	private final int lineCharacterOffset;

	private final int lineDocumentOffset;

	private final int lineLength;

	private final int lineNumber;

	private final int lineSegmentEndOffset;

	public LocatorImpl(Locator other) {
		documentOffset = other.getDocumentOffset();
		lineCharacterOffset = other.getLineCharacterOffset();
		lineDocumentOffset = other.getLineDocumentOffset();
		lineLength = other.getLineLength();
		lineNumber = other.getLineNumber();
		lineSegmentEndOffset = other.getLineSegmentEndOffset();
	}

	@Override
	public int getDocumentOffset() {
		return documentOffset;
	}

	@Override
	public int getLineCharacterOffset() {
		return lineCharacterOffset;
	}

	@Override
	public int getLineDocumentOffset() {
		return lineDocumentOffset;
	}

	@Override
	public int getLineLength() {
		return lineLength;
	}

	@Override
	public int getLineNumber() {
		return lineNumber;
	}

	@Override
	public int getLineSegmentEndOffset() {
		return lineSegmentEndOffset;
	}

}
