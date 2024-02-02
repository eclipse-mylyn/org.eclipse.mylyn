/*******************************************************************************
 * Copyright (c) 2015 David Green.
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

package org.eclipse.mylyn.wikitext.commonmark.internal.inlines;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.eclipse.mylyn.wikitext.commonmark.internal.Line;
import org.eclipse.mylyn.wikitext.commonmark.internal.ProcessingContext;
import org.eclipse.mylyn.wikitext.commonmark.internal.ProcessingContextBuilder;
import org.eclipse.mylyn.wikitext.commonmark.internal.SimpleLocator;
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
		checkArgument(offset >= 0);
		checkArgument(length > 0);
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
		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}
		Inline other = (Inline) obj;
		return other.offset == offset && other.length == length;
	}

	@Override
	public String toString() {
		return toStringHelper(getClass()).add("offset", getOffset()).add("length", getLength()).toString();
	}
}
