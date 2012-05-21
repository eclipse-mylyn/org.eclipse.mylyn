/*******************************************************************************
 * Copyright (c) 2011, 2012 Torkild U. Resheim.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Torkild U. Resheim - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.docs.epub.ant.core;

import java.io.File;
import java.util.Locale;

/**
 * @author Torkild U. Resheim
 * @ant.type name="item" category="epub"
 */
public class ItemType {

	String dest;

	File file;

	String id;

	Locale lang;

	/** Default is that items are in reading order */
	public boolean linear = true;

	boolean noToc = false;

	String page;

	/** Default is to add the item to the spine */
	boolean spine = true;

	String type;

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
