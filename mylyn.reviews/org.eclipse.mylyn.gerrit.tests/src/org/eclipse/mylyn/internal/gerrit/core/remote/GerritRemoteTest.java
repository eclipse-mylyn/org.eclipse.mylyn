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
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.remote;

import org.eclipse.mylyn.gerrit.tests.support.GerritFixture;
import org.eclipse.mylyn.gerrit.tests.support.GerritProject;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.junit.After;
import org.junit.Before;
import org.osgi.framework.Version;

import junit.framework.TestCase;

/**
 * @author Miles Parker
 */
public class GerritRemoteTest extends TestCase {

	//The maximum difference between two dates to account for clock skew between test machines
	static final long CREATION_TIME_DELTA = 30 * 60 * 1000; //30 Minutes

	ReviewHarness reviewHarness;

	IReview getReview() {
		return reviewHarness.getReview();
	}

	@Override
	@Before
	public void setUp() throws Exception {
		reviewHarness = new ReviewHarness();
		reviewHarness.init();
	}

	@Override
	@After
	public void tearDown() throws Exception {
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
