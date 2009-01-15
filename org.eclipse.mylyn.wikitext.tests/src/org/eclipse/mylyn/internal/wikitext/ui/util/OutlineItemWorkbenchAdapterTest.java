/*******************************************************************************
 * Copyright (c) 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.ui.util;

import junit.framework.TestCase;

import org.eclipse.mylyn.wikitext.core.parser.outline.OutlineItem;
import org.eclipse.mylyn.wikitext.tests.EclipseRuntimeRequired;

/**
 * 
 * @author David Green
 */
@EclipseRuntimeRequired
public class OutlineItemWorkbenchAdapterTest extends TestCase {

	private OutlineItem outline;

	private OutlineItem child1;

	private OutlineItem child2;

	private OutlineItemWorkbenchAdapter adapter;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		outline = new OutlineItem(null, 0, "<root>", 0, 100, "<root>");

		child1 = new OutlineItem(outline, 1, "id1", 0, 10, "one");
		child2 = new OutlineItem(outline, 1, "id2", 11, 50, "two");

		adapter = new OutlineItemWorkbenchAdapter();
	}

	public void testGetParent() {
		assertNull(adapter.getParent(outline));
		assertSame(outline, adapter.getParent(child1));
		assertSame(outline, adapter.getParent(child2));
	}

	public void testGetChildren() {
		assertEquals(2, adapter.getChildren(outline).length);
		assertSame(child1, adapter.getChildren(outline)[0]);
		assertSame(child2, adapter.getChildren(outline)[1]);
		assertEquals(0, adapter.getChildren(child1).length);
		assertEquals(0, adapter.getChildren(child2).length);
	}

	public void testGetLabel() {
		assertTrue(adapter.getLabel(outline).indexOf(outline.getLabel()) != -1);
		assertTrue(adapter.getLabel(child1).indexOf(child1.getLabel()) != -1);
		assertTrue(adapter.getLabel(child2).indexOf(child2.getLabel()) != -1);
	}
}
