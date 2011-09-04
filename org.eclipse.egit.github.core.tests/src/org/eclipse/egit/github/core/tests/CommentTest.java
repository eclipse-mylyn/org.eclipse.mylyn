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

import org.eclipse.egit.github.core.Comment;
import org.junit.Test;

/**
 * Unit tests of {@link Comment}
 */
public class CommentTest {

	/**
	 * Test non-mutable created at date
	 */
	@Test
	public void getCreatedAtReferenceMutableObject() {
		Comment comment = new Comment();
		comment.setCreatedAt(new Date(12345));
		comment.getCreatedAt().setTime(0);
		assertTrue(comment.getCreatedAt().getTime() != 0);
	}

	/**
	 * Test non-mutable updated at date
	 */
	@Test
	public void getUpdatedAtReferenceMutableObject() {
		Comment comment = new Comment();
		comment.setUpdatedAt(new Date(54321));
		comment.getUpdatedAt().setTime(0);
		assertTrue(comment.getUpdatedAt().getTime() != 0);
	}
}
