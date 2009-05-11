/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import java.util.Iterator;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.internal.tasks.ui.search.SearchHitCollector;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Steffen Pingel
 */
public class ShowInSearchViewAction extends BaseSelectionListenerAction {

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.open.browser"; //$NON-NLS-1$

	public ShowInSearchViewAction() {
		super(Messages.ShowInSearchViewAction_Open_in_Search_Label);
		setId(ID);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		if (super.getStructuredSelection() != null) {
			for (Iterator iter = super.getStructuredSelection().iterator(); iter.hasNext();) {
				runWithSelection(iter.next());
			}
		}
	}

	private void runWithSelection(Object selectedObject) {
		if (selectedObject instanceof IRepositoryQuery) {
			IRepositoryQuery query = (IRepositoryQuery) selectedObject;
			AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
					query.getConnectorKind());
			TaskRepository taskRepository = TasksUi.getRepositoryManager().getRepository(query.getConnectorKind(),
					query.getRepositoryUrl());
			if (connector != null) {
				SearchHitCollector collector = new SearchHitCollector(TasksUiInternal.getTaskList(), taskRepository,
						query);
				NewSearchUI.runQueryInBackground(collector);
			}
		}
	}

	@Override
	protected boolean updateSelection(IStructuredSelection selection) {
		return selection.size() == 1 && selection.getFirstElement() instanceof IRepositoryQuery;
	}

}