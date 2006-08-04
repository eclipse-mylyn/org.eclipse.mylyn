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

import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.mylar.tasks.core.AbstractQueryHit;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;

/**
 * @author Mik Kersten
 */
public class RepositoryTaskDecorator implements ILightweightLabelDecorator {

	public void decorate(Object element, IDecoration decoration) {
		if (element instanceof AbstractRepositoryQuery) {
			AbstractRepositoryQuery query = (AbstractRepositoryQuery)element;
			String repositoryUrl = query.getRepositoryUrl();
			if (repositoryUrl != null) {
				try {
					URL url = new URL(repositoryUrl);
					decoration.addSuffix("   [" + url.getHost() + "]");
				} catch (MalformedURLException e) {
					decoration.addSuffix("   [ <unknown host> ]");
				}
			} 
			if (query.isSynchronizing()) {
				decoration.addOverlay(TaskListImages.OVERLAY_SYNCHRONIZING, IDecoration.TOP_LEFT);
			}
		} else if (element instanceof AbstractRepositoryTask) { 
			AbstractRepositoryTask task = (AbstractRepositoryTask)element;
			AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(task.getRepositoryKind());
			TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(task.getRepositoryKind(), task.getRepositoryUrl());
			if (!connector.hasRichEditor()) {
				decoration.addOverlay(TaskListImages.OVERLAY_WEB, IDecoration.BOTTOM_LEFT);
			} else if (connector != null && connector.hasRepositoryContext(repository, task)) { 
				decoration.addOverlay(TaskListImages.OVERLAY_REPOSITORY_CONTEXT, IDecoration.BOTTOM_LEFT);
			} else {
				decoration.addOverlay(TaskListImages.OVERLAY_REPOSITORY, IDecoration.BOTTOM_LEFT);
			}
			if (task.isSynchronizing()) {
				decoration.addOverlay(TaskListImages.OVERLAY_SYNCHRONIZING, IDecoration.TOP_LEFT);
			}
		} else if (element instanceof AbstractQueryHit) {
			ITask correspondingTask = ((AbstractQueryHit)element).getCorrespondingTask();
			decorate(correspondingTask, decoration);
		} else if (element instanceof ITask) {
			String url = ((ITask)element).getUrl();
			if (url != null && !url.trim().equals("") && !url.equals("http://")) {
				decoration.addOverlay(TaskListImages.OVERLAY_WEB, IDecoration.BOTTOM_LEFT);
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
