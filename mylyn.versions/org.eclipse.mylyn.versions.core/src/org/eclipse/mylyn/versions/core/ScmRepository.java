/*******************************************************************************
 * Copyright (c) 2010, 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.versions.core;

import org.eclipse.mylyn.versions.core.spi.ScmConnector;

/**
 * @author Steffen Pingel
 */
public class ScmRepository {

	private String name;

	private String url;

	private ScmConnector connector;

	protected ScmRepository() {
	}

	public ScmRepository(ScmConnector connector, String name, String url) {
		this.name = name;
		this.url = url;
		this.connector = connector;
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}

	public ScmConnector getConnector() {
		return connector;
	}

	protected void setName(String name) {
		this.name = name;
	}

	protected void setUrl(String url) {
		this.url = url;
	}

	protected void setConnector(ScmConnector connector) {
		this.connector = connector;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((connector == null) ? 0 : connector.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		ScmRepository other = (ScmRepository) obj;
		if (connector == null) {
			if (other.connector != null) {
				return false;
			}
		} else if (!connector.equals(other.connector)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
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
