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
 * @ant.type name="identifier" category="epub"
 */
public class IdentifierType {

	public String id;

	public String scheme;

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
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @ant.required
	 */
	public void setScheme(String scheme) {
		this.scheme = scheme;
	}
}
