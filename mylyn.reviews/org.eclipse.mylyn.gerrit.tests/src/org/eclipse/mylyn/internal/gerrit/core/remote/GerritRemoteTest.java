/*******************************************************************************
 * Copyright (c) 2013, 2016 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.gerrit.core.remote;

import static org.junit.jupiter.api.Assumptions.assumeFalse;

import org.eclipse.mylyn.gerrit.tests.AbstractGerritFixtureTest;
import org.eclipse.mylyn.gerrit.tests.support.GerritFixture;
import org.eclipse.mylyn.gerrit.tests.support.GerritProject;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.osgi.framework.Version;

/**
 * @author Miles Parker
 */
@SuppressWarnings("nls")
public class GerritRemoteTest extends AbstractGerritFixtureTest {
	@BeforeEach
	void skipIfExcluded() {
		assumeFalse(fixture.isExcluded(), "Fixture is excluded");
	}

	//The maximum difference between two dates to account for clock skew between test machines
	static final long CREATION_TIME_DELTA = 30 * 60 * 1000; //30 Minutes

	ReviewHarness reviewHarness;

	IReview getReview() {
		return reviewHarness.getReview();
	}

	@BeforeEach
	public void setUp() throws Exception {
		reviewHarness = new ReviewHarness();
		reviewHarness.init();
	}

	@AfterEach
	void tearDown() throws Exception {
		reviewHarness.dispose();
	}

	protected boolean canMakeMultipleCommits() {
		// when using cgit we can't make multiple commits on fixtures other than 2.10
		return !Boolean.getBoolean(GerritProject.PROP_ALTERNATE_PUSH) || isGerrit210();
	}

	private boolean isGerrit210() {
		Version version = GerritFixture.current().getGerritVersion();
		return version.getMajor() == 2 && version.getMinor() == 10;
	}
}
