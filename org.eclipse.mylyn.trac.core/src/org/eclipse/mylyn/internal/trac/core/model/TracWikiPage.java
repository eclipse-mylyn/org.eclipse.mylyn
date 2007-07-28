/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
}
