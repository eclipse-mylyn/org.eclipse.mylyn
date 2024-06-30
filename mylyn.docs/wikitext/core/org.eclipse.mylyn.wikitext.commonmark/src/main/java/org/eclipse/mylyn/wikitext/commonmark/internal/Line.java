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

import java.util.Objects;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.eclipse.mylyn.wikitext.parser.Locator;
import org.eclipse.mylyn.wikitext.util.WikiToStringStyle;

import com.google.common.base.CharMatcher;

public class Line {

	private final String text;

	private final int offset;

	private final int lineNumber;

	public Line(int lineNumber, int offset, String text) {
		Validate.isTrue(offset >= 0);
		Validate.isTrue(lineNumber >= 0);
		this.lineNumber = lineNumber;
		this.offset = offset;
		this.text = Objects.requireNonNull(text);
	}

	public boolean isEmpty() {
		return !CharMatcher.whitespace().negate().matchesAnyOf(text);
	}

	public String getText() {
		return text;
	}

	/**
	 * Provides the 0-based offset of the first character of the line.
	 * 
	 * @return the line offset
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * Provides the 0-based line number.
	 * 
	 * @return the line number
	 */
	public int getLineNumber() {
		return lineNumber;
	}

	/**
	 * Provides a segment of this line, with {@link #getText() text}.
	 * 
	 * @param offset
	 *            the 0-based offset of the {@link #getText() text} of this line
	 * @param length
	 *            the length of the {@link #getText() text} from the given {@code offset}
	 * @return the segment
	 */
	public Line segment(int offset, int length) {
		return new Line(lineNumber, this.offset + offset, text.substring(offset, offset + length));
	}

	public Locator toLocator() {
		return new SimpleLocator(this);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, WikiToStringStyle.WIKI_TO_STRING_STYLE) //
				.append("lineNumber", lineNumber) //$NON-NLS-1$
				.append("offset", offset) //$NON-NLS-1$
				.append("text", ToStringHelper.toStringValue(text)) //$NON-NLS-1$
				.toString();
	}
}