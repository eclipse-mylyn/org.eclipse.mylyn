/*******************************************************************************
 * Copyright (c) 2011, 2012 Torkild U. Resheim.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Torkild U. Resheim - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.docs.epub.ant.core;

import java.util.Locale;

/**
 * @author Torkild U. Resheim
 * @ant.type name="source" category="epub"
 */
public class SourceType {

	public String id;

	public Locale lang;

	public String text;

	/**
	 * @ant.required
	 */
	public void addText(String text) {
		this.text = text;
	}

	/**
	 * @ant.not-required
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @ant.not-required
	 */
	public void setLang(Locale lang) {
		this.lang = lang;
	}

}
