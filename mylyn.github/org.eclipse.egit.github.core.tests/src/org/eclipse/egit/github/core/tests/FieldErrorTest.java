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
 *    See git history
 *****************************************************************************/
package org.eclipse.egit.github.core.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.eclipse.egit.github.core.FieldError;
import org.junit.jupiter.api.Test;

/**
 * Unit tests of {@link FieldError}
 */
@SuppressWarnings("nls")
public class FieldErrorTest {

	/**
	 * Test default state of field error
	 */
	@Test
	public void defaultState() {
		FieldError error = new FieldError();
		assertNull(error.getCode());
		assertNull(error.getField());
		assertNull(error.getMessage());
		assertNull(error.getResource());
		assertNull(error.getValue());
	}

	/**
	 * Test updating field error fields
	 */
	@Test
	public void updateFields() {
		FieldError error = new FieldError();
		assertEquals("invalid", error.setCode("invalid").getCode());
		assertEquals("name", error.setField("name").getField());
		assertEquals("commit", error.setResource("commit").getResource());
		assertEquals("message", error.setMessage("message").getMessage());
		assertEquals("-1", error.setValue("-1").getValue());
	}
}
