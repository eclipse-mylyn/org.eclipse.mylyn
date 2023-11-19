/*******************************************************************************
 * Copyright (c) 2014 Tasktop Technologies and others.
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

import static java.util.Objects.requireNonNull;

import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;

public class SpanHtmlElementStrategy extends HtmlElementStrategy<SpanType> {

	private final SpanStrategy spanStrategy;

	protected SpanHtmlElementStrategy(ElementMatcher<SpanType> matcher, SpanStrategy spanStrategy) {
		super(matcher);
		this.spanStrategy = requireNonNull(spanStrategy);
	}

	public SpanStrategy spanStrategy() {
		return spanStrategy;
	}

}
