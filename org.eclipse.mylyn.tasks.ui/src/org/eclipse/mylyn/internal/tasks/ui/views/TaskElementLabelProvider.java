/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.views;

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
import org.eclipse.mylyn.internal.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.Person;
import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.core.TaskGroup;
import org.eclipse.mylyn.internal.tasks.core.UncategorizedTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.UnmatchedTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask.PriorityLevel;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask.SynchronizationState;
import org.eclipse.mylyn.internal.tasks.ui.ITaskHighlighter;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskElement;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.themes.IThemeManager;

/**
 * @author Mik Kersten
 */
// API 3.0 rename to TaskContainerLabelProvider?
public class TaskElementLabelProvider extends LabelProvider implements IColorProvider, IFontProvider {

	private static final String NO_SUMMARY_AVAILABLE = ": <no summary available>";

	private final IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();

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
		if (element instanceof ITask) {
			if (compositeDescriptor.overlayKind == null) {
				compositeDescriptor.overlayKind = CommonImages.OVERLAY_CLEAR;
			}
			return CommonImages.getCompositeTaskImage(compositeDescriptor.icon, compositeDescriptor.overlayKind,
					wideImages);
		} else if (element instanceof ITaskElement) {
			return CommonImages.getCompositeTaskImage(compositeDescriptor.icon, CommonImages.OVERLAY_CLEAR, wideImages);
		} else {
			return CommonImages.getCompositeTaskImage(compositeDescriptor.icon, null, wideImages);
		}
	}

	private CompositeImageDescriptor getImageDescriptor(Object object) {
		CompositeImageDescriptor compositeDescriptor = new CompositeImageDescriptor();
		if (object instanceof UncategorizedTaskContainer) {
			compositeDescriptor.icon = TasksUiImages.CATEGORY_ARCHIVE;
			return compositeDescriptor;
		} else if (object instanceof TaskCategory) {
			compositeDescriptor.icon = TasksUiImages.CATEGORY;
		} else if (object instanceof TaskGroup) {
			compositeDescriptor.icon = CommonImages.GROUPING;
		}

		if (object instanceof ITaskElement) {
			ITaskElement element = (ITaskElement) object;

			AbstractRepositoryConnectorUi connectorUi = null;
			if (element instanceof ITask) {
				ITask repositoryTask = (ITask) element;
				connectorUi = TasksUiPlugin.getConnectorUi(((ITask) element).getConnectorKind());
				if (connectorUi != null) {
					compositeDescriptor.overlayKind = connectorUi.getTaskKindOverlay(repositoryTask);
				}
			} else if (element instanceof AbstractRepositoryQuery) {
				connectorUi = TasksUiPlugin.getConnectorUi(((AbstractRepositoryQuery) element).getConnectorKind());
			}

			if (connectorUi != null) {
				compositeDescriptor.icon = connectorUi.getTaskListElementIcon(element);
				return compositeDescriptor;
			} else {
				if (element instanceof UnmatchedTaskContainer) {
					compositeDescriptor.icon = TasksUiImages.QUERY_UNMATCHED;
				} else if (element instanceof AbstractRepositoryQuery || object instanceof UnmatchedTaskContainer) {
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

//					for (TaskRepository repository : TasksUiPlugin.getRepositoryManager().getAllRepositories()) {
					if (repository != null
							&& !repository.isAnonymous()
							&& (repository.getUserName() != null && repository.getUserName().equalsIgnoreCase(
									element.getHandleIdentifier()))) {
						compositeDescriptor.icon = CommonImages.PERSON_ME;
//						break;
					}
//					}
				}
				return compositeDescriptor;
			}
		}
		return compositeDescriptor;
	}

	public static ImageDescriptor getSynchronizationImageDescriptor(Object element, boolean synchViewStyle) {
		if (element instanceof ITask) {
			ITask repositoryTask = (ITask) element;
			if (repositoryTask.getSynchronizationState() == SynchronizationState.INCOMING_NEW) {
				if (synchViewStyle) {
					return CommonImages.OVERLAY_SYNC_OLD_INCOMMING_NEW;
				} else {
					return CommonImages.OVERLAY_SYNC_INCOMMING_NEW;
				}
			} else if (repositoryTask.getSynchronizationState() == SynchronizationState.INCOMING
					&& repositoryTask.getLastReadTimeStamp() == null) {
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
			if (imageDescriptor == null && repositoryTask.getSynchronizationStatus() != null) {
				return CommonImages.OVERLAY_SYNC_WARNING;
			} else if (imageDescriptor != null) {
				return imageDescriptor;
			}
		} else if (element instanceof AbstractRepositoryQuery) {
			AbstractRepositoryQuery query = (AbstractRepositoryQuery) element;
			if (query.getSynchronizationStatus() != null) {
				return CommonImages.OVERLAY_SYNC_WARNING;
			}
		}
		// HACK: need a proper blank image
		return CommonImages.OVERLAY_CLEAR;
	}

	public static ImageDescriptor getPriorityImageDescriptor(Object element) {
		AbstractRepositoryConnectorUi connectorUi;
		if (element instanceof ITask) {
			ITask repositoryTask = (ITask) element;
			connectorUi = TasksUiPlugin.getConnectorUi(((ITask) element).getConnectorKind());
			if (connectorUi != null) {
				return connectorUi.getTaskPriorityOverlay(repositoryTask);
			}
		}
		if (element instanceof ITask) {
			ITask task = TaskElementLabelProvider.getCorrespondingTask((ITaskElement) element);
			if (task != null) {
				return TasksUiImages.getImageDescriptorForPriority(PriorityLevel.fromString(task.getPriority()));
			}
		}
		return null;
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
		} else if (object instanceof UnmatchedTaskContainer) {

			UnmatchedTaskContainer container = (UnmatchedTaskContainer) object;

			String result = container.getSummary();
			TaskRepository repository = TasksUi.getRepositoryManager().getRepository(container.getConnectorKind(),
					container.getRepositoryUrl());
			if (repository != null) {
				result = "Unmatched [" + repository.getRepositoryLabel() + "]";
			}

			return result;

		} else if (object instanceof ITaskElement) {
			ITaskElement element = (ITaskElement) object;
			return element.getSummary();
		} else {
			return super.getText(object);
		}
	}

	public Color getForeground(Object object) {
		if (object instanceof ITaskElement && object instanceof ITask) {
			AbstractTask task = getCorrespondingTask((ITaskElement) object);
			if (task != null) {
				if (TasksUiPlugin.getTaskActivityManager().isCompletedToday(task)) {
					return themeManager.getCurrentTheme().getColorRegistry().get(CommonThemes.COLOR_COMPLETED_TODAY);
				} else if (task.isCompleted()) {
					return themeManager.getCurrentTheme().getColorRegistry().get(CommonThemes.COLOR_COMPLETED);
				} else if (task.isActive()) {
					return CommonColors.CONTEXT_ACTIVE;
				} else if (TasksUiPlugin.getTaskActivityManager().isOverdue(task)) {
					return themeManager.getCurrentTheme().getColorRegistry().get(CommonThemes.COLOR_OVERDUE);
				} else if (TasksUiPlugin.getTaskActivityManager().isDueToday(task)) {
					return themeManager.getCurrentTheme().getColorRegistry().get(CommonThemes.COLOR_SCHEDULED_TODAY);
				} else if (!task.internalIsFloatingScheduledDate() && task.isPastReminder()) {
					return themeManager.getCurrentTheme().getColorRegistry().get(CommonThemes.COLOR_SCHEDULED_PAST);
				} else if (TasksUiPlugin.getTaskActivityManager().isScheduledForToday(task)) {
					return themeManager.getCurrentTheme().getColorRegistry().get(CommonThemes.COLOR_SCHEDULED_TODAY);
				} else if (TasksUiPlugin.getTaskActivityManager().isScheduledForThisWeek(task)) {
					return themeManager.getCurrentTheme()
							.getColorRegistry()
							.get(CommonThemes.COLOR_SCHEDULED_THIS_WEEK);
				}
			}
		} else if (object instanceof ITaskElement) {
			for (ITask child : ((ITaskElement) object).getChildren()) {
				if (child.isActive() || showHasActiveChild(child)) {
					return CommonColors.CONTEXT_ACTIVE;
				} else if (TasksUiPlugin.getTaskActivityManager().isOverdue(child)) {
//				} else if ((child.isPastReminder() && !child.isCompleted()) || showHasChildrenPastDue(child)) {
					return themeManager.getCurrentTheme().getColorRegistry().get(CommonThemes.COLOR_OVERDUE);
				}
			}
		}
		return null;
	}

