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

import org.eclipse.egit.github.core.Id;
import org.junit.Test;

/**
 * Unit tests of {@link Id}
 */
public class IdTest {

	/**
	 * Test default state of id
	 */
	@Test
	public void defaultState() {
		Id id = new Id();
		assertNull(id.getId());
	}

	/**
	 * Test updating field of id
	 */
	@Test
	public void updateField() {
		Id id = new Id();
		assertEquals("12345", id.setId("12345").getId());
	}
}
