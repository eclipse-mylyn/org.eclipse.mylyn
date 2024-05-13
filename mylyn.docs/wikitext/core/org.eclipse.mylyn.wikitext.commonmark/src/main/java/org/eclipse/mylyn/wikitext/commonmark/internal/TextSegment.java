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

import java.util.List;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.eclipse.mylyn.wikitext.internal.util.WikiStringStyle;

public class TextSegment {

	private final List<Line> lines;

	private final String text;

	public TextSegment(Iterable<Line> lines) {
		this.lines = List.copyOf(StreamSupport.stream(lines.spliterator(), false).toList());
		text = computeText(this.lines);
	}

	private static String computeText(List<Line> lines) {
		String text = ""; //$NON-NLS-1$
		for (Line line : lines) {
			if (text.length() > 0) {
				text += "\n"; //$NON-NLS-1$
			}
			text += line.getText();
		}
		return text;
	}

	public String getText() {
		return text;
	}

	public List<Line> getLines() {
		return lines;
	}

	public int offsetOf(int textOffset) {
		Validate.isTrue(textOffset >= 0);
		int textOffsetOfLine = 0;
		int remainder = textOffset;
		for (Line line : lines) {
			textOffsetOfLine = line.getOffset();
			int linePlusSeparatorLength = line.getText().length() + 1;
			if (linePlusSeparatorLength > remainder) {
				break;
			}
			remainder -= linePlusSeparatorLength;
		}
		return textOffsetOfLine + remainder;
	}

	public int toTextOffset(int documentOffset) {
		int textOffset = 0;
		for (Line line : lines) {
			int lineRelativeOffset = documentOffset - line.getOffset();
			int linePlusSeparatorLength = line.getText().length() + 1;
			if (lineRelativeOffset >= 0 && lineRelativeOffset < linePlusSeparatorLength) {
				return textOffset + lineRelativeOffset;
			}
			textOffset += linePlusSeparatorLength;
		}
		throw new IllegalArgumentException();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, WikiStringStyle.WIKI_STRING_STYLE) //
				.append("text", text) //$NON-NLS-1$
				.toString();
	}

	public Line getLineAtOffset(int textOffset) {
		int documentOffset = offsetOf(textOffset);
		Line previous = null;
		for (Line line : lines) {
			if (line.getOffset() > documentOffset) {
				break;
			}
			previous = line;
		}
		return requireNonNull(previous);
	}

}
