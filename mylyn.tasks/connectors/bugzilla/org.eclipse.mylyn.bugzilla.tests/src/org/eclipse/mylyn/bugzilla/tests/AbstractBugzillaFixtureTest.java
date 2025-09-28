/*******************************************************************************
 * Copyright (c) 2025 george
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html.
 *
 * Contributors:
 *      See git history
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.eclipse.mylyn.bugzilla.tests.support.BugzillaFixture;
import org.eclipse.mylyn.commons.sdk.util.TestConfiguration;
import org.eclipse.mylyn.commons.sdk.util.junit5.EnabledIfCI;
import org.eclipse.mylyn.commons.sdk.util.junit5.MylynTestSetup;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.Parameter;
import org.junit.jupiter.params.ParameterizedClass;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@ParameterizedClass(name = "{1}")
@MethodSource("fixtureProvider")
@EnabledIfCI
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // so that the dummy "done" test is always last
@MylynTestSetup
@Timeout(value = 5, unit = TimeUnit.MINUTES) // overall timeout for each test
@SuppressWarnings("nls")
public abstract class AbstractBugzillaFixtureTest {

	private static List<BugzillaFixture> discoveredFixtures;

	@Parameter(0)
	protected static BugzillaFixture fixture;

	@Parameter(1)
	protected String info;

	static Stream<Arguments> fixtureProvider() {
		if (discoveredFixtures == null) {
			TestConfiguration defFixture = TestConfiguration.getDefault();
			discoveredFixtures = (List<BugzillaFixture>) defFixture.discover(BugzillaFixture.class,
					"bugzilla", false);
			assertTrue(discoveredFixtures.size() > 0, "No fixtures discovered");
			for (BugzillaFixture fixture : discoveredFixtures) {
				System.out.println("Discovered fixture: " + fixture.getInfo());
			}
		}
		return discoveredFixtures.stream()
				.map(fixture -> Arguments.of(fixture, fixture.getInfo()));
	}

	/**
	 * This is a dummy test so that classes with a single test method shows up in the JUnit view.
	 */
	@Test
	@Order(Integer.MAX_VALUE)
	void done() {
	}
}