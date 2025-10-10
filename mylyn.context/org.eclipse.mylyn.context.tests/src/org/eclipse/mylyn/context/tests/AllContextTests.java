/*******************************************************************************
 * Copyright (c) 2004, 2014 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.context.tests;

import org.eclipse.mylyn.context.sdk.util.ContextTestUtil;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Mik Kersten
 */
public class AllContextTests {

	public static Test suite() {
		ContextTestUtil.triggerContextUiLazyStart();

		TestSuite suite = new TestSuite(AllContextTests.class.getName());
		suite.addTestSuite(InteractionContextListeningTest.class);
		suite.addTestSuite(ScalingFactorsTest.class);
		suite.addTestSuite(InteractionContextTest.class);
		suite.addTestSuite(ContextExternalizerTest.class);
		suite.addTestSuite(DegreeOfInterestTest.class);
		suite.addTestSuite(ContextTest.class);
		suite.addTestSuite(InteractionEventTest.class);
		suite.addTestSuite(ShadowsBridgeTest.class);
		suite.addTestSuite(EditorStateParticipantTest.class);
		suite.addTestSuite(ToggleFocusActiveViewHandlerTest.class);
		return suite;
	}

}
