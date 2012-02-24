/*******************************************************************************
 * Copyright (c) 2011 Torkild U. Resheim.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Torkild U. Resheim - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.docs.epub.ant.core;

import java.util.Locale;

/**
 * 
 * @author Torkild U. Resheim
 * @ant.type name="creator" category="epub"
 */
public class CreatorType {

	String fileAs;

	String id;

	Locale lang;

	String name;

	String role;

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
