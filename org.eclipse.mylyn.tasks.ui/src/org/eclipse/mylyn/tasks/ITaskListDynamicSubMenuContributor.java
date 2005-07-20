package org.eclipse.mylar.tasks;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.mylar.tasks.ui.views.TaskListView;

public interface ITaskListDynamicSubMenuContributor {

	public abstract MenuManager getSubMenuManager(TaskListView view,
			ITaskListElement selection);

}