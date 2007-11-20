/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskComment;
import org.eclipse.mylyn.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.tasks.core.TaskSelection;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

/**
 * @author Frank Becker
 */
public class NewTaskFromCommentAction extends Action {

	private static final String LABEL = "New Task from Comment";

	public static final String ID = "org.eclipse.mylyn.tasks.ui.actions.createTaskFromComment";

	private TaskComment taskComment;

	protected RepositoryTaskData taskData;
	
	protected String selectedCommentText;

	public NewTaskFromCommentAction() {
		super(LABEL);
		setId(ID);
		setImageDescriptor(TasksUiImages.TASK_NEW);
	}

	public void run(IAction action) {
		run();
	}

	@Override
	public void run() {
		AbstractRepositoryConnector connector = null;
		TaskRepositoryManager repositoryManager = TasksUiPlugin.getRepositoryManager();
		connector = repositoryManager.getRepositoryConnector(taskData.getRepositoryKind());
		String textToInsert ;
		if (selectedCommentText != null && selectedCommentText.length() > 0) {
			textToInsert = selectedCommentText;
		} else {
			textToInsert = taskComment.getText();
		}

		StringBuilder sb = new StringBuilder();
		sb.append("\n-- Created from Comment --\nURL: ");
		sb.append(connector.getTaskUrl(taskData.getRepositoryUrl(), taskData.getId()));
		sb.append("\nComment: ");
		sb.append(taskComment.getNumber());
		sb.append("\n\n");
		sb.append(textToInsert);

		TaskSelection taskSelection = new TaskSelection("", sb.toString());
		NewTaskAction action = new NewTaskAction();
		action.showWizard(taskSelection);
	}

	public void setTaskComment(TaskComment taskComment) {
		this.taskComment = taskComment;
	}

	public void setTaskData(RepositoryTaskData taskData) {
		this.taskData = taskData;
	}

	public void setSelectedCommentText(String selectedCommentText) {
		this.selectedCommentText = selectedCommentText;
	}

}
