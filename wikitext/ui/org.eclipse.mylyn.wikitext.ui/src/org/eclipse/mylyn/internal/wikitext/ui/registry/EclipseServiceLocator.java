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
package org.eclipse.mylyn.internal.wikitext.ui.registry;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.util.ServiceLocator;

/**
 * A service locator that uses the {@link WikiTextPlugin} to resolve markup languages
 * 
 * @author David Green
 */
public class EclipseServiceLocator extends ServiceLocator {

	public EclipseServiceLocator(ClassLoader classLoader) {
		super(classLoader);
	}

	@Override
	public MarkupLanguage getMarkupLanguage(String languageName) throws IllegalArgumentException {
		if (languageName == null) {
			throw new IllegalArgumentException();
		}
		MarkupLanguage markupLanguage = WikiTextExtensionPointReader.instance().getMarkupLanguage(languageName);
		if (markupLanguage == null) {
			try {
				// dispatch to super in case we've been given a fully qualified class name
				markupLanguage = super.getMarkupLanguage(languageName);
			} catch (IllegalArgumentException e) {
				// specified language not found.
				// create a useful error message
				StringBuilder buf = new StringBuilder();
				for (String name : new TreeSet<>(WikiTextExtensionPointReader.instance().getMarkupLanguageNames())) {
					if (buf.length() != 0) {
						buf.append(", "); //$NON-NLS-1$
					}
					buf.append('\'');
					buf.append(name);
					buf.append('\'');
				}
				throw new IllegalArgumentException(MessageFormat.format(Messages.getString("EclipseServiceLocator.1"), //$NON-NLS-1$
						languageName, buf.length() == 0 ? Messages.getString("EclipseServiceLocator.2") //$NON-NLS-1$
								: MessageFormat.format(Messages.getString("EclipseServiceLocator.3"), buf))); //$NON-NLS-1$
			}
		}
		return markupLanguage;
	}

	@Override
	public Set<MarkupLanguage> getAllMarkupLanguages() {
		Set<MarkupLanguage> markupLanguages = new HashSet<>();

		for (String languageName : WikiTextExtensionPointReader.instance().getMarkupLanguageNames()) {
			MarkupLanguage markupLanguage = getMarkupLanguage(languageName);
			if (markupLanguage != null) {
				markupLanguages.add(markupLanguage);
			}
		}

		return markupLanguages;
	}
}
