/*******************************************************************************
 * Copyright (c) 2007, 2013 David Green and others.
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

package org.eclipse.mylyn.internal.wikitext;

import java.util.Set;

import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguageProvider;

public class MockMarkupLanguageProvider extends MarkupLanguageProvider {

	@Override
	protected Set<MarkupLanguage> loadMarkupLanguages() {
		return Set.of(new MockMarkupLanguage());
	}

}
