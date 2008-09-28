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

package org.eclipse.mylyn.tasks.ui;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonColors;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonFonts;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonThemes;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AutomaticRepositoryTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.Person;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.core.TaskGroup;
import org.eclipse.mylyn.internal.tasks.core.UncategorizedTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.UnmatchedTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.UnsubmittedTaskContainer;
import org.eclipse.mylyn.internal.tasks.ui.ITaskHighlighter;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskContainer;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.themes.IThemeManager;

/**
 * @author Mik Kersten
 * @since 3.0
 */
public class TaskElementLabelProvider extends LabelProvider implements IColorProvider, IFontProvider {

	private static final String NO_SUMMARY_AVAILABLE = ": <no summary available>";

	private final IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();

	private static final Pattern pattern = Pattern.compile("\\d*: .*");

	private boolean wideImages = false;

	private class CompositeImageDescriptor {

		ImageDescriptor icon;

		ImageDescriptor overlayKind;

	};

	public TaskElementLabelProvider() {
		this(false);
	}

	public TaskElementLabelProvider(boolean wideImages) {
		super();
		this.wideImages = wideImages;
	}

	@Override
	public Image getImage(Object element) {
		CompositeImageDescriptor compositeDescriptor = getImageDescriptor(element);
		if (element instanceof ITask) {
			if (compositeDescriptor.overlayKind == null) {
				compositeDescriptor.overlayKind = CommonImages.OVERLAY_CLEAR;
			}
			return CommonImages.getCompositeTaskImage(compositeDescriptor.icon, compositeDescriptor.overlayKind,
					wideImages);
		} else if (element instanceof ITaskContainer) {
			return CommonImages.getCompositeTaskImage(compositeDescriptor.icon, CommonImages.OVERLAY_CLEAR, wideImages);
		} else {
			return CommonImages.getCompositeTaskImage(compositeDescriptor.icon, null, wideImages);
		}
	}

	private CompositeImageDescriptor getImageDescriptor(Object object) {
		CompositeImageDescriptor compositeDescriptor = new CompositeImageDescriptor();
		if (object instanceof UncategorizedTaskContainer) {
			compositeDescriptor.icon = TasksUiImages.CATEGORY_UNCATEGORIZED;
			return compositeDescriptor;
		} else if (object instanceof UnsubmittedTaskContainer) {
			compositeDescriptor.icon = TasksUiImages.CATEGORY_UNCATEGORIZED;
			return compositeDescriptor;
		} else if (object instanceof TaskCategory) {
			compositeDescriptor.icon = TasksUiImages.CATEGORY;
		} else if (object instanceof TaskGroup) {
			compositeDescriptor.icon = CommonImages.GROUPING;
		}

		if (object instanceof ITaskContainer) {
			IRepositoryElement element = (IRepositoryElement) object;

			AbstractRepositoryConnectorUi connectorUi = null;
			if (element instanceof ITask) {
				ITask repositoryTask = (ITask) element;
				connectorUi = TasksUiPlugin.getConnectorUi(((ITask) element).getConnectorKind());
				if (connectorUi != null) {
					compositeDescriptor.overlayKind = connectorUi.getTaskKindOverlay(repositoryTask);
				}
			} else if (element instanceof IRepositoryQuery) {
				connectorUi = TasksUiPlugin.getConnectorUi(((IRepositoryQuery) element).getConnectorKind());
			}

			if (connectorUi != null) {
				compositeDescriptor.icon = connectorUi.getImageDescriptor(element);
				return compositeDescriptor;
			} else {
				if (element instanceof UnmatchedTaskContainer) {
					compositeDescriptor.icon = TasksUiImages.QUERY_UNMATCHED;
				} else if (element instanceof IRepositoryQuery || object instanceof UnmatchedTaskContainer) {
					compositeDescriptor.icon = TasksUiImages.QUERY;
				} else if (element instanceof ITask) {
					compositeDescriptor.icon = TasksUiImages.TASK;
				} else if (element instanceof ScheduledTaskContainer) {
					compositeDescriptor.icon = CommonImages.CALENDAR;
				} else if (element instanceof Person) {
					compositeDescriptor.icon = CommonImages.PERSON;
					Person person = (Person) element;
					TaskRepository repository = TasksUi.getRepositoryManager().getRepository(person.getConnectorKind(),
							person.getRepositoryUrl());

					if (repository != null
							&& !repository.isAnonymous()
							&& (repository.getUserName() != null && repository.getUserName().equalsIgnoreCase(
									element.getHandleIdentifier()))) {
						compositeDescriptor.icon = CommonImages.PERSON_ME;
					}
				}
				return compositeDescriptor;
			}
		}
		return compositeDescriptor;
	}

	@Override
	public String getText(Object object) {
		if (object instanceof ITask) {
			ITask task = (ITask) object;
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
		} else if (object instanceof AutomaticRepositoryTaskContainer) {
			AutomaticRepositoryTaskContainer container = (AutomaticRepositoryTaskContainer) object;

			String result = container.getSummary();
			TaskRepository repository = TasksUi.getRepositoryManager().getRepository(container.getConnectorKind(),
					container.getRepositoryUrl());
			if (repository != null) {
				result = container.getSummaryLabel() + " [" + repository.getRepositoryLabel() + "]";
			}

			return result;

		} else if (object instanceof ITaskContainer) {
			IRepositoryElement element = (IRepositoryElement) object;
			return element.getSummary();
		} else {
			return super.getText(object);
		}
	}

