/*******************************************************************************
 * Copyright (c) 2010, 2012 David Green and others.
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

package org.eclipse.mylyn.wikitext.mediawiki;

/**
 * A representation of <a href="http://www.mediawiki.org/wiki/Help:Templates">MediaWiki Templates</a>, which provide a
 * means to expand commonly-used content with token-replacement.
 *
 * @since 3.0
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
