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

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.mylar.internal.tasks.ui.TaskListImages;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.graphics.Image;

/**
 * @author Mik Kersten
 */
public class TaskRepositoryLabelProvider extends LabelProvider implements ITableLabelProvider {

	public String getColumnText(Object object, int index) {
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

	public Image getColumnImage(Object obj, int index) {
		return getImage(obj);
	}

	public Image getImage(Object object) {
		String kind = null;
		if (object instanceof AbstractRepositoryConnector) {
			AbstractRepositoryConnector repositoryConnector = (AbstractRepositoryConnector) object;
			kind = repositoryConnector.getRepositoryType();
		} else if (object instanceof TaskRepository) {
			TaskRepository repository = (TaskRepository) object;
			kind = repository.getKind();
		}
		if (kind != null) {
			Image image = TasksUiPlugin.getDefault().getBrandingIcon(kind);
			if (image != null) {
				return image;
			}
		}
		return TaskListImages.getImage(TaskListImages.REPOSITORY);
	}
}