	public Color getForeground(Object object) {
		if (object instanceof AbstractTask) {
			AbstractTask task = (AbstractTask) object;
			if (task != null) {
				if (TasksUiPlugin.getTaskActivityManager().isCompletedToday(task)) {
					return themeManager.getCurrentTheme().getColorRegistry().get(CommonThemes.COLOR_COMPLETED_TODAY);
				} else if (task.isCompleted()) {
					return themeManager.getCurrentTheme().getColorRegistry().get(CommonThemes.COLOR_COMPLETED);
				} else if (TasksUi.getTaskActivityManager().isActive(task)) {
					return CommonColors.CONTEXT_ACTIVE;
				} else if (TasksUiPlugin.getTaskActivityManager().isOverdue(task)) {
					return themeManager.getCurrentTheme().getColorRegistry().get(CommonThemes.COLOR_OVERDUE);
				} else if (TasksUiPlugin.getTaskActivityManager().isDueToday(task)) {
					return themeManager.getCurrentTheme().getColorRegistry().get(CommonThemes.COLOR_SCHEDULED_TODAY);
				} else if (task.getScheduledForDate() != null
						&& TasksUiPlugin.getTaskActivityManager().isPastReminder(task)) {
					return themeManager.getCurrentTheme().getColorRegistry().get(CommonThemes.COLOR_SCHEDULED_PAST);
				} else if (TasksUiPlugin.getTaskActivityManager().isScheduledForToday(task)) {
					return themeManager.getCurrentTheme().getColorRegistry().get(CommonThemes.COLOR_SCHEDULED_TODAY);
				} else if (TasksUiPlugin.getTaskActivityManager().isScheduledForThisWeek(task)) {
					return themeManager.getCurrentTheme()
							.getColorRegistry()
							.get(CommonThemes.COLOR_SCHEDULED_THIS_WEEK);
				}
			}
		} else if (object instanceof ITaskContainer) {
			for (ITask child : ((ITaskContainer) object).getChildren()) {
				if (child.isActive() || (child instanceof ITaskContainer && showHasActiveChild((ITaskContainer) child))) {
					return CommonColors.CONTEXT_ACTIVE;
				} else if (TasksUiPlugin.getTaskActivityManager().isOverdue(child)) {
//				} else if ((child.isPastReminder() && !child.isCompleted()) || showHasChildrenPastDue(child)) {
					return themeManager.getCurrentTheme().getColorRegistry().get(CommonThemes.COLOR_OVERDUE);
				}
			}
		}
		return null;
	}

	public Color getBackground(Object element) {
		if (element instanceof ITask) {
			ITask task = (ITask) element;
			ITaskHighlighter highlighter = TasksUiPlugin.getDefault().getHighlighter();
			if (highlighter != null) {
				return highlighter.getHighlightColor(task);
			}
		}
		return null;
	}

	public Font getFont(Object element) {
		if (element instanceof AbstractTask) {
			if (((AbstractTask) element).isSynchronizing()) {
				return CommonFonts.ITALIC;
			}
		}

		if (element instanceof IRepositoryQuery) {
			if (((RepositoryQuery) element).isSynchronizing()) {
				return CommonFonts.ITALIC;
			}
		}

		if (element instanceof ITaskContainer) {
			for (ITask child : ((ITaskContainer) element).getChildren()) {
				if (child.isActive() || (child instanceof ITaskContainer && showHasActiveChild((ITaskContainer) child))) {
					return CommonFonts.BOLD;
				}
			}
		}

		if (element instanceof AbstractTask) {
			if (((AbstractTask) element).isActive()) {
				return CommonFonts.BOLD;
			} else if (((AbstractTask) element).isCompleted()) {
				if (CommonFonts.HAS_STRIKETHROUGH
						&& TasksUiPlugin.getDefault().getPluginPreferences().getBoolean(
								ITasksUiPreferenceConstants.USE_STRIKETHROUGH_FOR_COMPLETED)) {
					return CommonFonts.STRIKETHROUGH;
				} else {
					return null;
				}
			}
			for (ITask child : ((ITaskContainer) element).getChildren()) {
				if (child.isActive() || (child instanceof ITaskContainer && showHasActiveChild((ITaskContainer) child))) {
					return CommonFonts.BOLD;
				}
			}
		}
		return null;
	}

	private boolean showHasActiveChild(ITaskContainer container) {
		if (!TasksUiPlugin.getDefault().groupSubtasks(container)) {
			return false;
		}

		return showHasActiveChildHelper(container, new HashSet<IRepositoryElement>());
	}

	private boolean showHasActiveChildHelper(ITaskContainer container, Set<IRepositoryElement> visitedContainers) {
		for (IRepositoryElement child : container.getChildren()) {
			if (visitedContainers.contains(child)) {
				continue;
			}
			visitedContainers.add(child);
			if (child instanceof ITask && ((AbstractTask) child).isActive()) {
				return true;
			} else if (child instanceof ITaskContainer) {
				if (showHasActiveChildHelper((ITaskContainer) child, visitedContainers)) {
					return true;
				}
			}
		}
		return false;
	}
}
