/*******************************************************************************
 * Copyright (c) 2009, 2011 David Green and others.
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

package org.eclipse.mylyn.internal.wikitext.ui.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.eclipse.mylyn.wikitext.parser.outline.OutlineItem;
import org.eclipse.mylyn.wikitext.tests.EclipseRuntimeRequired;
import org.junit.Before;
import org.junit.Test;

/**
 * @author David Green
 */
@EclipseRuntimeRequired
public class OutlineItemWorkbenchAdapterTest {

	private OutlineItem outline;

	private OutlineItem child1;

	private OutlineItem child2;

	private OutlineItemWorkbenchAdapter adapter;

	@Before
	public void setUp() throws Exception {
		outline = new OutlineItem(null, 0, "<root>", 0, 100, "<root>");

		child1 = new OutlineItem(outline, 1, "id1", 0, 10, "one");
		child2 = new OutlineItem(outline, 1, "id2", 11, 50, "two");

		adapter = new OutlineItemWorkbenchAdapter();
	}

	@Test
	public void testGetParent() {
		assertNull(adapter.getParent(outline));
		assertSame(outline, adapter.getParent(child1));
		assertSame(outline, adapter.getParent(child2));
	}

	@Test
	public void testGetChildren() {
		assertEquals(2, adapter.getChildren(outline).length);
		assertSame(child1, adapter.getChildren(outline)[0]);
		assertSame(child2, adapter.getChildren(outline)[1]);
		assertEquals(0, adapter.getChildren(child1).length);
		assertEquals(0, adapter.getChildren(child2).length);
	}

	@Test
	public void testGetLabel() {
		assertTrue(adapter.getLabel(outline).indexOf(outline.getLabel()) != -1);
		assertTrue(adapter.getLabel(child1).indexOf(child1.getLabel()) != -1);
		assertTrue(adapter.getLabel(child2).indexOf(child2.getLabel()) != -1);
	}
}
