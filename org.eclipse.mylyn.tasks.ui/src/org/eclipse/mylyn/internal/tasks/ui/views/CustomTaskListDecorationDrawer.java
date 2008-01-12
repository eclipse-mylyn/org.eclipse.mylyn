/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.views;

import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.tasks.core.OrphanedTasksContainer;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.ui.AbstractTaskListFilter;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPreferenceConstants;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TreeItem;

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
	private final int platformSpecificSquish;

	private final Rectangle lastClippingArea = new Rectangle(0, 0, 0, 0);

	private final boolean tweakClipping;

	CustomTaskListDecorationDrawer(TaskListView taskListView, int activationImageOffset) {
		this.taskListView = taskListView;
		this.activationImageOffset = activationImageOffset;
		this.taskListView.synchronizationOverlaid = TasksUiPlugin.getDefault().getPluginPreferences().getBoolean(
				TasksUiPreferenceConstants.OVERLAYS_INCOMING_TIGHT);

		if (SWT.getPlatform().equals("gtk")) {
			this.platformSpecificSquish = 8;
			this.tweakClipping = true;
		} else if (SWT.getPlatform().equals("carbon")) {
			this.platformSpecificSquish = 3;
			this.tweakClipping = false;
		} else {
			this.platformSpecificSquish = 0;
			this.tweakClipping = false;
		}
	}

	/*
	 * NOTE: MeasureItem, PaintItem and EraseItem are called repeatedly.
	 * Therefore, it is critical for performance that these methods be as
	 * efficient as possible.
	 */
	public void handleEvent(Event event) {
		Object data = event.item.getData();
		Image activationImage = null;
		if (data instanceof AbstractTask) {
			AbstractTask task = (AbstractTask) data;
			if (task.isActive()) {
				activationImage = taskActive;
			} else if (ContextCorePlugin.getContextManager().hasContext(task.getHandleIdentifier())) {
				activationImage = taskInactiveContext;
			} else {
				activationImage = taskInactive;
			}
		}
		if (data instanceof AbstractTaskContainer) {
			switch (event.type) {
			case SWT.EraseItem: {
				if (activationImage != null) {
					drawActivationImage(activationImageOffset, event, activationImage);
				}
				if (!this.taskListView.synchronizationOverlaid) {
					if (data instanceof AbstractTaskContainer) {
						drawSyncronizationImage((AbstractTaskContainer) data, event);
					}
				}

				// TODO: would be nice not to do this on each item's painting
//				String text = tree.getFilterControl().getText();
//				if (text != null && !text.equals("") && tree.getViewer().getExpandedElements().length <= 12) {
//					int offsetY = tree.getViewer().getExpandedElements().length * tree.getViewer().getTree().getItemHeight();
//					event.gc.drawText("Open search dialog...", 20, offsetY - 10);
//				}
				if (tweakClipping) {
					lastClippingArea.x = event.x;
					lastClippingArea.y = event.y;
					lastClippingArea.width = event.width;
					lastClippingArea.height = event.height;
				}
				break;
			}
			case SWT.PaintItem: {
				Rectangle clipping = null;
				if (tweakClipping) {
					clipping = event.gc.getClipping();
					event.gc.setClipping(lastClippingArea);
				}
				if (activationImage != null) {
					drawActivationImage(activationImageOffset, event, activationImage);
				}
				if (data instanceof AbstractTaskContainer) {
					drawSyncronizationImage((AbstractTaskContainer) data, event);
				}
				if (tweakClipping) {
					event.gc.setClipping(clipping);
				}
				break;
			}
			}
		}
	}

	private void drawSyncronizationImage(AbstractTaskContainer element, Event event) {
		Image image = null;
		int offsetX = 6;
		int offsetY = (event.height / 2) - 5;
		if (taskListView.synchronizationOverlaid) {
			offsetX = event.x + 18 - platformSpecificSquish;
			offsetY += 2;
		}
		if (element != null) {
			if (element instanceof AbstractTask) {
				image = TasksUiImages.getImage(TaskElementLabelProvider.getSynchronizationImageDescriptor(element,
						taskListView.synchronizationOverlaid));
			} else {
				int imageOffset = 0;
				if (!hideDecorationOnContainer(element, (TreeItem) event.item)
						&& AbstractTaskListFilter.hasDescendantIncoming(element)) {
					if (taskListView.synchronizationOverlaid) {
						image = TasksUiImages.getImage(TasksUiImages.OVERLAY_SYNCH_INCOMMING);
					} else {
						image = TasksUiImages.getImage(TasksUiImages.OVERLAY_INCOMMING);
					}
				} else if (element instanceof AbstractRepositoryQuery) {
					AbstractRepositoryQuery query = (AbstractRepositoryQuery) element;
					if (query.getSynchronizationStatus() != null) {
						image = TasksUiImages.getImage(TasksUiImages.OVERLAY_WARNING);
						if (taskListView.synchronizationOverlaid) {
							imageOffset = 11;
						} else {
							imageOffset = 3;
						}
					}
				}

				int additionalSquish = 0;
				if (platformSpecificSquish > 0 && taskListView.synchronizationOverlaid) {
					additionalSquish = platformSpecificSquish + 3;
				} else if (platformSpecificSquish > 0) {
					additionalSquish = platformSpecificSquish / 2;
				}
				if (taskListView.synchronizationOverlaid) {
					offsetX = 42 - imageOffset - additionalSquish;
				} else {
					offsetX = 24 - imageOffset - additionalSquish;
				}
			}
		}

		if (image != null) {
			event.gc.drawImage(image, offsetX, event.y + offsetY);
		}
	}

	private boolean hideDecorationOnContainer(AbstractTaskContainer element, TreeItem treeItem) {
		if (element instanceof OrphanedTasksContainer) {
			return true;
		} else if (element instanceof AbstractRepositoryQuery) {
			AbstractRepositoryQuery query = (AbstractRepositoryQuery) element;
			if (query.getSynchronizationStatus() != null) {
				return true;
			}
		}

		if (!taskListView.isFocusedMode()) {
			return false;
		} else if (element instanceof AbstractRepositoryQuery || element instanceof TaskCategory) {
			return treeItem.getExpanded();
		} else {
			return false;
		}
	}

	private void drawActivationImage(final int activationImageOffset, Event event, Image image) {
		Rectangle rect = image.getBounds();
		int offset = Math.max(0, (event.height - rect.height) / 2);
		event.gc.drawImage(image, activationImageOffset, event.y + offset);
	}
}