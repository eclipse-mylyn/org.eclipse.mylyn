/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import java.util.List;

import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;

/**
 * @author Rob Elves
 */
public class MarkTaskReadAction extends AbstractTaskAction {

	private static final String ACTION_NAME = "Read";

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.mark.read";

	public static final String DEFINITION_ID = "org.eclipse.mylyn.tasks.ui.command.markTaskRead";

	public MarkTaskReadAction(List<AbstractTaskContainer> selectedElements) {
		super.selectedElements = selectedElements;
		setText(ACTION_NAME);
		setToolTipText("Mark " + ACTION_NAME);
		setId(ID);
		setActionDefinitionId(DEFINITION_ID);
//		if (containsArchiveContainer(selectedElements)) {
//			setEnabled(false);
//		} else {
		setEnabled(selectedElements.size() > 0);
		if (selectedElements.size() == 1 && (selectedElements.get(0) instanceof AbstractTask)) {
			AbstractTask task = (AbstractTask) selectedElements.get(0);
			setEnabled(!(task instanceof LocalTask));
		} else {
			setEnabled(true);
		}
//		}
	}

	@Override
	protected void performActionOnTask(AbstractTask repositoryTask) {
		TasksUiPlugin.getTaskDataManager().setTaskRead(repositoryTask, true);
	}

}
