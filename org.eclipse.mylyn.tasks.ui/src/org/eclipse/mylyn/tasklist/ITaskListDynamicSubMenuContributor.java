package org.eclipse.mylar.tasklist;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.mylar.tasklist.ui.views.TaskListView;

public interface ITaskListDynamicSubMenuContributor {

	public abstract MenuManager getSubMenuManager(TaskListView view,
			ITaskListElement selection);

}