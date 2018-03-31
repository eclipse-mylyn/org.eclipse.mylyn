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
 * @ant.type name="epubfileset" category="epub"
 */
public class FileSetType extends org.apache.tools.ant.types.FileSet {

	public String dest;

	public Locale lang;

	public FileSetType() {

	}

	public void setLocale(Locale lang) {
		this.lang = lang;
	}

	public void setDest(String dest) {
		this.dest = dest;
	}
}
