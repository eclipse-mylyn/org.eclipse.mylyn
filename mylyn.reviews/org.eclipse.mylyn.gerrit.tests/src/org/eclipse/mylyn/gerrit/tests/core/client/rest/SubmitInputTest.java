/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.gerrit.tests.core.client.rest;

import static org.eclipse.mylyn.commons.sdk.util.CommonTestUtil.getFile;
import static org.eclipse.mylyn.commons.sdk.util.CommonTestUtil.read;

import org.eclipse.mylyn.internal.gerrit.core.client.JSonSupport;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.SubmitInput;
import org.junit.Test;

import junit.framework.TestCase;

@SuppressWarnings("nls")
public class SubmitInputTest extends TestCase {

	@Test
	public void testFromValid() throws Exception {
		SubmitInput submitInput = new SubmitInput(true);
		assertTrue(submitInput.isWaitForMerge());

		String json = new JSonSupport().toJson(submitInput);

		assertNotNull(json);
		assertFalse(json.isEmpty());
		assertEquals(read(getFile(this, "testdata/SubmitInput_waitForMerge.json")), json);
	}
}
