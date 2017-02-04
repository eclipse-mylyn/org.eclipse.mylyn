/*******************************************************************************
 * Copyright (c) 2015 David Green.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.commonmark.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;

import org.junit.Test;

import com.google.common.base.Predicate;

public class LinePredicatesTest {

	@Test
	public void empty() {
		Predicate<Line> predicate = LinePredicates.empty();
		assertFalse(predicate.apply(null));
		assertTrue(predicate.apply(new Line(0, 0, "")));
		assertFalse(predicate.apply(new Line(0, 0, "a")));
		assertEquals("empty(line)", predicate.toString());
	}

	@Test
	public void matches() {
		Pattern pattern = Pattern.compile("\\s*abc\\s*");
		Predicate<Line> predicate = LinePredicates.matches(pattern);
		assertFalse(predicate.apply(null));
		assertTrue(predicate.apply(new Line(0, 0, "  abc ")));
		assertTrue(predicate.apply(new Line(0, 0, "  abc  ")));
		assertFalse(predicate.apply(new Line(0, 0, "  de ")));
		assertEquals("matches(\\s*abc\\s*)", predicate.toString());
	}
}
