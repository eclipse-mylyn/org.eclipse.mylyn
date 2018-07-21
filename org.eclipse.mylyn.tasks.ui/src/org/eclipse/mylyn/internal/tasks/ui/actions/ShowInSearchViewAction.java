/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import java.util.Iterator;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.internal.tasks.ui.search.SearchUtil;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
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

	@Override
	public void run() {
		if (super.getStructuredSelection() != null) {
			for (Iterator<?> iter = super.getStructuredSelection().iterator(); iter.hasNext();) {
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
				SearchUtil.runSearchQuery(TasksUiInternal.getTaskList(), taskRepository, query);
			}
		}
	}

	@Override
	protected boolean updateSelection(IStructuredSelection selection) {
		return SearchUtil.supportsTaskSearch() && selection.size() == 1
				&& selection.getFirstElement() instanceof IRepositoryQuery;
	}

}