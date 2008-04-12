/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.internal.tasks.ui.wizards.NewAttachmentWizard;
import org.eclipse.mylyn.internal.tasks.ui.wizards.NewAttachmentWizardDialog;
import org.eclipse.mylyn.tasks.core.AbstractAttachmentHandler;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchImages;

/**
 * @author Mik Kersten
 */
public class AttachAction extends AbstractTaskEditorAction {

	public static final String LABEL = "Attach...";

	public AttachAction(String label) {
		super(label);
	}

	public AttachAction() {
		super(LABEL);
		setId("org.eclipse.mylyn.tasks.ui.actions.add.attachment");
		setImageDescriptor(WorkbenchImages.getImageDescriptor(ISharedImages.IMG_OBJ_FILE));
	}

	@Override
	public void run() {
		if (editor != null) {
			editor.showBusy(true);
		}
		Object selection = this.getStructuredSelection().getFirstElement();
		if (selection instanceof AbstractTask) {
			if (taskDirty((AbstractTask) selection)) {
				openInformationDialog(LABEL, "Submit changes or synchronize task before adding attachments.");
				return;
			}

			AbstractTask repositoryTask = (AbstractTask) selection;
			TaskRepository repository = TasksUi.getRepositoryManager().getRepository(
					repositoryTask.getConnectorKind(), repositoryTask.getRepositoryUrl());

			NewAttachmentWizard attachmentWizard = new NewAttachmentWizard(repository, repositoryTask);
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

	@Override
	protected boolean updateSelection(IStructuredSelection selection) {
		if (selection.getFirstElement() instanceof AbstractTask) {
			AbstractTask task = (AbstractTask) selection.getFirstElement();
			TaskRepository repository = TasksUi.getRepositoryManager().getRepository(task.getConnectorKind(),
					task.getRepositoryUrl());
			if (repository != null) {
				AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
						task.getConnectorKind());
				if (connector != null) {
					AbstractAttachmentHandler handler = connector.getAttachmentHandler();
					return handler != null && handler.canUploadAttachment(repository, task);
				}
			}
			return false;
		}
		return super.updateSelection(selection);
	}

}
