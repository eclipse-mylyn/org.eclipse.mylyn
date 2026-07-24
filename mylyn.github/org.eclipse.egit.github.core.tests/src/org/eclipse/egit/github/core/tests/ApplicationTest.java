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

import org.eclipse.egit.github.core.Application;
import org.junit.Test;

/**
 * Unit tests of {@link Application}
 */
public class ApplicationTest {

	/**
	 * Test default state of application
	 */
	@Test
	public void defaultState() {
		Application app = new Application();
		assertNull(app.getName());
		assertNull(app.getUrl());
	}

	/**
	 * Test updating application fields
	 */
	@Test
	public void updateFields() {
		Application app = new Application();
		assertEquals("name", app.setName("name").getName());
		assertEquals("url", app.setUrl("url").getUrl());
	}
}
