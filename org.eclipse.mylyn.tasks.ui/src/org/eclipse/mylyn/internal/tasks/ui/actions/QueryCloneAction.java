/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jevgeni Holodkov - initial API and implementation
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
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
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
		IRepositoryQuery selectedQuery = getSelectedQuery(selection);
		action.setEnabled(true);
		if (selectedQuery != null) {
			action.setEnabled(true);
		} else {
			action.setEnabled(false);
		}
	}

	protected RepositoryQuery getSelectedQuery(ISelection newSelection) {
		if (selection instanceof StructuredSelection) {
			// allow to select only one element
			if (((StructuredSelection) selection).size() == 1) {
				Object selectedObject = ((StructuredSelection) selection).getFirstElement();
				if (selectedObject instanceof IRepositoryQuery) {
					return (RepositoryQuery) selectedObject;
				}
			}
		}
		return null;
	}

	public void run(RepositoryQuery selectedQuery) {
		if (selectedQuery == null) {
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					Messages.QueryCloneAction_Clone_Query, Messages.QueryCloneAction_No_query_selected);
			return;
		}

		List<RepositoryQuery> queries = new ArrayList<RepositoryQuery>();
		queries.add(selectedQuery);
		Document queryDoc = TasksUiPlugin.getTaskListWriter().createQueryDocument(queries);
		List<RepositoryQuery> clonedQueries = TasksUiPlugin.getTaskListWriter().readQueryDocument(queryDoc);

		if (clonedQueries.size() > 0) {
			for (RepositoryQuery query : clonedQueries) {
				query.setHandleIdentifier(TasksUiPlugin.getTaskList().getUniqueHandleIdentifier());
				TasksUiPlugin.getTaskList().addQuery(query);
				AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(query.getConnectorKind());
				TasksUiInternal.openEditQueryDialog(connectorUi, query);
			}
		} else {
			// cannot happen
			TasksUiInternal.displayStatus(Messages.QueryCloneAction_Clone_Query_Failes, new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
					Messages.QueryCloneAction_Query_cloning_did_not_succeeded));
		}
	}

}
