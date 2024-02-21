/*******************************************************************************
 * Copyright (c) 2015, 2024 David Green and others.
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

package org.eclipse.mylyn.wikitext.commonmark.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.junit.Test;

@SuppressWarnings("nls")
public class LinePredicatesTest {

	@Test
	public void empty() {
		Predicate<Line> predicate = LinePredicates.empty();
		assertFalse(predicate.test(null));
		assertTrue(predicate.test(new Line(0, 0, "")));
		assertFalse(predicate.test(new Line(0, 0, "a")));
		assertEquals("empty(line)", predicate.toString());
	}

	@Test
	public void matches() {
		Pattern pattern = Pattern.compile("\\s*abc\\s*");
		Predicate<Line> predicate = LinePredicates.matches(pattern);
		assertFalse(predicate.test(null));
		assertTrue(predicate.test(new Line(0, 0, "  abc ")));
		assertTrue(predicate.test(new Line(0, 0, "  abc  ")));
		assertFalse(predicate.test(new Line(0, 0, "  de ")));
		assertEquals("matches(\\s*abc\\s*)", predicate.toString());
	}
}
