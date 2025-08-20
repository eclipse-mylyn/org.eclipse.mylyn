/*******************************************************************************
 * Copyright (c) 2004, 2011 Frank Becker and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.tasks.core.ITaskComment;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.TaskMapping;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * @author Frank Becker
 * @author Steffen Pingel
 */
public class NewTaskFromSelectionAction extends Action {

	public static final String ID = "org.eclipse.mylyn.tasks.ui.actions.newTaskFromSelection"; //$NON-NLS-1$

	private ITaskMapping taskMapping;

	public NewTaskFromSelectionAction() {
		super(Messages.NewTaskFromSelectionAction_New_Task_from_Selection);
		setId(ID);
		setImageDescriptor(TasksUiImages.TASK_NEW);
	}

	public ITaskMapping getTaskMapping() {
		return taskMapping;
	}

	public void run(IAction action) {
		run();
	}

	@Override
	public void run() {
		if (taskMapping == null) {
			MessageDialog.openError(null, Messages.NewTaskFromSelectionAction_New_Task_from_Selection,
					Messages.NewTaskFromSelectionAction_Nothing_selected_to_create_task_from);
			return;
		}

		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		TasksUiUtil.openNewTaskEditor(shell, taskMapping, null);
	}

	public void selectionChanged(ISelection selection) {
		if (selection instanceof TextSelection textSelection) {
			final String text = textSelection.getText();
			if (text != null && text.length() > 0) {
				taskMapping = new TaskMapping() {
					@Override
					public String getDescription() {
						return text;
					}
				};
			} else {
				taskMapping = null;
			}
//		} else if (selection instanceof RepositoryTaskSelection) {
//			RepositoryTaskSelection repositoryTaskSelection = (RepositoryTaskSelection) selection;
//			IRepositoryManager repositoryManager = TasksUi.getRepositoryManager();
//			AbstractRepositoryConnector connector = repositoryManager.getRepositoryConnector(repositoryTaskSelection.getRepositoryKind());
//
//			TaskComment comment = repositoryTaskSelection.getComment();
//			if (comment != null) {
//				StringBuilder sb = new StringBuilder();
//				sb.append("\n-- Created from Comment --");
//				if (connector != null) {
//					sb.append("\nURL: ");
//					sb.append(connector.getTaskUrl(repositoryTaskSelection.getRepositoryUrl(),
//							repositoryTaskSelection.getId()));
//				}
//				sb.append("\nComment: ");
//				sb.append(comment.getNumber());
//
//				sb.append("\n\n");
//				if (taskSelection != null) {
//					// if text was selected, prefer that
//					sb.append(taskSelection.getLegacyTaskData().getDescription());
//				} else {
//					sb.append(comment.getText());
//				}
//
//				taskSelection = new TaskSelection("", sb.toString());
//			} else if (taskSelection != null) {
//				StringBuilder sb = new StringBuilder();
//				if (connector != null) {
//					sb.append("\n-- Created from Task --");
//					sb.append("\nURL: ");
//					sb.append(connector.getTaskUrl(repositoryTaskSelection.getRepositoryUrl(),
//							repositoryTaskSelection.getId()));
//				}
//
//				sb.append("\n\n");
//				sb.append(taskSelection.getLegacyTaskData().getDescription());
//
//				taskSelection = new TaskSelection("", sb.toString());
//			}
		} else if (selection instanceof StructuredSelection) {
			Object element = ((StructuredSelection) selection).getFirstElement();
			if (element instanceof ITaskComment comment) {
				final StringBuilder sb = new StringBuilder();
				sb.append("\n" + Messages.NewTaskFromSelectionAction____Created_from_Comment___); //$NON-NLS-1$
				if (comment.getUrl() == null) {
					sb.append("\n" + Messages.NewTaskFromSelectionAction_URL_); //$NON-NLS-1$
					sb.append(comment.getTask().getUrl());
					sb.append("\n" + Messages.NewTaskFromSelectionAction_Comment_); //$NON-NLS-1$
					sb.append(comment.getNumber());
				} else {
					sb.append("\n" + Messages.NewTaskFromSelectionAction_URL_); //$NON-NLS-1$
					sb.append(comment.getUrl());
				}

				sb.append("\n\n"); //$NON-NLS-1$
				sb.append(comment.getText());
				taskMapping = new TaskMapping() {
					@Override
					public String getDescription() {
						return sb.toString();
					}
				};
			}
		}
		setEnabled(taskMapping != null);
	}

}
