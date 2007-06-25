/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.mylyn.internal.tasks.ui.actions.ClearOutgoingAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.MarkTaskCompleteAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.MarkTaskIncompleteAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.MarkTaskReadAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.MarkTaskUnreadAction;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;

/**
 * @author Rob Elves
 */
public class TaskStatusMenuContributor implements IDynamicSubMenuContributor {

	private static final String LABEL = "Mark";

	public MenuManager getSubMenuManager(final List<AbstractTaskContainer> selectedElements) {
		final MenuManager subMenuManager = new MenuManager(LABEL);
		AbstractTask singleTask = null;
		if (selectedElements.size() == 1) {
			if (selectedElements.get(0) instanceof AbstractTask) {
				singleTask = (AbstractTask) selectedElements.get(0);
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