/*******************************************************************************
 * Copyright (c) 2007, 2024 David Green and others.
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

package org.eclipse.mylyn.wikitext.parser.markup.tests;

import java.util.Set;

import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguageProvider;
import org.eclipse.mylyn.wikitext.parser.tests.MockMarkupLanguage;

@SuppressWarnings("restriction")
public class MockMarkupLanguageProvider extends MarkupLanguageProvider {

	@Override
	protected Set<MarkupLanguage> loadMarkupLanguages() {
		return Set.of(new MockMarkupLanguage());
	}

}
