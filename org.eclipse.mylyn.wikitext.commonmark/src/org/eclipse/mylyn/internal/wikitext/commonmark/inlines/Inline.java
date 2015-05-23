/*******************************************************************************
 * Copyright (c) 2015 David Green.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.commonmark.inlines;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Objects;

import org.eclipse.mylyn.internal.wikitext.commonmark.Line;
import org.eclipse.mylyn.internal.wikitext.commonmark.ProcessingContext;
import org.eclipse.mylyn.internal.wikitext.commonmark.ProcessingContextBuilder;
import org.eclipse.mylyn.internal.wikitext.commonmark.SimpleLocator;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.Locator;

import com.google.common.base.Optional;

public abstract class Inline {

	private final Line line;

	private final int offset;

	private final int length;

	public Inline(Line line, int offset, int length) {
		this.line = checkNotNull(line);
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
		return Optional.absent();
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
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Inline other = (Inline) obj;
		return other.offset == offset && other.length == length;
	}

	@Override
	public String toString() {
		return com.google.common.base.Objects.toStringHelper(getClass())
				.add("offset", getOffset())
				.add("length", getLength())
				.toString();
	}
}
