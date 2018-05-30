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

import org.eclipse.egit.github.core.ShaResource;
import org.junit.Test;

/**
 * Unit tests of {@link ShaResource}
 */
public class ShaResourceTest {

	/**
	 * Test default state of sha resource
	 */
	@Test
	public void defaultState() {
		ShaResource resource = new ShaResource();
		assertNull(resource.getSha());
	}

	/**
	 * Test updating fields of sha resource
	 */
	@Test
	public void updateFields() {
		ShaResource resource = new ShaResource();
		assertEquals("0a1b", resource.setSha("0a1b").getSha());
	}
}
