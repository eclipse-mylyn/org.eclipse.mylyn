/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.ide.tests;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.resources.ui.ResourcesUiPreferenceInitializer;

/**
 * @author Mik Kersten
 */
public class IdePreferencesTest extends TestCase {

	public void testExclusionPatterns() {
		ResourcesUiPreferenceInitializer.setExcludedResourcePatterns(new HashSet<String>());
		assertEquals(0, ResourcesUiPreferenceInitializer.getExcludedResourcePatterns().size());

		Set<String> ignored = new HashSet<String>();
		ignored.add("one*");
		ignored.add(".two");

		ResourcesUiPreferenceInitializer.setExcludedResourcePatterns(ignored);
		Set<String> read = ResourcesUiPreferenceInitializer.getExcludedResourcePatterns();
		assertEquals(2, read.size());
		assertTrue(read.contains("one*"));
		assertTrue(read.contains(".two"));
	}

}
