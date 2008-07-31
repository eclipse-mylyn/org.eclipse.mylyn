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

import org.eclipse.mylyn.wikitext.core.WikiTextPlugin;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.osgi.framework.Bundle;

/**
 * A handle to help content.
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
	 * @param provider the bundle that provides the content
	 * @param resourcePath a bundle-relative path to the content
	 * @param resourceContentLanguage the markup language of the content, or null if it is HTML
	 * @param markupLanguage the markup language name for which this help content is relevant
	 */
	public HelpContent(Bundle provider,String resourcePath,String resourceContentLanguage,String markupLanguage) {
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
	 * Get the help content, which may be formatted using HTML markup.
	 * If HTML markup is used, the content must be well-formed HTML.
	 */
	@SuppressWarnings("serial")
	public String getContent() throws IOException {
		try {
			String content = null;
			URL resource = provider.getResource(resourcePath);
			if (resource == null) {
				throw new Exception(String.format("Cannot find resource '%s' in plugin '%s'",resourcePath,provider.getSymbolicName()));
			}
			Reader reader = new InputStreamReader(new BufferedInputStream(resource.openStream()));
			try {
				StringBuilder buf = new StringBuilder();
				int i;
				while ((i = reader.read()) != -1) {
					buf.append((char)i);
				}
				content = buf.toString();
			} finally {
				reader.close();
			}
			if (resourceContentLanguage == null || "html".equalsIgnoreCase(resourceContentLanguage)) {
				return content;
			}
			MarkupLanguage markupLanguage = WikiTextPlugin.getDefault().getMarkupLanguage(resourceContentLanguage);
			if (markupLanguage == null) {
				throw new IOException(String.format("No such markup language: %s",resourceContentLanguage));
			}
			MarkupParser markupParser = new MarkupParser(markupLanguage);
			return markupParser.parseToHtml(content);
		} catch (final Exception e) {
			throw new IOException(String.format("Cannot access content %s/%s: %s",provider.getSymbolicName(),resourcePath,e.getMessage())) {
				@Override
				public Throwable getCause() {
					return e;
				}
			};
		}
	}
}
