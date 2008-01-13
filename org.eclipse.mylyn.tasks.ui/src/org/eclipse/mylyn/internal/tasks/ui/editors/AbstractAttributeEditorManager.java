/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskComment;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.editors.RepositoryTaskEditorInput;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Control;

/**
 * @author Steffen Pingel
 */
public abstract class AbstractAttributeEditorManager {

	private final RepositoryTaskEditorInput input;

	public AbstractAttributeEditorManager(RepositoryTaskEditorInput input) {
		this.input = input;
	}

	public abstract void addTextViewer(SourceViewer viewer);

	public abstract boolean attributeChanged(RepositoryTaskAttribute attribute);

	public abstract void configureContextMenuManager(MenuManager menuManager);

	public abstract Color getColorIncoming();

	public abstract TaskRepository getTaskRepository();

	public boolean hasIncomingChanges(RepositoryTaskAttribute taskAttribute) {
		RepositoryTaskData oldTaskData = input.getOldTaskData();
		if (oldTaskData == null) {
			return false;
		}

		if (hasOutgoingChanges(taskAttribute)) {
			return false;
		}

		RepositoryTaskAttribute oldAttribute = oldTaskData.getAttribute(taskAttribute.getId());
		if (oldAttribute == null) {
			return true;
		}
		if (oldAttribute.getValue() != null && !oldAttribute.getValue().equals(taskAttribute.getValue())) {
			return true;
		} else if (oldAttribute.getValues() != null && !oldAttribute.getValues().equals(taskAttribute.getValues())) {
			return true;
		}
		return false;
	}

	public boolean hasOutgoingChanges(RepositoryTaskAttribute taskAttribute) {
		return input.getOldEdits().contains(taskAttribute);
	}

	public boolean isNewComment(TaskComment comment) {
		// Simple test (will not reveal new comments if offline data was lost
		if (input.getOldTaskData() != null) {
			return (comment.getNumber() > input.getOldTaskData().getComments().size());
		}
		return false;

		// OLD METHOD FOR DETERMINING NEW COMMENTS
		// if (repositoryTask != null) {
		// if (repositoryTask.getLastSyncDateStamp() == null) {
		// // new hit
		// return true;
		// }
		// AbstractRepositoryConnector connector = (AbstractRepositoryConnector)
		// TasksUiPlugin.getRepositoryManager()
		// .getRepositoryConnector(taskData.getRepositoryKind());
		// AbstractTaskDataHandler offlineHandler = connector.getTaskDataHandler();
		// if (offlineHandler != null) {
		//
		// Date lastSyncDate =
		// taskData.getAttributeFactory().getDateForAttributeType(
		// RepositoryTaskAttribute.DATE_MODIFIED,
		// repositoryTask.getLastSyncDateStamp());
		//
		// if (lastSyncDate != null) {
		//
		// // reduce granularity to minutes
		// Calendar calLastMod = Calendar.getInstance();
		// calLastMod.setTimeInMillis(lastSyncDate.getTime());
		// calLastMod.set(Calendar.SECOND, 0);
		//
		// Date commentDate =
		// taskData.getAttributeFactory().getDateForAttributeType(
		// RepositoryTaskAttribute.COMMENT_DATE, comment.getCreated());
		// if (commentDate != null) {
		//
		// Calendar calComment = Calendar.getInstance();
		// calComment.setTimeInMillis(commentDate.getTime());
		// calComment.set(Calendar.SECOND, 0);
		// if (calComment.after(calLastMod)) {
		// return true;
		// }
		// }
		// }
		// }
		// }
		// return false;

	}

	public void decorate(RepositoryTaskAttribute taskAttribute, Control control) {
		if (hasIncomingChanges(taskAttribute)) {
			control.setBackground(getColorIncoming());
		}
	}

}
