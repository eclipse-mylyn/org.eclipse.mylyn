/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.core.test;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EqualityAsserts {

	public static void assertEquality(Object one, Object two) {
		checkNotNull(one, "Must provide a value");
		checkNotNull(two, "Must provide a value");
		assertEquals(one, two);
		assertEquals(two, one);
		assertEquals(one.hashCode(), two.hashCode());
		assertEquality(one);
		assertEquality(two);
	}

	public static void assertInequality(Object one, Object two) {
		checkNotNull(one, "Must provide a value");
		checkNotNull(two, "Must provide a value");
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
