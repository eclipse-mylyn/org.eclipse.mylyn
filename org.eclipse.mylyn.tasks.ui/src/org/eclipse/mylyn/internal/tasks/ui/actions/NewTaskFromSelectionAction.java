/*******************************************************************************
 * Copyright (c) 2004, 2008 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
import org.eclipse.mylyn.internal.tasks.core.deprecated.TaskSelection;
import org.eclipse.mylyn.tasks.core.ITaskComment;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * @author Frank Becker
 * @author Steffen Pingel
 */
public class NewTaskFromSelectionAction extends Action {

	private static final String LABEL = "New Task from Selection";

	public static final String ID = "org.eclipse.mylyn.tasks.ui.actions.newTaskFromSelection";

	private TaskSelection taskSelection;

	public NewTaskFromSelectionAction() {
		super(LABEL);
		setId(ID);
		setImageDescriptor(TasksUiImages.TASK_NEW);
	}

	public TaskSelection getTaskSelection() {
		return taskSelection;
	}

	public void run(IAction action) {
		run();
	}

	@Override
	public void run() {
		if (taskSelection == null) {
			MessageDialog.openError(null, LABEL, "Nothing selected to create task from.");
			return;
		}

		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		TasksUiUtil.openNewTaskEditor(shell, taskSelection, null);
	}

	public void selectionChanged(ISelection selection) {
		if (selection instanceof TextSelection) {
			TextSelection textSelection = (TextSelection) selection;
			String text = textSelection.getText();
			if (text != null && text.length() > 0) {
				taskSelection = new TaskSelection("", text);
			} else {
				taskSelection = null;
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
			if (element instanceof ITaskComment) {
				ITaskComment comment = (ITaskComment) element;
				StringBuilder sb = new StringBuilder();
				sb.append("\n-- Created from Comment --");
				if (comment.getUrl() == null) {
					sb.append("\nURL: ");
					sb.append(comment.getTask().getUrl());
					sb.append("\nComment: ");
					sb.append(comment.getNumber());
				} else {
					sb.append("\nURL: ");
					sb.append(comment.getUrl());
				}

				sb.append("\n\n");
				sb.append(comment.getText());
				taskSelection = new TaskSelection("", sb.toString());
			}
		}
		setEnabled(taskSelection != null);
	}

}
