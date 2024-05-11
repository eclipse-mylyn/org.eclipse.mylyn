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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.eclipse.mylyn.wikitext.commonmark.internal.Line;

public abstract class InlineWithNestedContents extends Inline {

	private final List<Inline> contents;

	public InlineWithNestedContents(Line line, int offset, int length, List<Inline> contents) {
		super(line, offset, length);
		this.contents = List.copyOf(contents);
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
		return new ToStringBuilder(getClass()).append("offset", getOffset()) //$NON-NLS-1$
				.append("length", getLength()) //$NON-NLS-1$
				.append("contents", getContents()) //$NON-NLS-1$
				.toString();
	}
}
