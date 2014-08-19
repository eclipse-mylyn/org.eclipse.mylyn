/*******************************************************************************
 * Copyright (c) 2014 Torkild U. Resheim and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Torkild U. Resheim - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.docs.epub.ant.core;

import java.io.File;

/**
 * * @author Torkild U. Resheim
 *
 * @ant.type name="include" category="epub"
 * @since 2.1
 */
public class ImportType {

	public File file;

	public String format;

	/**
	 * @ant.required
	 */
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * @ant.required
	 */
	public void setFormat(String format) {
		this.format = format;
	}

}
