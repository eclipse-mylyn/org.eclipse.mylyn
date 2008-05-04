/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.core.ITaskList;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten and
 * @author Ken Sueda
 * @author Eugene Kuleshov
 */
public class MarkTaskIncompleteAction extends AbstractChangeCompletionAction {

	private static final String ACTION_NAME = "Incomplete";

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.mark.incomplete";

	private final List<AbstractTaskContainer> selectedElements;

	public MarkTaskIncompleteAction(List<AbstractTaskContainer> selectedElements) {
		this.selectedElements = selectedElements;
		setText(ACTION_NAME);
		setToolTipText("Mark " + ACTION_NAME);
		setId(ID);
		setImageDescriptor(TasksUiImages.TASK_INCOMPLETE);

		setEnabled(shouldEnable(selectedElements));
	}

	@Override
	public void run() {
		List<AbstractTask> toComplete = new ArrayList<AbstractTask>();
		for (Object selectedObject : selectedElements) {
			if (selectedObject instanceof AbstractTask) {
				AbstractTask task = (AbstractTask) selectedObject;
				if (task.isLocal()) {
					toComplete.add(task);
				}
			}
		}
		if (toComplete.isEmpty()) {
			return;
		} else if (toComplete.size() > 1) {

			String message = generateMessage(toComplete, ACTION_NAME);
			boolean markConfirmed = MessageDialog.openQuestion(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow()
					.getShell(), "Confirm Mark Incompleted", message);
			if (!markConfirmed) {
				return;
			}
		}

		ITaskList taskList = TasksUi.getTaskList();
		for (AbstractTask task : toComplete) {
			task.setCompletionDate(null);
			taskList.notifyTaskChanged(task, false);
		}
	}
}
