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
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.commonmark.internal.inlines;

import static com.google.common.base.MoreObjects.toStringHelper;
import static java.util.Objects.requireNonNull;

import java.util.Objects;

import org.eclipse.mylyn.wikitext.commonmark.internal.Line;
import org.eclipse.mylyn.wikitext.commonmark.internal.ProcessingContextBuilder;
import org.eclipse.mylyn.wikitext.commonmark.internal.ToStringHelper;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;

public class ReferenceDefinition extends Inline {

	private final String href;

	private final String title;

	private final String name;

	public ReferenceDefinition(Line line, int offset, int length, String href, String title, String name) {
		super(line, offset, length);
		this.href = requireNonNull(href);
		this.name = requireNonNull(name);
		this.title = title;
	}

	public String getHref() {
		return href;
	}

	@Override
	public void emit(DocumentBuilder builder) {
		// nothing to do
	}

	@Override
	public void createContext(ProcessingContextBuilder contextBuilder) {
		contextBuilder.referenceDefinition(name, href, title);
	}

	@Override
	public int hashCode() {
		return Objects.hash(getOffset(), getLength(), name, href, title);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		ReferenceDefinition other = (ReferenceDefinition) obj;
		return href.equals(other.href) && name.equals(other.name) && Objects.equals(title, other.title);
	}

	@Override
	public String toString() {
		return toStringHelper(ReferenceDefinition.class).add("offset", getOffset()) //$NON-NLS-1$
				.add("length", getLength()) //$NON-NLS-1$
				.add("name", name) //$NON-NLS-1$
				.add("href", ToStringHelper.toStringValue(href)) //$NON-NLS-1$
				.add("title", title) //$NON-NLS-1$
				.toString();
	}
}
