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

/**
 * @author Torkild U. Resheim
 * @ant.type name="language" category="epub"
 */
public class LanguageType {
	String code;

	String id;

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @ant.required
	 */
	public void setCode(String code) {
		this.code = code;
	}
}
