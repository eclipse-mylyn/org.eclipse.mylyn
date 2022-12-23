/*******************************************************************************
 * Copyright (c) 2006, 2008 Steffen Pingel and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Steffen Pingel - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core.model;

import java.util.Date;

/**
 * @author Steffen Pingel
 */
public class TracAttachment {

	private String author;

	private Date created;

	private String description;

	private String filename;

	int size;

	public TracAttachment(String filename) {
		this.filename = filename;
	}

	public String getAuthor() {
		return author;
	}

	public Date getCreated() {
		return created;
	}

	public String getDescription() {
		return description;
	}

	public String getFilename() {
		return filename;
	}

	public int getSize() {
		return size;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void setSize(int size) {
		this.size = size;
	}

	@Override
	public String toString() {
		return filename;
	}

}
