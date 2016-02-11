/*******************************************************************************
 * Copyright (c) 2016 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
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
