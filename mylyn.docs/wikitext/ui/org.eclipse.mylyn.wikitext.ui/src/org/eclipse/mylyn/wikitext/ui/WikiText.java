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

package org.eclipse.mylyn.wikitext.ui;

import java.util.Set;

import org.eclipse.mylyn.internal.wikitext.ui.registry.WikiTextExtensionPointReader;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.util.ServiceLocator;
import org.eclipse.mylyn.wikitext.validation.MarkupValidator;

/**
 * A utility class for accessing markup languages and validation. Use only in an Eclipse runtime environment. Programs should use the
 * {@link ServiceLocator} instead of this class if possible. Stand-alone programs (that is, those programs that do not run in an Eclipse
 * runtime) must not use this class.
 *
 * @see ServiceLocator
 * @noextend This class is not intended to be subclassed by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 * @author David Green
 * @since 2.0
 */
public class WikiText {
	/**
	 * the {@link org.eclipse.core.runtime.content.IContentType#getId() content type id} of wikitext files.
	 */
	public static final String CONTENT_TYPE = "org.eclipse.mylyn.wikitext"; //$NON-NLS-1$

	private WikiText() { // prevent instantiation and subclassing
	}

	/**
	 * Get the file extensions that are registered for markup languages. File extensions are specified without the leading dot.
	 */
	public static Set<String> getMarkupFileExtensions() {
		return WikiTextExtensionPointReader.instance().getMarkupFileExtensions();
	}

	/**
	 * Get a markup language by name.
	 *
	 * @param name
	 *            the name of the markup language to retrieve
	 * @return the markup language or null if there is no markup language known by the given name
	 * @see #getMarkupLanguageNames()
	 * @since 3.0
	 */
	public static MarkupLanguage getMarkupLanguage(String name) {
		return WikiTextExtensionPointReader.instance().getMarkupLanguage(name);
	}

	/**
	 * Get a markup language for a file. A markup language is selected based on the registered languages and their expected file extensions.
	 *
	 * @param name
	 *            the name of the file for which a markup language is desired
	 * @return the markup language, or null if no markup language is registered for the specified file name
	 * @see #getMarkupLanguageForFilename(String)
	 * @since 3.0
	 */
	public static MarkupLanguage getMarkupLanguageForFilename(String name) {
		return WikiTextExtensionPointReader.instance().getMarkupLanguageForFilename(name);
	}

	/**
	 * Get a markup language name for a file. A markup language is selected based on the registered languages and their expected file
	 * extensions.
	 *
	 * @param name
	 *            the name of the file for which a markup language is desired
	 * @return the markup language name, or null if no markup language is registered for the specified file name
	 * @see #getMarkupLanguageForFilename(String)
	 */
	public static String getMarkupLanguageNameForFilename(String name) {
		return WikiTextExtensionPointReader.instance().getMarkupLanguageNameForFilename(name);
	}

	/**
	 * Get the names of all known markup languages
	 *
	 * @see #getMarkupLanguage(String)
	 */
	public static Set<String> getMarkupLanguageNames() {
		return WikiTextExtensionPointReader.instance().getMarkupLanguageNames();
	}

	/**
	 * Get a markup validator by language name.
	 *
	 * @param name
	 *            the name of the markup language for which a validator is desired
	 * @return the markup validator
	 * @see #getMarkupLanguageNames()
	 * @since 3.0
	 */
	public static MarkupValidator getMarkupValidator(String name) {
		return WikiTextExtensionPointReader.instance().getMarkupValidator(name);
	}

}
