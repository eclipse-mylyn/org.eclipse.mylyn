/*******************************************************************************
 * Copyright (c) 2013 David Green and others.
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

package org.eclipse.mylyn.wikitext.core.osgi;

import static com.google.common.base.Preconditions.checkArgument;

import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;

import com.google.common.base.Strings;

@SuppressWarnings("restriction")
public class MockMarkupLanguage extends MarkupLanguage {
	public MockMarkupLanguage(String name) {
		checkArgument(!Strings.isNullOrEmpty(name));
		setName(name);
	}

	public MockMarkupLanguage() {
		this(MockMarkupLanguage.class.getSimpleName());
	}

	@Override
	public void processContent(MarkupParser parser, String markupContent, boolean asDocument) {
		throw new UnsupportedOperationException();
	}
}
