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
import org.eclipse.mylyn.reviews.tests.ui.ReviewColumnLabelProviderTest;
import org.eclipse.mylyn.reviews.tests.ui.ReviewCompareAnnotationSupportTest;
import org.eclipse.mylyn.reviews.tests.ui.ReviewUiTest;
import org.eclipse.mylyn.reviews.tests.ui.UiDataLocatorTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * @author Steffen Pingel
 */
@Suite
@SelectClasses({ ReviewUiTest.class, UiDataLocatorTest.class, AbstractRemoteEditFactoryProviderTest.class,
		ReviewAnnotationModelTest.class, ReviewCompareAnnotationSupportTest.class,
		ReviewColumnLabelProviderTest.class })
public class AllReviewsTests {
}
