/******************************************************************************
 *  Copyright (c) 2018 Singaram Subramanian <to.ramsubramanian@gmail.com>
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Singaram Subramanian (Capital One) - (Bug: 529850)
 *    			 User teams across GitHub organizations implementation
 *****************************************************************************/
package org.eclipse.egit.github.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.eclipse.egit.github.core.Organization;
import org.junit.Test;

/**
 * Unit tests of {@link Organization}
 */
public class OrganizationTest {

	/**
	 * Test default state of organization
	 */
	@Test
	public void defaultState() {
		Organization organization = new Organization();
		assertEquals(0, organization.getId());
		assertNull(organization.getLogin());
		assertNull(organization.getDescription());
		assertNull(organization.getUrl());
	}

	/**
	 * Test updating organization fields
	 */
	@Test
	public void updateFields() {
		Organization organization = new Organization();
		assertEquals(12, organization.setId(12).getId());
		assertEquals("orgName", organization.setLogin("orgName").getLogin());
		assertEquals("description", organization.setDescription("description").getDescription());
		assertEquals("url", organization.setUrl("url").getUrl());
	}
}
