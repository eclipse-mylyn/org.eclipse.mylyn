/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
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
import org.eclipse.mylyn.internal.discovery.core.util.DiscoveryCategoryComparator;

@SuppressWarnings("restriction")
public class DiscoveryCategoryComparatorTest extends TestCase {

	private DiscoveryCategoryComparator comparator;

	private DiscoveryCategory category1;

	private DiscoveryCategory category2;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		comparator = new DiscoveryCategoryComparator();
		category1 = new DiscoveryCategory();
		category2 = new DiscoveryCategory();
	}

	public void testSortByRelevanceInequal() {
		category1.setRelevance("100");
		category2.setRelevance("50");
		assertEquals(-1, comparator.compare(category1, category2));
		assertEquals(1, comparator.compare(category2, category1));
	}

	public void testSortByRelevanceOneNotSpecified() {
		category1.setRelevance("10");
		assertEquals(-1, comparator.compare(category1, category2));
		assertEquals(1, comparator.compare(category2, category1));
	}

	public void testSortByRelevanceSame() {
		category1.setRelevance("10");
		category1.setName("test");
		category1.setId("1");
		category2.setRelevance("10");
		category2.setName("test");
		category2.setId("1");
		assertEquals(0, comparator.compare(category1, category2));
		assertEquals(0, comparator.compare(category2, category1));
	}

	public void testSortByRelevanceSameIdsDiffer() {
		category1.setRelevance("10");
		category1.setName("test");
		category1.setId("a");
		category2.setRelevance("10");
		category2.setName("test");
		category2.setId("b");
		assertEquals(-1, comparator.compare(category1, category2));
		assertEquals(1, comparator.compare(category2, category1));
	}

	public void testSortByRelevanceSameNamesDiffer() {
		category1.setRelevance("10");
		category1.setName("a");
		category1.setId("a");
		category2.setRelevance("10");
		category2.setName("b");
		category2.setId("a");
		assertEquals(-1, comparator.compare(category1, category2));
		assertEquals(1, comparator.compare(category2, category1));
	}
}
