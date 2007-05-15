/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.tasks.ui.views;

import java.util.Arrays;

import org.eclipse.mylar.context.core.ContextCorePlugin;
import org.eclipse.mylar.internal.tasks.ui.TaskListPreferenceConstants;
import org.eclipse.mylar.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylar.tasks.core.AbstractQueryHit;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.AbstractTaskContainer;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.ITaskListElement;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask.RepositoryTaskSyncState;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Mik Kersten
 */
class CustomTaskListDecorationDrawer implements Listener {

	private final TaskListView taskListView;

	private int activationImageOffset;

	private Image taskActive = TasksUiImages.getImage(TasksUiImages.TASK_ACTIVE);

	private Image taskInactive = TasksUiImages.getImage(TasksUiImages.TASK_INACTIVE);

	private Image taskInactiveContext = TasksUiImages.getImage(TasksUiImages.TASK_INACTIVE_CONTEXT);

	// see bug 185004 
	private int platformSpecificSquish = 0;
	
	CustomTaskListDecorationDrawer(TaskListView taskListView, int activationImageOffset) {
		this.taskListView = taskListView;
		this.activationImageOffset = activationImageOffset;
		this.taskListView.synchronizationOverlaid = TasksUiPlugin.getDefault().getPluginPreferences().getBoolean(
				TaskListPreferenceConstants.INCOMING_OVERLAID);
		
		if (SWT.getPlatform().equals("gtk")) {
			platformSpecificSquish = 8;
		} else if (SWT.getPlatform().equals("carbon")) {
			platformSpecificSquish = 3;
		}
	}

	/*
	 * NOTE: MeasureItem, PaintItem and EraseItem are called repeatedly.
	 * Therefore, it is critical for performance that these methods be as
	 * efficient as possible.
	 */
	public void handleEvent(Event event) {
		Object data = event.item.getData();
		ITask task = null;
		Image activationImage = null;
		if (data instanceof ITask) {
			task = (ITask) data;
		} else if (data instanceof AbstractQueryHit) {
			task = ((AbstractQueryHit) data).getCorrespondingTask();
		}
		if (task != null) {
			if (task.isActive()) {
				activationImage = taskActive;
			} else if (ContextCorePlugin.getContextManager().hasContext(task.getHandleIdentifier())) {
				activationImage = taskInactiveContext;
			} else {
				activationImage = taskInactive;
			}
		} else if (data instanceof AbstractQueryHit) {
			activationImage = taskInactive;
		}
		if (data instanceof ITaskListElement) {
			switch (event.type) {
			case SWT.EraseItem: {
				if (activationImage != null) {
					drawActivationImage(activationImageOffset, event, activationImage);
				}
				if (!this.taskListView.synchronizationOverlaid) {
					if (data instanceof ITaskListElement) {
						drawSyncronizationImage((ITaskListElement) data, event);
					}
				}
				break;
			}
			case SWT.PaintItem: {
				if (activationImage != null) {
					drawActivationImage(activationImageOffset, event, activationImage);
				}
				if (data instanceof ITaskListElement) {
					drawSyncronizationImage((ITaskListElement) data, event);
				}
				break;
			}
			}
		}
	}

	private void drawSyncronizationImage(ITaskListElement element, Event event) {
		Image image = null;
		int offsetX = 6;
		int offsetY = (event.height / 2) - 5;
		if (taskListView.synchronizationOverlaid) {
			offsetX = event.x + 19 - platformSpecificSquish;
			offsetY += 2;
		}
		if (element instanceof AbstractTaskContainer) {
			if (element instanceof AbstractTaskContainer) {
				if (!Arrays.asList(this.taskListView.getViewer().getExpandedElements()).contains(element)
						&& hasIncoming((AbstractTaskContainer) element)) {
					if (taskListView.synchronizationOverlaid) {
						image = TasksUiImages.getImage(TasksUiImages.OVERLAY_SYNCH_INCOMMING);
						offsetX = 42 - platformSpecificSquish;
					} else {
						image = TasksUiImages.getImage(TasksUiImages.STATUS_NORMAL_INCOMING);
						offsetX = 24 - platformSpecificSquish;						
					}
				}
			}
		} else {
			image = TasksUiImages.getImage(TaskElementLabelProvider.getSynchronizationImageDescriptor(element, taskListView.synchronizationOverlaid));
		}
		if (image != null) {
			event.gc.drawImage(image, offsetX, event.y + offsetY);
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
			for (AbstractQueryHit hit : query.getHits()) {
				if (hit.getCorrespondingTask() == null) {
					return true;
				}
			}
		}
		return false;
	}

	private void drawActivationImage(final int activationImageOffset, Event event, Image image) {
		Rectangle rect = image.getBounds();
		int offset = Math.max(0, (event.height - rect.height) / 2);
		event.gc.drawImage(image, activationImageOffset, event.y + offset);
	}
}