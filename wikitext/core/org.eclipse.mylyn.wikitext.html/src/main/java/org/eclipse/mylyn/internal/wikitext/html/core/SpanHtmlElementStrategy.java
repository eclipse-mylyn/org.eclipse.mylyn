/*******************************************************************************
 * Copyright (c) 2014 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.html.core;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;

public class SpanHtmlElementStrategy extends HtmlElementStrategy<SpanType> {

	private final SpanStrategy spanStrategy;

	protected SpanHtmlElementStrategy(ElementMatcher<SpanType> matcher, SpanStrategy spanStrategy) {
		super(matcher);
		this.spanStrategy = checkNotNull(spanStrategy);
	}

	public SpanStrategy spanStrategy() {
		return spanStrategy;
	}

}
