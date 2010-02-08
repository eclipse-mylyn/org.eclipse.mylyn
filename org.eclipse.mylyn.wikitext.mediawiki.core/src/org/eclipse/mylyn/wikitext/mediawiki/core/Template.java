/*******************************************************************************
 * Copyright (c) 2010 David Green and others.
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

	public String getTemplateMarkup() {
		return templateMarkup;
	}

	public void setTemplateMarkup(String templateMarkup) {
		this.templateMarkup = templateMarkup;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
