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

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * Connector Discovery extension point reader, for extension points of type
 * <tt>org.eclipse.mylyn.discovery.core.connectorDiscovery</tt>
 * 
 * @author David Green
 */
public class ConnectorDiscoveryExtensionReader {

	public static final String EXTENSION_POINT_ID = "org.eclipse.mylyn.discovery.core.connectorDiscovery"; //$NON-NLS-1$

	public static final String CONNECTOR_DESCRIPTOR = "connectorDescriptor"; //$NON-NLS-1$

	public static final String CONNECTOR_CATEGORY = "connectorCategory"; //$NON-NLS-1$

	public static final String ICON = "icon"; //$NON-NLS-1$

	public static final String OVERVIEW = "overview"; //$NON-NLS-1$

	public ConnectorDescriptor readConnectorDescriptor(IConfigurationElement element) throws ValidationException {
		return readConnectorDescriptor(element, ConnectorDescriptor.class);
	}

	public <T extends ConnectorDescriptor> T readConnectorDescriptor(IConfigurationElement element, Class<T> clazz)
			throws ValidationException {
		T connectorDescriptor;
		try {
			connectorDescriptor = clazz.newInstance();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}

		try {
			connectorDescriptor.setKind(ConnectorDescriptorKind.fromValue(element.getAttribute("kind"))); //$NON-NLS-1$
		} catch (IllegalArgumentException e) {
			throw new ValidationException("Unexpected value for kind");
		}
		connectorDescriptor.setName(element.getAttribute("name")); //$NON-NLS-1$
		connectorDescriptor.setProvider(element.getAttribute("provider")); //$NON-NLS-1$
		connectorDescriptor.setLicense(element.getAttribute("license")); //$NON-NLS-1$
		connectorDescriptor.setDescription(element.getAttribute("description")); //$NON-NLS-1$
		connectorDescriptor.setSiteUrl(element.getAttribute("siteUrl")); //$NON-NLS-1$
		connectorDescriptor.setId(element.getAttribute("id")); //$NON-NLS-1$
		connectorDescriptor.setCategoryId(element.getAttribute("categoryId")); //$NON-NLS-1$
		connectorDescriptor.setPlatformFilter(element.getAttribute("platformFilter")); //$NON-NLS-1$

		for (IConfigurationElement child : element.getChildren("icon")) { //$NON-NLS-1$
			Icon iconItem = readIcon(child);
			iconItem.setConnectorDescriptor(connectorDescriptor);
			if (connectorDescriptor.getIcon() != null) {
				throw new ValidationException("Unexpected element icon");
			}
			connectorDescriptor.setIcon(iconItem);
		}
		for (IConfigurationElement child : element.getChildren("overview")) { //$NON-NLS-1$
			Overview overviewItem = readOverview(child);
			overviewItem.setConnectorDescriptor(connectorDescriptor);
			if (connectorDescriptor.getOverview() != null) {
				throw new ValidationException("Unexpected element overview");
			}
			connectorDescriptor.setOverview(overviewItem);
		}

		connectorDescriptor.validate();

		return connectorDescriptor;
	}

	public ConnectorCategory readConnectorCategory(IConfigurationElement element) throws ValidationException {
		return readConnectorCategory(element, ConnectorCategory.class);
	}

	public <T extends ConnectorCategory> T readConnectorCategory(IConfigurationElement element, Class<T> clazz)
			throws ValidationException {
		T connectorCategory;
		try {
			connectorCategory = clazz.newInstance();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}

		connectorCategory.setId(element.getAttribute("id")); //$NON-NLS-1$
		connectorCategory.setName(element.getAttribute("name")); //$NON-NLS-1$
		connectorCategory.setDescription(element.getAttribute("description")); //$NON-NLS-1$

		for (IConfigurationElement child : element.getChildren("icon")) { //$NON-NLS-1$
			Icon iconItem = readIcon(child);
			iconItem.setConnectorCategory(connectorCategory);
			if (connectorCategory.getIcon() != null) {
				throw new ValidationException("Unexpected element icon");
			}
			connectorCategory.setIcon(iconItem);
		}

		connectorCategory.validate();

		return connectorCategory;
	}

	public Icon readIcon(IConfigurationElement element) throws ValidationException {
		Icon icon = new Icon();

		icon.setImage16(element.getAttribute("image16")); //$NON-NLS-1$
		icon.setImage32(element.getAttribute("image32")); //$NON-NLS-1$
		icon.setImage48(element.getAttribute("image48")); //$NON-NLS-1$
		icon.setImage64(element.getAttribute("image64")); //$NON-NLS-1$
		icon.setImage128(element.getAttribute("image128")); //$NON-NLS-1$

		icon.validate();

		return icon;
	}

	public Overview readOverview(IConfigurationElement element) throws ValidationException {
		Overview overview = new Overview();

		overview.setSummary(element.getAttribute("summary")); //$NON-NLS-1$
		overview.setUrl(element.getAttribute("url")); //$NON-NLS-1$
		overview.setScreenshot(element.getAttribute("screenshot")); //$NON-NLS-1$

		overview.validate();

		return overview;
	}

}
