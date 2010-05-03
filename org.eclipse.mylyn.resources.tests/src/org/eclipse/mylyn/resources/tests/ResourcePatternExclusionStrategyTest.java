/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
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
import org.eclipse.mylyn.internal.resources.ui.ResourcePatternExclusionStrategy;
import org.eclipse.mylyn.internal.resources.ui.ResourcesUiPreferenceInitializer;

/**
 * @author Shawn Minto
 */
public class ResourcePatternExclusionStrategyTest extends TestCase {
	public void testForcedExclusionPatterns() {
		String pattern = "file:/foo";
		try {
			ResourcesUiPreferenceInitializer.addForcedExclusionPattern(pattern);
			assertTrue(ResourcesUiPreferenceInitializer.getForcedExcludedResourcePatterns().contains(pattern));
			assertFalse(ResourcesUiPreferenceInitializer.getExcludedResourcePatterns().contains(pattern));
		} finally {
			ResourcesUiPreferenceInitializer.removeForcedExclusionPattern(pattern);
		}
	}

	public void testFileUriExclusionPattern() throws URISyntaxException {
		URI uri = new URI("file:/C:");
		assertTrue(ResourcePatternExclusionStrategy.isUriExcluded(uri.toString(), "file:/C:"));

		uri = new URI("file:/C:/foo/bar");
		assertTrue(ResourcePatternExclusionStrategy.isUriExcluded(uri.toString(), "file:/C:"));
	}

	public void testSnapshotExclusionPattern() {
		// .*

		Set<String> patterns = new HashSet<String>();
		patterns.addAll(ResourcePatternExclusionStrategy.convertToAntPattern(".*"));

		assertTrue(ResourcePatternExclusionStrategy.isExcluded(new Path("/.foo"), null, patterns));
		assertTrue(ResourcePatternExclusionStrategy.isExcluded(new Path("/.foo/test"), null, patterns));
		assertTrue(ResourcePatternExclusionStrategy.isExcluded(new Path("/folder/.foo"), null, patterns));
		assertTrue(ResourcePatternExclusionStrategy.isExcluded(new Path("/folder/.foo/foo2"), null, patterns));

		assertFalse(ResourcePatternExclusionStrategy.isExcluded(new Path("/foo.test"), null, patterns));
		assertFalse(ResourcePatternExclusionStrategy.isExcluded(new Path("/d.foo"), null, patterns));
		assertFalse(ResourcePatternExclusionStrategy.isExcluded(new Path("/folder/d.foo"), null, patterns));
		assertFalse(ResourcePatternExclusionStrategy.isExcluded(new Path("/folder/d.foo/foo2"), null, patterns));

		assertTrue(ResourcePatternExclusionStrategy.isExcluded(new Path("/.foo2"), null, patterns));
		assertTrue(ResourcePatternExclusionStrategy.isExcluded(new Path("/.foo2/test"), null, patterns));
		assertTrue(ResourcePatternExclusionStrategy.isExcluded(new Path("/folder/.foo2/foo3"), null, patterns));
	}

	public void testFileExclusionPattern() {
		// *.doc

		Set<String> patterns = new HashSet<String>();
		patterns.addAll(ResourcePatternExclusionStrategy.convertToAntPattern("*.doc"));

		assertTrue(ResourcePatternExclusionStrategy.isExcluded(new Path("/.doc"), null, patterns));
		assertTrue(ResourcePatternExclusionStrategy.isExcluded(new Path("/.doc/test"), null, patterns));
		assertTrue(ResourcePatternExclusionStrategy.isExcluded(new Path("/folder/.doc"), null, patterns));
		assertTrue(ResourcePatternExclusionStrategy.isExcluded(new Path("/folder/.doc/foo2"), null, patterns));
		assertTrue(ResourcePatternExclusionStrategy.isExcluded(new Path("/d.doc"), null, patterns));
		assertTrue(ResourcePatternExclusionStrategy.isExcluded(new Path("/~d.doc"), null, patterns));

		assertFalse(ResourcePatternExclusionStrategy.isExcluded(new Path("/folder/d.doc2"), null, patterns));
		assertFalse(ResourcePatternExclusionStrategy.isExcluded(new Path("/folder/d.doc2/foo2"), null, patterns));

		assertFalse(ResourcePatternExclusionStrategy.isExcluded(new Path("/document"), null, patterns));
		assertFalse(ResourcePatternExclusionStrategy.isExcluded(new Path("/.doc2"), null, patterns));
		assertFalse(ResourcePatternExclusionStrategy.isExcluded(new Path("/.doc2/test"), null, patterns));
		assertFalse(ResourcePatternExclusionStrategy.isExcluded(new Path("/folder/.doc2/foo3"), null, patterns));
	}

