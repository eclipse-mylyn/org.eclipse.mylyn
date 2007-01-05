/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.tasks.ui.actions;

import org.eclipse.mylar.internal.tasks.ui.wizards.NewAttachmentWizard;
import org.eclipse.mylar.internal.tasks.ui.wizards.NewAttachmentWizardDialog;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Mik Kersten
 */
public class AttachFileAction extends BaseSelectionListenerAction {

	public AttachFileAction() {
		super("Attach File...");
		setId("org.eclipse.mylar.tasks.ui.actions.add.attachment");
	}

	@Override
	public void run() {
		Object selection = super.getStructuredSelection().getFirstElement();
		if (selection instanceof AbstractRepositoryTask) {
			AbstractRepositoryTask repositoryTask = (AbstractRepositoryTask)selection;
			TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(repositoryTask.getUrl());
			
			NewAttachmentWizard attachmentWizard = new NewAttachmentWizard(repository, repositoryTask);
			NewAttachmentWizardDialog dialog = new NewAttachmentWizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), attachmentWizard);
			attachmentWizard.setDialog(dialog);
			dialog.create();
			dialog.open();
		}
	}
	
}
