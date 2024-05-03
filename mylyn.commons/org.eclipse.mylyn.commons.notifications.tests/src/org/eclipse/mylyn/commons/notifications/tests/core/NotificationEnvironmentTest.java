/*******************************************************************************
 * Copyright (c) 2011, 2024 Tasktop Technologies.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     ArSysOp - ongoing support
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.commons.notifications.tests.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.mylyn.commons.core.CoreUtil;
import org.eclipse.mylyn.commons.notifications.core.IFilterable;
import org.eclipse.mylyn.commons.notifications.core.NotificationEnvironment;
import org.eclipse.mylyn.internal.commons.notifications.feed.FeedEntry;
import org.eclipse.osgi.service.resolver.VersionRange;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Version;

/**
 * @author Steffen Pingel
 */
@SuppressWarnings("nls")
public class NotificationEnvironmentTest {

	private class StubEntry extends FeedEntry implements IAdaptable, IFilterable {

		private final Map<String, String> map;

		public StubEntry(Map<String, String> map) {
			super("eventId");
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

		@Override
		public <T> T getAdapter(Class<T> adapter) {
			if (adapter == IFilterable.class) {
				return adapter.cast(this);
			}
			return null;
		}

	}

	private NotificationEnvironment environment;

	Set<String> installedFeatures;

	@Before
	public void setUp() throws Exception {
		installedFeatures = new HashSet<>();
		System.setProperty("EnvironmentTest", "2");
		environment = new NotificationEnvironment() {
			@Override
			public Set<String> getInstalledFeatures(IProgressMonitor monitor) {
				return installedFeatures;
			}
		};
	}

	@Test
	public void testGetRuntimeVersion() {
		Version runtimeVersion = environment.getRuntimeVersion();
		assertTrue("Expected value between 11.0-25.0, got " + runtimeVersion,
				new VersionRange("[11.0.0,25.0.0)").isIncluded(runtimeVersion));
	}

	@Test
	public void testGetPlatformVersion() {
		Version platformVersion = environment.getPlatformVersion();
		if (Platform.isRunning()) {
			assertTrue("Expected value between 3.3-5.0, got " + platformVersion,
					new VersionRange("[3.3.0,5.0.0)").isIncluded(platformVersion));
		} else {
			assertEquals(Version.emptyVersion, platformVersion);
		}
	}

	@Test
	public void testGetFrameworkVersion() {
		Version frameworkVersion = environment.getFrameworkVersion();
		if (Platform.isRunning()) {
			assertTrue("Expected value > 4.0, got " + frameworkVersion,
					new VersionRange("4.0.0").isIncluded(frameworkVersion));
		} else {
			assertEquals(CoreUtil.getFrameworkVersion(), frameworkVersion);
		}
	}

	@Test
	public void testMatchesFrameworkVersion() {
		Map<String, String> values = new HashMap<>();
		assertTrue(environment.matches(new StubEntry(values), null));

		values.put("frameworkVersion", "[1.0.0,2.0.0)");
		assertFalse(environment.matches(new StubEntry(values), null));

		values.put("frameworkVersion", "[0.0.0,10.0.0)");
		assertTrue(environment.matches(new StubEntry(values), null));
	}

	@Test
	public void testMatchesRequires() {
		Map<String, String> values = new HashMap<>();
		values.put("requires", "org.eclipse.mylyn");
		assertFalse(environment.matches(new StubEntry(values), null));

		installedFeatures.add("org.eclipse.mylyn");
		assertTrue(environment.matches(new StubEntry(values), null));
	}

	@Test
	public void testMatchesConflicts() {
		Map<String, String> values = new HashMap<>();
		values.put("conflicts", "org.eclipse.mylyn");
		assertTrue(environment.matches(new StubEntry(values), null));

		installedFeatures.add("org.eclipse.mylyn");
		assertFalse(environment.matches(new StubEntry(values), null));
	}

	@Test
	public void testMatchesRequiresConflicts() {
		Map<String, String> values = new HashMap<>();
		values.put("requires", "org.eclipse.mylyn");
		values.put("conflicts", "org.eclipse.cdt");
		assertFalse(environment.matches(new StubEntry(values), null));

		installedFeatures.add("org.eclipse.mylyn");
		assertTrue(environment.matches(new StubEntry(values), null));

		installedFeatures.add("org.eclipse.cdt");
		assertFalse(environment.matches(new StubEntry(values), null));
	}

	@Test
	public void testMatchesFilter() {
		Map<String, String> values = new HashMap<>();
		values.put("filter", "(EnvironmentTest<=1)");
		assertFalse(environment.matches(new StubEntry(values), null));

		values.put("filter", "(EnvironmentTest=2)");
		assertTrue(environment.matches(new StubEntry(values), null));
	}

}
