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

package org.eclipse.mylar.internal.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.mylar.internal.tasklist.ui.IDynamicSubMenuContributor;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskListView;
import org.eclipse.mylar.internal.ui.actions.EditHighlightersAction;
import org.eclipse.mylar.provisional.core.IMylarContextListener;
import org.eclipse.mylar.provisional.core.MylarPlugin;
import org.eclipse.mylar.provisional.tasklist.AbstractQueryHit;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.ITaskListElement;
import org.eclipse.mylar.provisional.ui.MylarUiPlugin;

/**
 * @author Mik Kersten
 */
public class TaskListHighlighterMenuContributor implements IDynamicSubMenuContributor {

	private static final String CHOOSE_HIGHLIGHTER = "Choose Highlighter";

	public MenuManager getSubMenuManager(TaskListView view, ITaskListElement selection) {
		final ITaskListElement selectedElement = selection;
		final TaskListView taskListView = view;
		final MenuManager subMenuManager = new MenuManager(CHOOSE_HIGHLIGHTER);
		for (final Highlighter highlighter : MylarUiPlugin.getDefault().getHighlighters()) {
			if (selectedElement instanceof ITaskListElement) {
				Action action = new Action() {
					@Override
					public void run() {
						ITask task = null;
						if (selectedElement instanceof ITask) {
							task = (ITask) selectedElement;
						} else if (selectedElement instanceof AbstractQueryHit) {
							if (((AbstractQueryHit) selectedElement).getCorrespondingTask() != null) {
								task = ((AbstractQueryHit) selectedElement).getCorrespondingTask();
							}
						}
						MylarUiPlugin.getDefault().setHighlighterMapping(task.getHandleIdentifier(),
								highlighter.getName());
						taskListView.getViewer().refresh();
						MylarPlugin.getContextManager().notifyPostPresentationSettingsChange(
								IMylarContextListener.UpdateKind.HIGHLIGHTER);
					}
				};
				if (highlighter.isGradient()) {
					action.setImageDescriptor(new HighlighterImageDescriptor(highlighter.getBase(), highlighter
							.getLandmarkColor()));
				} else {
					action.setImageDescriptor(new HighlighterImageDescriptor(highlighter.getLandmarkColor(),
							highlighter.getLandmarkColor()));
				}
				action.setText(highlighter.toString());
				subMenuManager.add(action);
			}
		}
		subMenuManager.add(new Separator());
		subMenuManager.add(new EditHighlightersAction());
		return subMenuManager;
	}
}
