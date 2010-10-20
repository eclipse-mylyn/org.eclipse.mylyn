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

import org.eclipse.mylyn.reviews.core.model.review.Patch;
import org.eclipse.mylyn.reviews.core.model.review.ReviewFactory;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;

/*
 * @author Kilian Matt
 */
public class NewReviewTaskEditorInput extends ReviewTaskEditorInput {

	private TaskDataModel model;

	public NewReviewTaskEditorInput(TaskDataModel model, Patch patch) {
		super(ReviewFactory.eINSTANCE.createReview());
		this.model = model;
		getReview().getScope().add(patch);
	}

	@Override
	public String getName() {
		return Messages.NewReviewTaskEditorInput_ReviewPrefix + model.getTask().getTaskKey() + " " //$NON-NLS-2$
				+ model.getTask().toString();
	}

	public TaskDataModel getModel() {
		return model;
	}
}
