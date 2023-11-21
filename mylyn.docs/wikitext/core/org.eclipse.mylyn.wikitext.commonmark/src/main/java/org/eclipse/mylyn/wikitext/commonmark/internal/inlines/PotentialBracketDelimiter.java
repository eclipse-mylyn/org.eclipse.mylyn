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

import org.eclipse.mylyn.wikitext.commonmark.internal.Line;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;

public class PotentialBracketDelimiter extends InlineWithText {

	public PotentialBracketDelimiter(Line line, int offset, int length, String text) {
		super(line, offset, length, text);
	}

	boolean isImageDelimiter() {
		return text.charAt(0) == '!';
	}

	boolean isLinkDelimiter() {
		return text.charAt(0) == '[';
	}

	@Override
	public void emit(DocumentBuilder builder) {
		builder.characters(text);
	}

}