//	private boolean showHasChildrenPastDueHelper(AbstractTaskContainer container) {
//		for (AbstractTaskContainer child : container.getChildren()) {
//			if (child instanceof AbstractTask && ((AbstractTask) child).isPastReminder()
//					&& !((AbstractTask) child).isCompleted()) {
//				return true;
//			} else {
//				if (showHasChildrenPastDueHelper(child)) {
//					return true;
//				}
//			}
//		}
//		return false;
//	}

//	private boolean showHasChildrenPastDue(AbstractTaskContainer container) {
//		if (!TasksUiPlugin.getDefault().groupSubtasks(container)) {
//			return false;
//		}
//
//		return showHasChildrenPastDueHelper(container);
//	}

	/**
	 * TODO: move
	 */
	public static AbstractTask getCorrespondingTask(ITaskElement element) {
		if (element instanceof ITask) {
			return (AbstractTask) element;
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
		}
		return null;
	}

	public Font getFont(Object element) {
		if (!(element instanceof ITaskElement)) {
			return null;
		}
		AbstractTask task = getCorrespondingTask((ITaskElement) element);
		if (task != null) {
			ITask repositoryTask = task;
			if (repositoryTask.isSynchronizing()) {
				return CommonFonts.ITALIC;
			}
		}
		if (element instanceof ITaskElement) {
			if (element instanceof AbstractRepositoryQuery) {
				if (((AbstractRepositoryQuery) element).isSynchronizing()) {
					return CommonFonts.ITALIC;
				}
			}

			for (ITask child : ((ITaskElement) element).getChildren()) {
				if (child.isActive() || showHasActiveChild(child)) {
					return CommonFonts.BOLD;
				}
			}
		}
		if (task != null) {
			if (task.isActive()) {
				return CommonFonts.BOLD;
			} else if (task.isCompleted()) {
				return CommonFonts.STRIKETHROUGH;
			}
			for (ITask child : ((ITaskElement) element).getChildren()) {
				if (child.isActive() || showHasActiveChild(child)) {
					return CommonFonts.BOLD;
				}
			}
		}
		return null;
	}

	private boolean showHasActiveChild(ITaskElement container) {
		if (!TasksUiPlugin.getDefault().groupSubtasks(container)) {
			return false;
		}

		return showHasActiveChildHelper(container, new HashSet<ITaskElement>());
	}

	private boolean showHasActiveChildHelper(ITaskElement container, Set<ITaskElement> visitedContainers) {
		for (ITaskElement child : container.getChildren()) {
			if (visitedContainers.contains(child)) {
				continue;
			}
			visitedContainers.add(child);
			if (child instanceof ITask && ((AbstractTask) child).isActive()) {
				return true;
			} else {
				if (showHasActiveChildHelper(child, visitedContainers)) {
					return true;
				}
			}
		}
		return false;
	}
}
