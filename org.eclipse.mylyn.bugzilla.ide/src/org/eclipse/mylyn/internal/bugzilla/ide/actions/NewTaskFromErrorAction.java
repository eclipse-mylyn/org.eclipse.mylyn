/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Chris Aniszczyk <zx@us.ibm.com> - bug 208819
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ide.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.internal.tasks.ui.actions.NewTaskAction;
import org.eclipse.mylyn.tasks.core.TaskSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.internal.views.log.LogEntry;

/**
 * Creates a new task from the selected error log entry.
 * 
 * @author Jeff Pound
 * @author Steffen Pingel
 */
public class NewTaskFromErrorAction implements IObjectActionDelegate {

	public static final String ID = "org.eclipse.mylyn.tasklist.ui.repositories.actions.create";

	private LogEntry entry;

	public void run() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n\n-- Error Log --\nDate: ");
		sb.append(entry.getDate());
		sb.append("\nMessage: ");
		sb.append(entry.getMessage());
		sb.append("\nSeverity: " + entry.getSeverityText());
		sb.append("\nPlugin ID: ");
		sb.append(entry.getPluginId());
		sb.append("\nStack Trace:\n");
		if (entry.getStack() == null) {
			sb.append("no stack trace available");
		} else {
			sb.append(entry.getStack());
		}

		TaskSelection taskSelection = new TaskSelection("", sb.toString());
		NewTaskAction action = new NewTaskAction();
		action.showWizard(taskSelection);
	}

	public void run(IAction action) {
		run();
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	public void selectionChanged(IAction action, ISelection selection) {
		Object object = ((IStructuredSelection) selection).getFirstElement();
		if (object instanceof LogEntry) {
			entry = (LogEntry) object;
		}
	}

}
