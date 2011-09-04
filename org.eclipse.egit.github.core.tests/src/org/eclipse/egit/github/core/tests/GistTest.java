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

import org.eclipse.egit.github.core.Gist;
import org.junit.Test;

/**
 * Unit tests of {@link Gist}
 */
public class GistTest {

	/**
	 * Test non-mutable created at date
	 */
	@Test
	public void getCreatedAtReferenceMutableObject() {
		Gist gist = new Gist();
		gist.setCreatedAt(new Date(11111));
		gist.getCreatedAt().setTime(0);
		assertTrue(gist.getCreatedAt().getTime() != 0);
	}

	/**
	 * Test non-mutable updated at date
	 */
	@Test
	public void getUpdatedAtReferenceMutableObject() {
		Gist gist = new Gist();
		gist.setUpdatedAt(new Date(22222));
		gist.getUpdatedAt().setTime(0);
		assertTrue(gist.getUpdatedAt().getTime() != 0);
	}
}
