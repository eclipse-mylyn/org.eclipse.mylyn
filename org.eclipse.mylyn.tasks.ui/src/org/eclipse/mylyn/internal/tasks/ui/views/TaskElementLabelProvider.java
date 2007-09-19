/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.views;

import java.util.regex.Pattern;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.mylyn.internal.tasks.core.Person;
import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityManager;
import org.eclipse.mylyn.internal.tasks.core.TaskArchive;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.core.TaskGroup;
import org.eclipse.mylyn.internal.tasks.core.UnfiledCategory;
import org.eclipse.mylyn.internal.tasks.ui.ITaskHighlighter;
import org.eclipse.mylyn.internal.tasks.ui.TaskListColorsAndFonts;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPreferenceConstants;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.AbstractTask.PriorityLevel;
import org.eclipse.mylyn.tasks.core.AbstractTask.RepositoryTaskSyncState;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.themes.IThemeManager;

/**
 * @author Mik Kersten
 */
public class TaskElementLabelProvider extends LabelProvider implements IColorProvider, IFontProvider {

	private static final String NO_SUMMARY_AVAILABLE = ": <no summary available>";

	private IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();

	private static final Pattern pattern = Pattern.compile("\\d*: .*");

	private boolean wideImages = false;

	private class CompositeImageDescriptor {

		ImageDescriptor icon;

		ImageDescriptor overlayKind;

	};

//	public TaskElementLabelProvider() {
//		super();
//	}

	public TaskElementLabelProvider(boolean wideImages) {
		super();
		this.wideImages = wideImages;
	}

	@Override
	public Image getImage(Object element) {
		CompositeImageDescriptor compositeDescriptor = getImageDescriptor(element);
		if (element instanceof AbstractTask) {
			if (compositeDescriptor.overlayKind == null) {
				compositeDescriptor.overlayKind = TasksUiImages.OVERLAY_BLANK;
			}
			return TasksUiImages.getCompositeTaskImage(compositeDescriptor.icon, compositeDescriptor.overlayKind,
					wideImages);
		} else if (element instanceof AbstractTaskContainer) {
			return TasksUiImages.getCompositeTaskImage(compositeDescriptor.icon, TasksUiImages.OVERLAY_BLANK,
					wideImages);
		} else {
			return TasksUiImages.getCompositeTaskImage(compositeDescriptor.icon, null, wideImages);
		}
	}

	private CompositeImageDescriptor getImageDescriptor(Object object) {
		CompositeImageDescriptor compositeDescriptor = new CompositeImageDescriptor();
		if (object instanceof TaskArchive || object instanceof UnfiledCategory) {
			compositeDescriptor.icon = TasksUiImages.CATEGORY_ARCHIVE;
			return compositeDescriptor;
		} else if (object instanceof TaskCategory || object instanceof UnfiledCategory) {
			compositeDescriptor.icon = TasksUiImages.CATEGORY;
		} else if (object instanceof TaskGroup) {
			compositeDescriptor.icon = TasksUiImages.TASKLIST_MODE;
		}

		if (object instanceof AbstractTaskContainer) {
			AbstractTaskContainer element = (AbstractTaskContainer) object;

			AbstractRepositoryConnectorUi connectorUi = null;
			if (element instanceof AbstractTask) {
				AbstractTask repositoryTask = (AbstractTask) element;
				connectorUi = TasksUiPlugin.getConnectorUi(((AbstractTask) element).getConnectorKind());
				if (connectorUi != null) {
					compositeDescriptor.overlayKind = connectorUi.getTaskKindOverlay(repositoryTask);
				}
			} else if (element instanceof AbstractRepositoryQuery) {
				connectorUi = TasksUiPlugin.getConnectorUi(((AbstractRepositoryQuery) element).getRepositoryKind());
			}

			if (connectorUi != null) {
				compositeDescriptor.icon = connectorUi.getTaskListElementIcon(element);
				return compositeDescriptor;
			} else {
				if (element instanceof AbstractRepositoryQuery) {
					compositeDescriptor.icon = TasksUiImages.QUERY;
				} else if (element instanceof AbstractTask) {
					compositeDescriptor.icon = TasksUiImages.TASK;
				} else if (element instanceof ScheduledTaskContainer) {
					compositeDescriptor.icon = TasksUiImages.CALENDAR;
				} else if (element instanceof Person) {
					compositeDescriptor.icon = TasksUiImages.PERSON;
					for (TaskRepository repository : TasksUiPlugin.getRepositoryManager().getAllRepositories()) {
						if (!repository.isAnonymous() && (repository.getUserName() == null || repository.getUserName().equalsIgnoreCase(
										element.getHandleIdentifier()))) {
							compositeDescriptor.icon = TasksUiImages.PERSON_ME;
							break;
						}
					}
				}
				return compositeDescriptor;
			}
		}
		return compositeDescriptor;
	}

