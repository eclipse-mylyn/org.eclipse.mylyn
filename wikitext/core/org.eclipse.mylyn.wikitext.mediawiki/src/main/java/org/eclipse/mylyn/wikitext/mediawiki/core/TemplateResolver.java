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
 * Dynamically resolve templates by name.
 * 
 * @since 1.3
 */
public abstract class TemplateResolver {

	/**
	 * Resolve a template by its name.
	 * 
	 * @param templateName
	 *            the name of the template
	 * @return the template, or null if the template name is unknown.
	 */
	public abstract Template resolveTemplate(String templateName);
}
