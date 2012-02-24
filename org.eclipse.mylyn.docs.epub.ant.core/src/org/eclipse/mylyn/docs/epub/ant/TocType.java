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
package org.eclipse.mylyn.docs.epub.ant;

import java.io.File;

/**
 * Represents a table of contents element in the {@link EpubTask}. One should
 * specify either a path to a NCX file or whether or not to generate the NCX.
 * 
 * @author Torkild U. Resheim
 * @ant.type name="toc" category="epub"
 */
public class TocType {

	File file;

	boolean generate;

	/**
	 * @ant.not-required
	 */
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * @ant.not-required
	 */
	public void setGenerate(boolean generate) {
		this.generate = generate;
	}
}
