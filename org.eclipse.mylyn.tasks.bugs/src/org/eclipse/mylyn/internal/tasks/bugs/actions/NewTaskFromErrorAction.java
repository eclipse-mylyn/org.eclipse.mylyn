/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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

	/**
	 * Fills a {@link StringBuilder} with {@link LogEntry} information, optionally including subentries too
	 * 
	 * @param entry
	 *            The {@link LogEntry} who provides the information
	 * @param sb
	 *            An {@link StringBuilder} to be filled with
	 * @param includeChildren
	 *            Indicates if it should include subentries, if the {@link LogEntry} have any
	 */
	private void buildDescriptionFromLogEntry(LogEntry entry, StringBuilder sb, boolean includeChildren) {
		sb.append(Messages.NewTaskFromErrorAction_ERROR_LOG_DATE);
		sb.append(entry.getDate());
		sb.append(Messages.NewTaskFromErrorAction_MESSGAE);
		sb.append(entry.getMessage());
		sb.append(Messages.NewTaskFromErrorAction_SEVERITY + entry.getSeverityText());
		sb.append(Messages.NewTaskFromErrorAction_PLUGIN_ID);
		sb.append(entry.getPluginId());
		sb.append(Messages.NewTaskFromErrorAction_STACK_TRACE);
		if (entry.getStack() == null) {
			sb.append(Messages.NewTaskFromErrorAction_no_stack_trace_available);
		} else {
			sb.append(entry.getStack());
		}

		if (includeChildren && entry.hasChildren()) {
			Object[] children = entry.getChildren(null);
			for (Object child : children) {
				if (child instanceof LogEntry) {
					buildDescriptionFromLogEntry((LogEntry) child, sb, includeChildren);
				}
			}
		}
	}

	private void createTask(LogEntry entry) {
		// FIXME reenable
//		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
//		boolean includeChildren = false;
//
//		if (entry.hasChildren()
//				&& MessageDialog.openQuestion(shell, "Report Bug", "Include children of this entry in the report?")) {
//			includeChildren = true;
//		}
//		StringBuilder sb = new StringBuilder();
//		buildDescriptionFromLogEntry(entry, sb, true);

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
