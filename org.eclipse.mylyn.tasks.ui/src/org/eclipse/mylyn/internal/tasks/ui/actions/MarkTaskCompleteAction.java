/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.mylyn.internal.tasks.core.WebTask;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskListElement;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

/**
 * @author Mik Kersten
 */
public class MarkTaskCompleteAction extends Action {

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.mark.completed";

	private static final String ACTION_NAME = "Complete";

	private List<AbstractTaskListElement> selectedElements;

	public MarkTaskCompleteAction(List<AbstractTaskListElement> selectedElements) {
		this.selectedElements = selectedElements;
		setText(ACTION_NAME);
		setToolTipText("Mark " + ACTION_NAME);
		setId(ID);
		setImageDescriptor(TasksUiImages.TASK_COMPLETE);
		if (selectedElements.size() == 1 && (selectedElements.get(0) instanceof AbstractTask)) {
			AbstractTask task = (AbstractTask) selectedElements.get(0);
			if (task instanceof WebTask) {
				setEnabled(true);
			} else {
				setEnabled(task.isLocal());
			}
//		} else if (selectedElements.size() == 1 && (selectedElements.get(0) instanceof WebQueryHit)) {
//			setEnabled(true);
		} else {
			setEnabled(false);
		}
	}

	@Override
	public void run() {
		for (Object selectedObject : selectedElements) {
			if (selectedObject instanceof AbstractTask) {
				TasksUiPlugin.getTaskListManager().getTaskList().markComplete(((AbstractTask) selectedObject), true);
			}
		}
	}
}
