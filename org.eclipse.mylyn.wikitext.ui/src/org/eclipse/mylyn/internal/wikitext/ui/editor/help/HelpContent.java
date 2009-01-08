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
package org.eclipse.mylyn.internal.wikitext.ui.editor.help;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.mylyn.wikitext.core.WikiText;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.osgi.framework.Bundle;

/**
 * A handle to help content. HelpContent is retrieved from a resource path from a bundle. Help content is retrieved in a
 * locale-specific manner, much in the same way as resource bundles are. A resource path is used with the
 * {@link Locale#getDefault() default locale} to construct a search path.
 * 
 * For example, the resource may be specified as <code>help/cheatSheet.textile</code> and for the locale
 * <tt>no_NO_NY</tt> the following resource paths would be searched in the following order:
 * 
 * <ul>
 * <li><code>help/cheatSheet_no_NO_NY.textile</code></li>
 * <li><code>help/cheatSheet_no_NO.textile</code></li>
 * <li><code>help/cheatSheet_no.textile</code></li>
 * <li><code>help/cheatSheet.textile</code></li>
 * </ul>
 * 
 * In this way the user is presented with the most locale-specific help resource.
 * 
 * @author David Green
 */
public class HelpContent {
	private final Bundle provider;

	private final String resourcePath;

	private final String resourceContentLanguage;

	private final String markupLanguageName;

	/**
	 * 
	 * @param provider
	 *            the bundle that provides the content
	 * @param resourcePath
	 *            a bundle-relative path to the content
	 * @param resourceContentLanguage
	 *            the markup language of the content, or null if it is HTML
	 * @param markupLanguage
	 *            the markup language name for which this help content is relevant
	 */
	public HelpContent(Bundle provider, String resourcePath, String resourceContentLanguage, String markupLanguage) {
		if (provider == null || resourcePath == null || markupLanguage == null) {
			throw new IllegalArgumentException();
		}
		this.provider = provider;
		this.resourcePath = resourcePath;
		this.resourceContentLanguage = resourceContentLanguage;
		this.markupLanguageName = markupLanguage;
	}

	/**
	 * The name of the markup language for which this help content is relevant
	 */
	public String getMarkupLanguageName() {
		return markupLanguageName;
	}

	/**
	 * the bundle that provides the cheat-sheet
	 */
	public Bundle getProvider() {
		return provider;
	}

	/**
	 * Get the help content, which may be formatted using HTML markup. If HTML markup is used, the content must be
	 * well-formed HTML.
	 */
	@SuppressWarnings("serial")
	public String getContent() throws IOException {
		try {
			String content = null;
			URL resource = getResource();
			Reader reader = new InputStreamReader(new BufferedInputStream(resource.openStream()));
			try {
				StringBuilder buf = new StringBuilder();
				int i;
				while ((i = reader.read()) != -1) {
					buf.append((char) i);
				}
				content = buf.toString();
			} finally {
				reader.close();
			}
			if (resourceContentLanguage == null || "html".equalsIgnoreCase(resourceContentLanguage)) { //$NON-NLS-1$
				return content;
			}
			MarkupLanguage markupLanguage = WikiText.getMarkupLanguage(resourceContentLanguage);
			if (markupLanguage == null) {
				throw new IOException(
						MessageFormat.format(Messages.getString("HelpContent.0"), resourceContentLanguage)); //$NON-NLS-1$
			}
			MarkupParser markupParser = new MarkupParser(markupLanguage);
			return markupParser.parseToHtml(content);
		} catch (final Exception e) {
			throw new IOException(MessageFormat.format(Messages.getString("HelpContent.1"), provider.getSymbolicName(), //$NON-NLS-1$
					resourcePath, e.getMessage())) {
				@Override
				public Throwable getCause() {
					return e;
				}
			};
		}
	}

	private URL getResource() throws Exception {
		int idx = resourcePath.lastIndexOf('.');
		List<String> paths = new ArrayList<String>();
		if (idx != -1) {

			// construct a search path based on the users locale
			String basePath = resourcePath.substring(0, idx);
			String extension = resourcePath.substring(idx + 1);

			Locale locale = Locale.getDefault();
			String language = locale.getLanguage();
			String country = locale.getCountry();
			String variant = locale.getVariant();

			if (variant.length() > 0) {
				paths.add(basePath + "_" + language + "_" + country + "_" + variant + "." + extension); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			}
			if (country.length() > 0) {
				paths.add(basePath + "_" + language + "_" + country + "." + extension); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			if (language.length() > 0) {
				paths.add(basePath + "_" + language + "." + extension); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		paths.add(resourcePath);

		for (String path : paths) {
			URL resource = provider.getResource(path);
			if (resource != null) {
				return resource;
			}
		}
		throw new Exception(MessageFormat.format(Messages.getString("HelpContent.11"), resourcePath, //$NON-NLS-1$
				provider.getSymbolicName()));
	}
}
