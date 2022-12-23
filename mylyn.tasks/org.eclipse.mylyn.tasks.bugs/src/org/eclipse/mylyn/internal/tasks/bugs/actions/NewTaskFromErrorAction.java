/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Jeff Pound - intial API and implementation
 *     Tasktop Technologies - improvements
 *     Chris Aniszczyk <zx@us.ibm.com> - bug 208819
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.bugs.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.internal.commons.core.ErrorReporterManager;
import org.eclipse.mylyn.internal.tasks.bugs.wizards.ErrorLogStatus;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.internal.views.log.LogEntry;
import org.eclipse.ui.internal.views.log.LogSession;

/**
 * Creates a new task from the selected error log entry.
 * 
 * @author Jeff Pound
 * @author Steffen Pingel
 */
public class NewTaskFromErrorAction implements IObjectActionDelegate {

	public static final String ID = "org.eclipse.mylyn.tasklist.ui.repositories.actions.create"; //$NON-NLS-1$

	private LogEntry entry;

	private void createTask(LogEntry entry) {
		ErrorLogStatus status = createStatus(entry);
		new ErrorReporterManager().fail(status);
	}

	private ErrorLogStatus createStatus(LogEntry entry) {
		ErrorLogStatus status = new ErrorLogStatus(entry.getSeverity(), entry.getPluginId(), entry.getCode(),
				entry.getMessage());
		try {
			status.setDate(entry.getDate());
			status.setStack(entry.getStack());
			LogSession session = entry.getSession();
			if (session != null) {
				status.setLogSessionData(session.getSessionData());
			}

			if (entry.hasChildren()) {
				Object[] children = entry.getChildren(entry);
				if (children != null) {
					for (Object child : children) {
						if (child instanceof LogEntry) {
							ErrorLogStatus childStatus = createStatus((LogEntry) child);
							status.add(childStatus);
						}
					}
				}
			}
		} catch (Exception e) {
			// ignore any errors for setting additional attributes
		}
		return status;
	}

	public void run() {
		createTask(entry);
	}

	public void run(IAction action) {
		run();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		Object object = ((IStructuredSelection) selection).getFirstElement();
		if (object instanceof LogEntry) {
			entry = (LogEntry) object;
		}
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

}
