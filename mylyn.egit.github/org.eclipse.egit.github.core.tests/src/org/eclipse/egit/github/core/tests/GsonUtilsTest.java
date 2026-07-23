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
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.eclipse.egit.github.core.Blob;
import org.eclipse.egit.github.core.client.GsonUtils;
import org.junit.Test;

/**
 * Unit tests of {@link GsonUtils}
 */
public class GsonUtilsTest {

	/**
	 * Get Gson instances
	 */
	@Test
	public void getGson() {
		assertNotNull(GsonUtils.getGson());
		assertNotNull(GsonUtils.getGson(true));
		assertNotNull(GsonUtils.getGson(false));
		assertNotSame(GsonUtils.getGson(true), GsonUtils.getGson(false));
	}

	/**
	 * Create Gson instances
	 */
	@Test
	public void createGson() {
		assertNotNull(GsonUtils.createGson());
		assertNotNull(GsonUtils.createGson(true));
		assertNotNull(GsonUtils.createGson(false));
		assertNotSame(GsonUtils.createGson(true), GsonUtils.createGson(false));
	}

	/**
	 * Serialize objects with all null fields
	 */
	@Test
	public void noSeriazlizeNulls() {
		Blob blob = new Blob();
		String json = GsonUtils.toJson(blob, false);
		assertEquals("{}", json);
	}

	/**
	 * Serialize objects with all null fields
	 */
	@Test
	public void seriazlizeNulls() {
		Blob blob = new Blob();
		String json = GsonUtils.toJson(blob, true);
		assertNotNull(json);
		assertTrue(json.length() > 2);
		assertFalse("{}".equals(json));
	}
}
