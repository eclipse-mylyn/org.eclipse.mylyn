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

import org.eclipse.mylyn.internal.discovery.core.model.DiscoveryCategory;
import org.eclipse.mylyn.internal.discovery.core.model.Icon;

/**
 * 
 * @author David Green
 */
@SuppressWarnings("restriction")
public class DiscoveryCategoryMockFactory extends
		AbstractMockFactory<DiscoveryCategory> {

	DiscoveryCategory category;
	
	@Override
	protected DiscoveryCategory createMockObject() {
		return new DiscoveryCategory();
	}

	@Override
	protected void populateMockData() {
		// mock up some data

		getMockObject().setSource(source);
		
		name("Category "+seed)
		.id(DiscoveryCategoryMockFactory.class.getPackage().getName()+".connector"+seed)
		.description("A category of things, "+seed);
		
		Icon icon = new Icon();
		icon.setImage128("images/ico128.png");
		icon.setImage16("images/ico16.png");
		icon.setImage32("images/ico32.png");
		icon.setImage64("images/ico64.png");
		
		getMockObject().setIcon(icon);
		icon.setConnectorCategory(getMockObject());
	}

	public DiscoveryCategoryMockFactory description(String description) {
		getMockObject().setDescription(description);
		return this;
	}

	public DiscoveryCategoryMockFactory icon(Icon icon) {
		getMockObject().setIcon(icon);
		return this;
	}

	public DiscoveryCategoryMockFactory id(String id) {
		getMockObject().setId(id);
		return this;
	}

	public DiscoveryCategoryMockFactory name(String name) {
		getMockObject().setName(name);
		return this;
	}

	
}
