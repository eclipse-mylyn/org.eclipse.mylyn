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
 * @see http://open-services.net/bin/view/Main/CmServiceDescriptionV1
 * 
 * @author Robert Elves
 */
public class OslcSelectionDialogDescriptor implements Serializable {

	private static final long serialVersionUID = -7728392262241197918L;

	private boolean isDefault = false;

	private String hintWidth;

	private String hintHeight;

	private final String title;

	private final String url;

	private String label;

	public OslcSelectionDialogDescriptor(String title, String url) {
		this.title = title;
		this.url = url;
	}

	public void setDefault(boolean b) {
		this.isDefault = b;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public String getHintWidth() {
		return hintWidth;
	}

	public void setHintWidth(String hintWidth) {
		this.hintWidth = hintWidth;
	}

	public String getHintHeight() {
		return hintHeight;
	}

	public void setHintHeight(String hintHeight) {
		this.hintHeight = hintHeight;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getTitle() {
		return title;
	}

	public String getUrl() {
		return url;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof OslcSelectionDialogDescriptor)) {
			return false;
		}
		OslcSelectionDialogDescriptor other = (OslcSelectionDialogDescriptor) obj;
		if (url == null) {
			if (other.url != null) {
				return false;
			}
		} else if (!url.equals(other.url)) {
			return false;
		}
		return true;
	}

}
