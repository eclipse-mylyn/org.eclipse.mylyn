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

package org.eclipse.mylar.internal.tasks.ui.views;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.mylar.internal.tasks.ui.TaskListImages;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.graphics.Image;

/**
 * @author Mik Kersten
 */
public class TaskRepositoryLabelProvider implements ILabelProvider {

//extends LabelProvider implements ITableLabelProvider {

	public Image getColumnImage(Object obj, int index) {
		if(index == 0) { 
			return getImage(obj);		
		} else {
			return null;
		}
	}

	public Image getImage(Object object) {
		if (object instanceof AbstractRepositoryConnector) {
			AbstractRepositoryConnector repositoryConnector = (AbstractRepositoryConnector) object;
			Image image = TasksUiPlugin.getDefault().getBrandingIcon(repositoryConnector.getRepositoryType());
			if (image != null) {
				return image;
			} else {
				return TaskListImages.getImage(TaskListImages.REPOSITORY);
			}
		} else if (object instanceof TaskRepository) {
			return TaskListImages.getImage(TaskListImages.REPOSITORY);
		}
		return null;
	}

	public String getText(Object object) {
		if (object instanceof TaskRepository) {
			TaskRepository repository = (TaskRepository) object;
			if (repository.getRepositoryLabel() != null && repository.getRepositoryLabel().length() > 0) {
				return repository.getRepositoryLabel();
			} else {
				return repository.getUrl();
			}
		} else if (object instanceof AbstractRepositoryConnector) {
			return ((AbstractRepositoryConnector) object).getLabel();
		} else {
			return getText(object);
		}		
	}

	public void addListener(ILabelProviderListener listener) {
		// ignore
		
	}

	public void dispose() {
		// ignore
		
	}

	public boolean isLabelProperty(Object element, String property) {
		// ignore
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
		// ignore
		
	}
}
