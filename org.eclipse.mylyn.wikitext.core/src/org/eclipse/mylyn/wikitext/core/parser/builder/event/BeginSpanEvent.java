/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.core.parser.builder.event;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;

import com.google.common.base.Objects;

/**
 * An {@link DocumentBuilderEvent} corresponding to {@link DocumentBuilder#beginSpan(SpanType, Attributes)}.
 * 
 * @author david.green
 * @since 2.0
 */
public class BeginSpanEvent extends DocumentBuilderEvent {

	private final SpanType type;

	private final Attributes attributes;

	public BeginSpanEvent(SpanType type, Attributes attributes) {
		this.type = checkNotNull(type, "Must provide type"); //$NON-NLS-1$
		this.attributes = checkNotNull(attributes, "Must provide attributes").clone(); //$NON-NLS-1$
	}

	@Override
	public void invoke(DocumentBuilder builder) {
		builder.beginSpan(type, attributes);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(type);
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