	public void testAllExclusionPattern() {
		// *
		Set<String> patterns = new HashSet<String>();
		patterns.addAll(ResourcePatternExclusionStrategy.convertToAntPattern("*"));

		assertTrue(ResourcePatternExclusionStrategy.isExcluded(new Path("/.doc"), null, patterns));
		assertTrue(ResourcePatternExclusionStrategy.isExcluded(new Path("/.doc2/test"), null, patterns));
		assertTrue(ResourcePatternExclusionStrategy.isExcluded(new Path("/folder/.doc2/test"), null, patterns));
		assertTrue(ResourcePatternExclusionStrategy.isExcluded(new Path("/.test"), null, patterns));
		assertTrue(ResourcePatternExclusionStrategy.isExcluded(new Path("/~"), null, patterns));
		assertTrue(ResourcePatternExclusionStrategy.isExcluded(new Path("/test"), null, patterns));

	}

	public void testAllFileExclusionPattern() {
		// *.*
		Set<String> patterns = new HashSet<String>();
		patterns.addAll(ResourcePatternExclusionStrategy.convertToAntPattern("*.*"));

		assertTrue(ResourcePatternExclusionStrategy.isExcluded(new Path("/.doc"), null, patterns));
		assertTrue(ResourcePatternExclusionStrategy.isExcluded(new Path("/.doc2/test"), null, patterns));
		assertTrue(ResourcePatternExclusionStrategy.isExcluded(new Path("/folder/.doc2/test"), null, patterns));
		assertTrue(ResourcePatternExclusionStrategy.isExcluded(new Path("/.test"), null, patterns));

		assertFalse(ResourcePatternExclusionStrategy.isExcluded(new Path("/~"), null, patterns));
		assertFalse(ResourcePatternExclusionStrategy.isExcluded(new Path("/test"), null, patterns));

	}

	public void testWildcardExclusionPattern() {
		// ~*
		Set<String> patterns = new HashSet<String>();
		patterns.addAll(ResourcePatternExclusionStrategy.convertToAntPattern("~*"));

		assertTrue(ResourcePatternExclusionStrategy.isExcluded(new Path("/~"), null, patterns));
		assertTrue(ResourcePatternExclusionStrategy.isExcluded(new Path("/~.doc"), null, patterns));
		assertTrue(ResourcePatternExclusionStrategy.isExcluded(new Path("/~.doc2/test"), null, patterns));
		assertTrue(ResourcePatternExclusionStrategy.isExcluded(new Path("/~folder/.doc2/test"), null, patterns));
		assertTrue(ResourcePatternExclusionStrategy.isExcluded(new Path("/~test.doc"), null, patterns));

		assertFalse(ResourcePatternExclusionStrategy.isExcluded(new Path("/test"), null, patterns));
		assertFalse(ResourcePatternExclusionStrategy.isExcluded(new Path("/test~"), null, patterns));

	}

