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
 *     Xiaoyang Guan - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core.model;

/**
 * Represents a Trac Wiki page at a specific version.
 * 
 * @author Xiaoyang Guan
 */
public class TracWikiPage {

	private TracWikiPageInfo pageInfo;

	private String content;

	private String pageHTML;

	public TracWikiPage() {
	}

	public TracWikiPageInfo getPageInfo() {
		return pageInfo;
	}

	public void setPageInfo(TracWikiPageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPageHTML() {
		return pageHTML;
	}

	public void setPageHTML(String pageHTML) {
		this.pageHTML = pageHTML;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else if (this == obj) {
			return true;
		} else if (getClass() != obj.getClass()) {
			return false;
		} else {
			TracWikiPage other = (TracWikiPage) obj;
			return content.equals(other.content) && pageInfo.toString().equals(other.pageInfo.toString());
		}
	}
}
