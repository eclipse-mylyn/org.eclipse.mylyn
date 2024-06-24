/*******************************************************************************
 * Copyright (c) 2013, 2024 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.parser.tests;

import java.util.Objects;

import org.eclipse.mylyn.wikitext.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;

@SuppressWarnings("restriction")
public class MockMarkupLanguage extends MarkupLanguage {

	public MockMarkupLanguage() {
		this(MockMarkupLanguage.class.getSimpleName());
	}

	public MockMarkupLanguage(String name) {
		setName(Objects.requireNonNull(name));
	}

	@Override
	public void processContent(MarkupParser parser, String markupContent, boolean asDocument) {
		throw new UnsupportedOperationException();
	}

	public static class MockMarkupLanguage2 extends MockMarkupLanguage {
		public MockMarkupLanguage2(String name) {
			super(name);
		}
	}
}
