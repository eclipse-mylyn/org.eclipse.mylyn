/*******************************************************************************
 * Copyright (c) 2009, 2024 David Green and others.
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

package org.eclipse.mylyn.wikitext.parser.css.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.parser.css.SparseCharSequence;
import org.junit.Before;
import org.junit.Test;

/**
 * @author David Green
 */
@SuppressWarnings("nls")
public class SparseCharSequenceTest {

	private SparseCharSequence sequence;

	private String testData;

	@Before
	public void setUp() throws Exception {
		configureTest("one /* two\n\n three */ four\n/* five */");
	}

	private void configureTest(String testData) {
		this.testData = testData;
		sequence = new SparseCharSequence(testData, Pattern.compile("/\\*.*?\\*/", Pattern.MULTILINE | Pattern.DOTALL));
	}

	@Test
	public void testLength() {
		assertEquals(10, sequence.length());
	}

	@Test
	public void testCharAt() {
		for (int x = 0; x < 4; ++x) {
			char c = sequence.charAt(x);
			assertEquals(testData.charAt(x), c);
		}
		for (int x = 4; x < 10; ++x) {
			char c = sequence.charAt(x);
			assertEquals(testData.charAt(x + 17), c);
		}
	}

	@Test
	public void testOriginalOffsetOf() {
		for (int x = 0; x < sequence.length(); ++x) {
			int originalOffset = x;
			if (x >= 4) {
				originalOffset += 17;
			}
			assertEquals(originalOffset, sequence.originalOffsetOf(x));
		}
	}

	@Test
	public void testSubSequence() {
		assertEquals("", sequence.subSequence(0, 0).toString());
		assertEquals("one ", sequence.subSequence(0, 4).toString());
		assertEquals("ne  ", sequence.subSequence(1, 5).toString());
		assertEquals("  fou", sequence.subSequence(3, 8).toString());
		assertEquals(" four", sequence.subSequence(4, 9).toString());
		assertEquals("four\n", sequence.subSequence(5, 10).toString());
	}

	@Test
	public void testToString() {
		assertEquals("one  four\n", sequence.toString());
	}

	@Test
	public void testCharAtOutOfBounds() {
		for (int x = -1; x < testData.length() + 2; ++x) {
			if (x < 0 || x >= sequence.length()) {
				try {
					sequence.charAt(x);
					fail("expected exception on index " + x);
				} catch (IndexOutOfBoundsException e) {
					// expected
				}
			}
		}
	}
}
