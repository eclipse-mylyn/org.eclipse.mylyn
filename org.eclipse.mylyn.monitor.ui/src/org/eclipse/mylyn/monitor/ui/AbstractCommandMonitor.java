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

package org.eclipse.mylyn.monitor.ui;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.mylyn.core.MylarStatusHandler;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;

/**
 * Self-registering on construction.
 * 
 * @author Mik Kersten
 */
public abstract class AbstractCommandMonitor implements IExecutionListener {

	/**
	 * Workbench must be active.
	 */
	public AbstractCommandMonitor() {
		try {
			ICommandService commandService = (ICommandService) PlatformUI.getWorkbench().getAdapter(ICommandService.class);
			commandService.addExecutionListener(this);
		} catch (NullPointerException npe) {
			MylarStatusHandler.log("Monitors can not be instantiated until the workbench is active: ", this);
		}
	}

	public void dispose() {
		try {
			ICommandService commandService = (ICommandService) PlatformUI.getWorkbench().getAdapter(ICommandService.class);
			commandService.removeExecutionListener(this);
		} catch (NullPointerException npe) {
			MylarStatusHandler.log(npe, "Could not dispose monitor.");
		}
	}

	public void postExecuteFailure(String commandId, ExecutionException exception) {
		// don't care about this
	}

	public void notHandled(String commandId, NotHandledException exception) {
		// don't care about this
	}

	public void postExecuteSuccess(String commandId, Object returnValue) {
		// don't care about this
	}

	public void preExecute(String commandId, ExecutionEvent event) {
		if (commandId != null)
			handleCommandExecution(commandId, event);
	}

	protected abstract void handleCommandExecution(String commandId, ExecutionEvent event);
}
