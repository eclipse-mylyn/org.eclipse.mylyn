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
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.html.internal;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;

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
		for (SpanStrategy strategy : IntStream.range(0, delegates.size())
				.map(i -> delegates.size() - 1 - i)
				.mapToObj(delegates::get)
				.collect(Collectors.toList())) {
			strategy.endSpan(builder);
		}
	}

}
