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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;

import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;

/**
 * An {@link DocumentBuilderEvent} corresponding to {@link DocumentBuilder#characters(String)}.
 *
 * @author david.green
 * @since 3.0
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
		return Objects.hash(text);
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
		return Objects.equals(other.text, text);
	}

	@Override
	public String toString() {
		return String.format("characters(\"%s\")", text); //$NON-NLS-1$
	}
}
