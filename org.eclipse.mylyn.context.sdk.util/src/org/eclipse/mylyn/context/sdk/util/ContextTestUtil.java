/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.context.sdk.util;

import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.internal.monitor.ui.MonitorUiPlugin;

/**
 * @author Steffen Pingel
 */
public class ContextTestUtil {

	private static boolean contextUiLazyStarted;

	/**
	 * Test cases that rely on lazy startup of Context Ui (e.g. context bridges) need to invoke this method prior to
	 * running the test.
	 */
	public static void triggerContextUiLazyStart() {
		if (contextUiLazyStarted) {
			return;
		}

		contextUiLazyStarted = true;

		// make sure monitor UI is started and logs the start interaction event 
		MonitorUiPlugin.getDefault();

		ContextCore.getContextManager().activateContext("startup");
		ContextCore.getContextManager().deactivateContext("startup");
	}

}
