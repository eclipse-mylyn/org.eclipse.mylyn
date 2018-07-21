/*******************************************************************************
 * Copyright (c) 2011, 2014 Tasktop Technologies and others.
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
import org.eclipse.mylyn.internal.gerrit.core.operations.SubmitRequest;
import org.eclipse.mylyn.internal.gerrit.ui.GerritUiPlugin;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.google.gerrit.common.data.ChangeDetail;
import com.google.gerrit.reviewdb.PatchSet;

/**
 * @author Steffen Pingel
 */
public class SubmitDialog extends GerritOperationDialog {

	private final PatchSet patchSet;

	public SubmitDialog(Shell parentShell, ITask task, PatchSet patchSet) {
		super(parentShell, task);
		this.patchSet = patchSet;
	}

	@Override
	public GerritOperation<ChangeDetail> createOperation() {
		int patchSetId = patchSet.getId().get();
		SubmitRequest request = new SubmitRequest(task.getTaskId(), patchSetId);
		return GerritUiPlugin.getDefault().getOperationFactory().createOperation(task, request);
	}

	@Override
	protected Control createPageControls(Composite parent) {
		setTitle(Messages.SubmitDialog_Submit_Change);
		setMessage(""); //$NON-NLS-1$

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		Label label = new Label(composite, SWT.NONE);
		label.setText(Messages.SubmitDialog_Submit_change_confirmation);

		return composite;
	}

}
