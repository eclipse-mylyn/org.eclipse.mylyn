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

import java.util.Objects;

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
		return Objects.hash(connector, name, url);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}
		ScmRepository other = (ScmRepository) obj;
		if (!Objects.equals(connector, other.connector)) {
			return false;
		}
		if (!Objects.equals(name, other.name)) {
			return false;
		}
		if (!Objects.equals(url, other.url)) {
			return false;
		}
		return true;
	}

}