	public static ImageDescriptor getSynchronizationImageDescriptor(Object element, boolean synchViewStyle) {
		AbstractTask repositoryTask = null;
		ImageDescriptor imageDescriptor = null;
		if (element instanceof AbstractTask) {
			repositoryTask = (AbstractTask) element;
		}
		if (repositoryTask != null) {
			if (repositoryTask.getSynchronizationState() == RepositoryTaskSyncState.INCOMING
					&& repositoryTask.getLastReadTimeStamp() == null) {
				if (synchViewStyle) {
					return TasksUiImages.OVERLAY_SYNCH_INCOMMING_NEW;
				} else {
					return TasksUiImages.OVERLAY_INCOMMING_NEW;
				}
			}
			if (repositoryTask.getSynchronizationState() == RepositoryTaskSyncState.OUTGOING) {
				if (synchViewStyle) {
					imageDescriptor = TasksUiImages.OVERLAY_SYNCH_OUTGOING;
				} else {
					imageDescriptor = TasksUiImages.OVERLAY_OUTGOING;
				}
			} else if (repositoryTask.getSynchronizationState() == RepositoryTaskSyncState.INCOMING) {
				if (synchViewStyle) {
					imageDescriptor = TasksUiImages.OVERLAY_SYNCH_INCOMMING;
				} else {
					imageDescriptor = TasksUiImages.OVERLAY_INCOMMING;
				}
			} else if (repositoryTask.getSynchronizationState() == RepositoryTaskSyncState.CONFLICT) {
				imageDescriptor = TasksUiImages.OVERLAY_CONFLICT;
			}
			if (imageDescriptor == null && repositoryTask.getSynchronizationStatus() != null) {
				return TasksUiImages.OVERLAY_WARNING;
			} else if (imageDescriptor != null) {
				return imageDescriptor;
			}
		} else if (element instanceof AbstractTaskContainer) {
			AbstractTaskContainer container = (AbstractTaskContainer) element;
			if (container instanceof AbstractRepositoryQuery) {
				AbstractRepositoryQuery query = (AbstractRepositoryQuery) container;
				if (query.getSynchronizationStatus() != null) {
					return TasksUiImages.OVERLAY_WARNING;
				}
			}
		}
		// HACK: need a proper blank image
		return TasksUiImages.OVERLAY_BLANK;
	}

	public static ImageDescriptor getPriorityImageDescriptor(Object element) {
		AbstractRepositoryConnectorUi connectorUi;
		if (element instanceof AbstractTask) {
			AbstractTask repositoryTask = (AbstractTask) element;
			connectorUi = TasksUiPlugin.getConnectorUi(((AbstractTask) element).getConnectorKind());
			if (connectorUi != null) {
				return connectorUi.getTaskPriorityOverlay(repositoryTask);
			}
		}
		if (element instanceof AbstractTask) {
			AbstractTask task = TaskElementLabelProvider.getCorrespondingTask((AbstractTaskContainer) element);
			if (task != null) {
				return TasksUiImages.getImageDescriptorForPriority(PriorityLevel.fromString(task.getPriority()));
			}
		}
		return null;
	}

	@Override
	public String getText(Object object) {
		if (object instanceof AbstractTask) {
			AbstractTask task = (AbstractTask) object;
			if (task.getSummary() == null) {
				if (task.getTaskKey() != null) {
					return task.getTaskKey() + NO_SUMMARY_AVAILABLE;
				} else {
					return task.getTaskId() + NO_SUMMARY_AVAILABLE;
				}
			} else if (!pattern.matcher(task.getSummary()).matches()) {
				if (task.getTaskKey() != null) {
					return task.getTaskKey() + ": " + task.getSummary();
				} else {
					return task.getSummary();
				}
			} else {
				return task.getSummary();
			}
		} else if (object instanceof TaskGroup) {
			TaskGroup element = (TaskGroup) object;
			return element.getSummary();// + " / " + element.getChildren().size();
		} else if (object instanceof AbstractTaskContainer) {
			AbstractTaskContainer element = (AbstractTaskContainer) object;
			return element.getSummary();
		} else {
			return super.getText(object);
		}
	}

