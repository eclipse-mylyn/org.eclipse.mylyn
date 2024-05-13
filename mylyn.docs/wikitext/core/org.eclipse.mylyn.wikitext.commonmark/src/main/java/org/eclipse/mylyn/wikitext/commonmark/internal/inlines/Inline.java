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

package org.eclipse.mylyn.wikitext.commonmark.internal.inlines;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.eclipse.mylyn.wikitext.commonmark.internal.Line;
import org.eclipse.mylyn.wikitext.commonmark.internal.ProcessingContext;
import org.eclipse.mylyn.wikitext.commonmark.internal.ProcessingContextBuilder;
import org.eclipse.mylyn.wikitext.commonmark.internal.SimpleLocator;
import org.eclipse.mylyn.wikitext.internal.util.WikiStringStyle;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.Locator;

public abstract class Inline {

	private final Line line;

	private final int offset;

	private final int length;

	public Inline(Line line, int offset, int length) {
		this.line = Objects.requireNonNull(line);
		this.offset = offset;
		this.length = length;
		Validate.isTrue(offset >= 0);
		Validate.isTrue(length > 0);
	}

	public int getOffset() {
		return offset;
	}

	public int getLength() {
		return length;
	}

	public Line getLine() {
		return line;
	}

	public Locator getLocator() {
		int lineCharacterOffset = getOffset() - line.getOffset();
		return new SimpleLocator(line, lineCharacterOffset, lineCharacterOffset + length);
	}

	public abstract void emit(DocumentBuilder builder);

	public void apply(ProcessingContext context, List<Inline> inlines, Cursor cursor) {
		cursor.advance(getLength());
		inlines.add(this);
	}

	Optional<InlinesSubstitution> secondPass(List<Inline> inlines) {
		return Optional.empty();
	}

	public void createContext(ProcessingContextBuilder contextBuilder) {
		// nothing to do
	}

	@Override
	public int hashCode() {
		return Objects.hash(offset, length);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		Inline other = (Inline) obj;
		return other.offset == offset && other.length == length;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, WikiStringStyle.WIKI_STRING_STYLE) //
				.append("offset", getOffset()) //$NON-NLS-1$
				.append("length", getLength()) //$NON-NLS-1$
				.toString();
	}
}
