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

import java.util.List;

import org.eclipse.mylyn.wikitext.commonmark.internal.Line;
import org.eclipse.mylyn.wikitext.commonmark.internal.ProcessingContext;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;

public class Characters extends InlineWithText {

	public Characters(Line line, int offset, int length, String text) {
		super(line, offset, length, text);
	}

	@Override
	public void emit(DocumentBuilder builder) {
		builder.characters(text);
	}

	@Override
	public void apply(ProcessingContext context, List<Inline> inlines, Cursor cursor) {
		cursor.advance(getLength());
		if (!inlines.isEmpty()) {
			Inline last = inlines.get(inlines.size() - 1);
			if (last instanceof Characters) {
				Characters lastCharacters = (Characters) last;
				int newLength = lastCharacters.getLength() + getLength();
				String newText = lastCharacters.getText() + getText();
				Characters substitution = new Characters(lastCharacters.getLine(), lastCharacters.getOffset(),
						newLength, newText);
				inlines.set(inlines.size() - 1, substitution);
				return;
			}
		}
		inlines.add(this);
	}
}
