/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.views;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.swt.graphics.Image;

/**
 * Displays task repository info from a {@link AbstractTask}
 * 
 * @author Willian Mitsuda
 */
public class TaskDetailLabelProvider extends LabelProvider implements ILabelProvider {

	@Override
	public Image getImage(Object element) {
		if (!(element instanceof AbstractTask)) {
			return super.getImage(element);
		}

		AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
				(AbstractTask) element);
		ImageDescriptor overlay = TasksUiPlugin.getDefault().getOverlayIcon(connector.getConnectorKind());
		if (overlay != null) {
			return CommonImages.getImageWithOverlay(TasksUiImages.REPOSITORY, overlay, false, false);
		} else {
			return CommonImages.getImage(TasksUiImages.REPOSITORY);
		}
	}

	@Override
	public String getText(Object element) {
		if (!(element instanceof AbstractTask)) {
			return super.getText(element);
		}

		TaskRepository repository = TasksUi.getRepositoryManager().getRepository(
				((AbstractTask) element).getRepositoryUrl());
		return repository.getRepositoryLabel();
	}

}