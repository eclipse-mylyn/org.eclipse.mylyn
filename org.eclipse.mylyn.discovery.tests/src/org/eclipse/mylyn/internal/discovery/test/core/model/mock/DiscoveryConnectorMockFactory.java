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

import org.eclipse.mylyn.internal.discovery.core.model.ConnectorDescriptorKind;
import org.eclipse.mylyn.internal.discovery.core.model.DiscoveryConnector;
import org.eclipse.mylyn.internal.discovery.core.model.Icon;
import org.eclipse.mylyn.internal.discovery.core.model.Overview;

/**
 * 
 * @author David Green
 */
@SuppressWarnings("restriction")
public class DiscoveryConnectorMockFactory extends AbstractMockFactory<DiscoveryConnector> {
	
	
	public DiscoveryConnectorMockFactory() {
	}

	@Override
	protected void populateMockData() {
		
		// mock up some data
		
		getMockObject().setSource(source);
		
		name("Connector "+seed).id(DiscoveryConnectorMockFactory.class.getPackage().getName()+".connector"+seed)
		.siteUrl("http://example.nodomain/some/path/updateSite3.x/")
		.kind(ConnectorDescriptorKind.TASK)
		.license(seed%2==0?"EPL 1.0":"APL 2.0")
		.description("a connector for the Example Task System versions 1.0 - 5.3")
		.categoryId("example")
		.provider("Testing 123 Inc.");
		
		Icon icon = new Icon();
		icon.setImage128("images/ico128.png");
		icon.setImage16("images/ico16.png");
		icon.setImage32("images/ico32.png");
		icon.setImage64("images/ico64.png");
		
		Overview overview = new Overview();
		overview.setScreenshot("images/screenshot-main.png");
		overview.setSummary("some long text that summarizes the connector");
		overview.setUrl("http://example.nodomain/some/path/updateSite3.x/overview.html");
		
		icon(icon).overview(overview);
		icon.setConnectorDescriptor(getMockObject());
		overview.setConnectorDescriptor(getMockObject());
	}
	
	
	@Override
	protected DiscoveryConnector createMockObject() {
		return new DiscoveryConnector();
	}

	public DiscoveryConnectorMockFactory categoryId(String categoryId) {
		getMockObject().setCategoryId(categoryId);
		return this;
	}

	public DiscoveryConnectorMockFactory description(String description) {
		getMockObject().setDescription(description);
		return this;
	}

	public DiscoveryConnectorMockFactory icon(Icon icon) {
		getMockObject().setIcon(icon);
		return this;
	}

	public DiscoveryConnectorMockFactory id(String id) {
		getMockObject().setId(id);
		return this;
	}

	public DiscoveryConnectorMockFactory kind(ConnectorDescriptorKind kind) {
		getMockObject().setKind(kind);
		return this;
	}

	public DiscoveryConnectorMockFactory license(String license) {
		getMockObject().setLicense(license);
		return this;
	}

	public DiscoveryConnectorMockFactory name(String name) {
		getMockObject().setName(name);
		return this;
	}

	public DiscoveryConnectorMockFactory overview(Overview overview) {
		getMockObject().setOverview(overview);
		return this;
	}

	public DiscoveryConnectorMockFactory platformFilter(String platformFilter) {
		getMockObject().setPlatformFilter(platformFilter);
		return this;
	}

	public DiscoveryConnectorMockFactory provider(String provider) {
		getMockObject().setProvider(provider);
		return this;
	}

	public DiscoveryConnectorMockFactory siteUrl(String siteUrl) {
		getMockObject().setSiteUrl(siteUrl);
		return this;
	}	
}
