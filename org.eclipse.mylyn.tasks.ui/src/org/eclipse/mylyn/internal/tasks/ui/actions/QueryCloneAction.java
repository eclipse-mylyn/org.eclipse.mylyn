/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.w3c.dom.Document;

/**
 * Allow to clone a selected query.
 * 
 * @author Jevgeni Holodkov
 */
public class QueryCloneAction extends Action implements IViewActionDelegate {

	protected ISelection selection;

	public void init(IViewPart view) {
		// ignore
	}

	public void run(IAction action) {
		run(getSelectedQuery(selection));
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
		AbstractRepositoryQuery selectedQuery = getSelectedQuery(selection);
		action.setEnabled(true);
		if (selectedQuery != null) {
			action.setEnabled(true);
		} else {
			action.setEnabled(false);
		}
	}

	protected AbstractRepositoryQuery getSelectedQuery(ISelection newSelection) {
		if (selection instanceof StructuredSelection) {
			// allow to select only one element
			if (((StructuredSelection) selection).size() == 1) {
				Object selectedObject = ((StructuredSelection) selection).getFirstElement();
				if (selectedObject instanceof AbstractRepositoryQuery) {
					return (AbstractRepositoryQuery) selectedObject;
				}
			}
		}
		return null;
	}

	public void run(AbstractRepositoryQuery selectedQuery) {
		if (selectedQuery == null) {
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					ITasksUiConstants.TITLE_DIALOG, "No query selected.");
			return;
		}

		List<AbstractRepositoryQuery> queries = new ArrayList<AbstractRepositoryQuery>();
		queries.add(selectedQuery);

		Document queryDoc = TasksUiPlugin.getTaskListManager().getTaskListWriter().createQueryDocument(queries);
		List<AbstractRepositoryQuery> clonedQueries = TasksUiPlugin.getTaskListManager()
				.getTaskListWriter()
				.readQueryDocument(queryDoc);

		if (clonedQueries.size() > 0) {
			for (AbstractRepositoryQuery query : clonedQueries) {
				String handle = TasksUiPlugin.getTaskListManager().resolveIdentifiersConflict(query);
				query.setHandleIdentifier(handle);
				AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(query.getConnectorKind());
				connectorUi.openEditQueryDialog(query);
			}
		} else {
			// cannot happen
			StatusHandler.fail(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Query cloning did not succeeded.",
					new IllegalStateException(selectedQuery.toString())));
		}
	}

}
