/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.discovery.tests.core.util;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.discovery.core.model.DiscoveryCategory;
import org.eclipse.mylyn.internal.discovery.core.model.DiscoveryConnector;
import org.eclipse.mylyn.internal.discovery.core.model.Group;
import org.eclipse.mylyn.internal.discovery.core.util.DiscoveryConnectorComparator;

public class DiscoveryConnectorComparatorTest extends TestCase {

	private DiscoveryCategory category;

	private DiscoveryConnectorComparator comparator;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		category = new DiscoveryCategory();
		comparator = new DiscoveryConnectorComparator(category);
	}

	private Group addGroup(String id) {
		Group group = new Group();
		group.setId(id);
		category.getGroup().add(group);
		return group;
	}

	private DiscoveryConnector addConnectorDescriptor(String id, String name, String groupId) {
		DiscoveryConnector connector = new DiscoveryConnector();
		connector.setId(id);
		connector.setName(name);
		connector.setGroupId(groupId);
		connector.setCategory(category);
		category.getConnectors().add(connector);
		return connector;
	}

	public void testOrderByGroup() {
		addGroup("1");
		addGroup("2");
		DiscoveryConnector t1 = addConnectorDescriptor("b", "btest", "2");
		DiscoveryConnector t2 = addConnectorDescriptor("a", "atest", "2");
		DiscoveryConnector t3 = addConnectorDescriptor("c", "ctest", "1");
		DiscoveryConnector t4 = addConnectorDescriptor("d", "dtest", "1");
		DiscoveryConnector t5 = addConnectorDescriptor("0", "0test", null);

		assertEquals(-1, comparator.compare(t2, t1));
		assertEquals(1, comparator.compare(t1, t2));
		assertEquals(-1, comparator.compare(t3, t4));
		assertEquals(1, comparator.compare(t4, t3));

		assertEquals(-1, comparator.compare(t1, t5));
		assertEquals(1, comparator.compare(t5, t1));
		assertEquals(-1, comparator.compare(t2, t5));
		assertEquals(1, comparator.compare(t5, t2));
		assertEquals(-1, comparator.compare(t3, t5));
		assertEquals(1, comparator.compare(t5, t3));
		assertEquals(-1, comparator.compare(t4, t5));
		assertEquals(1, comparator.compare(t5, t4));

		assertEquals(-1, comparator.compare(t3, t1));
		assertEquals(1, comparator.compare(t1, t3));
		assertEquals(-1, comparator.compare(t3, t2));
		assertEquals(1, comparator.compare(t2, t3));

		assertEquals(-1, comparator.compare(t4, t1));
		assertEquals(1, comparator.compare(t1, t4));
		assertEquals(-1, comparator.compare(t4, t2));
		assertEquals(1, comparator.compare(t2, t4));
	}
}
