/*******************************************************************************
 * Copyright (c) 2010 David Green and others.
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
 * Dynamically resolve templates by name.
 *
 * @since 3.0
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
