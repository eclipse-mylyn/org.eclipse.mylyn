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
import static org.junit.Assert.assertNotNull;

import org.eclipse.egit.github.core.RequestError;
import org.eclipse.egit.github.core.client.GsonUtils;
import org.eclipse.egit.github.core.client.RequestException;
import org.junit.Test;

/**
 * Unit tests of {@link RequestException}
 */
public class RequestExceptionTest {

	/**
	 * Formatted error message for invalid field
	 */
	@Test
	public void invalidField() {
		RequestError error = GsonUtils.fromJson(
				"{\"errors\":[{\"code\":\"invalid\", \"field\":\"page\"}]}",
				RequestError.class);
		RequestException e = new RequestException(error, 400);
		String formatted = e.formatErrors();
		assertNotNull(formatted);
		assertEquals("400: Invalid value for 'page' field", formatted);
	}

	/**
	 * Formatted error message with invalid field value
	 */
	@Test
	public void invalidFieldValue() {
		RequestError error = GsonUtils
				.fromJson(
						"{\"errors\":[{\"code\":\"invalid\", \"field\":\"name\", \"value\":\"100\"}]}",
						RequestError.class);
		RequestException e = new RequestException(error, 401);
		String formatted = e.formatErrors();
		assertNotNull(formatted);
		assertEquals("401: Invalid value of '100' for 'name' field", formatted);
	}

	/**
	 * Formatted error message for missing field
	 */
	@Test
	public void missingField() {
		RequestError error = GsonUtils
				.fromJson(
						"{\"errors\":[{\"code\":\"missing_field\", \"field\":\"due\"}]}",
						RequestError.class);
		RequestException e = new RequestException(error, 422);
		String formatted = e.formatErrors();
		assertNotNull(formatted);
		assertEquals("422: Missing required 'due' field", formatted);
	}

	/**
	 * Formatted error message for existing resource with field
	 */
	@Test
	public void existentField() {
		RequestError error = GsonUtils
				.fromJson(
						"{\"errors\":[{\"code\":\"already_exists\", \"field\":\"severity\",  \"resource\":\"Issue\"}]}",
						RequestError.class);
		RequestException e = new RequestException(error, 500);
		String formatted = e.formatErrors();
		assertNotNull(formatted);
		assertEquals(
				"500: Issue resource with 'severity' field already exists",
				formatted);
	}

	/**
	 * Formatted error message for error in field
	 */
	@Test
	public void errorField() {
		RequestError error = GsonUtils
				.fromJson(
						"{\"errors\":[{\"field\":\"priority\", \"resource\":\"Gist\"}]}",
						RequestError.class);
		RequestException e = new RequestException(error, 400);
		String formatted = e.formatErrors();
		assertNotNull(formatted);
		assertEquals("400: Error with 'priority' field in Gist resource",
				formatted);
	}

	/**
	 * Formatted error message for custom error code that contains message
	 */
	@Test
	public void customCode() {
		RequestError error = GsonUtils
				.fromJson(
						"{\"errors\":[{\"code\":\"custom\", \"message\":\"Integer instead of String\"}]}",
						RequestError.class);
		RequestException e = new RequestException(error, 400);
		String formatted = e.formatErrors();
		assertNotNull(formatted);
		assertEquals("400: Integer instead of String", formatted);
	}
}
