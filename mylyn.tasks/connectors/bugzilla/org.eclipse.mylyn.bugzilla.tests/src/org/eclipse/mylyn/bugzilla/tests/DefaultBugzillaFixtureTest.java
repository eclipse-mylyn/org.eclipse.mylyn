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

import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@MethodSource("defaultFixtureProvider")
public class DefaultBugzillaFixtureTest extends AbstractBugzillaFixtureTest {

	static Stream<Arguments> defaultFixtureProvider() {
		return fixtureProvider(true);
	}

}
