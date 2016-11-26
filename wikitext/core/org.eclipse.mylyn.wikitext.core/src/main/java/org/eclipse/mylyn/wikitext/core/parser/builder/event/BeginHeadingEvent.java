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

import com.google.common.base.Objects;

/**
 * An {@link DocumentBuilderEvent} corresponding to {@link DocumentBuilder#beginHeading(int, Attributes)}.
 * 
 * @author david.green
 * @since 2.0
 */
public class BeginHeadingEvent extends DocumentBuilderEvent {

	private final int level;

	private final Attributes attributes;

	public BeginHeadingEvent(int level, Attributes attributes) {
		this.level = level;
		this.attributes = checkNotNull(attributes, "Must provide attributes").clone(); //$NON-NLS-1$
	}

	@Override
	public void invoke(DocumentBuilder builder) {
		builder.beginHeading(level, attributes);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(level);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof BeginHeadingEvent)) {
			return false;
		}
		BeginHeadingEvent other = (BeginHeadingEvent) obj;
		return other.level == level;
	}

	@Override
	public String toString() {
		return String.format("beginHeading(%s)", level); //$NON-NLS-1$
	}

}
