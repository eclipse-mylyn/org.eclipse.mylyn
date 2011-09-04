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

import org.eclipse.egit.github.core.Issue;
import org.junit.Test;

/**
 * Unit tests of {@link Issue}
 */
public class IssueTest {

	/**
	 * Test non-mutable created at date
	 */
	@Test
	public void getCreatedAtReferenceMutableObject() {
		Issue issue = new Issue();
		issue.setCreatedAt(new Date(55555555));
		issue.getCreatedAt().setTime(0);
		assertTrue(issue.getCreatedAt().getTime() != 0);
	}

	/**
	 * Test non-mutable updated at date
	 */
	@Test
	public void getUpdatedAtReferenceMutableObject() {
		Issue issue = new Issue();
		issue.setUpdatedAt(new Date(44444444));
		issue.getUpdatedAt().setTime(0);
		assertTrue(issue.getUpdatedAt().getTime() != 0);
	}

	/**
	 * Test non-mutable closed at date
	 */
	@Test
	public void getClosedAtReferenceMutableObject() {
		Issue issue = new Issue();
		issue.setClosedAt(new Date(99999999));
		issue.getClosedAt().setTime(0);
		assertTrue(issue.getClosedAt().getTime() != 0);
	}
}
