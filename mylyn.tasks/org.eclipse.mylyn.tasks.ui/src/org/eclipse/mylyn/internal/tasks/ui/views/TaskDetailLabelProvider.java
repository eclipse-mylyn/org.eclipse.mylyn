/*******************************************************************************
 * Copyright (c) 2004, 2015 Willian Mitsuda and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Willian Mitsuda - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.views;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.swt.graphics.Image;

/**
 * Displays task repository info from a {@link AbstractTask}
 *
 * @author Willian Mitsuda
 */
public class TaskDetailLabelProvider extends LabelProvider implements ILabelProvider {

	@Override
	public Image getImage(Object element) {
		if (!(element instanceof ITask)) {
			return super.getImage(element);
		}

		ImageDescriptor overlay = TasksUiPlugin.getDefault().getBrandManager().getOverlayIcon((ITask) element);
		if (overlay != null) {
			return CommonImages.getImageWithOverlay(TasksUiImages.REPOSITORY, overlay, false, false);
		} else {
			return CommonImages.getImage(TasksUiImages.REPOSITORY);
		}
	}

	@Override
	public String getText(Object element) {
		if (!(element instanceof ITask task)) {
			return super.getText(element);
		}

		TaskRepository repository = TasksUi.getRepositoryManager()
				.getRepository(task.getConnectorKind(), task.getRepositoryUrl());
		return repository.getRepositoryLabel();
	}
}