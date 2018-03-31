/*******************************************************************************
 * Copyright (c) 2007, 2011 David Green and others.
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
package org.eclipse.mylyn.internal.wikitext.ui.editor.syntax;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;

/**
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
		return String.format("<%s offset=\"%s\" length=\"%s\"/>", type.name(), getOffset(), getLength()); //$NON-NLS-1$
	}
}
