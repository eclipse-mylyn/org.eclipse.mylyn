/*******************************************************************************
 * Copyright (c) 2010 Research Group for Industrial Software (INSO), Vienna University of Technology
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

import org.eclipse.mylyn.reviews.core.ReviewsUtil;
import org.eclipse.mylyn.reviews.core.model.review.Review;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/*
 * @author Kilian Matt
 */
public class ReviewTaskEditorPart extends AbstractTaskEditorPart {
	public static final String ID_PART_REVIEW = "org.eclipse.mylyn.reviews.ui.editors.ReviewTaskEditorPart"; //$NON-NLS-1$

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {

		try {
			final TaskDataModel model = getModel();
			List<Review> reviews = ReviewsUtil.getReviewAttachmentFromTask(
					TasksUi.getTaskDataManager(), TasksUi.getRepositoryModel(),
					model.getTask());


			if (reviews.size() > 0) {

				setControl(new EditorSupport(new ReviewTaskEditorInput(reviews
						.get(0)), new ReviewSubmitHandler() {

					public void doSubmit(ReviewTaskEditorInput editorInput) {
						new UpdateReviewTask(model,editorInput.getReview()).schedule();

					}
				}).createPartControl(parent));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
