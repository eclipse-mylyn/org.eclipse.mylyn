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

/**
 * @author Torkild U. Resheim
 * @ant.type name="cover" category="epub"
 */
public class CoverType {

	public String image;

	public String value;

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
