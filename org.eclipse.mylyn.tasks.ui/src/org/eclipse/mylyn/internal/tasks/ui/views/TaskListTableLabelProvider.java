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
/*
 * Created on Feb 18, 2005
 */
package org.eclipse.mylar.internal.tasks.ui.views;

import java.util.Arrays;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.mylar.context.core.ContextCorePlugin;
import org.eclipse.mylar.internal.tasks.ui.TaskListColorsAndFonts;
import org.eclipse.mylar.internal.tasks.ui.TaskListImages;
import org.eclipse.mylar.tasks.core.AbstractQueryHit;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.AbstractTaskContainer;
import org.eclipse.mylar.tasks.core.DateRangeContainer;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.ITaskListElement;
import org.eclipse.mylar.tasks.core.TaskArchive;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask.RepositoryTaskSyncState;
import org.eclipse.mylar.tasks.core.Task.PriorityLevel;
import org.eclipse.mylar.tasks.ui.TasksUiUtil;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * @author Mik Kersten
 */
public class TaskListTableLabelProvider extends DecoratingLabelProvider implements ITableLabelProvider,
		ITableColorProvider, ITableFontProvider {

	private Color categoryBackgroundColor;

	private TaskListView view;

	/**
	 * @param view
	 *            can be null
	 */
	public TaskListTableLabelProvider(ILabelProvider provider, ILabelDecorator decorator, Color parentBacground,
			TaskListView view) {
		super(provider, decorator);
		this.categoryBackgroundColor = parentBacground;
		this.view = view;
	}

	public String getColumnText(Object obj, int columnIndex) {
		if (obj instanceof ITaskListElement) {
			switch (columnIndex) {
			case 0:
				return null;
			case 1:
				return null;
			case 2:
				return null;
			case 3:
				return null;
			case 4:
				return super.getText(obj);
			}
		}
		return null;
	}

	public Image getColumnImage(Object element, int columnIndex) {
		if (!(element instanceof ITaskListElement)) {
			return null;
		}
		if (columnIndex == 0) {
			if (element instanceof DateRangeContainer) {
				return TaskListImages.getImage(TaskListImages.CALENDAR);
			} else if (element instanceof AbstractTaskContainer) {
				return super.getImage(element);
			} else {
				ITask task = TaskElementLabelProvider.getCorrespondingTask((ITaskListElement) element);
				if (task != null) {
					if (task.isActive()) {
						return TaskListImages.getImage(TaskListImages.TASK_ACTIVE);
					} else {
						if (ContextCorePlugin.getContextManager().hasContext(task.getHandleIdentifier())) {
							return TaskListImages.getImage(TaskListImages.TASK_INACTIVE_CONTEXT);
						} else {
							return TaskListImages.getImage(TaskListImages.TASK_INACTIVE);
						}
					}
				} else {
					return TaskListImages.getImage(TaskListImages.TASK_INACTIVE);
				}
			}
		} else if (columnIndex == 1) {
			if (element instanceof AbstractTaskContainer) {
				return null;
			}
			return super.getImage(element);
		} else if (columnIndex == 2) {
			if (element instanceof ITaskListElement && !(element instanceof AbstractTaskContainer)) {
				ITaskListElement taskElement = (ITaskListElement) element;
				return TasksUiUtil.getImageForPriority(PriorityLevel.fromString(taskElement.getPriority()));
			}
		} else if (columnIndex == 3) {
			AbstractRepositoryTask repositoryTask = null;
			if (element instanceof AbstractQueryHit) {
				repositoryTask = ((AbstractQueryHit) element).getCorrespondingTask();
			} else if (element instanceof AbstractRepositoryTask) {
				repositoryTask = (AbstractRepositoryTask) element;
			}
			if (repositoryTask != null) {
				ImageDescriptor image = null;
				if (repositoryTask.getSyncState() == RepositoryTaskSyncState.OUTGOING) {
					image = TaskListImages.STATUS_NORMAL_OUTGOING;
				} else if (repositoryTask.getSyncState() == RepositoryTaskSyncState.INCOMING) {
					image = TaskListImages.STATUS_NORMAL_INCOMING;
				} else if (repositoryTask.getSyncState() == RepositoryTaskSyncState.CONFLICT) {
					image = TaskListImages.STATUS_NORMAL_CONFLICT;
				}
				if (repositoryTask.getStatus() != null) {
					if (image == null) {
						image = TaskListImages.STATUS_NORMAL;
					}
					return TaskListImages.getImage(TaskListImages.STATUS_WARNING);
				} else if (image != null) {
					return TaskListImages.getImage(image);
				}
			} else if (element instanceof AbstractQueryHit) {
				return TaskListImages.getImage(TaskListImages.STATUS_NORMAL_INCOMING);
			} else if (element instanceof AbstractTaskContainer) {
				AbstractTaskContainer container = (AbstractTaskContainer) element;
				if (container instanceof AbstractRepositoryQuery) {
					AbstractRepositoryQuery query = (AbstractRepositoryQuery) container;
					if (query.getStatus() != null) {
						return TaskListImages.getImage(TaskListImages.STATUS_WARNING);
					}
				}
				if (view != null && !Arrays.asList(view.getViewer().getExpandedElements()).contains(element)
						&& hasIncoming(container)) {
					return TaskListImages.getImage(TaskListImages.STATUS_NORMAL_INCOMING);
				}
			}
		}
		return null;
	}

	private boolean hasIncoming(AbstractTaskContainer container) {
		for (ITask task : container.getChildren()) {
			if (task instanceof AbstractRepositoryTask) {
				AbstractRepositoryTask containedRepositoryTask = (AbstractRepositoryTask) task;
				if (containedRepositoryTask.getSyncState() == RepositoryTaskSyncState.INCOMING) {
					return true;
				}
			}
		}
		if (container instanceof AbstractRepositoryQuery) {
			AbstractRepositoryQuery query = (AbstractRepositoryQuery) container;
			for (AbstractQueryHit hit : query.getHits()) { // FIXME should not
				// create new tasks!
				if (hit.getCorrespondingTask() == null) {
					return true;
				}
			}
		}
		return false;
	}

	public Font getFont(Object element, int columnIndex) {
		return super.getFont(element);
	}

	public Color getForeground(Object element, int columnIndex) {
		return super.getForeground(element);
	}

	public Color getBackground(Object element, int columnIndex) {
		if (element instanceof AbstractTaskContainer) {
			AbstractTaskContainer category = (AbstractTaskContainer) element;
			if (category instanceof TaskArchive) {
				return TaskListColorsAndFonts.BACKGROUND_ARCHIVE;
			} else {
				return categoryBackgroundColor;
			}
		} else if (element instanceof AbstractRepositoryQuery) {
			return categoryBackgroundColor;
		}

		return super.getBackground(element);
	}

	public void setCategoryBackgroundColor(Color parentBackgroundColor) {
		this.categoryBackgroundColor = parentBackgroundColor;
	}
}
