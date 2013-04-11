/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.sdk.util;

import java.util.List;
import java.util.Map;

/**
 * @author Steffen Pingel
 */
public class FixtureConfiguration {

	String type;

	String url;

	String version;

	String info;

	Map<String, String> properties;

	List<String> tags;

	public FixtureConfiguration() {
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public List<String> getTags() {
		return tags;
	}

	public boolean isDefault() {
		return properties != null && "1".equals(properties.get("default"));
	}

}
