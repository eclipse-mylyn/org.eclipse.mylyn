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

import org.eclipse.egit.github.core.GistRevision;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit tests for {@link GistRevision}
 */
@RunWith(MockitoJUnitRunner.class)
public class GistRevisionTest {

	private static final Gson gson = new GsonBuilder().setDateFormat(
			"yyyy-MM-dd").create();

	@Test
	public void getCreatedAt_ReferenceMutableObject() {
		GistRevision gistRevision = gson.fromJson(
				"{committedAt : '2003-10-10'}", GistRevision.class);
		gistRevision.getCommittedAt().setTime(0);
		assertTrue(gistRevision.getCommittedAt().getTime() != 0);
	}
}
