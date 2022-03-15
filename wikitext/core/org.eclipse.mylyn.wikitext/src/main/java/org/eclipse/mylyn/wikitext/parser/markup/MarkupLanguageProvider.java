/*******************************************************************************
 * Copyright (c) 2013, 2022 Tasktop Technologies and others.
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

package org.eclipse.mylyn.wikitext.parser.markup;

import static com.google.common.base.Preconditions.checkState;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.eclipse.mylyn.wikitext.util.ServiceLocator;

/**
 * A provider of {@link MarkupLanguage}.
 *
 * @see ServiceLocator
 * @since 3.0
 */
public abstract class MarkupLanguageProvider {

	/**
	 * Provides all {@link MarkupLanguage markup languages} supported by this provider.
	 *
	 * @return the markup languages, or an empty set if there are none
	 */
	public final Set<MarkupLanguage> getMarkupLanguages() {
		Set<MarkupLanguage> languages = Set
				.copyOf(Objects.requireNonNull(loadMarkupLanguages(), "loadMarkupLanguages() must not return null")); //$NON-NLS-1$
		assertLanguageNames(languages);
		return languages;
	}

	private void assertLanguageNames(Set<MarkupLanguage> languages) {
		Set<String> names = new HashSet<>();
		for (MarkupLanguage language : languages) {
			Objects.requireNonNull(language.getName(), "Provided languages must have a name"); //$NON-NLS-1$
			checkState(names.add(language.getName()), "Language name '%s' must not be provided more than once", //$NON-NLS-1$
					language.getName());
		}
	}

	protected abstract Set<MarkupLanguage> loadMarkupLanguages();

}
