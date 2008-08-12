/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.ui.editor.syntax;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;

/**
 * 
 * 
 * @author David Green
 */
public class Span extends Segment<Span> {

	private final SpanType type;

	public Span(SpanType type, int offset, int length) {
		super(offset, length);
		this.type = type;
	}

	public Span(SpanType type, Attributes attributes, int offset, int length) {
		super(attributes, offset, length);
		this.type = type;
	}

	public SpanType getType() {
		return type;
	}

	@Override
	public String toString() {
		return String.format("<%s offset=\"%s\" length=\"%s\"/>", type.name(), getOffset(), getLength());
	}
}
