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
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.parser.LinkAttributes;

public class Link extends InlineWithNestedContents {

	private final String href;

	private final String title;

	public Link(Line line, int offset, int length, String href, String title, List<Inline> contents) {
		super(line, offset, length, contents);
		this.href = Objects.requireNonNull(href);
		this.title = title;
	}

	public String getHref() {
		return href;
	}

	@Override
	public void emit(DocumentBuilder builder) {
		LinkAttributes attributes = new LinkAttributes();
		attributes.setTitle(title);
		attributes.setHref(href);
		builder.beginSpan(SpanType.LINK, attributes);

		InlineParser.emit(builder, getContents());

		builder.endSpan();
	}

	@Override
	public int hashCode() {
		return Objects.hash(getOffset(), getLength(), getContents(), href, title);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		Link other = (Link) obj;
		return href.equals(other.href) && getContents().equals(other.getContents())
				&& Objects.equals(title, other.title);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, WikiToStringStyle.WIKI_TO_STRING_STYLE) //
				.append("offset", getOffset()) //$NON-NLS-1$
				.append("length", getLength()) //$NON-NLS-1$
				.append("href", ToStringHelper.toStringValue(href)) //$NON-NLS-1$
				.append("title", title) //$NON-NLS-1$
				.append("contents", getContents()) //$NON-NLS-1$
				.toString();
	}
}
