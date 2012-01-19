/*******************************************************************************
 * Copyright (c) 2010, 2011 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.mediawiki.core;

/**
 * A representation of <a href="http://www.mediawiki.org/wiki/Help:Templates">MediaWiki Templates</a>, which provide a
 * means to expand commonly-used content with token-replacement.
 * 
 * @since 1.3
 */
public class Template {
	private String name;

	private String templateMarkup;

	public Template() {
	}

	/**
	 * @param name
	 *            the name of the template
	 * @param templateMarkup
	 *            the substitution content of the template
	 * @since 1.6
	 */
	public Template(String name, String templateMarkup) {
		super();
		this.name = name;
		this.templateMarkup = templateMarkup;
	}

	/**
	 * the substitution content of the template
	 */
	public String getTemplateMarkup() {
		return templateMarkup;
	}

	/**
	 * the substitution content of the template
	 */
	public void setTemplateMarkup(String templateMarkup) {
		this.templateMarkup = templateMarkup;
	}

	/**
	 * The content of the template as it should be included in the document. The default implementation simply returns
	 * {@link #getTemplateMarkup()}. Templates that produce dynamically generated content should override this method.
	 * 
	 * @since 1.6
	 */
	public String getTemplateContent() {
		return getTemplateMarkup();
	}

	/**
	 * the name of the template
	 */
	public String getName() {
		return name;
	}

	/**
	 * the name of the template
	 */
	public void setName(String name) {
		this.name = name;
	}
}
