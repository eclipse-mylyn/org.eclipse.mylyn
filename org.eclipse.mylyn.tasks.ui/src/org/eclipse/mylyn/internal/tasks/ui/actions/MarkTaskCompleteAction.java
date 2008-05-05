/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskElement;
import org.eclipse.mylyn.tasks.core.ITaskList;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 * @author Eugene Kuleshov
 */
public class MarkTaskCompleteAction extends AbstractChangeCompletionAction {

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.mark.completed";

	private static final String ACTION_NAME = "Complete";

	private final List<ITaskElement> selectedElements;

	public MarkTaskCompleteAction(List<ITaskElement> selectedElements) {
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
			if (selectedObject instanceof ITask) {
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

		ITaskList taskList = TasksUi.getTaskList();
		for (AbstractTask task : toComplete) {
			task.setCompletionDate(new Date());
			taskList.notifyTaskChanged(task, false);
		}
	}
}
