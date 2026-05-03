/*******************************************************************************
 * Copyright (c) 2014 Tasktop Technologies and others.
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

package org.eclipse.mylyn.gerrit.tests.core.client;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import org.eclipse.mylyn.gerrit.tests.AbstractGerritFixtureTest;
import org.eclipse.mylyn.gerrit.tests.support.GerritFixture;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritCapabilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.osgi.framework.Version;

@SuppressWarnings("nls")
@Disabled("No gerrit instance available")
public class GerritCapabilitiesTest extends AbstractGerritFixtureTest {
	@BeforeEach
	void skipIfExcluded() {
		assumeFalse(fixture.isExcluded(), "Fixture is excluded");
	}

	@Test
	public void testIsSupported() {
		Version version = GerritFixture.current().getGerritVersion();
		GerritCapabilities capabilities = new GerritCapabilities(version);
		assertTrue(capabilities.isSupported());
	}

}
