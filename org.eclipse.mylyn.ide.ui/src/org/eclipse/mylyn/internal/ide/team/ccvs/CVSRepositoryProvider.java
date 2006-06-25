/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *     Gunnar Wagenknecht - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.ide.team.ccvs;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylar.internal.ide.ui.wizards.MylarCommitWizard;
import org.eclipse.mylar.provisional.ide.team.TeamRepositoryProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.team.internal.ccvs.core.CVSException;
import org.eclipse.team.internal.ccvs.core.mapping.CVSActiveChangeSetCollector;
import org.eclipse.team.internal.ccvs.ui.CVSUIPlugin;
import org.eclipse.team.internal.core.subscribers.ActiveChangeSetManager;
import org.eclipse.ui.PlatformUI;

/**
 * CVS integration for Mylar.
 */
public class CVSRepositoryProvider extends TeamRepositoryProvider {
	private static final String WIZARD_LABEL = "Commit Resources in Task Context";
	
	@Override
	public ActiveChangeSetManager getActiveChangeSetManager() {
		return (CVSActiveChangeSetCollector) CVSUIPlugin.getPlugin()
				.getChangeSetManager();
	}
	
	@Override
	public boolean hasOutgoingChanges(IResource[] resources) {
		try {
			MylarCommitWizard wizard = new MylarCommitWizard(resources, null);
			return wizard.hasOutgoingChanges();
		} catch (CVSException e) {
			return false;
		}
	}
	
	@Override
	public void commit(IResource[] resources) {
		try {
			MylarCommitWizard wizard = new MylarCommitWizard(resources, null);
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			if (shell != null && !shell.isDisposed() && wizard.hasOutgoingChanges()) {
				wizard.loadSize();
				WizardDialog dialog = new WizardDialog(shell, wizard);
				dialog.setMinimumPageSize(wizard.loadSize());
				dialog.create();
				dialog.setTitle(WIZARD_LABEL);
				dialog.setBlockOnOpen(true);
				if (dialog.open() == Dialog.CANCEL) {
					dialog.close();
				}
			}
		} catch (CVSException e) {
		}
	}
}
