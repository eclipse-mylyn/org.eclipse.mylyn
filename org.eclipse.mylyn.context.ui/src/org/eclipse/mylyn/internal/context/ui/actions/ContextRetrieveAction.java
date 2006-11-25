/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.context.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylar.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylar.internal.tasks.ui.wizards.ContextRetrieveWizard;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.IAttachmentHandler;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 * @author Rob Elves
 * @author Steffen Pingel
 */
public class ContextRetrieveAction implements IViewActionDelegate {
	
	private AbstractRepositoryTask task;
	private TaskRepository repository;
	private AbstractRepositoryConnector connector;

	public void init(IViewPart view) {
		// ignore
	}

	public void run(IAction action) {
		if (task == null) {
			return;
		}
		
//		if (!connector.validate(repository)) {
//			return;
//		}
		ContextRetrieveWizard wizard = new ContextRetrieveWizard(task);
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		if (wizard != null && shell != null && !shell.isDisposed()) {
			WizardDialog dialog = new WizardDialog(shell, wizard);
			dialog.create();
			dialog.setTitle(ContextRetrieveWizard.WIZARD_TITLE);
			dialog.setBlockOnOpen(true);
			if (dialog.open() == Dialog.CANCEL) {
				dialog.close();
				return;
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		ITask selectedTask = TaskListView.getSelectedTask(selection);
		if (selectedTask instanceof AbstractRepositoryTask) {
			task = (AbstractRepositoryTask)selectedTask;
			repository = TasksUiPlugin.getRepositoryManager().getRepository(task.getRepositoryKind(),
					task.getRepositoryUrl());
			connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(task.getRepositoryKind());
			IAttachmentHandler handler = connector.getAttachmentHandler();
			action.setEnabled(handler != null && handler.canDownloadAttachment(repository, task) && connector.hasRepositoryContext(repository, task));
		} else {
			task = null;
			action.setEnabled(false);
		}
	}

}
