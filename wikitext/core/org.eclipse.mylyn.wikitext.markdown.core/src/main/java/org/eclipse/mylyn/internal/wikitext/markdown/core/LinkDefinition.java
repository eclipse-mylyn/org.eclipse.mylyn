/*******************************************************************************
 * Copyright (c) 2013 Stefan Seelmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Stefan Seelmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.markdown.core;

/**
 * Bean that holds information about reference-style link definitions.
 * 
 * @author Stefan Seelmann
 */
public class LinkDefinition {

	private final String id;

	private final String url;

	private final String title;

	private final int offset;

	private final int length;

	public LinkDefinition(String id, String url, String title, int offset, int length) {
		this.id = id;
		this.url = url;
		this.title = title;
		this.offset = offset;
		this.length = length;
	}

	public String getId() {
		return id;
	}

	public String getUrl() {
		return url;
	}

	public String getTitle() {
		return title;
	}

	public int getOffset() {
		return offset;
	}

	public int getLength() {
		return length;
	}

}
