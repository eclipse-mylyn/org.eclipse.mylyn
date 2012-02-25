/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.mylyn.reviews.tests.ui.ReviewUiTest;

/**
 * @author Steffen Pingel
 */
public class AllReviewsTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllReviewsTests.class.getName());
		suite.addTestSuite(ReviewUiTest.class);
		return suite;
	}

}
