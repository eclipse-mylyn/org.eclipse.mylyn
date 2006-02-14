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

package org.eclipse.mylar.internal.tasklist.ui.views;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.mylar.internal.tasklist.AbstractQueryHit;
import org.eclipse.mylar.internal.tasklist.AbstractRepositoryQuery;
import org.eclipse.mylar.internal.tasklist.AbstractRepositoryTask;
import org.eclipse.mylar.internal.tasklist.ITask;
import org.eclipse.mylar.internal.tasklist.ITaskContainer;
import org.eclipse.mylar.internal.tasklist.ITaskListElement;
import org.eclipse.mylar.internal.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.internal.tasklist.TaskCategory;
import org.eclipse.mylar.internal.tasklist.AbstractRepositoryTask.RepositoryTaskSyncState;
import org.eclipse.mylar.internal.tasklist.ui.ITaskHighlighter;
import org.eclipse.mylar.internal.tasklist.ui.TaskListImages;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * @author Mik Kersten
 */
public class TaskElementLabelProvider extends LabelProvider implements IColorProvider {

	@Override
	public Image getImage(Object element) {
		Image image = null;
		if (element instanceof TaskCategory) {
			TaskCategory category = (TaskCategory) element;
			if (category.isArchive()) {
				image = TaskListImages.getImage(TaskListImages.CATEGORY_ARCHIVE);
			} else {
				image = TaskListImages.getImage(TaskListImages.CATEGORY);
			}
		} else if (element instanceof AbstractRepositoryQuery) {
			image = TaskListImages.getImage(TaskListImages.QUERY);
		} else if (element instanceof AbstractQueryHit) {
			AbstractQueryHit hit = (AbstractQueryHit)element;
			if (hit.getCorrespondingTask() != null) {
				image = getImage(hit.getCorrespondingTask());
			} else {
				image = TaskListImages.getImage(TaskListImages.TASK_REMOTE);
			}
		} else if (element instanceof AbstractRepositoryTask) {
			AbstractRepositoryTask repositoryTask = (AbstractRepositoryTask)element;
			if (repositoryTask.getSyncState() == RepositoryTaskSyncState.SYNCHRONIZED) {
				image = TaskListImages.getImage(TaskListImages.TASK_REPOSITORY);
			} else if (repositoryTask.getSyncState() == RepositoryTaskSyncState.OUTGOING) {
				image = TaskListImages.getImage(TaskListImages.TASK_REPOSITORY_OUTGOING);
			} else if (repositoryTask.getSyncState() == RepositoryTaskSyncState.INCOMING) {
				image = TaskListImages.getImage(TaskListImages.TASK_REPOSITORY_INCOMMING);
			} else if (repositoryTask.getSyncState() == RepositoryTaskSyncState.CONFLICT) {
				image = TaskListImages.getImage(TaskListImages.TASK_REPOSITORY_CONFLICT);
			} else {
				image = TaskListImages.getImage(TaskListImages.TASK_REPOSITORY);
			}
		} else if (element instanceof ITask) {
			ITask task = (ITask)element;
			String url = task.getUrl();
			if (url != null && !url.trim().equals("") && !url.equals("http://")) {
				image = TaskListImages.getImage(TaskListImages.TASK_WEB);
			} else {
				image = TaskListImages.getImage(TaskListImages.TASK);
			}
		} 
		return image;
//		return decorator.decorateImage(image, element);
	}

	@Override
	public String getText(Object object) {
		String text = "";
		if (object instanceof ITaskListElement) {
			ITaskListElement element = (ITaskListElement) object;
			text = element.getDescription();
		} else {
			text = super.getText(object);
		}
		return text;
//		System.err.println("> " + object.getClass()+ ": " + decorator.decorateText(text, object));
//		System.err.println(">>>>> " + PlatformUI.getWorkbench().getDecoratorManager().
//		return PlatformUI.getWorkbench().getDecoratorManager().decorateText(text, object);
//		return decorator.decorateText(text, object);  
	}

	public Color getForeground(Object object) {
		if (object instanceof ITaskContainer) {
			for (ITask child : ((ITaskContainer) object).getChildren()) {
				if (child.isActive()) {
					return TaskListImages.COLOR_TASK_ACTIVE;
				} else if (child.isPastReminder() && !child.isCompleted()) {
					return TaskListImages.COLOR_TASK_OVERDUE;
				}
			}
		} else if (object instanceof AbstractRepositoryQuery) {
			for (ITaskListElement child : ((AbstractRepositoryQuery) object).getHits()) {
				if (child instanceof AbstractQueryHit) {
					ITask task = ((AbstractQueryHit) child).getCorrespondingTask();
					if (task != null && task.isActive()) {
						return TaskListImages.COLOR_TASK_ACTIVE;
					}
				}
			}
		} else if (object instanceof AbstractQueryHit && ((AbstractQueryHit) object).getCorrespondingTask() == null) {
			AbstractQueryHit hit = (AbstractQueryHit) object;
			if (hit.getCorrespondingTask() != null && hit.getCorrespondingTask().isCompleted()) {
				return TaskListImages.COLOR_TASK_COMPLETED;
			}
		} else if (object instanceof ITaskListElement) {
			ITask task = getCorrespondingTask((ITaskListElement) object);
			if (task != null) {
				if (task.isCompleted()) {
					return TaskListImages.COLOR_TASK_COMPLETED;
				} else if (task.isActive()) {
					return TaskListImages.COLOR_TASK_ACTIVE;
				} else if (task.isPastReminder()) {
					return TaskListImages.COLOR_TASK_OVERDUE;
				}
			}
		}
		return null;
	}

	protected ITask getCorrespondingTask(ITaskListElement element) {
		if (element instanceof ITask) {
			return (ITask) element;
		} else if (element instanceof AbstractQueryHit) {
			return ((AbstractQueryHit) element).getCorrespondingTask();
		} else {
			return null;
		}
	}

	public Color getBackground(Object element) {
		if (element instanceof ITask) {
			ITask task = (ITask) element;
			ITaskHighlighter highlighter = MylarTaskListPlugin.getDefault().getHighlighter();
			if (highlighter != null) {
				return highlighter.getHighlightColor(task);
			}
		}
		return TaskListImages.BACKGROUND_WHITE;
	}

	public Font getFont(Object element) {
		if (!(element instanceof ITaskListElement)) {
			return null;
		}
		if (element instanceof AbstractRepositoryTask) {
			AbstractRepositoryTask repositoryTask = (AbstractRepositoryTask) element;
			if (repositoryTask.isCurrentlyDownloading()) {
				return TaskListImages.ITALIC;
			}
		}
		if (element instanceof ITaskContainer) {
			for (ITask child : ((ITaskContainer) element).getChildren()) {
				if (child.isActive())
					return TaskListImages.BOLD;
			}
		}
		ITask task = getCorrespondingTask((ITaskListElement) element);
		if (task != null) {
			if (task.isActive())
				return TaskListImages.BOLD;
			for (ITask child : task.getChildren()) {
				if (child.isActive())
					return TaskListImages.BOLD;
			}
		}
		return null;
	}
}
