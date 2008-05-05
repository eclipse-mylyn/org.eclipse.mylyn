/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
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
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskElement;

/**
 * @author Rob Elves
 */
public class MarkTaskMenuContributor implements IDynamicSubMenuContributor {

	private static final String LABEL = "Mark as";

	public MenuManager getSubMenuManager(final List<ITaskElement> selectedElements) {
		final MenuManager subMenuManager = new MenuManager(LABEL);
		ITask singleTask = null;
		if (selectedElements.size() == 1) {
			if (selectedElements.get(0) instanceof ITask) {
				singleTask = (ITask) selectedElements.get(0);
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