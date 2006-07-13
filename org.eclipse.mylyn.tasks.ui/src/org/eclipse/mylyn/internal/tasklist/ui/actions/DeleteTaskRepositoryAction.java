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

package org.eclipse.mylar.internal.tasklist.ui.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskRepositoriesView;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchImages;

/**
 * @author Mik Kersten
 */
public class DeleteTaskRepositoryAction extends Action {

	private static final String ID = "org.eclipse.mylar.tasklist.repositories.delete";

	private TaskRepositoriesView repositoriesView;

	public DeleteTaskRepositoryAction(TaskRepositoriesView repositoriesView) {
		this.repositoriesView = repositoriesView;
		setImageDescriptor(WorkbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
		setText("Delete Repository");
		setId(ID);
	}

	public void init(IViewPart view) {
		// ignore
	}

	public void run() {
		try {

			boolean deleteConfirmed = MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getShell(), "Confirm Delete", "Delete the selected task repositories?");
			if (deleteConfirmed) {
				IStructuredSelection selection = (IStructuredSelection) repositoriesView.getViewer().getSelection();
				Set<AbstractRepositoryQuery> queries = MylarTaskListPlugin.getTaskListManager().getTaskList()
						.getQueries();
				List<TaskRepository> repositoriesInUse = new ArrayList<TaskRepository>();
				List<TaskRepository> repositoriesToDelete = new ArrayList<TaskRepository>();
				for (Object selectedObject : selection.toList()) {
					if (selectedObject instanceof TaskRepository) {
						TaskRepository taskRepository = (TaskRepository) selectedObject;
						if (queries != null && queries.size() > 0) {
							for (AbstractRepositoryQuery query : queries) {
								if (query.getRepositoryUrl().equals(taskRepository.getUrl())) {
									repositoriesInUse.add(taskRepository);
									break;
								}
							}
						}
						if (!repositoriesInUse.contains(taskRepository)) {
							repositoriesToDelete.add(taskRepository);
						}
					}
				}

				for (TaskRepository taskRepository : repositoriesToDelete) {
					MylarTaskListPlugin.getRepositoryManager().removeRepository(taskRepository);
				}

				if (repositoriesInUse.size() > 0) {
					MessageDialog
							.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
									"Repository In Use",
									"One or more of the selected repositories is being used by a query and can not be deleted.");
				}
			}
		} catch (Exception e) {
			MylarStatusHandler.fail(e, e.getMessage(), true);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
	}
}
