/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.monitor.ui;

import java.util.List;

import org.eclipse.mylyn.internal.monitor.ui.MonitorUiPlugin;
import org.eclipse.mylyn.monitor.core.IInteractionEventListener;

/**
 * @author Steffen Pingel
 * @since 3.0
 */
public class MonitorUi {

	public static void addInteractionListener(IInteractionEventListener listener) {
		MonitorUiPlugin.getDefault().addInteractionListener(listener);
	}

	public static List<AbstractUserInteractionMonitor> getSelectionMonitors() {
		return MonitorUiPlugin.getDefault().getSelectionMonitors();
	}

	public static void removeInteractionListener(IInteractionEventListener listener) {
		MonitorUiPlugin.getDefault().removeInteractionListener(listener);
	}

	public static IActivityContextManager getActivityContextManager() {
		return MonitorUiPlugin.getDefault().getActivityContextManager();
	}

}
