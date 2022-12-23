/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;

/**
 * @author Willian Mitsuda
 */
public class LinkWithEditorAction extends Action {

	private final TaskListView taskListView;

	public LinkWithEditorAction(TaskListView taskListView) {
		super(Messages.LinkWithEditorAction_Link_with_Editor, IAction.AS_CHECK_BOX);
		this.taskListView = taskListView;
		setImageDescriptor(CommonImages.LINK_EDITOR);
	}

	@Override
	public void run() {
		taskListView.setLinkWithEditor(isChecked());
	}

}
