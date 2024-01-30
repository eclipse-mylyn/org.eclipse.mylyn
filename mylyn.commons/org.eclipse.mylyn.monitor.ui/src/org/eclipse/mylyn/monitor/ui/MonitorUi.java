/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.monitor.ui;

import java.util.List;
import java.util.Set;

import org.eclipse.mylyn.internal.monitor.ui.MonitorUiPlugin;
import org.eclipse.mylyn.monitor.core.IInteractionEventListener;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * @author Steffen Pingel
 * @author Mik Kersten
 * @since 3.0
 */
public class MonitorUi {

	public static void addWindowPostSelectionListener(ISelectionListener listener) {
		MonitorUiPlugin.getDefault().addWindowPostSelectionListener(listener);
	}

	public static void removeWindowPostSelectionListener(ISelectionListener listener) {
		MonitorUiPlugin.getDefault().removeWindowPostSelectionListener(listener);
	}

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

	/**
	 * This call returns the first workbench window that when Monitor UI was started. It is not guaranteed that this window still exists.
	 * 
	 * @deprecated
	 */
	@Deprecated
	public static IWorkbenchWindow getLaunchingWorkbenchWindow() {
		return MonitorUiPlugin.getDefault().getLaunchingWorkbenchWindow();
	}

	public static Set<IWorkbenchWindow> getMonitoredWindows() {
		return MonitorUiPlugin.getDefault().getMonitoredWindows();
	}

	public static void addWindowPartListener(IPartListener listener) {
		MonitorUiPlugin.getDefault().addWindowPartListener(listener);
	}

	public static void removeWindowPartListener(IPartListener listener) {
		MonitorUiPlugin.getDefault().removeWindowPartListener(listener);
	}

	public static void addWindowPerspectiveListener(IPerspectiveListener listener) {
		MonitorUiPlugin.getDefault().addWindowPerspectiveListener(listener);
	}

	public static void removeWindowPerspectiveListener(IPerspectiveListener listener) {
		MonitorUiPlugin.getDefault().removeWindowPerspectiveListener(listener);
	}
}
