/*******************************************************************************
 * Copyright (c) 2012, 2014 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.tests;

import org.eclipse.mylyn.reviews.spi.edit.remote.AbstractRemoteEditFactoryProviderTest;
import org.eclipse.mylyn.reviews.tests.ui.ReviewAnnotationModelTest;
import org.eclipse.mylyn.reviews.tests.ui.ReviewCompareAnnotationSupportTest;
import org.eclipse.mylyn.reviews.tests.ui.ReviewUiTest;
import org.eclipse.mylyn.reviews.tests.ui.UiDataLocatorTest;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Steffen Pingel
 */
public class AllReviewsTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllReviewsTests.class.getName());
		suite.addTestSuite(ReviewUiTest.class);
		suite.addTestSuite(UiDataLocatorTest.class);
		suite.addTestSuite(AbstractRemoteEditFactoryProviderTest.class);
		suite.addTestSuite(ReviewAnnotationModelTest.class);
		suite.addTestSuite(ReviewCompareAnnotationSupportTest.class);
		return suite;
	}

}
