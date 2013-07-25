/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.gerrit.tests.core.client.rest;

import static org.eclipse.mylyn.commons.sdk.util.CommonTestUtil.getFile;
import static org.eclipse.mylyn.commons.sdk.util.CommonTestUtil.read;
import junit.framework.TestCase;

import org.eclipse.mylyn.internal.gerrit.core.client.JSonSupport;
import org.eclipse.mylyn.internal.gerrit.core.client.rest.SubmitInput;
import org.junit.Test;

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
