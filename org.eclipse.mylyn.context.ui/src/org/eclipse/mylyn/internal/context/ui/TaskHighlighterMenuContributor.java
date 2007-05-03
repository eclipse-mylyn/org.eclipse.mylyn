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

package org.eclipse.mylar.internal.context.ui;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.mylar.context.ui.ContextUiPlugin;
import org.eclipse.mylar.internal.context.ui.actions.EditHighlightersAction;
import org.eclipse.mylar.internal.tasks.ui.IDynamicSubMenuContributor;
import org.eclipse.mylar.tasks.core.AbstractQueryHit;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.ITaskListElement;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;

/**
 * @author Mik Kersten
 */
public class TaskHighlighterMenuContributor implements IDynamicSubMenuContributor {

	private static final String CHOOSE_HIGHLIGHTER = "Highlighter";

	public MenuManager getSubMenuManager(final List<ITaskListElement> selectedElements) {
		final MenuManager subMenuManager = new MenuManager(CHOOSE_HIGHLIGHTER);
		for (final Highlighter highlighter : ContextUiPlugin.getDefault().getHighlighters()) {
			Action action = new Action() {
				@Override
				public void run() {
					ITask task = null;
					for (ITaskListElement selectedElement : selectedElements) {
						if (selectedElement instanceof ITask) {
							task = (ITask) selectedElement;
						} else if (selectedElement instanceof AbstractQueryHit) {
							if (((AbstractQueryHit) selectedElement).getCorrespondingTask() != null) {
								task = ((AbstractQueryHit) selectedElement).getCorrespondingTask();
							}
						}
						if (task != null) {
							ContextUiPlugin.getDefault().setHighlighterMapping(task.getHandleIdentifier(),
									highlighter.getName());
							TasksUiPlugin.getTaskListManager().getTaskList().notifyLocalInfoChanged(task);
						}
					}
				}
			};
			if (highlighter.isGradient()) {
				action.setImageDescriptor(new HighlighterImageDescriptor(highlighter.getBase(), highlighter
						.getHighlightColor()));
			} else {
				action.setImageDescriptor(new HighlighterImageDescriptor(highlighter.getHighlightColor(), highlighter
						.getHighlightColor()));
			}
			action.setText(highlighter.toString());
			subMenuManager.add(action);
		}
		subMenuManager.add(new Separator());
		subMenuManager.add(new EditHighlightersAction());
		return subMenuManager;
	}
}
