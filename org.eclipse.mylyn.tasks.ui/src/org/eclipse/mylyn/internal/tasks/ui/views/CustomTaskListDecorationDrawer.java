/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.views;

import java.util.Arrays;

import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskListElement;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskListElement;
import org.eclipse.mylyn.tasks.core.AbstractTask.RepositoryTaskSyncState;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
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
				TasksUiPreferenceConstants.OVERLAYS_INCOMING_TIGHT);

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
		AbstractTask task = null;
		Image activationImage = null;
		if (data instanceof AbstractTask) {
			task = (AbstractTask) data;
		}
		if (task != null) {
			if (task.isActive()) {
				activationImage = taskActive;
			} else if (ContextCorePlugin.getContextManager().hasContext(task.getHandleIdentifier())) {
				activationImage = taskInactiveContext;
			} else {
				activationImage = taskInactive;
			}
		}
		if (data instanceof AbstractTaskListElement) {
			switch (event.type) {
			case SWT.EraseItem: {
				if (activationImage != null) {
					drawActivationImage(activationImageOffset, event, activationImage);
				}
				if (!this.taskListView.synchronizationOverlaid) {
					if (data instanceof AbstractTaskListElement) {
						drawSyncronizationImage((AbstractTaskListElement) data, event);
					}
				}
				break;
			}
			case SWT.PaintItem: {
				if (activationImage != null) {
					drawActivationImage(activationImageOffset, event, activationImage);
				}
				if (data instanceof AbstractTaskListElement) {
					drawSyncronizationImage((AbstractTaskListElement) data, event);
				}
				break;
			}
			}
		}
	}

	private void drawSyncronizationImage(AbstractTaskListElement element, Event event) {
		Image image = null;
		int offsetX = 6;
		int offsetY = (event.height / 2) - 5;
		if (taskListView.synchronizationOverlaid) {
			offsetX = event.x + 18 - platformSpecificSquish;
			offsetY += 2;
		}
		if (element instanceof AbstractTaskListElement && !(element instanceof AbstractTask)) {
			if (!Arrays.asList(this.taskListView.getViewer().getExpandedElements()).contains(element)
					&& hasIncoming((AbstractTaskListElement) element)) {
				int additionalSquish = 0;
				if (platformSpecificSquish > 0 && taskListView.synchronizationOverlaid) {
					additionalSquish = platformSpecificSquish + 3;
				} else if (platformSpecificSquish > 0) {
					additionalSquish = platformSpecificSquish / 2;
				}
				if (taskListView.synchronizationOverlaid) {
					image = TasksUiImages.getImage(TasksUiImages.OVERLAY_SYNCH_INCOMMING);
					offsetX = 42 - additionalSquish;
				} else {
					image = TasksUiImages.getImage(TasksUiImages.OVERLAY_INCOMMING);
					offsetX = 24 - additionalSquish;
				}
			}
		} else {
			image = TasksUiImages.getImage(TaskElementLabelProvider.getSynchronizationImageDescriptor(element,
					taskListView.synchronizationOverlaid));
		}
		if (image != null) {
			event.gc.drawImage(image, offsetX, event.y + offsetY);
		}
	}

	private boolean hasIncoming(AbstractTaskListElement container) {
		for (AbstractTask task : container.getChildren()) {
			if (task instanceof AbstractTask) {
				AbstractTask containedRepositoryTask = (AbstractTask) task;
				if (containedRepositoryTask.getSyncState() == RepositoryTaskSyncState.INCOMING) {
					return true;
				}
			}
		}
// if (container instanceof AbstractRepositoryQuery) {
// AbstractRepositoryQuery query = (AbstractRepositoryQuery) container;
// for (AbstractTask hit : query.getHits()) {
// if (hit.getSyncState() == RepositoryTaskSyncState.INCOMING) {
// return true;
// }
// }
// }
		return false;
	}

	private void drawActivationImage(final int activationImageOffset, Event event, Image image) {
		Rectangle rect = image.getBounds();
		int offset = Math.max(0, (event.height - rect.height) / 2);
		event.gc.drawImage(image, activationImageOffset, event.y + offset);
	}
}