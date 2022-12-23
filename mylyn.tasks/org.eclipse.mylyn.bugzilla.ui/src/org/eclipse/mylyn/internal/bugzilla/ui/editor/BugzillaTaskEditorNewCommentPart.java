/*******************************************************************************
 * Copyright (c) 2011 Frank Becker and others.
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
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorNewCommentPart;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;

public class BugzillaTaskEditorNewCommentPart extends TaskEditorNewCommentPart {
	private Action privateAction;

	public BugzillaTaskEditorNewCommentPart() {
		// ignore
	}

	@Override
	protected void fillToolBar(ToolBarManager manager) {
		String insidergroup = getModel().getTaskRepository().getProperty(IBugzillaConstants.BUGZILLA_INSIDER_GROUP);
		if (Boolean.parseBoolean(insidergroup)) {

			privateAction = new Action() {

				private void updateActionState(String newValue) {
					if (newValue.equals("1")) { //$NON-NLS-1$
						this.setImageDescriptor(TasksUiImages.LOCK_CLOSE);
						this.setToolTipText(Messages.BugzillaTaskEditorNewCommentPart_privateComment);
					} else {
						this.setImageDescriptor(TasksUiImages.LOCK_OPEN);
						this.setToolTipText(Messages.BugzillaTaskEditorNewCommentPart_publicComment);
					}

				}

				@Override
				public void run() {
					TaskAttribute isprivate = getAttribute().getParentAttribute().getAttribute("comment_is_private"); //$NON-NLS-1$
					String value = isprivate.getValue();
					String newValue = value.equals("1") ? "0" : "1"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					isprivate.setValue(newValue);
					updateActionState(newValue);
				}

			};
			privateAction.setImageDescriptor(TasksUiImages.LOCK_OPEN);
			privateAction.setToolTipText(Messages.BugzillaTaskEditorNewCommentPart_publicComment);
			manager.add(privateAction);
		}
		super.fillToolBar(manager);
	}

	@Override
	public void initialize(AbstractTaskEditorPage taskEditorPage) {
		super.initialize(taskEditorPage);
		if (getAttribute() != null) {
			TaskAttribute isprivate = getAttribute().getParentAttribute().getAttribute("comment_is_private"); //$NON-NLS-1$
			if (isprivate == null) {
				isprivate = getAttribute().getParentAttribute().createAttribute("comment_is_private"); //$NON-NLS-1$
			}
			isprivate.setValue("0"); //$NON-NLS-1$
		}
	}

}
