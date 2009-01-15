/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.context.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.mylyn.context.tests.support.TestUtil;

/**
 * @author Mik Kersten
 */
public class AllContextTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.eclipse.mylyn.context.tests");

		TestUtil.triggerContextUiLazyStart();

		// $JUnit-BEGIN$
		suite.addTestSuite(InteractionContextListeningTest.class);
		suite.addTestSuite(ScalingFactorsTest.class);
		suite.addTestSuite(InteractionContextTest.class);
		suite.addTestSuite(ContextExternalizerTest.class);
		suite.addTestSuite(DegreeOfInterestTest.class);
		suite.addTestSuite(ContextTest.class);
		suite.addTestSuite(InteractionEventTest.class);
		suite.addTestSuite(ShadowsBridgeTest.class);
		// $JUnit-END$
		return suite;
	}

}
