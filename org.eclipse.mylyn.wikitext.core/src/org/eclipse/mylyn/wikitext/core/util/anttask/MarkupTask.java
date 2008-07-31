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
package org.eclipse.mylyn.wikitext.core.util.anttask;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.core.util.ServiceLocator;

/**
 *
 *
 * @author David Green
 */
public abstract class MarkupTask extends Task {

	private String markupLanguage;

	/**
	 * The markup language to use.  Should correspond to a {@link MarkupLanguage#getName() markup language name}.
	 */
	public String getMarkupLanguage() {
		return markupLanguage;
	}

	/**
	 * The markup language to use.   Should correspond to a {@link MarkupLanguage#getName() markup language name}.
	 */
	public void setMarkupLanguage(String markupLanguage) {
		this.markupLanguage = markupLanguage;
	}

	/**
	 * Create a {@link MarkupLanguage markup language parser} for the {@link #getMarkupLanguage() specified markup language}.
	 * 
	 * @return the markup language
	 * 
	 * @throws BuildException if the markup language is not specified or if it is unknown.
	 */
	protected MarkupLanguage createMarkupLanguage() throws BuildException {
		if (markupLanguage == null) {
			throw new BuildException("Must specify @markupLanguage");
		}
		try {
			return ServiceLocator.getInstance(getClass().getClassLoader()).getMarkupLanguage(markupLanguage);
		} catch (IllegalArgumentException e) {
			throw new BuildException(e.getMessage(),e);
		}
	}
}
