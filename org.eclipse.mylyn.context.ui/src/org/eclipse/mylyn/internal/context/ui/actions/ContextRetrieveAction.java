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
import org.eclipse.mylyn.internal.context.ui.commands.RetrieveContextAttachmentHandler;
import org.eclipse.mylyn.internal.context.ui.commands.RetrieveContextHandler;
import org.eclipse.mylyn.internal.context.ui.wizards.ContextRetrieveWizard;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryAttachment;
import org.eclipse.mylyn.internal.tasks.ui.util.AttachmentUtil;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
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
 * @deprecated use {@link RetrieveContextHandler} or {@link RetrieveContextAttachmentHandler} instead
 */
@SuppressWarnings("restriction")
@Deprecated
public class ContextRetrieveAction extends Action implements IViewActionDelegate {

	private AbstractTask task;

	private TaskRepository repository;

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
				ITask currentTask = null;
				if (activeEditor instanceof TaskEditor) {
					currentTask = ((TaskEditor) activeEditor).getTaskEditorInput().getTask();
				}

				if (currentTask != null) {
					// legacy
//					AttachmentUtil.downloadContext(currentTask, attachment, PlatformUI.getWorkbench()
//							.getProgressService());
				} else {
					MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
							"Retrieve Context", "Can not retrieve contenxt for local tasks.");
				}
			}
		}
	}

	public void run(ITask task) {
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
			repository = TasksUi.getRepositoryManager().getRepository(task.getConnectorKind(), task.getRepositoryUrl());
			action.setEnabled(AttachmentUtil.canDownloadAttachment(task) && AttachmentUtil.hasContext(repository, task));
		}
	}
}
