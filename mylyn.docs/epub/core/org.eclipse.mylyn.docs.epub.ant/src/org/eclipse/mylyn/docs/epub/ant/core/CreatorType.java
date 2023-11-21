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
 * @ant.type name="creator" category="epub"
 */
public class CreatorType {

	public String fileAs;

	public String id;

	public Locale lang;

	public String name;

	public String role;

	/**
	 * @ant.not-required
	 */
	public void setFileAs(String fileAs) {
		this.fileAs = fileAs;
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

	/**
	 * @ant.required
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @ant.not-required
	 */
	public void setRole(String role) {
		this.role = role;
	}
}
