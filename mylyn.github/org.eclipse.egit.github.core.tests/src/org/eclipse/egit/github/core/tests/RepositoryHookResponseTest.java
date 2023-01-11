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

import org.eclipse.egit.github.core.RepositoryHookResponse;
import org.junit.Test;

/**
 * Unit tests of {@link RepositoryHookResponse}
 */
public class RepositoryHookResponseTest {

	/**
	 * Test default state of response
	 */
	@Test
	public void defaultState() {
		RepositoryHookResponse response = new RepositoryHookResponse();
		assertEquals(0, response.getCode());
		assertNull(response.getMessage());
	}

	/**
	 * Test updating response fields
	 */
	@Test
	public void updateFields() {
		RepositoryHookResponse response = new RepositoryHookResponse();
		assertEquals("error", response.setMessage("error").getMessage());
		assertEquals(404, response.setCode(404).getCode());
	}
}
