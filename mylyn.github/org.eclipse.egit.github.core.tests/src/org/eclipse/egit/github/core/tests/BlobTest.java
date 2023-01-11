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

import org.eclipse.egit.github.core.Blob;
import org.junit.Test;

/**
 * Unit tests of {@link Blob} class
 */
public class BlobTest {

	/**
	 * Test default state of blob
	 */
	@Test
	public void defaultState() {
		Blob blob = new Blob();
		assertNull(blob.getContent());
		assertNull(blob.getEncoding());
	}

	/**
	 * Test updating blob fields
	 */
	@Test
	public void updateFields() {
		Blob blob = new Blob();
		assertEquals("content123", blob.setContent("content123").getContent());
		assertEquals(Blob.ENCODING_UTF8, blob.setEncoding(Blob.ENCODING_UTF8)
				.getEncoding());
	}
}
