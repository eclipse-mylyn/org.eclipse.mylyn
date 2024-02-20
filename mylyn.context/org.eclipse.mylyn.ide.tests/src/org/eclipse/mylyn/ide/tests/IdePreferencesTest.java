/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.ide.tests;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.mylyn.internal.resources.ui.ResourcesUiPreferenceInitializer;

import junit.framework.TestCase;

/**
 * @author Mik Kersten
 */
@SuppressWarnings("nls")
public class IdePreferencesTest extends TestCase {

	public void testExclusionPatterns() {
		ResourcesUiPreferenceInitializer.setExcludedResourcePatterns(new HashSet<>());
		assertEquals(0, ResourcesUiPreferenceInitializer.getExcludedResourcePatterns().size());

		Set<String> ignored = new HashSet<>();
		ignored.add("one*");
		ignored.add(".two");

		ResourcesUiPreferenceInitializer.setExcludedResourcePatterns(ignored);
		Set<String> read = ResourcesUiPreferenceInitializer.getExcludedResourcePatterns();
		assertEquals(2, read.size());
		assertTrue(read.contains("one*"));
		assertTrue(read.contains(".two"));
	}

}
