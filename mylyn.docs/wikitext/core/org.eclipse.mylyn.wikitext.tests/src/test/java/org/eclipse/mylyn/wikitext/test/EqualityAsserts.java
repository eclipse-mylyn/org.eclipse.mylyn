/*******************************************************************************
 * Copyright (c) 2013, 2024 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.test;

import static java.util.Objects.requireNonNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EqualityAsserts {

	public static void assertEquality(Object one, Object two) {
		requireNonNull(one, "Must provide a value");
		requireNonNull(two, "Must provide a value");
		assertEquals(one, two);
		assertEquals(two, one);
		assertEquals(one.hashCode(), two.hashCode());
		assertEquality(one);
		assertEquality(two);
	}

	public static void assertInequality(Object one, Object two) {
		requireNonNull(one, "Must provide a value");
		requireNonNull(two, "Must provide a value");
		assertFalse(one.equals(two));
		assertFalse(two.equals(one));
		assertEquality(one);
		assertEquality(two);
	}

	private static void assertEquality(Object obj) {
		assertFalse(obj.equals(null));
		assertFalse(obj.equals(new Object()));
		assertTrue(obj.equals(obj));
	}

	private EqualityAsserts() {
		// prevent instantiation
	}
}
