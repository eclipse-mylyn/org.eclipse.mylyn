/*******************************************************************************
 * Copyright (c) 2012, 2014 Tasktop Technologies and others.
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

import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.internal.gerrit.core.operations.GerritOperation;
import org.eclipse.mylyn.internal.gerrit.core.operations.RebaseRequest;
import org.eclipse.mylyn.internal.gerrit.ui.GerritUiPlugin;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.osgi.util.NLS;
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
public class RebaseDialog extends GerritOperationDialog {

	private final PatchSet patchSet;

	public RebaseDialog(Shell parentShell, ITask task, PatchSet patchSet) {
		super(parentShell, task);
		this.patchSet = patchSet;
	}

	@Override
	public GerritOperation<ChangeDetail> createOperation() {
		int patchSetId = patchSet.getId().get();
		RebaseRequest request = new RebaseRequest(task.getTaskId(), patchSetId);
		return GerritUiPlugin.getDefault().getOperationFactory().createOperation(task, request);
	}

	@Override
	protected Control createPageControls(final Composite parent) {
		setTitle(Messages.RebaseDialog_Rebase_Patch_Set);
		setMessage(""); //$NON-NLS-1$

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		Label label = new Label(composite, SWT.NONE);
		label.setText(NLS.bind(Messages.RebaseDialog_Rebase_patch_set_X, patchSet.getRefName()));

		parent.getDisplay().asyncExec(() -> {
			if (!parent.isDisposed()) {
				setReturnCode(Window.OK);
				close();
			}
		});

		return composite;
	}

}
