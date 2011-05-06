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
package org.eclipse.mylyn.github.internal;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Unit tests for {@link Comment}
 */
@SuppressWarnings("restriction")
@RunWith(MockitoJUnitRunner.class)
public class CommentTest {

	private static final Gson gson = new GsonBuilder().setDateFormat(
			"yyyy-MM-dd").create();

	@Test
	public void getCreatedAt_ReferenceMutableObject() {
		Comment comment = gson.fromJson("{createdAt : '2003-10-10'}",
				Comment.class);
		comment.getCreatedAt().setTime(0);
		assertTrue(comment.getCreatedAt().getTime() != 0);
	}

	@Test
	public void getUpdatedAt_ReferenceMutableObject() {
		Comment comment = gson.fromJson("{updatedAt : '2003-10-10'}",
				Comment.class);
		comment.getUpdatedAt().setTime(0);
		assertTrue(comment.getUpdatedAt().getTime() != 0);
	}
}
