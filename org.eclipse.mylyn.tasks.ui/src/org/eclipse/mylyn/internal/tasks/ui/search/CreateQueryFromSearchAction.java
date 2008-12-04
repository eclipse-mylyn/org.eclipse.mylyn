/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Balazs Brinkus - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.search;

import java.text.MessageFormat;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.PlatformUI;

/**
 * Used for add the last search result to the Task List.
 * 
 * @author Balazs Brinkus (bug 172699)
 * @author Mik Kersten
 */
public class CreateQueryFromSearchAction extends Action {

	/** The view this action works on */
	private final RepositorySearchResultView resultView;

	/**
	 * Constructor
	 * 
	 * @param text
	 *            The text for this action
	 * @param resultView
	 *            The <code>RepositorySearchResultView</code> this action works on
	 */
	public CreateQueryFromSearchAction(String text, RepositorySearchResultView resultView) {
		setText(text);
		setImageDescriptor(TasksUiImages.QUERY_NEW);
		this.resultView = resultView;
	}

	/**
	 * Add the search result to the Task List.
	 */
	@Override
	public void run() {
		ISelection selection = resultView.getViewer().getSelection();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			if (structuredSelection.getFirstElement() instanceof ITask) {
				ISearchQuery[] queries = NewSearchUI.getQueries();
				ITask task = (ITask) structuredSelection.getFirstElement();
				AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
						task.getConnectorKind());
				if (queries.length != 0 && connector != null) {
					SearchHitCollector searchHitCollector = (SearchHitCollector) queries[0];
					IRepositoryQuery query = searchHitCollector.getRepositoryQuery();
					InputDialog dialog = new InputDialog(PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow()
							.getShell(), Messages.CreateQueryFromSearchAction_CLEAR_QUERY, MessageFormat.format(
							Messages.CreateQueryFromSearchAction_Name_of_query_to_be_added_to_the_X, TaskListView.LABEL_VIEW)
							+ ": ", "", null); //$NON-NLS-1$ //$NON-NLS-2$
					int dialogResult = dialog.open();
					if (dialogResult == Window.OK) {
						query.setSummary(dialog.getValue());
						TasksUiInternal.getTaskList().addQuery((RepositoryQuery) query);
						TasksUiInternal.synchronizeQuery(connector, (RepositoryQuery) query, null, true);
					}
				}
			}
		}
	}

}
