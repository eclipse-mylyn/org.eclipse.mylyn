package org.eclipse.mylar.tasks;

import org.eclipse.jface.action.Action;
import org.eclipse.mylar.tasks.internal.TaskCategory;
import org.eclipse.mylar.tasks.ui.views.TaskListView;
import org.eclipse.ui.IWorkbenchPage;

public interface ITaskHandler {

	public abstract void itemDeleted(ITaskListElement element);

	public abstract void taskCompleted(ITask task);

	public abstract void itemOpened(ITaskListElement element);

	public abstract void taskClosed(ITask element, IWorkbenchPage page);

	public abstract boolean acceptsItem(ITaskListElement element);

	public abstract void dropItem(ITaskListElement element,
			TaskCategory category);

	public abstract ITask taskAdded(ITask newTask);

	public abstract void restoreState(TaskListView taskListView);

	public abstract boolean enableAction(Action action, ITaskListElement element);
}