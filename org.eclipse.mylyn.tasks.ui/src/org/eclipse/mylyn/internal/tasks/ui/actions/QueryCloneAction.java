/*******************************************************************************
 * Copyright (c) 2004, 2008 Jevgeni Holodkov and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jevgeni Holodkov - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

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

		List<RepositoryQuery> clonedQueries = new ArrayList<RepositoryQuery>(queries.size());
		for (RepositoryQuery query : queries) {
			TaskRepository repository = TasksUi.getRepositoryManager().getRepository(query.getConnectorKind(),
					query.getRepositoryUrl());
			RepositoryQuery clonedQuery = (RepositoryQuery) TasksUi.getRepositoryModel().createRepositoryQuery(
					repository);
			clonedQuery.setSummary(NLS.bind(Messages.QueryCloneAction_Copy_of_X, query.getSummary()));
			clonedQuery.setUrl(query.getUrl());
			Map<String, String> attributes = query.getAttributes();
			for (Map.Entry<String, String> entry : attributes.entrySet()) {
				clonedQuery.setAttribute(entry.getKey(), entry.getValue());
			}
			clonedQueries.add(clonedQuery);
		}
		for (RepositoryQuery query : clonedQueries) {
			TasksUiPlugin.getTaskList().addQuery(query);
			AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(query.getConnectorKind());
			TasksUiInternal.openEditQueryDialog(connectorUi, query);
		}
	}
}
