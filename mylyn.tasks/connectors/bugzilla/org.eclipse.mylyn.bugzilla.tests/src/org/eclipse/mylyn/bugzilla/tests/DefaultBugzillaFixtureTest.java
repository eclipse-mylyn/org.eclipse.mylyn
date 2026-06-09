/*******************************************************************************
 * Copyright (c) 2025 frank
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html.
 *
 * Contributors:
 *      See git history
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.util.stream.Stream;

import org.eclipse.core.runtime.CoreException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@MethodSource("defaultFixtureProvider")
@SuppressWarnings("nls")
public class DefaultBugzillaFixtureTest extends AbstractBugzillaFixtureTest {

	static Stream<Arguments> defaultFixtureProvider() {
		return fixtureProvider(true);
	}

	@BeforeEach
	void checkService() {
		try {
			fixture.client();
		} catch (CoreException | IOException e) {
			System.err.println("\n*****\n* " + fixture.getRepositoryUrl() + " - " + e.getMessage() + "\n*****");
			assumeTrue(false, "Unable to connect to Bugzilla server: " + e.getMessage());
		}
	}

}
