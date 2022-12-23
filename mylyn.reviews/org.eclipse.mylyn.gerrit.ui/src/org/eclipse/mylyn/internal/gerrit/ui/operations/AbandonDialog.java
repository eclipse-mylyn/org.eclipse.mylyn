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

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.internal.gerrit.core.operations.AbandonRequest;
import org.eclipse.mylyn.internal.gerrit.core.operations.GerritOperation;
import org.eclipse.mylyn.internal.gerrit.ui.GerritUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.editors.RichTextEditor;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.google.gerrit.common.data.ChangeDetail;
import com.google.gerrit.reviewdb.PatchSet;

/**
 * @author Steffen Pingel
 * @author Miles Parker
 */
public class AbandonDialog extends GerritOperationDialog {

	private RichTextEditor messageEditor;

	private final PatchSet patchSet;

	public AbandonDialog(Shell parentShell, ITask task, PatchSet patchSet) {
		super(parentShell, task);
		this.patchSet = patchSet;
	}

	@Override
	public GerritOperation<ChangeDetail> createOperation() {
		int patchSetId = patchSet.getId().get();
		AbandonRequest request = new AbandonRequest(task.getTaskId(), patchSetId);
		request.setMessage(messageEditor.getText());
		return GerritUiPlugin.getDefault().getOperationFactory().createOperation(task, request);
	}

	@Override
	protected Control createPageControls(Composite parent) {
		setTitle(Messages.AbandonDialog_Abandon_Change);
		setMessage(Messages.AbandonDialog_Enter_optional_message);

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		messageEditor = createRichTextEditor(composite, ""); //$NON-NLS-1$
		GridDataFactory.fillDefaults().grab(true, true).applyTo(messageEditor.getControl());

		return composite;
	}

}
