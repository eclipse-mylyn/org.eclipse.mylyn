/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.discovery.core.model;

import java.net.MalformedURLException;

/**
 * A description of a connector, including kind, description, licensing and
 * brand.
 * 
 * @author David Green
 */
public class ConnectorDescriptor {

	protected ConnectorDescriptorKind kind;
	protected String name;
	protected String provider;
	protected String license;
	protected String description;
	protected String siteUrl;
	protected String id;
	protected String categoryId;
	protected String platformFilter;
	protected Icon icon;
	protected Overview overview;

	public ConnectorDescriptor() {
	}

	/**
	 * must be one of 'document', 'task', 'vcs'
	 */
	public ConnectorDescriptorKind getKind() {
		return kind;
	}

	public void setKind(ConnectorDescriptorKind kind) {
		this.kind = kind;
	}

	/**
	 * the name of the connector including the name of the organization that
	 * produces the repository if appropriate, for example 'Mozilla Bugzilla'.
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * The name of the organization that supplies the connector.
	 */
	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	/**
	 * The short name of the license, for example 'EPL 1.0', 'GPL 2.0', or
	 * 'Commercial'.
	 */
	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	/**
	 * A description of the connector. Plug-ins should provide a description,
	 * especially if the description is not self-evident from the @name and
	 * @organization.
	 */
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * The URL of the update site containing the connector.
	 */
	public String getSiteUrl() {
		return siteUrl;
	}

	public void setSiteUrl(String siteUrl) {
		this.siteUrl = siteUrl;
	}

	/**
	 * The id of the feature that installs this connector
	 */
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * the id of the connectorCategory in which this connector belongs
	 */
	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	/**
	 * E.g., "(& (osgi.os=macosx) (osgi.ws=carbon))"
	 */
	public String getPlatformFilter() {
		return platformFilter;
	}

	public void setPlatformFilter(String platformFilter) {
		this.platformFilter = platformFilter;
	}

	public Icon getIcon() {
		return icon;
	}

	public void setIcon(Icon icon) {
		this.icon = icon;
	}

	public Overview getOverview() {
		return overview;
	}

	public void setOverview(Overview overview) {
		this.overview = overview;
	}

	public void validate() throws ValidationException {
		if (kind == null) {
			throw new ValidationException(
					"Must specify connectorDescriptor/@kind");
		}
		if (name == null || name.length() == 0) {
			throw new ValidationException(
					"Must specify connectorDescriptor/@name");
		}
		if (provider == null || provider.length() == 0) {
			throw new ValidationException(
					"Must specify connectorDescriptor/@provider");
		}
		if (license == null || license.length() == 0) {
			throw new ValidationException(
					"Must specify connectorDescriptor/@license");
		}
		if (siteUrl == null || siteUrl.length() == 0) {
			throw new ValidationException(
					"Must specify connectorDescriptor/@siteUrl");
		}
		try {
			new java.net.URL(siteUrl);
		} catch (MalformedURLException e) {
			throw new ValidationException(
			"Invalid connectorDescriptor/@siteUrl");
		}
		if (id == null || id.length() == 0) {
			throw new ValidationException(
					"Must specify connectorDescriptor/@id");
		}
		if (categoryId == null || categoryId.length() == 0) {
			throw new ValidationException(
					"Must specify connectorDescriptor/@categoryId");
		}
		if (icon != null) {
			icon.validate();
		}
		if (overview != null) {
			overview.validate();
		}
	}
}
