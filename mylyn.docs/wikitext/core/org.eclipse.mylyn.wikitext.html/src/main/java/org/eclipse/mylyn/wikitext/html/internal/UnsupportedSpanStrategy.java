/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
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

package org.eclipse.mylyn.wikitext.html.internal;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;

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
