/*******************************************************************************
 * Copyright (c) 2011, 2014 Tasktop Technologies.
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.mylyn.internal.gerrit.core.operations.AddReviewersRequest;
import org.eclipse.mylyn.internal.gerrit.core.operations.GerritOperation;
import org.eclipse.mylyn.internal.gerrit.ui.GerritUiPlugin;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.google.gerrit.common.data.ReviewerResult;

/**
 * @author Steffen Pingel
 * @author Benjamin Muskalla
 */
public class AddReviewersDialog extends GerritOperationDialog {

	private Text reviewersEditor;

	public AddReviewersDialog(Shell parentShell, ITask task) {
		super(parentShell, task);
	}

	@Override
	public GerritOperation<ReviewerResult> createOperation() {
		AddReviewersRequest request = new AddReviewersRequest(task.getTaskId(), getReviewers());
		return GerritUiPlugin.getDefault().getOperationFactory().createOperation(task, request);
	}

	List<String> getReviewers() {
		String[] reviewers = reviewersEditor.getText().split(","); //$NON-NLS-1$
		List<String> result = new ArrayList<String>(reviewers.length);
		for (int i = 0; i < reviewers.length; i++) {
			reviewers[i] = reviewers[i].trim();
			if (reviewers[i].length() > 0) {
				result.add(reviewers[i]);
			}
		}
		return result;
	}

	@Override
	protected Control createPageControls(Composite parent) {
		setTitle(Messages.AddReviewersDialog_Add_Reviewers);
		setMessage(Messages.AddReviewersDialog_Enter_list_of_names_or_emails);

		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = GridLayoutFactory.fillDefaults().margins(8, 8).create();
		composite.setLayout(layout);

		reviewersEditor = createPersonTextEditor(composite, ""); //$NON-NLS-1$
		GridDataFactory.fillDefaults().grab(true, true).applyTo(reviewersEditor);

		return composite;
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

	/**
	 * Sets the text of the message control. Intended for testing.
	 * 
	 * @param text
	 *            the text to set
	 * @see #getReviewers()
	 */
	void setText(String text) {
		reviewersEditor.setText(text);
	}

}
