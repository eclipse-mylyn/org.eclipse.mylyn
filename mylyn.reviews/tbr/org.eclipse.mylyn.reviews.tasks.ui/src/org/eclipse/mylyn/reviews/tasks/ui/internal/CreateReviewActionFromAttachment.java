/*******************************************************************************
 * Copyright (c) 2010 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kilian Matt (Research Group for Industrial Software (INSO), Vienna University of Technology) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.reviews.tasks.ui.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.reviews.tasks.core.Attachment;
import org.eclipse.mylyn.reviews.tasks.core.IReviewMapper;
import org.eclipse.mylyn.reviews.tasks.core.ITaskProperties;
import org.eclipse.mylyn.reviews.tasks.core.PatchScopeItem;
import org.eclipse.mylyn.reviews.tasks.core.ResourceScopeItem;
import org.eclipse.mylyn.reviews.tasks.core.ReviewScope;
import org.eclipse.mylyn.reviews.tasks.core.internal.ReviewsUtil;
import org.eclipse.mylyn.reviews.tasks.core.internal.TaskProperties;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.ITaskDataManager;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskMapper;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.ui.IActionDelegate;

/**
 * 
 * @author mattk
 * 
 */
@SuppressWarnings("restriction")
public class CreateReviewActionFromAttachment extends Action implements
		IActionDelegate {

	private List<ITaskAttachment> selection2;

	public void run(IAction action) {
		try {
			// FIXME move common creation to a subclass
			ITaskAttachment taskAttachment = selection2.get(0);

			TaskRepository taskRepository = taskAttachment.getTaskRepository();
			ITaskDataManager manager = TasksUi.getTaskDataManager();
			TaskData parentTaskData = manager.getTaskData(taskAttachment
					.getTask());
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

			initTaskProperties(taskMapper, taskProperties, parentTask);

			TasksUiInternal.createAndOpenNewTask(taskData);
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}

	}

	private void initTaskProperties(IReviewMapper taskMapper,
			ITaskProperties taskProperties, ITaskProperties parentTask) {
		ReviewScope scope = new ReviewScope();
		for (ITaskAttachment taskAttachment : selection2) {
			// FIXME date from task attachment
			Attachment attachment = ReviewsUtil.findAttachment(taskAttachment
					.getFileName(), taskAttachment.getAuthor().getPersonId(),
					taskAttachment.getCreationDate().toString(), parentTask);
			if (attachment.isPatch()) {
				scope.addScope(new PatchScopeItem(attachment));
			} else {
				scope.addScope(new ResourceScopeItem(attachment));
			}
		}
		taskMapper.mapScopeToTask(scope, taskProperties);
	}

	public void selectionChanged(IAction action, ISelection selection) {
		action.setEnabled(true);
		if (selection instanceof IStructuredSelection) {
			if (selection.isEmpty()) {
				action.setEnabled(false);
				return;
			}
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			selection2 = new ArrayList<ITaskAttachment>();
			@SuppressWarnings("unchecked")
			Iterator<ITaskAttachment> iterator = structuredSelection.iterator();
			TaskRepository taskRepository = null;
			while (iterator.hasNext()) {
				ITaskAttachment attachment = iterator.next();
				if (taskRepository == null) {
					taskRepository = attachment.getTaskRepository();
				} else if (!taskRepository.equals(attachment
						.getTaskRepository())) {
					action.setEnabled(false);
				}

				selection2.add(attachment);
			}
		}
	}
}
