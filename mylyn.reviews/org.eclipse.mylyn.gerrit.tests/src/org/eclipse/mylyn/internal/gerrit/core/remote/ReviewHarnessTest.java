/*******************************************************************************
 * Copyright (c) 2016 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.remote;

import junit.framework.TestCase;

public class ReviewHarnessTest extends TestCase {

	public void testParseShortId() {
		assertEquals("243",
				ReviewHarness.parseShortId("http://lvps92-51-163-75.dedicated.hosteurope.de/gerrit-2.9.4/243 &#27;[K"));
		assertEquals("243", ReviewHarness
				.parseShortId("http://lvps92-51-163-75.dedicated.hosteurope.de/gerrit-2.9.4/243 &#27;[K\nsdsd"));
		assertEquals("243", ReviewHarness.parseShortId(
				"remote: slfjsldfj\nhttp://lvps92-51-163-75.dedicated.hosteurope.de/gerrit-2.9.4/243 &#27;[K\nremote: sldjfdlsk"));
		assertEquals("243",
				ReviewHarness.parseShortId("http://lvps92-51-163-75.dedicated.hosteurope.de/gerrit-2.9.4/243&#27;[K"));
		assertEquals("243", ReviewHarness.parseShortId(
				"remote: slfjsldfj\nhttp://lvps92-51-163-75.dedicated.hosteurope.de/gerrit-2.9.4/243&#27;[K\nremote: sldjfdlsk"));

	}

}
