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

import org.eclipse.egit.github.core.util.EncodingUtils;
import org.junit.Test;

/**
 * Unit tests of {@link EncodingUtils}
 */
public class EncodingUtilsTest {

	/**
	 * Test default constructor through anonymous sub-class
	 */
	@Test
	public void constructor() {
		assertNotNull(new EncodingUtils() {
		});
	}

	/**
	 * Encode and decode content
	 */
	@Test
	public void encodeDecode() {
		String test = "content";
		String encoded = EncodingUtils.toBase64(test.getBytes());
		assertNotNull(encoded);
		assertFalse(encoded.length() == 0);
		assertFalse(test.equals(encoded));
		byte[] decoded = EncodingUtils.fromBase64(encoded);
		assertNotNull(decoded);
		assertFalse(decoded.length == 0);
		assertEquals(test, new String(decoded));
	}

}
