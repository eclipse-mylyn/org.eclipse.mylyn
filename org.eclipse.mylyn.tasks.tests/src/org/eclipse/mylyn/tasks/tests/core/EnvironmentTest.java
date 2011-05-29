/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.mylyn.internal.tasks.core.notifications.Environment;
import org.eclipse.mylyn.internal.tasks.core.notifications.FeedEntry;
import org.eclipse.osgi.service.resolver.VersionRange;
import org.osgi.framework.Version;

/**
 * @author Steffen Pingel
 */
public class EnvironmentTest extends TestCase {

	private class StubEntry extends FeedEntry {

		private final Map<String, String> map;

		public StubEntry(Map<String, String> map) {
			this.map = map;
		}

		@Override
		public List<String> getFilters(String key) {
			String value = getFilter(key);
			if (value != null) {
				return Collections.singletonList(value);
			}
			return Collections.emptyList();
		}

		@Override
		public String getFilter(String key) {
			return map.get(key);
		}

	}

	private Environment environment;

	Set<String> installedFeatures;

	@Override
	protected void setUp() throws Exception {
		installedFeatures = new HashSet<String>();
		environment = new Environment() {
			@Override
			public Set<String> getInstalledFeatures(IProgressMonitor monitor) {
				return installedFeatures;
			}
		};
	}

	public void testGetRuntimeVersion() {
		Version runtimeVersion = environment.getRuntimeVersion();
		assertTrue("Expected value between 1.5-1.8, got " + runtimeVersion,
				new VersionRange("[1.5.0,1.8.0)").isIncluded(runtimeVersion));
	}

	public void testGetPlatformVersion() {
		Version platformVersion = environment.getPlatformVersion();
		if (Platform.isRunning()) {
			assertTrue("Expected value between 3.4-5.0, got " + platformVersion,
					new VersionRange("[3.4.0,5.0.0)").isIncluded(platformVersion));
		} else {
			assertEquals(Version.emptyVersion, platformVersion);
		}
	}

	public void testGetFrameworkVersion() {
		Version frameworkVersion = environment.getFrameworkVersion();
		if (Platform.isRunning()) {
			assertTrue("Expected value > 3.6, got " + frameworkVersion,
					new VersionRange("3.6.0").isIncluded(frameworkVersion));
		} else {
			assertEquals(Version.emptyVersion, frameworkVersion);
		}
	}

	public void testMatchesFrameworkVersion() {
		Map<String, String> values = new HashMap<String, String>();
		assertTrue(environment.matches(new StubEntry(values), null));

		values.put("frameworkVersion", "[1.0.0,2.0.0)");
		assertFalse(environment.matches(new StubEntry(values), null));

		values.put("frameworkVersion", "[0.0.0,10.0.0)");
		assertTrue(environment.matches(new StubEntry(values), null));
	}

	public void testMatchesRequires() {
		Map<String, String> values = new HashMap<String, String>();
		values.put("requires", "org.eclipse.mylyn");
		assertFalse(environment.matches(new StubEntry(values), null));

		installedFeatures.add("org.eclipse.mylyn");
		assertTrue(environment.matches(new StubEntry(values), null));
	}

	public void testMatchesConflicts() {
		Map<String, String> values = new HashMap<String, String>();
		values.put("conflicts", "org.eclipse.mylyn");
		assertTrue(environment.matches(new StubEntry(values), null));

		installedFeatures.add("org.eclipse.mylyn");
		assertFalse(environment.matches(new StubEntry(values), null));
	}

	public void testMatchesRequiresConflicts() {
		Map<String, String> values = new HashMap<String, String>();
		values.put("requires", "org.eclipse.mylyn");
		values.put("conflicts", "org.eclipse.cdt");
		assertFalse(environment.matches(new StubEntry(values), null));

		installedFeatures.add("org.eclipse.mylyn");
		assertTrue(environment.matches(new StubEntry(values), null));

		installedFeatures.add("org.eclipse.cdt");
		assertFalse(environment.matches(new StubEntry(values), null));
	}

}
