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
public class OslcServiceFactory implements Serializable {

	private static final long serialVersionUID = -8495019838789015468L;

	private final String url;

	private final String title;

	private String label;

	private boolean isDefault;

	public OslcServiceFactory(String title, String url) {
		this.title = title;
		this.url = url;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	public boolean isDefault() {
		return this.isDefault;
	}

	public String getUrl() {
		return url;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String l) {
		this.label = l;
	}

	public String getTitle() {
		return title;
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
		if (!(obj instanceof OslcServiceFactory)) {
			return false;
		}
		OslcServiceFactory other = (OslcServiceFactory) obj;
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
