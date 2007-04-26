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
import org.eclipse.mylar.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylar.tasks.core.AbstractQueryHit;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.AbstractTaskContainer;
import org.eclipse.mylar.tasks.core.DateRangeContainer;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.ITaskListElement;
import org.eclipse.mylar.tasks.core.TaskArchive;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask.RepositoryTaskSyncState;
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
	public TaskListTableLabelProvider(ILabelProvider provider, ILabelDecorator decorator, Color parentBackground,
			TaskListView view) {
		super(provider, decorator);
		this.categoryBackgroundColor = parentBackground;
		this.view = view;
	}

	public String getColumnText(Object obj, int columnIndex) {
		if (obj instanceof ITaskListElement) {
			switch (columnIndex) {
			case 0:
				if (obj instanceof DateRangeContainer) {
					if (((DateRangeContainer) obj).isPresent()) {
						return super.getText(obj) + " - Today";
					}
				}
				return super.getText(obj);
			case 1:
				return null;
			case 2:
				return null;
			case 3:
				return null;
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
				return TasksUiImages.getImage(TasksUiImages.CALENDAR, true);
			} else {
				return super.getImage(element);
//				TaskElementLabelProvider taskElementProvider = (TaskElementLabelProvider) super.getLabelProvider();
//				return TasksUiImages.getImage(taskElementProvider.getImageDescriptor(element), true);
			}
		} else if (columnIndex == 1) {
			return getSynchronizationStateImage(element);
		} else if (columnIndex == 2) {
			if (!(element instanceof AbstractTaskContainer)) {
				return getContextActivationImage(element);
			}
		}
		return null;
	}

	private Image getSynchronizationStateImage(Object element) {
		AbstractRepositoryTask repositoryTask = null;
		ImageDescriptor image = null;
		if (element instanceof AbstractQueryHit) {
			repositoryTask = ((AbstractQueryHit) element).getCorrespondingTask();
			if (repositoryTask == null) {
//				return TasksUiImages.getImage(TasksUiImages.OVERLAY_INCOMMING_NEW, true);
			}
		} else if (element instanceof AbstractRepositoryTask) {
			repositoryTask = (AbstractRepositoryTask) element;
		}
		if (repositoryTask != null) {
			if (repositoryTask.getSyncState() == RepositoryTaskSyncState.OUTGOING) {
				image = TasksUiImages.STATUS_NORMAL_OUTGOING;
			} else if (repositoryTask.getSyncState() == RepositoryTaskSyncState.INCOMING) {
				image = TasksUiImages.STATUS_NORMAL_INCOMING;
			} else if (repositoryTask.getSyncState() == RepositoryTaskSyncState.CONFLICT) {
				image = TasksUiImages.STATUS_NORMAL_CONFLICT;
			}
			if (image == null && repositoryTask.getStatus() != null) {
				return TasksUiImages.getImage(TasksUiImages.STATUS_WARNING, true);
			} else if (image != null) {
				return TasksUiImages.getImage(image, true);
			}
		} else if (element instanceof AbstractQueryHit) {
			return TasksUiImages.getImage(TasksUiImages.STATUS_NORMAL_INCOMING, true);
		} else if (element instanceof AbstractTaskContainer) {
			AbstractTaskContainer container = (AbstractTaskContainer) element;
			if (container instanceof AbstractRepositoryQuery) {
				AbstractRepositoryQuery query = (AbstractRepositoryQuery) container;
				if (query.getStatus() != null) {
					return TasksUiImages.getImage(TasksUiImages.STATUS_WARNING, true);
				}
			}
			if (view != null && !Arrays.asList(view.getViewer().getExpandedElements()).contains(element)
					&& hasIncoming(container)) {
				return TasksUiImages.getImage(TasksUiImages.STATUS_NORMAL_INCOMING, true);
			}
		}
		return null;
	}

	private Image getContextActivationImage(Object element) {
		ITask task = TaskElementLabelProvider.getCorrespondingTask((ITaskListElement) element);
		if (task != null) {
			if (task.isActive()) {
				return TasksUiImages.getImage(TasksUiImages.TASK_ACTIVE, true);
			} else {
				if (ContextCorePlugin.getContextManager().hasContext(task.getHandleIdentifier())) {
					return TasksUiImages.getImage(TasksUiImages.TASK_INACTIVE_CONTEXT, true);
				} else {
					return TasksUiImages.getImage(TasksUiImages.TASK_INACTIVE, true);
				}
			}
		} else {
			return TasksUiImages.getImage(TasksUiImages.TASK_INACTIVE, true);
		}
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
