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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.eclipse.egit.github.core.Milestone;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit tests for {@link Milestone}
 */
@RunWith(MockitoJUnitRunner.class)
public class MilestoneTest {

	private static final Gson gson = new GsonBuilder().setDateFormat(
			"yyyy-MM-dd").create();

	@Test
	public void getCreatedAt_ReferenceMutableObject() {
		Milestone milestone = gson.fromJson("{createdAt : '2003-10-10'}",
				Milestone.class);
		milestone.getCreatedAt().setTime(0);
		assertTrue(milestone.getCreatedAt().getTime() != 0);
	}

	@Test
	public void getDueOn_ReferenceMutableObject() {
		Milestone milestone = gson.fromJson("{dueOn : '2003-10-10'}",
				Milestone.class);
		milestone.getDueOn().setTime(0);
		assertTrue(milestone.getDueOn().getTime() != 0);
	}

}
