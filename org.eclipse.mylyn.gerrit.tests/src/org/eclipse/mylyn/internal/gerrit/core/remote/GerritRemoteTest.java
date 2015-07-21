/*******************************************************************************
 * Copyright (c) 2013, 2015 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.remote;

import junit.framework.TestCase;

import org.eclipse.mylyn.reviews.core.model.IReview;
import org.junit.After;
import org.junit.Before;

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
}
