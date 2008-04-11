/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.ide.tests;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.resources.ui.ResourcesUiBridgePlugin;

/**
 * @author Mik Kersten
 */
public class IdePreferencesTest extends TestCase {

	public void testExclusionPatterns() {
		ResourcesUiBridgePlugin.getDefault().setExcludedResourcePatterns(new HashSet<String>());
		assertEquals(0, ResourcesUiBridgePlugin.getDefault().getExcludedResourcePatterns().size());

		Set<String> ignored = new HashSet<String>();
		ignored.add("one*");
		ignored.add(".two");

		ResourcesUiBridgePlugin.getDefault().setExcludedResourcePatterns(ignored);
		Set<String> read = ResourcesUiBridgePlugin.getDefault().getExcludedResourcePatterns();
		assertEquals(2, read.size());
		assertTrue(read.contains("one*"));
		assertTrue(read.contains(".two"));
	}

}
