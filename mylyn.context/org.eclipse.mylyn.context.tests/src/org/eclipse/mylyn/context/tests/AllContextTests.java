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
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.context.tests;

import org.eclipse.mylyn.context.sdk.util.ContextTestUtil;
import org.junit.platform.suite.api.BeforeSuite;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * @author Mik Kersten
 */
@Suite
@SelectClasses({ ContextExternalizerTest.class, ContextTest.class, DegreeOfInterestTest.class,
	EditorStateParticipantTest.class, InteractionContextListeningTest.class, InteractionContextTest.class,
	InteractionEventTest.class, ScalingFactorsTest.class, ShadowsBridgeTest.class,
	ToggleFocusActiveViewHandlerTest.class })
public class AllContextTests {
	@BeforeSuite
	static void suiteSetup() {
		ContextTestUtil.triggerContextUiLazyStart();
	}
}
