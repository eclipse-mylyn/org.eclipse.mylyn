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
 * Information about a Trac Wiki page at a specific version
 * 
 * @author Xiaoyang Guan
 */
public class TracWikiPageInfo {

	private String pageName;

	private String author;

	private Date lastModified;

	private int version;

	private String comment;

	public TracWikiPageInfo() {
	}

	public String getPageName() {
		return pageName;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public String toString() {
		return pageName + ": version " + version + " by " + author + " last modified at " + lastModified; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

}
