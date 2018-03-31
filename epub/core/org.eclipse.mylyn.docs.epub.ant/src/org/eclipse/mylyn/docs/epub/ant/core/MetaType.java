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
 * @ant.type name="meta" category="epub"
 */
public class MetaType {

	public String name;

	public String content;

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
