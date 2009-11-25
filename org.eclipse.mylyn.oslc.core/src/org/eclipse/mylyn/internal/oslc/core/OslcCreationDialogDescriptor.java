/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *  Contributors:
 *      Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.oslc.core;

import java.io.Serializable;

/**
 * @author Robert Elves
 */
public class OslcCreationDialogDescriptor implements Serializable {

	private static final long serialVersionUID = 5159045583444273413L;

	private String title;

	private String relativeUrl;

	private boolean isDefault;

	public OslcCreationDialogDescriptor(String title, String relativeurl) {
		this.title = title;
		this.relativeUrl = relativeurl;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public String getUrl() {
		return relativeUrl;
	}

	public void setUrl(String url) {
		this.relativeUrl = url;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
}
