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
 * @author Torkild U. Resheim
 * @ant.type name="epubfileset" category="epub"
 */
public class FileSetType extends org.apache.tools.ant.types.FileSet {

	String dest;

	Locale lang;

	public FileSetType() {

	}

	public void setLocale(Locale lang) {
		this.lang = lang;
	}

	public void setDest(String dest) {
		this.dest = dest;
	}
}
