/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.egit.github.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.eclipse.egit.github.core.TypedResource;
import org.junit.Test;

/**
 * Unit tests of {@link TypedResource}
 */
public class TypedResourceTest {

	/**
	 * Test default state of typed resource
	 */
	@Test
	public void defaultState() {
		TypedResource resource = new TypedResource();
		assertNull(resource.getSha());
		assertNull(resource.getType());
		assertNull(resource.getUrl());
	}

	/**
	 * Test updating typed resource fields
	 */
	@Test
	public void updateFields() {
		TypedResource resource = new TypedResource();
		assertEquals("011", resource.setSha("011").getSha());
		assertEquals("blob", resource.setType("blob").getType());
		assertEquals("url://a", resource.setUrl("url://a").getUrl());
	}
}
