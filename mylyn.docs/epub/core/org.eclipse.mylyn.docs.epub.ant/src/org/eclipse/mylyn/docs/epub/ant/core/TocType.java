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

import java.io.File;

/**
 * Represents a table of contents element in the {@link EpubTask}. One should specify either a path to a NCX file or
 * whether or not to generate the NCX.
 *
 * @author Torkild U. Resheim
 * @ant.type name="toc" category="epub"
 */
public class TocType {

	public File file;

	public boolean generate;

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
