/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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

		// API-3.0 replace with context UI lazy start extension 
		// NOTE: used to trigger activation on start
		TestUtil.triggerContextUiLazyStart();

		// $JUnit-BEGIN$
		suite.addTestSuite(InteractionContextListeningTest.class);
		suite.addTestSuite(ScalingFactorsTest.class);
		suite.addTestSuite(InteractionContextTest.class);
		suite.addTestSuite(ContextExternalizerTest.class);
		suite.addTestSuite(DegreeOfInterestTest.class);
		suite.addTestSuite(ContextTest.class);
		suite.addTestSuite(InteractionEventTest.class);
		// $JUnit-END$
		return suite;
	}

}
