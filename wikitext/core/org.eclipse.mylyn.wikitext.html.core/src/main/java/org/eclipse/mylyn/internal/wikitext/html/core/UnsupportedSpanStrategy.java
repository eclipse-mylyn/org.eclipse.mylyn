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

package org.eclipse.mylyn.internal.wikitext.html.core;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;

class UnsupportedSpanStrategy implements SpanStrategy {

	static final UnsupportedSpanStrategy instance = new UnsupportedSpanStrategy();

	@Override
	public void beginSpan(DocumentBuilder builder, SpanType type, Attributes attributes) {
		// nothing to do
	}

	@Override
	public void endSpan(DocumentBuilder builder) {
		// nothing to do		
	}

}
