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
package org.eclipse.mylyn.internal.discovery.test.core.model.mock;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.internal.discovery.core.model.AbstractDiscoveryStrategy;
import org.eclipse.mylyn.internal.discovery.core.model.DiscoveryCategory;
import org.eclipse.mylyn.internal.discovery.core.model.DiscoveryConnector;

/**
 * 
 * @author David Green
 */
@SuppressWarnings("restriction")
public class MockDiscoveryStrategy extends AbstractDiscoveryStrategy {

	private int connectorCount = 15;

	private int categoryCount = 5;

	protected DiscoveryConnectorMockFactory connectorMockFactory = new DiscoveryConnectorMockFactory();

	protected DiscoveryCategoryMockFactory categoryMockFactory = new DiscoveryCategoryMockFactory();

	@Override
	public void performDiscovery(IProgressMonitor monitor) throws CoreException {
		for (int x = 0; x < categoryCount; ++x) {
			DiscoveryCategory mockCategory = createDiscoveryCategory();
			getCategories().add(mockCategory);
		}
		for (int x = 0; x < connectorCount; ++x) {
			DiscoveryConnector mockConnector = createDiscoveryConnector();
			// put the connector in a category
			if (!getCategories().isEmpty()) {
				int categoryIndex = x % getCategories().size();
				mockConnector.setCategoryId(getCategories().get(categoryIndex).getId());
			}
			getConnectors().add(mockConnector);
		}
	}

	protected DiscoveryCategory createDiscoveryCategory() {
		return categoryMockFactory.get();
	}

	protected DiscoveryConnector createDiscoveryConnector() {
		return connectorMockFactory.get();
	}

	public DiscoveryCategoryMockFactory getCategoryMockFactory() {
		return categoryMockFactory;
	}

	public void setCategoryMockFactory(DiscoveryCategoryMockFactory categoryMockFactory) {
		this.categoryMockFactory = categoryMockFactory;
	}

	public DiscoveryConnectorMockFactory getConnectorMockFactory() {
		return connectorMockFactory;
	}

	public void setConnectorMockFactory(DiscoveryConnectorMockFactory connectorMockFactory) {
		this.connectorMockFactory = connectorMockFactory;
	}

	public int getConnectorCount() {
		return connectorCount;
	}

	public void setConnectorCount(int connectorCount) {
		this.connectorCount = connectorCount;
	}

	public int getCategoryCount() {
		return categoryCount;
	}

	public void setCategoryCount(int categoryCount) {
		this.categoryCount = categoryCount;
	}

}
