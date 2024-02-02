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

/**
 * An {@link DocumentBuilderEvent} corresponding to {@link DocumentBuilder#beginHeading(int, Attributes)}.
 *
 * @author david.green
 * @since 3.0
 */
public class BeginHeadingEvent extends DocumentBuilderEvent {

	private final int level;

	private final Attributes attributes;

	public BeginHeadingEvent(int level, Attributes attributes) {
		this.level = level;
		this.attributes = Objects.requireNonNull(attributes, "Must provide attributes").clone(); //$NON-NLS-1$
	}

	@Override
	public void invoke(DocumentBuilder builder) {
		builder.beginHeading(level, attributes);
	}

	@Override
	public int hashCode() {
		return Objects.hash(level);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || !(obj instanceof BeginHeadingEvent other)) {
			return false;
		}
		return other.level == level;
	}

	@Override
	public String toString() {
		return String.format("beginHeading(%s)", level); //$NON-NLS-1$
	}

}
