/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.monitor.ui.workbench;

import org.eclipse.mylar.monitor.core.InteractionEvent;
import org.eclipse.mylar.monitor.ui.MylarMonitorUiPlugin;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * Logs all bug root window selections (i.e. the window that the workbench is
 * launced with).
 * 
 * @author Leah Findlater and Mik Kersten
 */
public class WindowChangeMonitor implements IWindowListener {

	public static final String WINDOW_CLOSED = "closed";

	public static final String WINDOW_OPENED = "opened";

	public static final String WINDOW_ACTIVATED = "activated";

	public static final String WINDOW_DEACTIVATED = "deactivated";

	public WindowChangeMonitor() {
		super();
	}

	// TODO: Should we add the default set of monitors to the new window as
	// well?
	public void windowOpened(IWorkbenchWindow window) {
			InteractionEvent interactionEvent = InteractionEvent.makeCommand(getWindowOrigin(window),
					WINDOW_OPENED);
			MylarMonitorUiPlugin.getDefault().notifyInteractionObserved(interactionEvent);
	}

	public void windowClosed(IWorkbenchWindow window) {
			InteractionEvent interactionEvent = InteractionEvent.makeCommand(getWindowOrigin(window),
					WINDOW_CLOSED);
			MylarMonitorUiPlugin.getDefault().notifyInteractionObserved(interactionEvent);
	}

	public void windowDeactivated(IWorkbenchWindow window) {
//		InteractionEvent interactionEvent = InteractionEvent.makeCommand(getWindowOrigin(window),
//				WINDOW_DEACTIVATED);
//		MylarMonitorUiPlugin.getDefault().notifyInteractionObserved(interactionEvent);
	}

	public void windowActivated(IWorkbenchWindow window) {
		InteractionEvent interactionEvent = InteractionEvent.makeCommand(getWindowOrigin(window),
				WINDOW_ACTIVATED);
		MylarMonitorUiPlugin.getDefault().notifyInteractionObserved(interactionEvent);
	}

	protected String getWindowOrigin(IWorkbenchWindow window) {
		return window.getClass().getCanonicalName();// + "@" + window.hashCode();
	}
}
