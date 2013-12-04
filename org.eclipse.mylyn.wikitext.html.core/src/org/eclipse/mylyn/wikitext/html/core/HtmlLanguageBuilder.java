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

package org.eclipse.mylyn.wikitext.html.core;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.base.Strings;

/**
 * Provides a way to build HTML languages that support a specific set of HTML tags.
 * 
 * @author david.green
 * @see HtmlLanguage#builder()
 * @since 2.0
 */
public class HtmlLanguageBuilder {
	private String name;

	HtmlLanguageBuilder() {
		// prevent direct instantiation
	}

	public HtmlLanguageBuilder name(String name) {
		checkNotNull(name, "Must provide a name"); //$NON-NLS-1$
		checkArgument(!Strings.isNullOrEmpty(name), "Name must not be empty"); //$NON-NLS-1$
		checkArgument(!name.equalsIgnoreCase(HtmlLanguage.NAME_HTML), "Name must not be equal to %s", //$NON-NLS-1$
				HtmlLanguage.NAME_HTML);
		checkArgument(name.equals(name.trim()), "Name must not have leading or trailing whitespace"); //$NON-NLS-1$
		this.name = name;
		return this;
	}

	public HtmlLanguage create() {
		checkState(name != null, "Name must be provided to create an HtmlLanguage"); //$NON-NLS-1$
		HtmlLanguage htmlLanguage = new HtmlLanguage();
		htmlLanguage.setName(name);
		return htmlLanguage;
	}
}
