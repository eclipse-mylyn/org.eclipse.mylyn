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

package org.eclipse.mylar.internal.tasks.ui;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.mylar.tasks.core.AbstractQueryHit;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;

/**
 * @author Mik Kersten
 */
public class RepositoryTaskDecorator implements ILightweightLabelDecorator {

	public void decorate(Object element, IDecoration decoration) {
		if (element instanceof AbstractRepositoryQuery) {
			AbstractRepositoryQuery query = (AbstractRepositoryQuery) element;
			String repositoryUrl = query.getRepositoryUrl();
			TaskRepository taskRepository = TasksUiPlugin.getRepositoryManager().getRepository(repositoryUrl);
			if (repositoryUrl != null && taskRepository != null) {
				if (taskRepository.getUrl().equals(taskRepository.getRepositoryLabel())) {
					try {
						URL url = new URL(repositoryUrl);
						decoration.addSuffix("   [" + url.getHost() + "]");
					} catch (MalformedURLException e) {
						decoration.addSuffix("   [ <unknown host> ]");
					}
				} else {
					decoration.addSuffix("   [" + taskRepository.getRepositoryLabel() + "]");
				}
			}
			if (query.isSynchronizing()) {
				decoration.addOverlay(TasksUiImages.OVERLAY_SYNCHRONIZING, IDecoration.TOP_LEFT);
			}
		} else if (element instanceof AbstractRepositoryTask) {
			AbstractRepositoryTask task = (AbstractRepositoryTask) element;
			AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
					task.getRepositoryKind());
			AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getRepositoryUi(task.getRepositoryKind());
			TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(task.getRepositoryKind(),
					task.getRepositoryUrl());
			if (connectorUi != null) {
				if (!connectorUi.hasRichEditor()) {
					decoration.addOverlay(TasksUiImages.OVERLAY_WEB, IDecoration.BOTTOM_LEFT);
				} else if (connector != null && connector.hasRepositoryContext(repository, task)) {
					decoration.addOverlay(TasksUiImages.OVERLAY_REPOSITORY_CONTEXT, IDecoration.BOTTOM_LEFT);
				} else {
					decoration.addOverlay(TasksUiImages.OVERLAY_REPOSITORY, IDecoration.BOTTOM_LEFT);
				}
			}
			if (task.isSynchronizing()) {
				decoration.addOverlay(TasksUiImages.OVERLAY_SYNCHRONIZING, IDecoration.TOP_LEFT);
			}
			if (!task.isCompleted() && TasksUiPlugin.getTaskListManager().isOverdue(task)) {
				decoration.addOverlay(TasksUiImages.OVERLAY_DUE, IDecoration.TOP_LEFT);
			} else if (!task.isCompleted() && task.getDueDate() != null) {
				decoration.addOverlay(TasksUiImages.OVERLAY_HAS_DUE, IDecoration.TOP_LEFT);
			}
			
			decoration.addOverlay(getSynchronizationStateImageDescriptor(element), IDecoration.TOP_RIGHT);
			
		} else if (element instanceof AbstractQueryHit) {
			ITask correspondingTask = ((AbstractQueryHit) element).getCorrespondingTask();
			decorate(correspondingTask, decoration);
		} else if (element instanceof ITask) {
			ITask task = (ITask) element;
			String url = task.getTaskUrl();
			if (url != null && !url.trim().equals("") && !url.equals("http://")) {
				decoration.addOverlay(TasksUiImages.OVERLAY_WEB, IDecoration.BOTTOM_LEFT);
			}
			if (!task.isCompleted() && TasksUiPlugin.getTaskListManager().isOverdue(task)) {
				decoration.addOverlay(TasksUiImages.OVERLAY_DUE, IDecoration.TOP_LEFT);
			}
			
			decoration.addOverlay(getSynchronizationStateImageDescriptor(element), IDecoration.TOP_RIGHT);
			
		} else if (element instanceof TaskRepository) {
			ImageDescriptor overlay = TasksUiPlugin.getDefault().getOverlayIcon(((TaskRepository) element).getKind());
			if (overlay != null) {
				decoration.addOverlay(overlay, IDecoration.BOTTOM_RIGHT);
			}
		}
	}

	
	
	private ImageDescriptor getSynchronizationStateImageDescriptor(Object element) {
//		if (element instanceof ITaskListElement && !(element instanceof AbstractTaskContainer)) {
//			ITaskListElement taskElement = (ITaskListElement) element;
//			return TasksUiUtil.getImageDescriptorForPriority(PriorityLevel.fromString(taskElement.getPriority()));
//		}
		return null;
		
//		AbstractRepositoryTask repositoryTask = null;
//		if (element instanceof AbstractQueryHit) {
//			repositoryTask = ((AbstractQueryHit) element).getCorrespondingTask();
//		} else if (element instanceof AbstractRepositoryTask) {
//			repositoryTask = (AbstractRepositoryTask) element;
//		}
//		if (repositoryTask != null) {
//			ImageDescriptor image = null;
//			if (repositoryTask.getSyncState() == RepositoryTaskSyncState.OUTGOING) {
//				image = TasksUiImages.OVERLAY_OUTGOING;
//			} else if (repositoryTask.getSyncState() == RepositoryTaskSyncState.INCOMING) {
//				image = TasksUiImages.OVERLAY_INCOMMING;
//			} else if (repositoryTask.getSyncState() == RepositoryTaskSyncState.CONFLICT) {
//				image = TasksUiImages.OVERLAY_CONFLICT;
//			}
//			if (image == null && repositoryTask.getStatus() != null) {
//				return TasksUiImages.OVERLAY_WARNING;
//			} else if (image != null) {
//				return image;
//			}
//		} else if (element instanceof AbstractQueryHit) {
//			return TasksUiImages.OVERLAY_INCOMMING;
//		} else if (element instanceof AbstractTaskContainer) {
//			AbstractTaskContainer container = (AbstractTaskContainer) element;
//			if (container instanceof AbstractRepositoryQuery) {
//				AbstractRepositoryQuery query = (AbstractRepositoryQuery) container;
//				if (query.getStatus() != null) {
//					return TasksUiImages.OVERLAY_WARNING;
//				}
//			}
//		}
//		return null;
	}
	public void addListener(ILabelProviderListener listener) {
		// ignore

	}

	public void dispose() {
		// ignore

	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
		// ignore

	}

}
