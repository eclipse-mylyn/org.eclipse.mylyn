/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.core.util;

import java.text.MessageFormat;
import java.util.TreeSet;

import org.eclipse.mylyn.internal.wikitext.core.WikiTextPlugin;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.core.util.ServiceLocator;

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
		MarkupLanguage markupLanguage = WikiTextPlugin.getDefault().getMarkupLanguage(languageName);
		if (markupLanguage == null) {
			try {
				// dispatch to super in case we've been given a fully qualified class name
				markupLanguage = super.getMarkupLanguage(languageName);
			} catch (IllegalArgumentException e) {
				// specified language not found.
				// create a useful error message
				StringBuilder buf = new StringBuilder();
				for (String name : new TreeSet<String>(WikiTextPlugin.getDefault().getMarkupLanguageNames())) {
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

}
