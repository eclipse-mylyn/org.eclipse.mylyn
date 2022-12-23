/*******************************************************************************
 * Copyright (c) 2004, 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Frank Becker - fixes for bug 169916
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.views;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.commons.ui.PlatformUiUtil;
import org.eclipse.mylyn.commons.ui.compatibility.CommonFonts;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.UnmatchedTaskContainer;
import org.eclipse.mylyn.internal.tasks.ui.AbstractTaskListFilter;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskScheduleContentProvider.StateTaskContainer;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
import org.eclipse.mylyn.tasks.core.ITaskContainer;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Mik Kersten
 * @author Frank Becker
 */
public class CustomTaskListDecorationDrawer implements Listener {

	private final int activationImageOffset;

	private final Image taskActive = CommonImages.getImage(TasksUiImages.CONTEXT_ACTIVE);

	private final Image taskInactive = CommonImages.getImage(TasksUiImages.CONTEXT_INACTIVE_EMPTY);

	private final Image taskInactiveContext = CommonImages.getImage(TasksUiImages.CONTEXT_INACTIVE);

	// see bug 185004
	private final int platformSpecificSquish;

	private boolean useStrikethroughForCompleted;

	private boolean synchronizationOverlaid;

	private boolean focusedMode;

	private final org.eclipse.jface.util.IPropertyChangeListener PROPERTY_LISTENER = new org.eclipse.jface.util.IPropertyChangeListener() {

		public void propertyChange(org.eclipse.jface.util.PropertyChangeEvent event) {
			if (event.getProperty().equals(ITasksUiPreferenceConstants.USE_STRIKETHROUGH_FOR_COMPLETED)) {
				if (event.getNewValue() instanceof Boolean) {
					useStrikethroughForCompleted = (Boolean) event.getNewValue();
				}
			} else if (event.getProperty().equals(ITasksUiPreferenceConstants.OVERLAYS_INCOMING_TIGHT)) {
				if (event.getNewValue() instanceof Boolean) {
					synchronizationOverlaid = (Boolean) event.getNewValue();
				}
			}
		}
	};

