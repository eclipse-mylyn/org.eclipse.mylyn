/*******************************************************************************
 * Copyright (c) 2012, 2015 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.ui.tests;

import org.eclipse.mylyn.internal.reviews.ui.annotations.CommentPopupDialogTest;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllReviewsUiTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllReviewsUiTests.class.getName());
		suite.addTestSuite(CommentPopupDialogTest.class);
		return suite;
	}

}
