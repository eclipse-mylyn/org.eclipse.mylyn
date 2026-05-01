/*******************************************************************************
 * Copyright (c) 2025 George Lindholm
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html.
 *
 * Contributors:
 *      See git history
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.rest.test.support;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.eclipse.mylyn.commons.sdk.util.TestConfiguration;
import org.eclipse.mylyn.commons.sdk.util.junit5.EnabledIfCI;
import org.eclipse.mylyn.commons.sdk.util.junit5.MylynTestSetup;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.Parameter;
import org.junit.jupiter.params.ParameterizedClass;
import org.junit.jupiter.params.provider.Arguments;

@ParameterizedClass(name = "{1}")
@EnabledIfCI
@MylynTestSetup
@Timeout(value = 5, unit = TimeUnit.MINUTES) // overall timeout for each test

@SuppressWarnings({ "nls", "restriction" })
public abstract class AbstractFixtureTest {

	private static List<BugzillaRestTestFixture>[] discoveredFixtures = new List[2];

	@Parameter(0)
	protected static BugzillaRestTestFixture fixture;

	@Parameter(1)
	protected String info;

	static Stream<Arguments> fixtureProvider(boolean defaultOnly) {
		if (discoveredFixtures[defaultOnly ? 1 : 0] == null) {
			TestConfiguration defFixture = TestConfiguration.getDefault();
			discoveredFixtures[defaultOnly ? 1 : 0] = (List<BugzillaRestTestFixture>) defFixture
					.discover(BugzillaRestTestFixture.class, "bugzillaREST", defaultOnly);
			assertTrue(discoveredFixtures[defaultOnly ? 1 : 0].size() > 0, "No fixtures discovered");
			for (BugzillaRestTestFixture fixture : discoveredFixtures[defaultOnly ? 1 : 0]) {
				System.out.println("Discovered fixture: " + fixture.getInfo());
			}
		}
		return discoveredFixtures[defaultOnly ? 1 : 0].stream()
				.map(fixture -> Arguments.of(fixture, fixture.getInfo()));
	}
}