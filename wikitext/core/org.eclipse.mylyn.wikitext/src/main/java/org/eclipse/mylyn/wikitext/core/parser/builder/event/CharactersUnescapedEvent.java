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
 * An {@link DocumentBuilderEvent} corresponding to {@link DocumentBuilder#charactersUnescaped(String)}.
 * 
 * @author david.green
 * @since 2.0
 */
public class CharactersUnescapedEvent extends DocumentBuilderEvent {

	private final String literal;

	public CharactersUnescapedEvent(String literal) {
		this.literal = checkNotNull(literal, "Must provide literal"); //$NON-NLS-1$
	}

	@Override
	public void invoke(DocumentBuilder builder) {
		builder.charactersUnescaped(literal);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(literal);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof CharactersUnescapedEvent)) {
			return false;
		}
		CharactersUnescapedEvent other = (CharactersUnescapedEvent) obj;
		return Objects.equal(other.literal, literal);
	}

	@Override
	public String toString() {
		return String.format("charactersUnescaped(\"%s\")", literal); //$NON-NLS-1$
	}

}
