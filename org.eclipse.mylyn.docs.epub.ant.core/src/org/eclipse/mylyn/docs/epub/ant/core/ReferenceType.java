/*******************************************************************************
 * Copyright (c) 2011, 2012 Torkild U. Resheim.
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
 * @ant.type name="reference" category="epub"
 */
public class ReferenceType {

	String href;

	String title;

	String type;

	/**
	 * @ant.required
	 */
	public void setHref(String href) {
		this.href = href;
	}

	/**
	 * @ant.required
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @ant.required
	 */
	public void setType(String type) {
		this.type = type;
	}

}
