/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext;

import java.util.Set;

import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguageProvider;

import com.google.common.collect.ImmutableSet;

public class MockMarkupLanguageProvider extends MarkupLanguageProvider {

	@Override
	protected Set<MarkupLanguage> loadMarkupLanguages() {
		return ImmutableSet.<MarkupLanguage> of(new MockMarkupLanguage());
	}

}
