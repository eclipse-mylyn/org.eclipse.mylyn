/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityManager;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskElementLabelProvider;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

/**
 * @author Mik Kersten
 */
public class TaskLabelDecorator implements ILightweightLabelDecorator {

	public void decorate(Object element, IDecoration decoration) {

		ImageDescriptor priorityOverlay = TaskElementLabelProvider.getPriorityImageDescriptor(element);
		if (priorityOverlay != null) {
			decoration.addOverlay(priorityOverlay, IDecoration.BOTTOM_LEFT);
		}

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
		} else if (element instanceof AbstractTask) {
			AbstractTask task = (AbstractTask) element;
			if (!task.isCompleted() && TaskActivityManager.getInstance().isOverdue(task)) {
				decoration.addOverlay(TasksUiImages.OVERLAY_OVER_DUE, IDecoration.TOP_RIGHT);
			} else if (!task.isCompleted() && task.getDueDate() != null) {
				decoration.addOverlay(TasksUiImages.OVERLAY_HAS_DUE, IDecoration.TOP_RIGHT);
			}
		} else if (element instanceof TaskRepository) {
			ImageDescriptor overlay = TasksUiPlugin.getDefault().getOverlayIcon(
					((TaskRepository) element).getConnectorKind());
			if (overlay != null) {
				decoration.addOverlay(overlay, IDecoration.BOTTOM_RIGHT);
			}
		}
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
