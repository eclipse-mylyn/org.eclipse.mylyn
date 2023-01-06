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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.egit.github.core.Label;
import org.junit.Test;

/**
 * Unit tests of {@link Label}
 */
public class LabelTest {

	/**
	 * Test label default state
	 */
	@Test
	public void defaultState() {
		Label label = new Label();
		assertNull(label.getColor());
		assertNull(label.getName());
		assertNull(label.getUrl());
	}

	/**
	 * Test updating label fields
	 */
	@Test
	public void updateFields() {
		Label label = new Label();
		assertEquals("red", label.setColor("red").getColor());
		assertEquals("bug", label.setName("bug").getName());
		assertEquals("url", label.setUrl("url").getUrl());
	}

	/**
	 * Test equality of labels
	 */
	@Test
	public void equality() {
		Label l1 = new Label();
		assertEquals(l1.hashCode(), l1.hashCode());
		assertNotNull(l1.toString());
		Label l2 = new Label().setName("b");
		assertFalse(l1.equals(l2));
		l1.setName("a");
		Label l3 = new Label().setName("a");
		assertTrue(l1.equals(l1));
		Object string = "a";
		assertFalse(l1.equals(string));
		assertFalse(l1.equals(l2));
		assertTrue(l1.equals(l3));
		assertEquals(l1.hashCode(), l3.hashCode());
		assertEquals(l1.toString(), l3.toString());
	}
}
