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
 *    See git history
 *****************************************************************************/
package org.eclipse.egit.github.core.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.eclipse.egit.github.core.RequestError;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

/**
 * Unit tests of {@link RequestError}
 */
@SuppressWarnings("nls")
public class RequestErrorTest {

	/**
	 * Get request error message for JSON that contains error property
	 *
	 * @throws Exception
	 */
	@Test
	public void requestErrorWithErrorField() throws Exception {
		Gson gson = new Gson();
		RequestError error = gson.fromJson("{\"error\":\"not authorized\"}", RequestError.class);
		assertNotNull(error);
		assertEquals("not authorized", error.getMessage());
		assertNull(error.getErrors());
	}

	/**
	 * Get request error message for JSON that contains message property
	 *
	 * @throws Exception
	 */
	@Test
	public void requestErrorWithMessageField() throws Exception {
		Gson gson = new Gson();
		RequestError error = gson.fromJson("{\"message\":\"not authorized\"}", RequestError.class);
		assertNotNull(error);
		assertEquals("not authorized", error.getMessage());
		assertNull(error.getErrors());
	}

	/**
	 * Get request error message for JSON that contains error and message property
	 *
	 * @throws Exception
	 */
	@Test
	public void requestErrorWithErrorAndMessageField() throws Exception {
		Gson gson = new Gson();
		RequestError error = gson.fromJson(
				"{\"message\":\"not authorized\",\"error\":\"bad username\"}", RequestError.class);
		assertNotNull(error);
		assertEquals("not authorized", error.getMessage());
		assertNull(error.getErrors());
	}
}
