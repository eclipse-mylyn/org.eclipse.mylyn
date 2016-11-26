/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.core.parser.markup;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Set;

import org.eclipse.mylyn.wikitext.core.util.ServiceLocator;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * A provider of {@link MarkupLanguage}.
 * 
 * @since 2.0
 * @see ServiceLocator
 */
public abstract class MarkupLanguageProvider {

	/**
	 * Provides all {@link MarkupLanguage markup languages} supported by this provider.
	 * 
	 * @return the markup languages, or an empty set if there are none
	 */
	public final Set<MarkupLanguage> getMarkupLanguages() {
		Set<MarkupLanguage> languages = ImmutableSet.copyOf(checkNotNull(loadMarkupLanguages(),
				"loadMarkupLanguages() must not return null")); //$NON-NLS-1$
		assertLanguageNames(languages);
		return languages;
	}

	private void assertLanguageNames(Set<MarkupLanguage> languages) {
		Set<String> names = Sets.newHashSet();
		for (MarkupLanguage language : languages) {
			checkNotNull(language.getName(), "Provided languages must have a name"); //$NON-NLS-1$
			checkState(names.add(language.getName()),
					"Language name '%s' must not be provided more than once", language.getName()); //$NON-NLS-1$
		}
	}

	protected abstract Set<MarkupLanguage> loadMarkupLanguages();

}
