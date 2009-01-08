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

package org.eclipse.mylyn.wikitext.core;

import java.util.Set;

import org.eclipse.mylyn.internal.wikitext.core.WikiTextPlugin;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.core.util.ServiceLocator;
import org.eclipse.mylyn.wikitext.core.validation.MarkupValidator;

/**
 * A utility class for accessing markup languages and validation. Use only in an Eclipse runtime environment. Programs
 * should use the {@link ServiceLocator} instead of this class if possible. Stand-alone programs (that is, those
 * programs that do not run in an Eclipse runtime) must not use this class.
 * 
 * @noextend This class is not intended to be subclassed by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 * @author David Green
 */
public class WikiText {

	private WikiText() { // prevent instantiation and subclassing
	}

	/**
	 * Get the file extensions that are registered for markup languages. File extensions are specified without the
	 * leading dot.
	 */
	public static Set<String> getMarkupFileExtensions() {
		return WikiTextPlugin.getDefault().getMarkupFileExtensions();
	}

	/**
	 * Get a markup language by name.
	 * 
	 * @param name
	 *            the name of the markup language to retrieve
	 * 
	 * @return the markup language or null if there is no markup language known by the given name
	 * 
	 * @see #getMarkupLanguageNames()
	 */
	public static MarkupLanguage getMarkupLanguage(String name) {
		return WikiTextPlugin.getDefault().getMarkupLanguage(name);
	}

	/**
	 * Get a markup language for a file. A markup language is selected based on the registered languages and their
	 * expected file extensions.
	 * 
	 * @param name
	 *            the name of the file for which a markup language is desired
	 * 
	 * @return the markup language, or null if no markup language is registered for the specified file name
	 * 
	 * @see #getMarkupLanguageForFilename(String)
	 */
	public static MarkupLanguage getMarkupLanguageForFilename(String name) {
		return WikiTextPlugin.getDefault().getMarkupLanguageForFilename(name);
	}

	/**
	 * Get a markup language name for a file. A markup language is selected based on the registered languages and their
	 * expected file extensions.
	 * 
	 * @param name
	 *            the name of the file for which a markup language is desired
	 * 
	 * @return the markup language name, or null if no markup language is registered for the specified file name
	 * 
	 * @see #getMarkupLanguageForFilename(String)
	 */
	public static String getMarkupLanguageNameForFilename(String name) {
		return WikiTextPlugin.getDefault().getMarkupLanguageNameForFilename(name);
	}

	/**
	 * Get the names of all known markup languages
	 * 
	 * @see #getMarkupLanguage(String)
	 */
	public static Set<String> getMarkupLanguageNames() {
		return WikiTextPlugin.getDefault().getMarkupLanguageNames();
	}

	/**
	 * Get a markup validator by language name.
	 * 
	 * @param name
	 *            the name of the markup language for which a validator is desired
	 * 
	 * @return the markup validator
	 * 
	 * @see #getMarkupLanguageNames()
	 */
	public static MarkupValidator getMarkupValidator(String name) {
		return WikiTextPlugin.getDefault().getMarkupValidator(name);
	}

}
