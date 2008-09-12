/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.resources.tests;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.mylyn.internal.resources.ui.ResourceChangeMonitor;
import org.eclipse.mylyn.internal.resources.ui.ResourcesUiPreferenceInitializer;

/**
 * @author Mik Kersten
 */
public class ResourceChangeMonitorTest extends TestCase {

	private final ResourceChangeMonitor changeMonitor = new ResourceChangeMonitor();

	public void testForcedExclusionPatterns() {
		String pattern = "file:/foo";
		ResourcesUiPreferenceInitializer.addForcedExclusionPattern(pattern);
		assertTrue(ResourcesUiPreferenceInitializer.getForcedExcludedResourcePatterns().contains(pattern));
		assertFalse(ResourcesUiPreferenceInitializer.getExcludedResourcePatterns().contains(pattern));
	}

	public void testFileUriExclusionPattern() throws URISyntaxException {
		URI uri = new URI("file:/C:");
		assertTrue(changeMonitor.isUriExcluded(uri.toString(), "file:/C:"));

		uri = new URI("file:/C:/foo/bar");
		assertTrue(changeMonitor.isUriExcluded(uri.toString(), "file:/C:"));
	}

	public void testExclusionPattern() {
		Set<String> patterns = new HashSet<String>();
		patterns.add(".*");
		patterns.add("target");

		IPath path1 = new Path(".foo");
		assertTrue(changeMonitor.isExcluded(path1, null, patterns));

		IPath path2 = new Path("target/bar");
		assertTrue(changeMonitor.isExcluded(path2, null, patterns));

		IPath path3 = new Path("bar/target/bar");
		assertTrue(changeMonitor.isExcluded(path3, null, patterns));

		IPath path4 = new Path("bla/bla");
		assertFalse(changeMonitor.isExcluded(path4, null, patterns));
	}

	public void testInclusion() {
		IPath path4 = new Path("bla/bla");
		assertFalse(changeMonitor.isExcluded(path4, null, new HashSet<String>()));
	}

}
