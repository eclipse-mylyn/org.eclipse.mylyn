/*******************************************************************************
 * Copyright (c) 2016 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui.operations;

import org.eclipse.mylyn.internal.gerrit.core.operations.GerritOperation;
import org.eclipse.mylyn.internal.gerrit.core.operations.RemoveReviewerRequest;
import org.eclipse.mylyn.internal.gerrit.ui.GerritUiPlugin;
import org.eclipse.mylyn.reviews.core.model.IUser;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.google.gerrit.common.data.ReviewerResult;

public class RemoveReviewerDialog extends GerritOperationDialog {

	private final IUser userToRemove;

	public RemoveReviewerDialog(Shell parentShell, ITask task, IUser user) {
		super(parentShell, task);
		this.userToRemove = user;
	}

	@Override
	public GerritOperation<ReviewerResult> createOperation() {
		RemoveReviewerRequest request = new RemoveReviewerRequest(getTask().getTaskId(), userToRemove.getId());
		return GerritUiPlugin.getDefault().getOperationFactory().createOperation(task, request);
	}

	@Override
	protected Control createPageControls(Composite parent) {

		setTitle(Messages.RemoveReviewerDialog_Remove_Reviewer);
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		Label label = new Label(composite, SWT.NONE);
		label.setText(
				NLS.bind(Messages.RemoveReviewerDialog_Are_You_Sure_You_Want_To_Remove, userToRemove.getDisplayName()));

		return parent;
	}

	@Override
	protected boolean processOperationResult(GerritOperation<?> operation) {
		Object result = operation.getOperationResult();
		if (result instanceof ReviewerResult) {
			ReviewerResult reviewerResult = (ReviewerResult) result;
			if (reviewerResult.getErrors() != null && reviewerResult.getErrors().size() > 0) {
				setErrorMessage(reviewerResult.getErrors().toString());
				return false;
			}
		}
		return super.processOperationResult(operation);
	}

}
