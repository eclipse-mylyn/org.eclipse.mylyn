/*******************************************************************************
 * Copyright (c) 2009, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.discovery.tests.core.mock;

/**
 * @author David Green
 */
public abstract class AbstractMockFactory<MockType> {

	protected int seed = 0;

	private MockType mockObject;

	protected MockDiscoverySource source = new MockDiscoverySource();

	public final MockType get() {
		MockType object = getMockObject();
		mockObject = null;
		return object;
	}

	public final MockType getMockObject() {
		if (mockObject == null) {
			++seed;
			mockObject = createMockObject();
			populateMockData();
		}
		return mockObject;
	}

	protected abstract void populateMockData();

	protected abstract MockType createMockObject();

	/**
	 * get the number of objects created by this factory
	 */
	public int getCreatedCount() {
		return seed;
	}
}
