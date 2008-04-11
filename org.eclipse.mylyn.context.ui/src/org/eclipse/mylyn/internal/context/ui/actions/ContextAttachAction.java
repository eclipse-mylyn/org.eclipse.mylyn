/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.internal.context.ui.wizards.ContextAttachWizard;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.AbstractAttachmentHandler;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.AbstractTask.RepositoryTaskSyncState;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 * @author Rob Elves
 * @author Steffen Pingel
 */
public class ContextAttachAction extends Action implements IViewActionDelegate {

	private AbstractTask task;

	private TaskRepository repository;

	private AbstractRepositoryConnector connector;

	private static final String ID_ACTION = "org.eclipse.mylyn.context.ui.repository.task.attach";

	public ContextAttachAction() {
		setText("Attach...");
		setToolTipText("Attach Task Context");
		setId(ID_ACTION);
		setImageDescriptor(TasksUiImages.CONTEXT_ATTACH);
	}

	public void init(IViewPart view) {
		// ignore
	}

	@Override
	public void run() {
		run(this);
	}

	public void run(IAction action) {
		if (task == null) {
			return;
		} else {
			run(task);
		}
	}

	public void run(AbstractTask task) {
		if (task.getSynchronizationState() != RepositoryTaskSyncState.SYNCHRONIZED) {
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					ITasksUiConstants.TITLE_DIALOG, "Task must be synchronized before attaching context");
			return;
		}

		ContextAttachWizard wizard = new ContextAttachWizard(task);
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		if (shell != null && !shell.isDisposed()) {
			WizardDialog dialog = new WizardDialog(shell, wizard);
			dialog.create();
			dialog.setTitle("Attach Context");
			dialog.setBlockOnOpen(true);
			if (dialog.open() == Window.CANCEL) {
				dialog.close();
				return;
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		AbstractTask selectedTask = TaskListView.getSelectedTask(selection);
		if (selectedTask != null) {
			task = selectedTask;
			repository = TasksUiPlugin.getRepositoryManager().getRepository(task.getConnectorKind(),
					task.getRepositoryUrl());
			connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(task.getConnectorKind());
			AbstractAttachmentHandler handler = connector.getAttachmentHandler();
			action.setEnabled(handler != null
					&& handler.canUploadAttachment(repository, task)
					&& (task.isActive() || ContextCore.getContextManager().hasContext(task.getHandleIdentifier())));
		} else {
			task = null;
			action.setEnabled(false);
		}
	}

}
