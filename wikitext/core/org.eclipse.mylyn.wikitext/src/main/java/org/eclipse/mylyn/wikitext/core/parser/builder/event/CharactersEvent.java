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
 * An {@link DocumentBuilderEvent} corresponding to {@link DocumentBuilder#characters(String)}.
 * 
 * @author david.green
 * @since 2.0
 */
public class CharactersEvent extends DocumentBuilderEvent {

	private final String text;

	public CharactersEvent(String text) {
		this.text = checkNotNull(text, "Must provide text"); //$NON-NLS-1$
	}

	@Override
	public void invoke(DocumentBuilder builder) {
		builder.characters(text);
	}

	/**
	 * Provides the text of this event.
	 * 
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(text);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof CharactersEvent)) {
			return false;
		}
		CharactersEvent other = (CharactersEvent) obj;
		return Objects.equal(other.text, text);
	}

	@Override
	public String toString() {
		return String.format("characters(\"%s\")", text); //$NON-NLS-1$
	}
}
