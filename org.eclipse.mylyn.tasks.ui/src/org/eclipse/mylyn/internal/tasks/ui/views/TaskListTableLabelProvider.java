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
import org.eclipse.mylar.tasks.core.Task.PriorityLevel;
import org.eclipse.mylar.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
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
			TaskListView view, boolean wideImages) {
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
			}
		}
		return null;
	}

	public Image getColumnImage(Object element, int columnIndex) {
		if (!(element instanceof ITaskListElement)) {
			return null;
		}
		if (columnIndex == 0) {
			return super.getImage(element);
		} else if (columnIndex == 1) {
			AbstractRepositoryConnectorUi connectorUi = null;
			ImageDescriptor priorityOverlay = null;
			if (element instanceof AbstractRepositoryTask) {
				AbstractRepositoryTask repositoryTask = (AbstractRepositoryTask) element;
				connectorUi = TasksUiPlugin.getRepositoryUi(((AbstractRepositoryTask) element).getRepositoryKind());
				if (connectorUi != null) {
					priorityOverlay = connectorUi.getTaskPriorityOverlay(repositoryTask);
				}
			}

			if (priorityOverlay == null && (element instanceof ITask || element instanceof AbstractQueryHit)) {
				ITask task = TaskElementLabelProvider.getCorrespondingTask((ITaskListElement) element);
				if (task != null) {
					priorityOverlay = TasksUiImages.getImageDescriptorForPriority(PriorityLevel.fromString(task
							.getPriority()));
				}
			}
			return TasksUiImages.getCompositeSynchronizationImage(getSynchronizationImageDescriptor(element),
					priorityOverlay);
		}
		return null;
	}

	private ImageDescriptor getSynchronizationImageDescriptor(Object element) {
		AbstractRepositoryTask repositoryTask = null;
		ImageDescriptor imageDescriptor = null;
		if (element instanceof AbstractQueryHit) {
			repositoryTask = ((AbstractQueryHit) element).getCorrespondingTask();
			if (repositoryTask == null) {
				return TasksUiImages.STATUS_OVERLAY_INCOMMING_NEW;
			}
		} else if (element instanceof AbstractRepositoryTask) {
			repositoryTask = (AbstractRepositoryTask) element;
		}
		if (repositoryTask != null) {
			if (repositoryTask.getSyncState() == RepositoryTaskSyncState.OUTGOING) {
				imageDescriptor = TasksUiImages.STATUS_NORMAL_OUTGOING;
			} else if (repositoryTask.getSyncState() == RepositoryTaskSyncState.INCOMING) {
				imageDescriptor = TasksUiImages.STATUS_NORMAL_INCOMING;
			} else if (repositoryTask.getSyncState() == RepositoryTaskSyncState.CONFLICT) {
				imageDescriptor = TasksUiImages.STATUS_NORMAL_CONFLICT;
			}
			if (imageDescriptor == null && repositoryTask.getStatus() != null) {
				return TasksUiImages.STATUS_WARNING;
			} else if (imageDescriptor != null) {
				return imageDescriptor;
			}
		} else if (element instanceof AbstractQueryHit) {
			return TasksUiImages.STATUS_NORMAL_INCOMING;
		} else if (element instanceof AbstractTaskContainer) {
			AbstractTaskContainer container = (AbstractTaskContainer) element;
			if (container instanceof AbstractRepositoryQuery) {
				AbstractRepositoryQuery query = (AbstractRepositoryQuery) container;
				if (query.getStatus() != null) {
					return TasksUiImages.STATUS_WARNING;
				}
			}
			if (view != null && !Arrays.asList(view.getViewer().getExpandedElements()).contains(element)
					&& hasIncoming(container)) {
				return TasksUiImages.STATUS_NORMAL_INCOMING;
			}
		}
		// HACK: need blank image
		return TasksUiImages.PRIORITY_3;
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
