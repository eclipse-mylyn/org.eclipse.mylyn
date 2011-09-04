/*******************************************************************************
 *  Copyright (c) 2011 Christian Trutz
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Christian Trutz - initial API and implementation
 *******************************************************************************/
package org.eclipse.egit.github.core.tests;

import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.eclipse.egit.github.core.Milestone;
import org.junit.Test;

/**
 * Unit tests of {@link Milestone}
 */
public class MilestoneTest {

	/**
	 * Test non-mutable created at date
	 */
	@Test
	public void getCreatedAtReferenceMutableObject() {
		Milestone milestone = new Milestone();
		milestone.setCreatedAt(new Date(5000));
		milestone.getCreatedAt().setTime(0);
		assertTrue(milestone.getCreatedAt().getTime() != 0);
	}

	/**
	 * Test non-mutable due on date
	 */
	@Test
	public void getDueOn_ReferenceMutableObject() {
		Milestone milestone = new Milestone();
		milestone.setDueOn(new Date(2000));
		milestone.getDueOn().setTime(0);
		assertTrue(milestone.getDueOn().getTime() != 0);
	}

	/**
	 * Test non-mutable due on date
	 */
	@Test
	public void setDueOnReferenceMutableObject() {
		Milestone milestone = new Milestone();
		Date longTimeAgo = new Date(0L);
		milestone.setDueOn(longTimeAgo);
		longTimeAgo.setTime(10000L);
		assertTrue(milestone.getDueOn().getTime() == 0L);
	}
}