	public Color getForeground(Object object) {
		if (object instanceof AbstractTaskContainer && object instanceof AbstractTask) {
			AbstractTask task = getCorrespondingTask((AbstractTaskContainer) object);
			if (task != null) {
				if (TaskActivityManager.getInstance().isCompletedToday(task)) {
					return themeManager.getCurrentTheme().getColorRegistry().get(
							TaskListColorsAndFonts.THEME_COLOR_TASK_TODAY_COMPLETED);
				} else if (task.isCompleted()) {
					return themeManager.getCurrentTheme().getColorRegistry().get(
							TaskListColorsAndFonts.THEME_COLOR_COMPLETED);
				} else if (task.isActive()) {
					return TaskListColorsAndFonts.COLOR_TASK_ACTIVE;
				} else if (task.isPastReminder()) {
					return themeManager.getCurrentTheme().getColorRegistry().get(
							TaskListColorsAndFonts.THEME_COLOR_TASK_OVERDUE);
				} else if (TaskActivityManager.getInstance().isScheduledForToday(task)) {
					return themeManager.getCurrentTheme().getColorRegistry().get(
							TaskListColorsAndFonts.THEME_COLOR_TASK_TODAY_SCHEDULED);
				} else if (TaskActivityManager.getInstance().isScheduledForThisWeek(task)) {
					return themeManager.getCurrentTheme().getColorRegistry().get(
							TaskListColorsAndFonts.THEME_COLOR_TASK_THISWEEK_SCHEDULED);
				}
			}
		} else if (object instanceof AbstractTaskContainer) {
			for (AbstractTask child : ((AbstractTaskContainer) object).getChildren()) {
				if (child.isActive() || showHasActiveChild(child)) {
					return TaskListColorsAndFonts.COLOR_TASK_ACTIVE;
				} else if ((child.isPastReminder() && !child.isCompleted()) || showHasChildrenPastDue(child)) {
					return themeManager.getCurrentTheme().getColorRegistry().get(
							TaskListColorsAndFonts.THEME_COLOR_TASK_OVERDUE);
				}
			}
		}
		return null;
	}

	/**
	 * TODO: move
	 */
	public static AbstractTask getCorrespondingTask(AbstractTaskContainer element) {
		if (element instanceof AbstractTask) {
			return (AbstractTask) element;
		} else {
			return null;
		}
	}

	public Color getBackground(Object element) {
		if (element instanceof AbstractTask) {
			AbstractTask task = (AbstractTask) element;
			ITaskHighlighter highlighter = TasksUiPlugin.getDefault().getHighlighter();
			if (highlighter != null) {
				return highlighter.getHighlightColor(task);
			}
		}
		return null;
	}

	public Font getFont(Object element) {
		if (!(element instanceof AbstractTaskContainer)) {
			return null;
		}
		AbstractTask task = getCorrespondingTask((AbstractTaskContainer) element);
		if (task != null) {
			AbstractTask repositoryTask = task;
			if (repositoryTask.isSynchronizing()) {
				return TaskListColorsAndFonts.ITALIC;
			}
		}
		if (element instanceof AbstractTaskContainer) {
			if (element instanceof AbstractRepositoryQuery) {
				if (((AbstractRepositoryQuery) element).isSynchronizing()) {
					return TaskListColorsAndFonts.ITALIC;
				}
			}

			for (AbstractTask child : ((AbstractTaskContainer) element).getChildren()) {
				if (child.isActive() || showHasActiveChild(child)) {
					return TaskListColorsAndFonts.BOLD;
				}
			}
		}
		if (task != null) {
			if (task.isActive()) {
				return TaskListColorsAndFonts.BOLD;
			} else if (task.isCompleted()) {
				return TaskListColorsAndFonts.STRIKETHROUGH;
			}
			for (AbstractTask child : ((AbstractTaskContainer) element).getChildren()) {
				if (child.isActive() || showHasActiveChild(child)) {
					return TaskListColorsAndFonts.BOLD;
				}
			}
		}
		return null;
	}

	private boolean showHasActiveChild(AbstractTaskContainer container) {
		if (!TasksUiPlugin.getDefault().getPreferenceStore().getBoolean(TasksUiPreferenceConstants.GROUP_SUBTASKS)) {
			return false;
		}

		for (AbstractTaskContainer child : container.getChildren()) {
			if (child instanceof AbstractTask && ((AbstractTask) child).isActive()) {
				return true;
			} else {
				if (showHasActiveChild(child)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean showHasChildrenPastDue(AbstractTaskContainer container) {
		if (!TasksUiPlugin.getDefault().getPreferenceStore().getBoolean(TasksUiPreferenceConstants.GROUP_SUBTASKS)) {
			return false;
		}

		for (AbstractTaskContainer child : container.getChildren()) {
			if (child instanceof AbstractTask && ((AbstractTask) child).isPastReminder()
					&& !((AbstractTask) child).isCompleted()) {
				return true;
			} else {
				if (showHasChildrenPastDue(child)) {
					return true;
				}
			}
		}
		return false;
	}
}
