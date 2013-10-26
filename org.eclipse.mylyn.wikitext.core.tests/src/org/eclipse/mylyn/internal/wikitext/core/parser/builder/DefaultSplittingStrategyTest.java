/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.core.parser.builder;

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
		strategy.heading(1, null, "ÀÁÂÃÄÅÇÈÉÊËÌÍÎÏÑÒÓÔÕÖÙÚÛÜÝàáâãäåçèéêëìíîïñòóôõöùúûüýÿ");
		assertEquals("AAAAAACEEEEIIIINOOOOOUUUUYaaaaaaceeeeiiiinooooouuuuyy", strategy.computeSplitTargetCandidate());
	}

	@Test
	public void stripUnsafeCharactersUnicode() {
		assertEquals("AA", strategy.stripUnsafeCharacters("ÀÁ"));
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
