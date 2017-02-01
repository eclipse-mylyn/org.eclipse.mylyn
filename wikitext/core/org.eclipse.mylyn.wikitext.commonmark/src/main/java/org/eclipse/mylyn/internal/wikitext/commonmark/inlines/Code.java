/*******************************************************************************
 * Copyright (c) 2015 David Green.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.commonmark.inlines;

import org.eclipse.mylyn.internal.wikitext.commonmark.Line;
import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;

public class Code extends InlineWithText {

	public Code(Line line, int offset, int delimiterSize, String text) {
		super(line, offset, text.length() + (2 * delimiterSize), text);
	}

	@Override
	public void emit(DocumentBuilder builder) {
		builder.beginSpan(SpanType.CODE, new Attributes());
		builder.characters(text.trim());
		builder.endSpan();
	}

}
