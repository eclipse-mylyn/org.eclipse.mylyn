/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.egit.github.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
}
