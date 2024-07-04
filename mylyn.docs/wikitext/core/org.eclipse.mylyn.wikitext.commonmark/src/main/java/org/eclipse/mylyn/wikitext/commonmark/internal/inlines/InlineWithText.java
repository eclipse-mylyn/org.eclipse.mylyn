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

import java.util.Objects;

import org.eclipse.mylyn.wikitext.commonmark.internal.Line;

abstract class InlineWithText extends Inline {

	protected final String text;

	public InlineWithText(Line line, int offset, int length, String text) {
		super(line, offset, length);
		this.text = Objects.requireNonNull(text);
	}

	public String getText() {
		return text;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getOffset(), getLength(), text);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		InlineWithText other = (InlineWithText) obj;
		return text.equals(other.text);
	}

	@SuppressWarnings("nls")
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("InlineWithText{offset=")
		.append(getOffset())
		.append(", length=")
		.append(getLength())
		.append(", text=")
		.append(text)
		.append("}");
		return builder.toString();
	}
}
