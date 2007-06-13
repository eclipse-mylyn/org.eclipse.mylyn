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
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

/**
 * @author Mik Kersten and Ken Sueda
 */
public class MarkTaskIncompleteAction extends Action {

	private static final String ACTION_NAME = "Incomplete";

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.mark.incomplete";

	private List<AbstractTaskContainer> selectedElements;

	public MarkTaskIncompleteAction(List<AbstractTaskContainer> selectedElements) {
		this.selectedElements = selectedElements;
		setText(ACTION_NAME);
		setToolTipText("Mark " + ACTION_NAME);
		setId(ID);
		setImageDescriptor(TasksUiImages.TASK_INCOMPLETE);
		if (selectedElements.size() == 1 && (selectedElements.get(0) instanceof AbstractTask)) {
			AbstractTask task = (AbstractTask) selectedElements.get(0);
			setEnabled(task.isLocal());
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
				TasksUiPlugin.getTaskListManager().getTaskList().markComplete(((AbstractTask) selectedObject), false);
			}
		}
	}
}
