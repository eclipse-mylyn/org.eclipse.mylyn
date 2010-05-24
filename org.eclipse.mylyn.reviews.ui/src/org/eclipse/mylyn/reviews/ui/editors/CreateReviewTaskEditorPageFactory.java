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
package org.eclipse.mylyn.reviews.ui.editors;

import java.util.List;

import org.eclipse.mylyn.internal.tasks.core.RepositoryModel;
import org.eclipse.mylyn.reviews.ui.Images;
import org.eclipse.mylyn.tasks.core.IRepositoryModel;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPageFactory;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.forms.editor.IFormPage;

/*
 * @author Kilian Matt
 */
public class CreateReviewTaskEditorPageFactory extends
		AbstractTaskEditorPageFactory {

	@Override
	public boolean canCreatePageFor(TaskEditorInput input) {
		ITask task = input.getTask();
		try {

			TaskData taskData = TasksUi.getTaskDataManager().getTaskData(task);
			IRepositoryModel repositoryModel = TasksUi.getRepositoryModel();
			if (taskData != null) {
				List<TaskAttribute> attributesByType = taskData
						.getAttributeMapper().getAttributesByType(taskData,
								TaskAttribute.TYPE_ATTACHMENT);
				for (TaskAttribute attribute : attributesByType) {
					// TODO move RepositoryModel.createTaskAttachment to
					// interface?
					ITaskAttachment taskAttachment = ((RepositoryModel) repositoryModel)
							.createTaskAttachment(attribute);
					if (taskAttachment.isPatch())
						return true;

				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return false;
	}

	@Override
	public IFormPage createPage(TaskEditor parentEditor) {
		return new CreateReviewTaskEditorPage(parentEditor);
	}

	@Override
	public Image getPageImage() {
		return Images.SMALL_ICON.createImage();
	}

	@Override
	public String getPageText() {
		return Messages.CreateReviewTaskEditorPageFactory_Reviews;
	}

	@Override
	public int getPriority() {
		return PRIORITY_ADDITIONS;
	}
}
