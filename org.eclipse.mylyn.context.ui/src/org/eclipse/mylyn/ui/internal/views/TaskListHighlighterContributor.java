/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.ui.internal.views;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.mylar.core.ITaskscapeListener;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.tasks.ITask;
import org.eclipse.mylar.tasks.ITaskListActionContributor;
import org.eclipse.mylar.tasks.ITaskListElement;
import org.eclipse.mylar.tasks.internal.TaskCategory;
import org.eclipse.mylar.tasks.ui.views.TaskListView;
import org.eclipse.mylar.ui.MylarUiPlugin;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Mik Kersten
 */
public class TaskListHighlighterContributor implements ITaskListActionContributor {

	private static final String CHOOSE_HIGHLIGHTER = "Choose Highlighter";

	public List<IAction> getToolbarActions(TaskListView view) {
		return Collections.emptyList();
	}

	public List<IAction> getPopupActions(TaskListView view, ITaskListElement selection) { 
		return Collections.emptyList();
	}
	
	public MenuManager getSubMenuManager(TaskListView view, ITaskListElement selection) {
		final ITaskListElement selectedElement = selection;
		final TaskListView taskListView = view;
		final MenuManager subMenuManager = new MenuManager(CHOOSE_HIGHLIGHTER);
//		List<IAction> actions = new ArrayList<IAction>();
		for (Iterator<Highlighter> it = MylarUiPlugin.getDefault().getHighlighters().iterator(); it.hasNext();) {
          final Highlighter highlighter = it.next();
          if (selectedElement instanceof ITask) {
              Action action = new Action() {
            	  @Override
              		public void run() { 
            		  ITask task = (ITask)selectedElement; 
            		  MylarUiPlugin.getDefault().setHighlighterMapping(task.getHandle(), highlighter.getName());
            		  taskListView.getViewer().refresh();
                      MylarPlugin.getTaskscapeManager().notifyPostPresentationSettingsChange(ITaskscapeListener.UpdateKind.HIGHLIGHTER);
                  }
              };
              if (highlighter.isGradient()) {
                  action.setImageDescriptor(new HighlighterImageDescriptor(highlighter.getBase(), highlighter.getLandmarkColor()));
              } else {
                  action.setImageDescriptor(new HighlighterImageDescriptor(highlighter.getLandmarkColor(), highlighter.getLandmarkColor()));
              }
              action.setText(highlighter.toString());
              subMenuManager.add(action);
          }
		} 
		return subMenuManager;
  	}

	public void taskActivated(ITask task) {
		// TODO Auto-generated method stub

	}

	public void taskDeactivated(ITask task) {
		// TODO Auto-generated method stub

	}

	public void itemDeleted(ITaskListElement element) {
		// TODO Auto-generated method stub

	}

	public void taskCompleted(ITask task) {
		// TODO Auto-generated method stub

	}

	public void itemOpened(ITaskListElement element) {
		// TODO Auto-generated method stub

	}

	public void taskClosed(ITask element, IWorkbenchPage page) {
		// TODO Auto-generated method stub

	}

	public boolean acceptsItem(ITaskListElement element) {
		// TODO Auto-generated method stub
		return false;
	}

	public void dropItem(ITaskListElement element, TaskCategory category) {
		// TODO Auto-generated method stub

	}

	public ITask taskAdded(ITask newTask) {
		// TODO Auto-generated method stub
		return null;
	}

	public void restoreState(TaskListView taskListView) {
		// TODO Auto-generated method stub

	}

}
