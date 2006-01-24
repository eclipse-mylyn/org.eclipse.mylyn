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

package org.eclipse.mylar.monitor.monitors;

import org.eclipse.mylar.core.InteractionEvent;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.internal.Workbench;

/**
 * Logs all bug root window selections (i.e. the window that the workbench is
 * launced with).
 * 
 * @author Leah Findlater and Mik Kersten
 */
public class WindowChangeMonitor implements IWindowListener {

	public static final String WINDOW_CLOSED = "closed";

	public static final String WINDOW_OPENED = "opened";

	public static final String ROOT_WINDOW_OPENED = "root";

	private IWorkbenchWindow rootWindow;

	public WindowChangeMonitor() {
		rootWindow = Workbench.getInstance().getActiveWorkbenchWindow();
	}

	// TODO: Should we add the default set of monitors to the new window as
	// well?
	public void windowOpened(IWorkbenchWindow window) {
		if (!window.equals(rootWindow)) {
			InteractionEvent interactionEvent = InteractionEvent.makeCommand(window.getClass().getCanonicalName(),
					WINDOW_OPENED);
			MylarPlugin.getDefault().notifyInteractionObserved(interactionEvent);
		}
	}

	public void windowClosed(IWorkbenchWindow window) {
		if (!window.equals(rootWindow)) {
			InteractionEvent interactionEvent = InteractionEvent.makeCommand(window.getClass().getCanonicalName(),
					WINDOW_CLOSED);
			MylarPlugin.getDefault().notifyInteractionObserved(interactionEvent);
		}
	}

	public void windowDeactivated(IWorkbenchWindow window) {
		// Do nothing
	}

	public void windowActivated(IWorkbenchWindow window) {
		// Do nothing
	}
}
