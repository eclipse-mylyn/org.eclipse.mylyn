/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.mylyn.internal.context.ui.actions.EditHighlightersAction;
import org.eclipse.mylyn.internal.tasks.ui.IDynamicSubMenuContributor;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;

/**
 * @author Mik Kersten
 */
public class TaskHighlighterMenuContributor implements IDynamicSubMenuContributor {

	private static final String CHOOSE_HIGHLIGHTER = "Highlighter";

	public MenuManager getSubMenuManager(final List<AbstractTaskContainer> selectedElements) {
		final MenuManager subMenuManager = new MenuManager(CHOOSE_HIGHLIGHTER);
		for (final Highlighter highlighter : ContextUiPlugin.getDefault().getHighlighters()) {
			Action action = new Action() {
				@Override
				public void run() {
					AbstractTask task = null;
					for (AbstractTaskContainer selectedElement : selectedElements) {
						if (selectedElement instanceof AbstractTask) {
							task = (AbstractTask) selectedElement;
						}
						if (task != null) {
							ContextUiPlugin.getDefault().setHighlighterMapping(task.getHandleIdentifier(),
									highlighter.getName());
							TasksUiPlugin.getTaskListManager().getTaskList().notifyTaskChanged(task, false);
						}
					}
				}
			};
			if (highlighter.isGradient()) {
				action.setImageDescriptor(new HighlighterImageDescriptor(highlighter.getBase(),
						highlighter.getHighlightColor()));
			} else {
				action.setImageDescriptor(new HighlighterImageDescriptor(highlighter.getHighlightColor(),
						highlighter.getHighlightColor()));
			}
			action.setText(highlighter.toString());
			subMenuManager.add(action);
		}
		subMenuManager.add(new Separator());
		subMenuManager.add(new EditHighlightersAction());
		return subMenuManager;
	}
}
