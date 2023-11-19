/*******************************************************************************
 * Copyright (c) 2013, 2014 Tasktop Technologies and others.
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

import java.util.List;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;

import com.google.common.collect.Lists;

class CompositeSpanStrategy implements SpanStrategy {

	private final List<SpanStrategy> delegates;

	CompositeSpanStrategy(List<SpanStrategy> delegates) {
		this.delegates = List.copyOf(delegates);
	}

	@Override
	public void beginSpan(DocumentBuilder builder, SpanType type, Attributes attributes) {
		for (SpanStrategy strategy : delegates) {
			strategy.beginSpan(builder, type, attributes);
		}
	}

	@Override
	public void endSpan(DocumentBuilder builder) {
		for (SpanStrategy strategy : Lists.reverse(delegates)) {
			strategy.endSpan(builder);
		}
	}

}
