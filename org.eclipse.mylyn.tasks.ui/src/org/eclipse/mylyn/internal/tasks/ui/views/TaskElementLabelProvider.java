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

package org.eclipse.mylar.internal.tasks.ui.views;

import java.util.regex.Pattern;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.mylar.context.core.ContextCorePlugin;
import org.eclipse.mylar.internal.tasks.ui.ITaskHighlighter;
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
import org.eclipse.mylar.tasks.core.TaskCategory;
import org.eclipse.mylar.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
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

	private boolean compositeImages = false;

	private class CompositeImageDescriptor {

		ImageDescriptor icon;

		ImageDescriptor overlayKind;

		ImageDescriptor contextToggle;
	};

	public TaskElementLabelProvider() {
		super();
	}

	/**
	 * @parma wide set true for wide images, with whitespace to right of icon.
	 */
	public TaskElementLabelProvider(boolean compositeImages) {
		super();
		this.compositeImages = compositeImages;
	}

	@Override
	public Image getImage(Object element) {
		CompositeImageDescriptor compositeDescriptor = getImageDescriptor(element, compositeImages);
		if (element instanceof ITask || element instanceof AbstractQueryHit) {
			return TasksUiImages.getCompositeTaskImage(compositeDescriptor.icon, compositeDescriptor.overlayKind,
					compositeDescriptor.contextToggle);
		} else if (element instanceof AbstractTaskContainer) {
			return TasksUiImages.getCompositeContainerImage(compositeDescriptor.icon, null);
		} else {
			return TasksUiImages.getImage(compositeDescriptor.icon);
		}
	}

	private CompositeImageDescriptor getImageDescriptor(Object object, boolean showActivation) {
		CompositeImageDescriptor compositeDescriptor = new CompositeImageDescriptor();
		if (object instanceof TaskArchive) {
			compositeDescriptor.icon = TasksUiImages.CATEGORY_ARCHIVE;
			return compositeDescriptor;
		} else if (object instanceof TaskCategory) {
			compositeDescriptor.icon = TasksUiImages.CATEGORY;
			return compositeDescriptor;
		} else if (object instanceof ITaskListElement) {
			ITaskListElement element = (ITaskListElement) object;

			AbstractRepositoryConnectorUi connectorUi = null;
			if (element instanceof AbstractRepositoryTask) {
				AbstractRepositoryTask repositoryTask = (AbstractRepositoryTask) element;
				connectorUi = TasksUiPlugin.getRepositoryUi(((AbstractRepositoryTask) element).getRepositoryKind());
				if (connectorUi != null) {
					compositeDescriptor.overlayKind = connectorUi.getTaskKindOverlay(repositoryTask);
				}
				if (showActivation) {
					compositeDescriptor.contextToggle = getContextActivationImage(element);
				}
			} else if (element instanceof AbstractQueryHit) {
				AbstractRepositoryTask repositoryTask = ((AbstractQueryHit) element).getCorrespondingTask();
				if (repositoryTask != null) {
					return getImageDescriptor(repositoryTask, showActivation);
				}
			} else if (element instanceof AbstractRepositoryQuery) {
				connectorUi = TasksUiPlugin.getRepositoryUi(((AbstractRepositoryQuery) element).getRepositoryKind());
			}

			if (connectorUi != null) {
				compositeDescriptor.icon = connectorUi.getTaskListElementIcon(element);
				return compositeDescriptor;
			} else {
				if (element instanceof ITask) {
// compositeDescriptor.overlayPriority =
// TasksUiUtil.getImageDescriptorForPriority(PriorityLevel.fromString(((ITask)element).getPriority()));
					if (showActivation) {
						compositeDescriptor.contextToggle = getContextActivationImage(element);
					}
				} else if (element instanceof AbstractQueryHit) {
// ITask correspondingTask = getCorrespondingTask(element);
// if (correspondingTask != null) {
// compositeDescriptor.overlayPriority =
// TasksUiUtil.getImageDescriptorForPriority(PriorityLevel.fromString(correspondingTask.getPriority()));
// } else {
// compositeDescriptor.overlayPriority =
// TasksUiUtil.getImageDescriptorForPriority(PriorityLevel.fromString(((AbstractQueryHit)element).getPriority()));
// }
					if (showActivation) {
						compositeDescriptor.contextToggle = getContextActivationImage(element);
					}
				}
				if (element instanceof ITask || element instanceof AbstractQueryHit) {

				}
				if (element instanceof AbstractRepositoryQuery) {
					compositeDescriptor.icon = TasksUiImages.QUERY;
				} else if (element instanceof AbstractQueryHit) {
					compositeDescriptor.icon = TasksUiImages.TASK;
				} else if (element instanceof ITask) {
					compositeDescriptor.icon = TasksUiImages.TASK;
				} else if (element instanceof DateRangeContainer) {
					compositeDescriptor.icon = TasksUiImages.CALENDAR;
				}
				return compositeDescriptor;
			}
		}
		return null;
	}

	private ImageDescriptor getContextActivationImage(Object element) {
		ITask task = TaskElementLabelProvider.getCorrespondingTask((ITaskListElement) element);
		if (task != null) {
			if (task.isActive()) {
				return TasksUiImages.TASK_ACTIVE;
			} else {
				// XXX: should not be reading from disk when refreshing
				if (ContextCorePlugin.getContextManager().hasContext(task.getHandleIdentifier())) {
					return TasksUiImages.TASK_INACTIVE_CONTEXT;
				} else {
					return TasksUiImages.TASK_INACTIVE;
				}
			}
		} else {
			return TasksUiImages.TASK_INACTIVE;
		}
	}

	@Override
	public String getText(Object object) {
		if (object instanceof AbstractQueryHit) {
			AbstractQueryHit hit = (AbstractQueryHit) object;
			if (hit.getSummary() == null) {
				if (hit.getIdentifyingLabel() != null) {
					return hit.getIdentifyingLabel() + NO_SUMMARY_AVAILABLE;
				} else {
					return hit.getTaskId() + NO_SUMMARY_AVAILABLE;
				}
			} else if (!pattern.matcher(hit.getSummary()).matches() && hit.getIdentifyingLabel() != null
					&& !hit.getIdentifyingLabel().equals("")) {
				return hit.getIdentifyingLabel() + ": " + hit.getSummary();
			} else {
				return hit.getSummary();
			}
		} else if (object instanceof AbstractRepositoryTask) {
			AbstractRepositoryTask task = (AbstractRepositoryTask) object;
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
		} else if (object instanceof ITaskListElement) {
			ITaskListElement element = (ITaskListElement) object;
			return element.getSummary();
		} else {
			return super.getText(object);
		}
	}

	public Color getForeground(Object object) {
		if (object instanceof AbstractTaskContainer) {
			for (ITask child : ((AbstractTaskContainer) object).getChildren()) {
				if (child.isActive()) {
					return TaskListColorsAndFonts.COLOR_TASK_ACTIVE;
				} else if (child.isPastReminder() && !child.isCompleted()) {
					return themeManager.getCurrentTheme().getColorRegistry().get(
							TaskListColorsAndFonts.THEME_COLOR_TASK_OVERDUE);
				}
			}
		} else if (object instanceof AbstractQueryHit && ((AbstractQueryHit) object).getCorrespondingTask() == null) {
			AbstractQueryHit hit = (AbstractQueryHit) object;
			if ((hit.getCorrespondingTask() != null && hit.getCorrespondingTask().isCompleted()) || hit.isCompleted()) {
				return themeManager.getCurrentTheme().getColorRegistry().get(
						TaskListColorsAndFonts.THEME_COLOR_COMPLETED);
			}
		} else if (object instanceof AbstractRepositoryQuery) {
			// FIXME AbstractRepositoryQuery is a subclass of
			// AbstractTaskContainer so this is probably a dead branch!
			for (AbstractQueryHit child : ((AbstractRepositoryQuery) object).getHits()) {
				ITask task = (child).getCorrespondingTask();
				if (task != null && task.isActive()) {
					return TaskListColorsAndFonts.COLOR_TASK_ACTIVE;
				}
			}
		} else if (object instanceof ITaskListElement) {
			ITask task = getCorrespondingTask((ITaskListElement) object);
			if (task != null) {
				if (TasksUiPlugin.getTaskListManager().isCompletedToday(task)) {
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
				} else if (TasksUiPlugin.getTaskListManager().isScheduledForToday(task)) {
					return themeManager.getCurrentTheme().getColorRegistry().get(
							TaskListColorsAndFonts.THEME_COLOR_TASK_TODAY_SCHEDULED);
				} else if (TasksUiPlugin.getTaskListManager().isScheduledForThisWeek(task)) {
					return themeManager.getCurrentTheme().getColorRegistry().get(
							TaskListColorsAndFonts.THEME_COLOR_TASK_THISWEEK_SCHEDULED);
				}
			}
		}
		return null;
	}

	/**
	 * TODO: move
	 */
	public static ITask getCorrespondingTask(ITaskListElement element) {
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
			ITaskHighlighter highlighter = TasksUiPlugin.getDefault().getHighlighter();
			if (highlighter != null) {
				return highlighter.getHighlightColor(task);
			}
		} else if (element instanceof AbstractQueryHit) {
			return getBackground(((AbstractQueryHit) element).getCorrespondingTask());
		}
		// return
		// Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
		return null;
	}

	public Font getFont(Object element) {
		if (!(element instanceof ITaskListElement)) {
			return null;
		}
		ITask task = getCorrespondingTask((ITaskListElement) element);
		if (task instanceof AbstractRepositoryTask) {
			AbstractRepositoryTask repositoryTask = (AbstractRepositoryTask) task;
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
			for (ITask child : ((AbstractTaskContainer) element).getChildren()) {
				if (child.isActive()) {
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
			for (ITask child : task.getChildren()) {
				if (child.isActive()) {
					return TaskListColorsAndFonts.BOLD;
				}
			}
		} else if (element instanceof AbstractQueryHit) {
			if (((AbstractQueryHit) element).isCompleted()) {
				return TaskListColorsAndFonts.STRIKETHROUGH;
			}
		}
		return null;
	}
}
