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

public class HtmlElementStrategy<ElementType extends Enum<ElementType>> {

	private final ElementMatcher<ElementType> matcher;

	protected HtmlElementStrategy(ElementMatcher<ElementType> matcher) {
		this.matcher = checkNotNull(matcher);
	}

	public ElementMatcher<ElementType> matcher() {
		return matcher;
	}

}
