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

import org.eclipse.egit.github.core.Reference;
import org.eclipse.egit.github.core.TypedResource;
import org.junit.Test;

/**
 * Unit tests of {@link Reference}
 */
public class ReferenceTest {

	/**
	 * Test default state of reference
	 */
	@Test
	public void defaultState() {
		Reference ref = new Reference();
		assertNull(ref.getObject());
		assertNull(ref.getRef());
		assertNull(ref.getUrl());
	}

	/**
	 * Test updating reference fields
	 */
	@Test
	public void updateFields() {
		Reference ref = new Reference();
		TypedResource obj = new TypedResource();
		obj.setSha("000");
		assertEquals(obj, ref.setObject(obj).getObject());
		assertEquals("master", ref.setRef("master").getRef());
		assertEquals("url://a", ref.setUrl("url://a").getUrl());
	}
}