	public void testNoWildcardExclusionPattern() {
		// folder
		Set<String> patterns = new HashSet<String>();
		patterns.addAll(ResourcePatternExclusionStrategy.convertToAntPattern("folder"));

		assertTrue(ResourcePatternExclusionStrategy.isExcluded(new Path("/folder/"), null, patterns));
		assertTrue(ResourcePatternExclusionStrategy.isExcluded(new Path("/folder"), null, patterns));
		assertTrue(ResourcePatternExclusionStrategy.isExcluded(new Path("/folder/.doc2/test"), null, patterns));
		assertTrue(ResourcePatternExclusionStrategy.isExcluded(new Path("/.doc2/folder/test"), null, patterns));
		assertTrue(ResourcePatternExclusionStrategy.isExcluded(new Path("/.doc2/test/folder"), null, patterns));

		assertFalse(ResourcePatternExclusionStrategy.isExcluded(new Path("/folder.test"), null, patterns));
		assertFalse(ResourcePatternExclusionStrategy.isExcluded(new Path("/.folder"), null, patterns));
		assertFalse(ResourcePatternExclusionStrategy.isExcluded(new Path("/folder2"), null, patterns));
		assertFalse(ResourcePatternExclusionStrategy.isExcluded(new Path("/.doc2folder/test"), null, patterns));
		assertFalse(ResourcePatternExclusionStrategy.isExcluded(new Path("/.doc"), null, patterns));
		assertFalse(ResourcePatternExclusionStrategy.isExcluded(new Path("/.doc2/test"), null, patterns));
	}

	public void testPathPrefixExclusionPattern() {
		// test/**
		Set<String> patterns = new HashSet<String>();
		patterns.add("/folder/**");

		assertTrue(ResourcePatternExclusionStrategy.isExcluded(new Path("/folder/"), null, patterns));
		assertTrue(ResourcePatternExclusionStrategy.isExcluded(new Path("/folder/.doc2/test"), null, patterns));

		assertFalse(ResourcePatternExclusionStrategy.isExcluded(new Path("/folder2/folder"), null, patterns));
		assertFalse(ResourcePatternExclusionStrategy.isExcluded(new Path("/.doc2/folder/test"), null, patterns));
		assertFalse(ResourcePatternExclusionStrategy.isExcluded(new Path("/.doc2/test/folder"), null, patterns));

	}

	public void testPathPostfixExclusionPattern() {
		// **/test/
		Set<String> patterns = new HashSet<String>();
		patterns.add("**/folder");

		assertTrue(ResourcePatternExclusionStrategy.isExcluded(new Path("/folder"), null, patterns));
		assertTrue(ResourcePatternExclusionStrategy.isExcluded(new Path("/folder1/folder"), null, patterns));

		assertFalse(ResourcePatternExclusionStrategy.isExcluded(new Path("/folder1/folder/folder2/test.doc"), null,
				patterns));
		assertFalse(ResourcePatternExclusionStrategy.isExcluded(new Path("/folder/.doc2/test"), null, patterns));
		assertFalse(ResourcePatternExclusionStrategy.isExcluded(new Path("/folder/.doc2/test"), null, patterns));
		assertFalse(ResourcePatternExclusionStrategy.isExcluded(new Path("/folder/folder2"), null, patterns));

	}

	public void testComplexExclusionPattern() {
		// **/folder/***.doc
		Set<String> patterns = new HashSet<String>();
		patterns.addAll(ResourcePatternExclusionStrategy.convertToAntPattern("**/folder/***.doc"));

		assertTrue(ResourcePatternExclusionStrategy.isExcluded(new Path("/folder/test.doc"), null, patterns));
		assertTrue(ResourcePatternExclusionStrategy.isExcluded(new Path("/folder/folder2/test.doc"), null, patterns));
		assertTrue(ResourcePatternExclusionStrategy.isExcluded(new Path("/folder1/folder/folder2/test.doc"), null,
				patterns));

		assertFalse(ResourcePatternExclusionStrategy.isExcluded(new Path("/folder1/folder/folder2/test.docx"), null,
				patterns));
		assertFalse(ResourcePatternExclusionStrategy.isExcluded(new Path("/folder1/folder2/folder3/test.doc"), null,
				patterns));
	}

	public void testInclusion() {
		IPath path4 = new Path("bla/bla");
		assertFalse(ResourcePatternExclusionStrategy.isExcluded(path4, null, new HashSet<String>()));
	}
}
