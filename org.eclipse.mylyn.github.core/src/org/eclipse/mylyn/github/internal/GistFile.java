/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.github.internal;

/**
 * Gist file class.
 */
public class GistFile {

	private int size;

	private String filename;

	private String rawUrl;

	/**
	 * @return size
	 */
	public int getSize() {
		return this.size;
	}

	/**
	 * @return filename
	 */
	public String getFilename() {
		return this.filename;
	}

	/**
	 * @return rawUrl
	 */
	public String getRawUrl() {
		return this.rawUrl;
	}

}
