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
package org.eclipse.mylyn.internal.wikitext.mediawiki.core.phrase;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;

/**
 *
 *
 * @author David Green
 */
public class SimplePhraseModifier extends SimpleWrappedPhraseModifier {

	public SimplePhraseModifier(String delimiter, SpanType spanType) {
		super(delimiter,delimiter,new SpanType[] { spanType });
	}

	public SimplePhraseModifier(String delimiter, SpanType spanType,boolean nesting) {
		super(delimiter,delimiter,new SpanType[] { spanType },nesting);
	}

	public SimplePhraseModifier(String delimiter, SpanType[] spanType) {
		super(delimiter,delimiter,spanType);
	}

	public SimplePhraseModifier(String delimiter, SpanType[] spanType,boolean nesting) {
		super(delimiter,delimiter,spanType,nesting);
	}
}
