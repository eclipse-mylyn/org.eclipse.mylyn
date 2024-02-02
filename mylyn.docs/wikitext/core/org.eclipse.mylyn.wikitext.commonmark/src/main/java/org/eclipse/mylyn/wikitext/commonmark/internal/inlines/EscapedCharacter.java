/*******************************************************************************
 * Copyright (c) 2015 David Green.
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

package org.eclipse.mylyn.wikitext.commonmark.internal.inlines;

import java.util.Objects;

import org.eclipse.mylyn.wikitext.commonmark.internal.Line;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;

public class EscapedCharacter extends Inline {

	private final char character;

	public EscapedCharacter(Line line, int offset, char c) {
		super(line, offset, 2);
		character = c;
	}

	@Override
	public void emit(DocumentBuilder builder) {
		builder.characters(Character.toString(character));
	}

	public char getCharacter() {
		return character;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getOffset(), getLength(), character);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		EscapedCharacter other = (EscapedCharacter) obj;
		return character == other.character;
	}

}
