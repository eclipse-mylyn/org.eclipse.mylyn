/*******************************************************************************
 * Copyright (c) 2012, 2014 Tasktop Technologies and others.
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

import org.eclipse.mylyn.reviews.spi.edit.remote.AbstractRemoteEditFactoryProviderTest;
import org.eclipse.mylyn.reviews.tests.ui.ReviewAnnotationModelTest;
import org.eclipse.mylyn.reviews.tests.ui.ReviewCompareAnnotationSupportTest;
import org.eclipse.mylyn.reviews.tests.ui.ReviewUiTest;
import org.eclipse.mylyn.reviews.tests.ui.UiDataLocatorTest;

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
