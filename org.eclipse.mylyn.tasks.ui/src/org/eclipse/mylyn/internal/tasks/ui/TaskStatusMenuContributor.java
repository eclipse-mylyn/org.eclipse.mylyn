/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.tasks.ui;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.mylar.internal.tasks.core.WebQueryHit;
import org.eclipse.mylar.internal.tasks.ui.actions.ClearOutgoingAction;
import org.eclipse.mylar.internal.tasks.ui.actions.MarkTaskCompleteAction;
import org.eclipse.mylar.internal.tasks.ui.actions.MarkTaskIncompleteAction;
import org.eclipse.mylar.internal.tasks.ui.actions.MarkTaskReadAction;
import org.eclipse.mylar.internal.tasks.ui.actions.MarkTaskUnreadAction;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.ITaskListElement;

/**
 * @author Rob Elves
 */
public class TaskStatusMenuContributor implements IDynamicSubMenuContributor {

	private static final String LABEL = "Mark";

	public MenuManager getSubMenuManager(final List<ITaskListElement> selectedElements) {
		final MenuManager subMenuManager = new MenuManager(LABEL);
		ITask singleTask = null;
		if (selectedElements.size() == 1) {
			if (selectedElements.get(0) instanceof ITask) {
				singleTask = (ITask)selectedElements.get(0);
			} else if (selectedElements.get(0) instanceof WebQueryHit) {
				singleTask = ((WebQueryHit)selectedElements.get(0)).getCorrespondingTask();
			}
		}
		
		Action action = new MarkTaskCompleteAction(selectedElements);
		if (singleTask != null && singleTask.isCompleted()) {
			action.setEnabled(false);
		}
		subMenuManager.add(action);
		action = new MarkTaskIncompleteAction(selectedElements);
		subMenuManager.add(action);
		if (singleTask != null && !singleTask.isCompleted()) {
			action.setEnabled(false);
		}		
		
		subMenuManager.add(new Separator());
		action = new MarkTaskReadAction(selectedElements);
		subMenuManager.add(action);
		action = new MarkTaskUnreadAction(selectedElements);
		subMenuManager.add(action);
		action = new ClearOutgoingAction(selectedElements);
		subMenuManager.add(action);
		return subMenuManager;
	}

}