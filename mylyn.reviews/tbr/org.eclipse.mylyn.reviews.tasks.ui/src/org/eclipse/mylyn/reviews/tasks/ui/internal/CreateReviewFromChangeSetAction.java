/*******************************************************************************
 * Copyright (c) 2010 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Research Group for Industrial Software (INSO), Vienna University of Technology - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.tasks.ui.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.reviews.tasks.core.ChangesetScopeItem;
import org.eclipse.mylyn.reviews.tasks.core.IReviewMapper;
import org.eclipse.mylyn.reviews.tasks.core.ITaskProperties;
import org.eclipse.mylyn.reviews.tasks.core.ReviewScope;
import org.eclipse.mylyn.reviews.tasks.core.internal.TaskProperties;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.ITaskDataManager;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskMapper;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.versions.tasks.core.TaskChangeSet;
import org.eclipse.ui.IActionDelegate;

/**
 * 
 * @author mattk
 *
 */
@SuppressWarnings("restriction")
public class CreateReviewFromChangeSetAction extends Action implements
		IActionDelegate {

	private IStructuredSelection selection;

	public void run(IAction action) {
		try {
			ITask task = ((TaskChangeSet)selection.getFirstElement()).getTask();
			TaskRepository taskRepository = TasksUi.getRepositoryManager()
					.getRepository(task.getConnectorKind(),
							task.getRepositoryUrl());
			ITaskDataManager manager = TasksUi.getTaskDataManager();
			TaskData parentTaskData = manager.getTaskData(task);
			ITaskProperties parentTask = TaskProperties.fromTaskData(manager,
					parentTaskData);

			TaskMapper initializationData = new TaskMapper(parentTaskData);
			IReviewMapper taskMapper = ReviewsUiPlugin.getMapper();

			TaskData taskData = TasksUiInternal.createTaskData(taskRepository,
					initializationData, null, new NullProgressMonitor());
			AbstractRepositoryConnector connector = TasksUiPlugin
					.getConnector(taskRepository.getConnectorKind());

			connector.getTaskDataHandler().initializeSubTaskData(
					taskRepository, taskData, parentTaskData,
					new NullProgressMonitor());

			ITaskProperties taskProperties = TaskProperties.fromTaskData(
					manager, taskData);
			taskProperties
					.setSummary("[review] " + parentTask.getDescription());

			String reviewer = taskRepository.getUserName();
			taskProperties.setAssignedTo(reviewer);

			initTaskProperties(taskMapper, taskProperties, parentTask,task);

			TasksUiInternal.createAndOpenNewTask(taskData);
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}

	}

	private void initTaskProperties(IReviewMapper taskMapper,
			ITaskProperties taskProperties, ITaskProperties parentTask,ITask task) {
		taskMapper.mapScopeToTask(getScope(task), taskProperties);
	}

	private ReviewScope getScope(ITask task) {
		List<TaskChangeSet> changesets = getSelectedChangesets(task);
		ReviewScope scope = new ReviewScope();
		for (TaskChangeSet cs : changesets) {
			scope.addScope(new ChangesetScopeItem(cs.getChangeset().getId(), cs
					.getChangeset().getRepository().getUrl()));
		}
		return scope;
	}

	private List<TaskChangeSet> getSelectedChangesets(ITask task) {
		List<TaskChangeSet> cs = new ArrayList<TaskChangeSet>();
		for (Object obj : selection.toArray()) {
			cs.add((TaskChangeSet) obj);
		}
		return cs;
	}

	public void selectionChanged(IAction action, ISelection selection) {
		setEnabled(selection.isEmpty()
				&& selection instanceof IStructuredSelection);
		this.selection = (IStructuredSelection) selection;
	}

}
