/*******************************************************************************
 * Copyright (c) 2011, 2012 Frank Becker and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ui.editor;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.internal.tasks.core.TaskComment;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorCommentPart;
import org.eclipse.mylyn.tasks.core.ITaskComment;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;

public class BugzillaTaskEditorCommentPart extends TaskEditorCommentPart {
	private class LockAction extends Action {
		private final ITaskComment taskComment;

		public LockAction(ITaskComment taskComment) {
			this.taskComment = taskComment;
			updateActionState();
		}

		private void updateActionState() {
			if (taskComment.getIsPrivate() != null) {
				if (taskComment.getIsPrivate()) {
					this.setImageDescriptor(TasksUiImages.LOCK_CLOSE);
					this.setToolTipText(Messages.BugzillaTaskEditorCommentPart_privateComment);
				} else {
					this.setImageDescriptor(TasksUiImages.LOCK_OPEN);
					this.setToolTipText(Messages.BugzillaTaskEditorCommentPart_publicComment);
				}
			}
		}

		@Override
		public void run() {
			if (taskComment.getIsPrivate() != null) {
				taskComment.setIsPrivate(!taskComment.getIsPrivate());
				TaskAttribute isprivate = taskComment.getTaskAttribute().getMappedAttribute(
						TaskAttribute.COMMENT_ISPRIVATE);
				if (isprivate == null) {
					isprivate = taskComment.getTaskAttribute().createMappedAttribute(TaskAttribute.COMMENT_ISPRIVATE);
				}
				isprivate.setValue(taskComment.getIsPrivate() ? "1" : "0"); //$NON-NLS-1$ //$NON-NLS-2$
				String value = taskComment.getTaskAttribute().getValue();
				TaskAttribute definedIsPrivate = taskComment.getTaskAttribute().getAttribute(
						IBugzillaConstants.BUGZILLA_PREFIX_DEFINED_ISPRIVATE + value);
				if (definedIsPrivate == null) {
					definedIsPrivate = taskComment.getTaskAttribute().createAttribute(
							IBugzillaConstants.BUGZILLA_PREFIX_DEFINED_ISPRIVATE + value);
				}
				TaskAttribute isPrivate = taskComment.getTaskAttribute().getAttribute(
						IBugzillaConstants.BUGZILLA_PREFIX_ISPRIVATE + value);
				if (isPrivate == null) {
					isPrivate = taskComment.getTaskAttribute().createAttribute(
							IBugzillaConstants.BUGZILLA_PREFIX_ISPRIVATE + value);
				}
				definedIsPrivate.setValue("1"); //$NON-NLS-1$
				isPrivate.setValue(taskComment.getIsPrivate() ? "1" : "0"); //$NON-NLS-1$ //$NON-NLS-2$
				getModel().attributeChanged(taskComment.getTaskAttribute());
				updateActionState();
			}
		}
	}

	public BugzillaTaskEditorCommentPart() {
		// ignore
	}

	@Override
	protected void addActionsToToolbarTitle(ToolBarManager toolBarManager, TaskComment taskComment,
			CommentViewer commentViewer) {
		String insidergroup = getModel().getTaskRepository().getProperty(IBugzillaConstants.BUGZILLA_INSIDER_GROUP);
		if (Boolean.parseBoolean(insidergroup)) {
			LockAction lockAction = new LockAction(taskComment);
			toolBarManager.add(lockAction);
		}
	}

}
