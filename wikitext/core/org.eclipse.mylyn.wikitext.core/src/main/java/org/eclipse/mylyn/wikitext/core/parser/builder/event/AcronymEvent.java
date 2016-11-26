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

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;

import com.google.common.base.Objects;

/**
 * An {@link DocumentBuilderEvent} corresponding to {@link DocumentBuilder#acronym(String, String)}.
 * 
 * @author david.green
 * @since 2.0
 */
public class AcronymEvent extends DocumentBuilderEvent {

	private final String text;

	private final String definition;

	public AcronymEvent(String text, String definition) {
		this.text = checkNotNull(text, "Must provide text"); //$NON-NLS-1$
		this.definition = checkNotNull(definition, "Must provide definition"); //$NON-NLS-1$
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
		return Objects.hashCode(text, definition);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof AcronymEvent)) {
			return false;
		}
		AcronymEvent other = (AcronymEvent) obj;
		return Objects.equal(text, other.text) && Objects.equal(definition, other.definition);
	}

}
