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
 * @ant.type name="meta" category="epub"
 */
public class MetaType {

	String name;

	String content;

	/**
	 * @ant.required
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @ant.required
	 */
	public void setContent(String content) {
		this.content = content;
	}

}
