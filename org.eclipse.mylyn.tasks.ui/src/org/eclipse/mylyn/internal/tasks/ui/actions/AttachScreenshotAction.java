/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.ui.wizards.NewAttachmentWizard;
import org.eclipse.mylyn.internal.tasks.ui.wizards.NewAttachmentWizardDialog;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 */
public class AttachScreenshotAction extends AttachAction {

	public static final String LABEL = "Attach Screenshot...";

	public AttachScreenshotAction() {
		super(LABEL);
		setId("org.eclipse.mylyn.tasks.ui.actions.add.screenshot");
		setImageDescriptor(CommonImages.IMAGE_CAPTURE);
	}

	@Override
	public void run() {
		if (editor != null) {
			editor.showBusy(true);
		}
		Object selection = super.getStructuredSelection().getFirstElement();
		if (selection instanceof ITask) {
			if (taskDirty((ITask) selection)) {
				openInformationDialog(LABEL, "Submit changes or synchronize task before adding attachments.");
				return;
			}
			AbstractTask repositoryTask = (AbstractTask) selection;
			TaskRepository repository = TasksUi.getRepositoryManager().getRepository(
					repositoryTask.getConnectorKind(), repositoryTask.getRepositoryUrl());

			NewAttachmentWizard attachmentWizard = new NewAttachmentWizard(repository, repositoryTask, true);
			NewAttachmentWizardDialog dialog = new NewAttachmentWizardDialog(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow()
					.getShell(), attachmentWizard, false) {
				@Override
				public boolean close() {
					boolean closed = super.close();
					if (closed && editor != null) {
						editor.showBusy(false);
					}
					return closed;
				}
			};
			attachmentWizard.setDialog(dialog);
			dialog.create();
			dialog.open();
		}
	}

}
