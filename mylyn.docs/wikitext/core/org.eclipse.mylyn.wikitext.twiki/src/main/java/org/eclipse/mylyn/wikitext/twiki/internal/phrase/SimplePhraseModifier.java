/*******************************************************************************
 * Copyright (c) 2007, 2011 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.twiki.internal.phrase;

import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;

/**
 * @author David Green
 */
public class SimplePhraseModifier extends SimpleWrappedPhraseModifier {

	public SimplePhraseModifier(String delimiter, SpanType spanType) {
		super(delimiter, delimiter, new SpanType[] { spanType });
	}

	public SimplePhraseModifier(String delimiter, SpanType spanType, boolean nesting) {
		super(delimiter, delimiter, new SpanType[] { spanType }, nesting);
	}

	public SimplePhraseModifier(String delimiter, SpanType[] spanType) {
		super(delimiter, delimiter, spanType);
	}

	public SimplePhraseModifier(String delimiter, SpanType[] spanType, boolean nesting) {
		super(delimiter, delimiter, spanType, nesting);
	}
}
