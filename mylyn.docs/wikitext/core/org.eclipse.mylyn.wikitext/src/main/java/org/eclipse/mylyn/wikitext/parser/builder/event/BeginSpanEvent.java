/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
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

package org.eclipse.mylyn.wikitext.parser.builder.event;

import java.util.Objects;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;

/**
 * An {@link DocumentBuilderEvent} corresponding to {@link DocumentBuilder#beginSpan(SpanType, Attributes)}.
 *
 * @author david.green
 * @since 3.0
 */
public class BeginSpanEvent extends DocumentBuilderEvent {

	private final SpanType type;

	private final Attributes attributes;

	public BeginSpanEvent(SpanType type, Attributes attributes) {
		this.type = Objects.requireNonNull(type, "Must provide type"); //$NON-NLS-1$
		this.attributes = Objects.requireNonNull(attributes, "Must provide attributes").clone(); //$NON-NLS-1$
	}

	@Override
	public void invoke(DocumentBuilder builder) {
		builder.beginSpan(type, attributes);
	}

	@Override
	public int hashCode() {
		return Objects.hash(type);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof BeginSpanEvent)) {
			return false;
		}
		BeginSpanEvent other = (BeginSpanEvent) obj;
		return type == other.type;
	}

	@Override
	public String toString() {
		return String.format("beginSpan(%s)", type); //$NON-NLS-1$
	}
}
