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
import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;

public class Strong extends InlineWithNestedContents {

	public Strong(Line line, int offset, int length, List<Inline> contents) {
		super(line, offset, length, contents);
	}

	@Override
	public void emit(DocumentBuilder builder) {
		builder.beginSpan(SpanType.STRONG, new Attributes());
		InlineParser.emit(builder, getContents());
		builder.endSpan();
	}

}
