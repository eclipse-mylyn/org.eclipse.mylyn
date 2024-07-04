/*******************************************************************************
 * Copyright (c) 2015, 2024 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     Alexander Fedorov (ArSysOp) - ongoing support
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.commonmark.internal;

import static java.util.Objects.requireNonNull;

import org.eclipse.mylyn.wikitext.parser.Locator;

public class SimpleLocator implements Locator {

	private final int lineNumber;

	private final int lineDocumentOffset;

	private final int lineLength;

	private final int lineCharacterOffset;

	private final int lineSegmentEndOffset;

	public SimpleLocator(Line line) {
		this(line, 0, line.getText().length());
	}

	public SimpleLocator(Line line, int lineCharacterOffset, int lineSegmentEndOffset) {
		requireNonNull(line);
		lineNumber = line.getLineNumber() + 1;
		lineDocumentOffset = line.getOffset();
		lineLength = line.getText().length();
		this.lineCharacterOffset = lineCharacterOffset;
		this.lineSegmentEndOffset = lineSegmentEndOffset;
	}

	@Override
	public int getLineNumber() {
		return lineNumber;
	}

	@Override
	public int getLineDocumentOffset() {
		return lineDocumentOffset;
	}

	@Override
	public int getDocumentOffset() {
		return getLineDocumentOffset() + getLineCharacterOffset();
	}

	@Override
	public int getLineLength() {
		return lineLength;
	}

	@Override
	public int getLineCharacterOffset() {
		return lineCharacterOffset;
	}

	@Override
	public int getLineSegmentEndOffset() {
		return lineSegmentEndOffset;
	}

	@SuppressWarnings("nls")
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SimpleLocator{lineNumber=")
		.append(lineNumber)
		.append(", lineDocumentOffset=")
		.append(lineDocumentOffset)
		.append(", lineLength=")
		.append(lineLength)
		.append(", lineCharacterOffset=")
		.append(lineCharacterOffset)
		.append(", lineSegmentEndOffset=")
		.append(lineSegmentEndOffset)
		.append("}");
		return builder.toString();
	}
}
