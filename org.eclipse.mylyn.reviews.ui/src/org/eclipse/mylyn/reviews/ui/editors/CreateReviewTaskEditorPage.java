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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorPartDescriptor;

/*
 * @author Kilian Matt
 */
public class CreateReviewTaskEditorPage extends AbstractTaskEditorPage {
	private static final String PAGE_ID = "org.eclipse.mylyn.reviews.ui.editors.CreateReviewTaskEditorPage"; //$NON-NLS-1$

	public CreateReviewTaskEditorPage(TaskEditor editor) {
		super(editor, PAGE_ID, "ReviewTaskFormPage", "mylynreviews"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	protected Set<TaskEditorPartDescriptor> createPartDescriptors() {
		Set<TaskEditorPartDescriptor> taskDescriptors = new HashSet<TaskEditorPartDescriptor>();
		try {
			TaskData data = TasksUi.getTaskDataManager().getTaskData(getTask());
			if (data != null) {

				taskDescriptors.add(new TaskEditorPartDescriptor(
						CreateReviewTaskEditorPart.ID_PART_CREATEREVIEW) {
					@Override
					public AbstractTaskEditorPart createPart() {
						return new CreateReviewTaskEditorPart();
					}

				});
				taskDescriptors.add(new TaskEditorPartDescriptor(
						ReviewSummaryTaskEditorPart.ID_PART_REVIEWSUMMARY) {
					@Override
					public AbstractTaskEditorPart createPart() {
						return new ReviewSummaryTaskEditorPart();
					}

				});
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return taskDescriptors;
	}

	@Override
	public void fillToolBar(IToolBarManager toolBarManager) {
	}

}
