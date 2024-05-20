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
import org.eclipse.mylyn.wikitext.commonmark.internal.ToStringHelper;
import org.eclipse.mylyn.wikitext.internal.util.WikiToStringStyle;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.ImageAttributes;

public class Image extends InlineWithNestedContents {

	private final String src;

	private final String title;

	public Image(Line line, int offset, int length, String src, String title, List<Inline> contents) {
		super(line, offset, length, contents);
		this.src = Objects.requireNonNull(src);
		this.title = title;
	}

	public String getHref() {
		return src;
	}

	@Override
	public void emit(DocumentBuilder builder) {
		ImageAttributes attributes = new ImageAttributes();
		attributes.setTitle(title);

		List<Inline> contents = getContents();
		if (!contents.isEmpty()) {
			attributes.setAlt(InlineParser.toStringContent(contents));
		}

		builder.image(attributes, src);
	}

	@Override
	public int hashCode() {
		return Objects.hash(getOffset(), getLength(), getContents(), src, title);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		Image other = (Image) obj;
		return src.equals(other.src) && getContents().equals(other.getContents()) && Objects.equals(title, other.title);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, WikiToStringStyle.WIKI_TO_STRING_STYLE) //
				.append("offset", getOffset()) //$NON-NLS-1$
				.append("length", getLength()) //$NON-NLS-1$
				.append("src", ToStringHelper.toStringValue(src)) //$NON-NLS-1$
				.append("title", title) //$NON-NLS-1$
				.append("contents", getContents()) //$NON-NLS-1$
				.toString();
	}
}
