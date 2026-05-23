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

package org.eclipse.mylyn.jenkins.tests;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.eclipse.mylyn.commons.sdk.util.TestConfiguration;
import org.eclipse.mylyn.commons.sdk.util.junit5.EnabledIfCI;
import org.eclipse.mylyn.commons.sdk.util.junit5.MylynTestSetup;
import org.eclipse.mylyn.jenkins.tests.support.JenkinsFixture;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.Parameter;
import org.junit.jupiter.params.ParameterizedClass;
import org.junit.jupiter.params.provider.Arguments;

@ParameterizedClass(name = "{1}")
@EnabledIfCI
@MylynTestSetup
@Timeout(value = 5, unit = TimeUnit.MINUTES) // overall timeout for each test

@SuppressWarnings("nls")
public abstract class AbstractFixtureTest {

	private static List<JenkinsFixture>[] discoveredFixtures = new List[2];

	@Parameter(0)
	protected static JenkinsFixture fixture;

	@Parameter(1)
	protected String info;

	static Stream<Arguments> fixtureProvider(boolean defaultOnly) {
		final int idx = defaultOnly ? 1 : 0;
		if (discoveredFixtures[idx] == null) {
			TestConfiguration defFixture = TestConfiguration.getDefault();
			try {
				discoveredFixtures[idx] = (List<JenkinsFixture>) defFixture.discover(JenkinsFixture.class, "jenkins",
						defaultOnly);
			} catch (RuntimeException e) {
				System.err.println("Error discovering Jenkins fixtures: " + e.getMessage());
			}
			assumeTrue(discoveredFixtures[idx] != null && discoveredFixtures[idx].size() > 0, "No fixtures discovered");
			for (JenkinsFixture fixture : discoveredFixtures[idx]) {
				System.out.println("Discovered fixture: " + fixture.getInfo());
			}
		}
		return discoveredFixtures[idx].stream().map(fixture -> Arguments.of(fixture, fixture.getInfo()));
	}
}