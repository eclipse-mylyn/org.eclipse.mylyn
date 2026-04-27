/*******************************************************************************
 * Copyright (c) 2026 george
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html.
 *
 * Contributors:
 *      See git history
 *******************************************************************************/

package org.eclipse.mylyn.debug.tests;

import org.eclipse.mylyn.internal.debug.ui.BreakpointsContextUtilTest;
import org.eclipse.mylyn.internal.debug.ui.BreakpointsStateUtilTest;
import org.eclipse.mylyn.internal.debug.ui.BreakpointsStructureBridgeTest;
import org.eclipse.mylyn.internal.debug.ui.BreakpointsTestUtil;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({ BreakpointsContextUtilTest.class, BreakpointsStateUtilTest.class, BreakpointsStructureBridgeTest.class,
	BreakpointsTestUtil.class })

public class AllDebugTests {

}
