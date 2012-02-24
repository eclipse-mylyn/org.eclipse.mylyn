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
 * 
 * @author Torkild U. Resheim
 * @ant.type name="cover" category="epub"
 */
public class CoverType {

	String image;

	String value;

	/**
	 * @ant.required
	 */
	public void addText(String value) {
		this.value = value;
	}

	/**
	 * @ant.required
	 */
	public void setImage(String image) {
		this.image = image;
	}

}
