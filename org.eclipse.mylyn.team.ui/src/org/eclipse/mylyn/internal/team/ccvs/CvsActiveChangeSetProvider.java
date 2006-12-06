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

package org.eclipse.mylar.internal.team.ccvs;

import org.eclipse.mylar.team.AbstractActiveChangeSetProvider;
import org.eclipse.team.internal.ccvs.ui.CVSUIPlugin;
import org.eclipse.team.internal.core.subscribers.ActiveChangeSetManager;

/**
 * CVS change set integration for Mylar.
 * 
 * @author Mik Kersten
 */
public class CvsActiveChangeSetProvider extends AbstractActiveChangeSetProvider {

	@Override
	public ActiveChangeSetManager getActiveChangeSetManager() {
		return CVSUIPlugin.getPlugin().getChangeSetManager();
	}

//	private static final String WIZARD_LABEL = "Commit Resources in Task Context";
	
//	@Override
//	public boolean hasOutgoingChanges(IResource[] resources) {
//		try {
//			CommitContextWizard wizard = new CommitContextWizard(resources, null);
//			return wizard.hasOutgoingChanges();
//		} catch (CVSException e) {
//			return false;
//		}
//	}
//
//	@Override
//	public void commit(IResource[] resources) {
//		try {
//			CommitContextWizard wizard = new CommitContextWizard(resources, null);
//			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
//			if (shell != null && !shell.isDisposed() && wizard.hasOutgoingChanges()) {
//				wizard.loadSize();
//				WizardDialog dialog = new WizardDialog(shell, wizard);
//				dialog.setMinimumPageSize(wizard.loadSize());
//				dialog.create();
//				dialog.setTitle(WIZARD_LABEL);
//				dialog.setBlockOnOpen(true);
//				if (dialog.open() == Dialog.CANCEL) {
//					dialog.close();
//				}
//			}
//		} catch (CVSException e) {
//		}
//	}
}
