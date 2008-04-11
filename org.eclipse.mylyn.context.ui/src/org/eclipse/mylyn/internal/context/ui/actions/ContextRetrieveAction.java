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
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.internal.context.ui.ContextUiUtil;
import org.eclipse.mylyn.internal.context.ui.wizards.ContextRetrieveWizard;
import org.eclipse.mylyn.internal.tasks.ui.AttachmentUtil;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.AbstractAttachmentHandler;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.RepositoryAttachment;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 * @author Rob Elves
 * @author Steffen Pingel
 */
public class ContextRetrieveAction extends Action implements IViewActionDelegate {

	private AbstractTask task;

	private TaskRepository repository;

	private AbstractRepositoryConnector connector;

	private StructuredSelection selection;

	private static final String ID_ACTION = "org.eclipse.mylyn.context.ui.repository.task.retrieve";

	public ContextRetrieveAction() {
		setText("Retrieve...");
		setToolTipText("Retrieve Task Context");
		setId(ID_ACTION);
		setImageDescriptor(TasksUiImages.CONTEXT_RETRIEVE);
	}

	public void init(IViewPart view) {
		// ignore
	}

	@Override
	public void run() {
		run(this);
	}

	public void run(IAction action) {
		if (task != null) {
			run(task);
		} else {
			// TODO: consider refactoring to be based on object contributions
			if (selection.getFirstElement() instanceof RepositoryAttachment) {
				RepositoryAttachment attachment = (RepositoryAttachment) selection.getFirstElement();

				// HACK: need better way of getting task
				IEditorPart activeEditor = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow()
						.getActivePage()
						.getActiveEditor();
				AbstractTask currentTask = null;
				if (activeEditor instanceof TaskEditor) {
					currentTask = ((TaskEditor) activeEditor).getTaskEditorInput().getTask();
				}

				if (currentTask != null) {
					ContextUiUtil.downloadContext(currentTask, attachment, PlatformUI.getWorkbench()
							.getProgressService());
				} else {
					MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
							"Retrieve Context", "Can not retrieve contenxt for local tasks.");
				}
			}
		}
	}

	public void run(AbstractTask task) {
		ContextRetrieveWizard wizard = new ContextRetrieveWizard(task);
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		if (shell != null && !shell.isDisposed()) {
			WizardDialog dialog = new WizardDialog(shell, wizard);
			dialog.create();
			dialog.setTitle("Retrieve Context");
			dialog.setBlockOnOpen(true);
			if (dialog.open() == Window.CANCEL) {
				dialog.close();
				return;
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		AbstractTask selectedTask = TaskListView.getSelectedTask(selection);

		if (selectedTask == null) {
			StructuredSelection structuredSelection = (StructuredSelection) selection;
			this.selection = structuredSelection;
			if (structuredSelection.getFirstElement() instanceof RepositoryAttachment) {
				RepositoryAttachment attachment = (RepositoryAttachment) structuredSelection.getFirstElement();
				if (AttachmentUtil.isContext(attachment)) {
					action.setEnabled(true);
				} else {
					action.setEnabled(false);
				}
			}
		} else {
			task = selectedTask;
			repository = TasksUiPlugin.getRepositoryManager().getRepository(task.getConnectorKind(),
					task.getRepositoryUrl());
			connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(task.getConnectorKind());
			AbstractAttachmentHandler handler = connector.getAttachmentHandler();
			action.setEnabled(handler != null && handler.canDownloadAttachment(repository, task)
					&& connector.getAttachmentHandler() != null && AttachmentUtil.hasContext(repository, task));
		}
	}
}
