/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.views;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.core.UnmatchedTaskContainer;
import org.eclipse.mylyn.internal.tasks.ui.AbstractTaskListFilter;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskContainer;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
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

	private final int activationImageOffset;

	private final Image taskActive = CommonImages.getImage(TasksUiImages.CONTEXT_ACTIVE);

	private final Image taskInactive = CommonImages.getImage(TasksUiImages.CONTEXT_INACTIVE_EMPTY);

	private final Image taskInactiveContext = CommonImages.getImage(TasksUiImages.CONTEXT_INACTIVE);

	// see bug 185004
	private final int platformSpecificSquish;

	private final Rectangle lastClippingArea = new Rectangle(0, 0, 0, 0);

	private final boolean tweakClipping;

	CustomTaskListDecorationDrawer(TaskListView taskListView, int activationImageOffset) {
		this.taskListView = taskListView;
		this.activationImageOffset = activationImageOffset;
		this.taskListView.synchronizationOverlaid = TasksUiPlugin.getDefault().getPluginPreferences().getBoolean(
				ITasksUiPreferenceConstants.OVERLAYS_INCOMING_TIGHT);

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
		if (data instanceof ITask) {
			AbstractTask task = (AbstractTask) data;
			if (task.isActive()) {
				activationImage = taskActive;
			} else if (ContextCore.getContextManager().hasContext(task.getHandleIdentifier())) {
				activationImage = taskInactiveContext;
			} else {
				activationImage = taskInactive;
			}
		}
		if (data instanceof ITaskContainer) {
			switch (event.type) {
			case SWT.EraseItem: {
				if (activationImage != null) {
					drawActivationImage(activationImageOffset, event, activationImage);
				}
				if (!this.taskListView.synchronizationOverlaid) {
					if (data instanceof ITaskContainer) {
						drawSyncronizationImage((ITaskContainer) data, event);
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
				if (data instanceof ITaskContainer) {
					drawSyncronizationImage((ITaskContainer) data, event);
				}
				if (tweakClipping) {
					event.gc.setClipping(clipping);
				}
				break;
			}
			}
		}
	}

	private void drawSyncronizationImage(ITaskContainer element, Event event) {
		Image image = null;
		int offsetX = 6;
		int offsetY = (event.height / 2) - 5;
		if (taskListView.synchronizationOverlaid) {
			offsetX = event.x + 18 - platformSpecificSquish;
			offsetY += 2;
		}
		if (element != null) {
			if (element instanceof ITask) {
				image = CommonImages.getImage(getSynchronizationImageDescriptor(element,
						taskListView.synchronizationOverlaid));
			} else {
				int imageOffset = 0;
				if (!hideDecorationOnContainer(element, (TreeItem) event.item)
						&& AbstractTaskListFilter.hasDescendantIncoming(element)) {
					if (taskListView.synchronizationOverlaid) {
						image = CommonImages.getImage(CommonImages.OVERLAY_SYNC_OLD_INCOMMING);
					} else {
						image = CommonImages.getImage(CommonImages.OVERLAY_SYNC_INCOMMING);
					}
				} else if (element instanceof IRepositoryQuery) {
					RepositoryQuery query = (RepositoryQuery) element;
					if (query.getStatus() != null) {
						image = CommonImages.getImage(CommonImages.OVERLAY_SYNC_WARNING);
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

	private boolean hideDecorationOnContainer(ITaskContainer element, TreeItem treeItem) {
		if (element instanceof UnmatchedTaskContainer) {
			if (!taskListView.isFocusedMode()) {
				return false;
			} else if (AbstractTaskListFilter.hasDescendantIncoming(element)) {
				return true;
			}
		} else if (element instanceof IRepositoryQuery) {
			RepositoryQuery query = (RepositoryQuery) element;
			if (query.getStatus() != null) {
				return true;
			}
		}

		if (!taskListView.isFocusedMode()) {
			return false;
		} else if (element instanceof IRepositoryQuery || element instanceof TaskCategory) {
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

	private ImageDescriptor getSynchronizationImageDescriptor(Object element, boolean synchViewStyle) {
		if (element instanceof ITask) {
			ITask repositoryTask = (ITask) element;
			if (repositoryTask.getSynchronizationState() == SynchronizationState.INCOMING_NEW) {
				if (synchViewStyle) {
					return CommonImages.OVERLAY_SYNC_OLD_INCOMMING_NEW;
				} else {
					return CommonImages.OVERLAY_SYNC_INCOMMING_NEW;
				}
			} else if (repositoryTask.getSynchronizationState() == SynchronizationState.OUTGOING_NEW) {
				if (synchViewStyle) {
					return CommonImages.OVERLAY_SYNC_OLD_OUTGOING;
				} else {
					return CommonImages.OVERLAY_SYNC_OUTGOING_NEW;
				}
			} else if (repositoryTask.getSynchronizationState() == SynchronizationState.INCOMING
					&& ((AbstractTask) repositoryTask).getLastReadTimeStamp() == null) {
				if (synchViewStyle) {
					return CommonImages.OVERLAY_SYNC_OLD_INCOMMING_NEW;
				} else {
					return CommonImages.OVERLAY_SYNC_INCOMMING_NEW;
				}
			}
			ImageDescriptor imageDescriptor = null;
			if (repositoryTask.getSynchronizationState() == SynchronizationState.OUTGOING
					|| repositoryTask.getSynchronizationState() == SynchronizationState.OUTGOING_NEW) {
				if (synchViewStyle) {
					imageDescriptor = CommonImages.OVERLAY_SYNC_OLD_OUTGOING;
				} else {
					imageDescriptor = CommonImages.OVERLAY_SYNC_OUTGOING;
				}
			} else if (repositoryTask.getSynchronizationState() == SynchronizationState.INCOMING) {
				if (synchViewStyle) {
					imageDescriptor = CommonImages.OVERLAY_SYNC_OLD_INCOMMING;
				} else {
					imageDescriptor = CommonImages.OVERLAY_SYNC_INCOMMING;
				}
			} else if (repositoryTask.getSynchronizationState() == SynchronizationState.CONFLICT) {
				imageDescriptor = CommonImages.OVERLAY_SYNC_CONFLICT;
			}
			if (imageDescriptor == null && repositoryTask instanceof AbstractTask
					&& ((AbstractTask) repositoryTask).getStatus() != null) {
				return CommonImages.OVERLAY_SYNC_WARNING;
			} else if (imageDescriptor != null) {
				return imageDescriptor;
			}
		} else if (element instanceof IRepositoryQuery) {
			RepositoryQuery query = (RepositoryQuery) element;
			if (query.getStatus() != null) {
				return CommonImages.OVERLAY_SYNC_WARNING;
			}
		}
		// HACK: need a proper blank image
		return CommonImages.OVERLAY_CLEAR;
	}
}