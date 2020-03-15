/*******************************************************************************
 * Copyright (c) 2007, 2014 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.splitter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DefaultSplittingStrategyTest {

	private final DefaultSplittingStrategy strategy = new DefaultSplittingStrategy();

	@Test
	public void computeSplitTargetByLabelIdNull() {
		strategy.heading(1, null, "Simple Label");
		assertEquals("Simple-Label.html", strategy.computeSplitTarget());
	}

	@Test
	public void computeSplitTargetByLabelNullId() {
		strategy.heading(1, "test", null);
		assertEquals("test.html", strategy.computeSplitTarget());
	}

	@Test
	public void computeSplitTargetByLabelEmptyId() {
		strategy.heading(1, "test", "");
		assertEquals("test.html", strategy.computeSplitTarget());
	}

	@Test
	public void computeSplitTargetByLabelIdNullDuplicate() {
		strategy.heading(1, null, "Simple Label");
		strategy.heading(1, null, "Simple Label");
		assertEquals("Simple-Label2.html", strategy.computeSplitTarget());
	}

	@Test
	public void computeSplitTargetByLabelId() {
		strategy.heading(1, "simpleLabel", "Simple Label");
		assertEquals("Simple-Label.html", strategy.computeSplitTarget());
	}

	@Test
	public void computeSplitTargetByLabelNullIdNull() {
		strategy.heading(1, null, null);
		assertEquals("h1p1", strategy.computeSplitTargetCandidate());
		assertEquals(null, strategy.getSplitTarget());
		strategy.heading(1, null, null);
		assertEquals("h1p2", strategy.computeSplitTargetCandidate());
		assertEquals("h1p2.html", strategy.getSplitTarget());
		strategy.heading(2, null, null);
		assertEquals("h2p3", strategy.computeSplitTargetCandidate());
		assertEquals("h1p2.html", strategy.getSplitTarget());
		strategy.heading(1, null, null);
		assertEquals("h1p4", strategy.computeSplitTargetCandidate());
		assertEquals("h1p4.html", strategy.getSplitTarget());
	}

	@Test
	public void normalizesUnsafeCharacters() {
		// ÀÁÂÃÄÅÇÈÉÊËÌÍÎÏÑÒÓÔÕÖÙÚÛÜÝàáâãäåçèéêëìíîïñòóôõöùúûüýÿ
		strategy.heading(1, null,
				"\u00C0\u00C1\u00C2\u00C3\u00C4\u00C5\u00C7\u00C8\u00C9\u00CA\u00CB\u00CC\u00CD\u00CE\u00CF\u00D1\u00D2\u00D3\u00D4\u00D5\u00D6\u00D9\u00DA\u00DB\u00DC\u00DD\u00E0\u00E1\u00E2\u00E3\u00E4\u00E5\u00E7\u00E8\u00E9\u00EA\u00EB\u00EC\u00ED\u00EE\u00EF\u00F1\u00F2\u00F3\u00F4\u00F5\u00F6\u00F9\u00FA\u00FB\u00FC\u00FD\u00FF");
		assertEquals("AAAAAACEEEEIIIINOOOOOUUUUYaaaaaaceeeeiiiinooooouuuuyy", strategy.computeSplitTargetCandidate());
	}

	@Test
	public void stripUnsafeCharactersUnicode() {
		// ÀÁ
		assertEquals("AA", strategy.stripUnsafeCharacters("\u00C0\u00C1"));
	}

	@Test
	public void stripUnsafeCharacters() {
		assertEquals("A-B", strategy.stripUnsafeCharacters("A ( B"));
	}

	@Test
	public void stripUnsafeCharactersMark() {
		assertEquals("A-B", strategy.stripUnsafeCharacters("A.B"));
	}
}
