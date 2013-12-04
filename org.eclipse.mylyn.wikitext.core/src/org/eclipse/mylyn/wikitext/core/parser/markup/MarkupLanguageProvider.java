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

import java.util.Set;

import org.eclipse.mylyn.wikitext.core.util.ServiceLocator;

import com.google.common.collect.ImmutableSet;

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
		return ImmutableSet.copyOf(checkNotNull(loadMarkupLanguages(), "loadMarkupLanguages() must not return null")); //$NON-NLS-1$
	}

	protected abstract Set<MarkupLanguage> loadMarkupLanguages();

}
