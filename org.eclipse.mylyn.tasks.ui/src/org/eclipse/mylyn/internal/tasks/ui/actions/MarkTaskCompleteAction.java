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
import org.eclipse.mylyn.tasks.core.TaskList;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 * @author Eugene Kuleshov
 */
public class MarkTaskCompleteAction extends AbstractChangeCompletionAction {

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.mark.completed";

	private static final String ACTION_NAME = "Complete";

	private List<AbstractTaskContainer> selectedElements;

	public MarkTaskCompleteAction(List<AbstractTaskContainer> selectedElements) {
		this.selectedElements = selectedElements;
		setText(ACTION_NAME);
		setToolTipText("Mark " + ACTION_NAME);
		setId(ID);
		setImageDescriptor(TasksUiImages.TASK_COMPLETE);

		setEnabled(shouldEnable(selectedElements));
//		if (allLocalTasks) {
//			setEnabled(selectedElements.size() > 0);
//		}
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
					.getShell(), "Confirm Mark Completed", message);
			if (!markConfirmed) {
				return;
			}
		}

		TaskList taskList = TasksUiPlugin.getTaskListManager().getTaskList();
		for (AbstractTask task : toComplete) {
			taskList.markComplete(task, true);
		}
	}
}
