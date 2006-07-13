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

package org.eclipse.mylar.monitor;

import org.eclipse.mylar.context.core.InteractionEvent;
import org.eclipse.mylar.internal.context.core.MylarContextManager;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;

/**
 * @author Mik Kersten
 */
class ShellLifecycleListener implements ShellListener {

	private final MylarContextManager manager;

	public ShellLifecycleListener(MylarContextManager manager) {
		this.manager = manager;
		manager.handleActivityMetaContextEvent(new InteractionEvent(InteractionEvent.Kind.COMMAND, MylarContextManager.ACTIVITY_STRUCTURE_KIND,
				MylarContextManager.ACTIVITY_HANDLE_LIFECYCLE, MylarContextManager.ACTIVITY_ORIGIN_ID, null, MylarContextManager.ACTIVITY_DELTA_STARTED, 1f));
	}

	public void shellClosed(ShellEvent shellEvent) {
		manager.handleActivityMetaContextEvent(new InteractionEvent(InteractionEvent.Kind.COMMAND, MylarContextManager.ACTIVITY_STRUCTURE_KIND,
				MylarContextManager.ACTIVITY_HANDLE_ATTENTION, MylarContextManager.ACTIVITY_ORIGIN_ID, null, MylarContextManager.ACTIVITY_DELTA_DEACTIVATED, 1f));

		manager.deactivateAllContexts();
		
		manager.handleActivityMetaContextEvent(new InteractionEvent(InteractionEvent.Kind.COMMAND, MylarContextManager.ACTIVITY_STRUCTURE_KIND,
				MylarContextManager.ACTIVITY_HANDLE_LIFECYCLE, MylarContextManager.ACTIVITY_ORIGIN_ID, null, MylarContextManager.ACTIVITY_DELTA_STOPPED, 1f));
	
	}

	public void shellDeactivated(ShellEvent arg0) {
		// ignore
	}

	public void shellActivated(ShellEvent arg0) {
		// ignore
	}

	public void shellDeiconified(ShellEvent arg0) {
		// ingore
	}

	public void shellIconified(ShellEvent arg0) {
		// ignore
	}
}