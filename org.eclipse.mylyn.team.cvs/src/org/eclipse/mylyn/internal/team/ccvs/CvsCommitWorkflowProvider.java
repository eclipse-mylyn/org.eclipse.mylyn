/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.team.ccvs;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.internal.team.ui.AbstractCommitWorkflowProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.team.internal.ccvs.core.CVSException;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 * 
 * 	TODO: consider deleting, not used
 */
@SuppressWarnings( { "deprecation", "restriction" })
public class CvsCommitWorkflowProvider extends AbstractCommitWorkflowProvider {

	private static final String WIZARD_LABEL = Messages.CvsCommitWorkflowProvider_Commit_Resources_in_Task_Context;

	@Override
	public boolean hasOutgoingChanges(IResource[] resources) {
		try {
			CommitContextWizard wizard = new CommitContextWizard(resources, null);
			return wizard.hasOutgoingChanges();
		} catch (CVSException e) {
			return false;
		}
	}

	@Override
	public void commit(IResource[] resources) {
		try {
			CommitContextWizard wizard = new CommitContextWizard(resources, null);
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			if (shell != null && !shell.isDisposed() && wizard.hasOutgoingChanges()) {
				wizard.loadSize();
				WizardDialog dialog = new WizardDialog(shell, wizard);
				dialog.setMinimumPageSize(wizard.loadSize());
				dialog.create();
				dialog.setTitle(WIZARD_LABEL);
				dialog.setBlockOnOpen(true);
				if (dialog.open() == Window.CANCEL) {
					dialog.close();
				}
			}
		} catch (CVSException e) {
		}
	}

}
