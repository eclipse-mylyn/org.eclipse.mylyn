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

import java.util.List;
import java.util.Objects;

import org.eclipse.mylyn.internal.wikitext.commonmark.Line;

import com.google.common.collect.ImmutableList;

public abstract class InlineWithNestedContents extends Inline {

	private final List<Inline> contents;

	public InlineWithNestedContents(Line line, int offset, int length, List<Inline> contents) {
		super(line, offset, length);
		this.contents = ImmutableList.copyOf(contents);
	}

	public List<Inline> getContents() {
		return contents;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getOffset(), getLength(), getContents());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		InlineWithNestedContents other = (InlineWithNestedContents) obj;
		return getContents().equals(other.getContents());
	}

	@Override
	public String toString() {
		return com.google.common.base.Objects.toStringHelper(getClass())
				.add("offset", getOffset())
				.add("length", getLength())
				.add("contents", getContents())
				.toString();
	}
}