	public CustomTaskListDecorationDrawer(int activationImageOffset, boolean focusedMode) {
		this.activationImageOffset = activationImageOffset;
		this.platformSpecificSquish = PlatformUiUtil.getTreeItemSquish();
		this.synchronizationOverlaid = TasksUiPlugin.getDefault()
				.getPreferenceStore()
				.getBoolean(ITasksUiPreferenceConstants.OVERLAYS_INCOMING_TIGHT);
		this.useStrikethroughForCompleted = TasksUiPlugin.getDefault()
				.getPreferenceStore()
				.getBoolean(ITasksUiPreferenceConstants.USE_STRIKETHROUGH_FOR_COMPLETED);
		this.focusedMode = focusedMode;
		TasksUiPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(PROPERTY_LISTENER);
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
			} else if (TasksUiPlugin.getContextStore().hasContext(task)) {
				activationImage = taskInactiveContext;
			} else {
				activationImage = taskInactive;
			}
		}
		if (!CommonFonts.HAS_STRIKETHROUGH) {
			if (data instanceof AbstractTask & useStrikethroughForCompleted) {
				AbstractTask task = (AbstractTask) data;
				if (task.isCompleted()) {
					Rectangle bounds;
					//if (isCOCOA) {
					bounds = ((TreeItem) event.item).getTextBounds(0);
//					} else {
//						bounds = ((TreeItem) event.item).getBounds();
//					}
					int lineY = bounds.y + (bounds.height / 2);
					String itemText = ((TreeItem) event.item).getText();
					Point extent = event.gc.textExtent(itemText);
					event.gc.drawLine(bounds.x, lineY, bounds.x + extent.x, lineY);
				}
			}
		}
		if (data instanceof ITaskContainer) {
			switch (event.type) {
			case SWT.EraseItem: {
				if ("gtk".equals(SWT.getPlatform())) { //$NON-NLS-1$
					// GTK requires drawing on erase event so that images don't disappear when selected.
					if (activationImage != null) {
						drawActivationImage(activationImageOffset, event, activationImage);
					}
					if (!this.synchronizationOverlaid) {
						if (data instanceof ITaskContainer) {
							drawSyncronizationImage((ITaskContainer) data, event);
						}
					}
				}

				// TODO: would be nice not to do this on each item's painting
//				String text = tree.getFilterControl().getText();
//				if (text != null && !text.equals("") && tree.getViewer().getExpandedElements().length <= 12) {
//					int offsetY = tree.getViewer().getExpandedElements().length * tree.getViewer().getTree().getItemHeight();
//					event.gc.drawText("Open search dialog...", 20, offsetY - 10);
//				}
				break;
			}
			case SWT.PaintItem: {
				if (activationImage != null) {
					drawActivationImage(activationImageOffset, event, activationImage);
				}
				if (data instanceof ITaskContainer) {
					drawSyncronizationImage((ITaskContainer) data, event);
				}
				break;
			}
			}
		}
	}

	private void drawSyncronizationImage(ITaskContainer element, Event event) {
		Image image = null;
		int offsetX = PlatformUiUtil.getIncomingImageOffset();
		int offsetY = (event.height / 2) - 5;
		if (synchronizationOverlaid) {
			offsetX = event.x + 18 - platformSpecificSquish;
			offsetY += 2;
		}
		if (element != null) {
			if (element instanceof ITask) {
				image = CommonImages.getImage(getSynchronizationImageDescriptor(element, synchronizationOverlaid));
			} else {
				int imageOffset = 0;
				if (!hideDecorationOnContainer(element, (TreeItem) event.item)
						&& AbstractTaskListFilter.hasDescendantIncoming(element)) {
					if (synchronizationOverlaid) {
						image = CommonImages.getImage(CommonImages.OVERLAY_SYNC_OLD_INCOMMING);
					} else {
						image = CommonImages.getImage(CommonImages.OVERLAY_SYNC_INCOMMING);
					}
				} else if (element instanceof IRepositoryQuery) {
					RepositoryQuery query = (RepositoryQuery) element;
					if (query.getStatus() != null) {
						image = CommonImages.getImage(TasksUiInternal.getIconFromStatusOfQuery(query));
						if (synchronizationOverlaid) {
							imageOffset = 11;
						} else {
							imageOffset = 3;
						}
					}
				}

				int additionalSquish = 0;
				if (platformSpecificSquish > 0 && synchronizationOverlaid) {
					additionalSquish = platformSpecificSquish + 3;
				} else if (platformSpecificSquish > 0) {
					additionalSquish = platformSpecificSquish / 2;
				}
				if (synchronizationOverlaid) {
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
		if (element instanceof StateTaskContainer) {
			return true;
		} else if (element instanceof UnmatchedTaskContainer) {
			if (!focusedMode) {
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

		if (focusedMode) {
			return false;
		} else if (!(element instanceof ITask)) {
			return treeItem.getExpanded();
		} else {
			return false;
		}
	}

	protected void drawActivationImage(final int activationImageOffset, Event event, Image image) {
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
				if (!Boolean.parseBoolean(
						repositoryTask.getAttribute(ITasksCoreConstants.ATTRIBUTE_TASK_SUPPRESS_INCOMING))) {
					if (synchViewStyle) {
						imageDescriptor = CommonImages.OVERLAY_SYNC_OLD_INCOMMING;
					} else {
						imageDescriptor = CommonImages.OVERLAY_SYNC_INCOMMING;
					}
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
			return TasksUiInternal.getIconFromStatusOfQuery(query);
		}
		// HACK: need a proper blank image
		return CommonImages.OVERLAY_CLEAR;
	}

	public void dispose() {
		TasksUiPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(PROPERTY_LISTENER);
	}

	public void setUseStrikethroughForCompleted(boolean useStrikethroughForCompleted) {
		this.useStrikethroughForCompleted = useStrikethroughForCompleted;
	}

	public void setSynchronizationOverlaid(boolean synchronizationOverlaid) {
		this.synchronizationOverlaid = synchronizationOverlaid;
	}

	public boolean isFocusedMode() {
		return focusedMode;
	}

	public void setFocusedMode(boolean focusedMode) {
		this.focusedMode = focusedMode;
	}

}
