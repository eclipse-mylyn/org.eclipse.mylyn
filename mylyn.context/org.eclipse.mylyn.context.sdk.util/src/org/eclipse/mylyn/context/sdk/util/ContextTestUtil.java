/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
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
package org.eclipse.mylyn.context.sdk.util;

import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.internal.monitor.ui.MonitorUiPlugin;
import org.eclipse.swt.widgets.Display;

/**
 * @author Steffen Pingel
 */
@SuppressWarnings("nls")
public class ContextTestUtil {

	private static boolean contextUiLazyStarted;

	/**
	 * Test cases that rely on lazy startup of Context Ui (e.g. context bridges) need to invoke this method prior to running the test.
	 */
	public static void triggerContextUiLazyStart() {
		if (contextUiLazyStarted) {
			return;
		}

		contextUiLazyStarted = true;

		// make sure monitor UI is started and logs the start interaction event
		MonitorUiPlugin.getDefault();

		// ensure that initialization is processed
		while (Display.getDefault().readAndDispatch()) {
			// spin event loop
		}

		// ensure activation of context UI
		ContextUiPlugin.getDefault();

		ContextCore.getContextManager().activateContext("startup");
		ContextCore.getContextManager().deactivateContext("startup");
	}

}
