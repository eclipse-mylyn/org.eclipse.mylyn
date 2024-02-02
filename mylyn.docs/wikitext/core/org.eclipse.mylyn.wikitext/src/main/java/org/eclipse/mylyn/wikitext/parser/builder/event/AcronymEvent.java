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

import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;

/**
 * An {@link DocumentBuilderEvent} corresponding to {@link DocumentBuilder#acronym(String, String)}.
 *
 * @author david.green
 * @since 3.0
 */
public class AcronymEvent extends DocumentBuilderEvent {

	private final String text;

	private final String definition;

	public AcronymEvent(String text, String definition) {
		this.text = Objects.requireNonNull(text, "Must provide text"); //$NON-NLS-1$
		this.definition = Objects.requireNonNull(definition, "Must provide definition"); //$NON-NLS-1$
	}

	@Override
	public void invoke(DocumentBuilder builder) {
		builder.acronym(text, definition);
	}

	@Override
	public String toString() {
		return String.format("acronym(%s,%s)", text, definition); //$NON-NLS-1$
	}

	@Override
	public int hashCode() {
		return Objects.hash(text, definition);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof AcronymEvent other)) {
			return false;
		}
		return Objects.equals(text, other.text) && Objects.equals(definition, other.definition);
	}

}
