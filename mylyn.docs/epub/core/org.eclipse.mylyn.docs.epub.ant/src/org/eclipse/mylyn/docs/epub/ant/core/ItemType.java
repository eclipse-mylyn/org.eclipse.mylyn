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
import java.util.Locale;

/**
 * @author Torkild U. Resheim
 * @ant.type name="item" category="epub"
 */
public class ItemType {

	public String dest;

	public File file;

	public String id;

	public Locale lang;

	/** Default is that items are in reading order */
	public boolean linear = true;

	public boolean noToc = false;

	public String page;

	/** Default is to add the item to the spine */
	public boolean spine = true;

	public String type;

	/**
	 * @ant.not-required
	 */
	public void setDest(String dest) {
		this.dest = dest;
	}

	/**
	 * A file on the local file system.
	 *
	 * @param file
	 * @ant.required
	 */
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * @ant.not-required
	 */
	public void setId(String id) {
		this.id = id;
	}

	public void setLang(Locale lang) {
		this.lang = lang;
	}

	public void setLinear(boolean linear) {
		this.linear = linear;
	}

	/**
	 * @ant.not-required
	 */
	public void setNoToc(boolean toc) {
		this.noToc = toc;
	}

	/**
	 * A page on the wiki.
	 *
	 * @param page
	 */
	public void setPage(String page) {
		this.page = page;
	}

	/**
	 * @ant.not-required
	 */
	public void setSpine(boolean spine) {
		this.spine = spine;
	}

	/**
	 * @ant.not-required
	 */
	public void setType(String type) {
		this.type = type;
	}
}
