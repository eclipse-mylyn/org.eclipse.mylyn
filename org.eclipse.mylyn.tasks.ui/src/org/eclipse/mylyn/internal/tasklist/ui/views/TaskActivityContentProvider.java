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

package org.eclipse.mylar.internal.tasklist.ui.views;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylar.internal.core.MylarContextManager;
import org.eclipse.mylar.provisional.core.IMylarContext;
import org.eclipse.mylar.provisional.core.IMylarContextListener;
import org.eclipse.mylar.provisional.core.IMylarElement;
import org.eclipse.mylar.provisional.core.InteractionEvent;
import org.eclipse.mylar.provisional.core.MylarPlugin;
import org.eclipse.mylar.provisional.tasklist.DateRangeTaskContainer;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.ITaskActivityListener;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;

/**
 * @author Rob Elves
 * @author Mik Kersten
 */
public class TaskActivityContentProvider implements IStructuredContentProvider, ITreeContentProvider {

	private static final String DESCRIPTION_THIS_WEEK = "This Week";

	private static final String DESCRIPTION_PREVIOUS_WEEK = "Previous Week";

	private static final String DESCRIPTION_NEXT_WEEK = "Next Week";

	private DateRangeTaskContainer thisWeek;

	private TreeViewer treeViewer;

	private DateRangeTaskContainer rangeTaskContainer = null;

	private String currentHandle = "";

	private ArrayList<DateRangeTaskContainer> dateRangeContainers = new ArrayList<DateRangeTaskContainer>();

	private Map<String, Calendar> taskToLastOccurrence = new HashMap<String, Calendar>();

	private final IMylarContextListener CONTEXT_LISTENER = new IMylarContextListener() {

		public void contextActivated(IMylarContext context) {
			parseInteractionHistory();
		}

		public void contextDeactivated(IMylarContext context) {

		}

		public void interestChanged(IMylarElement element) {
			// String taskHandle = element.getHandleIdentifier();
			List<InteractionEvent> events = MylarPlugin.getContextManager().getActivityHistoryMetaContext()
					.getInteractionHistory();
			InteractionEvent event = events.get(events.size() - 1);
			parseInteractionEvent(event);
		}

		public void presentationSettingsChanging(UpdateKind kind) {
			// ignore
		}

		public void presentationSettingsChanged(UpdateKind kind) {
			// ignore
		}

		public void interestChanged(List<IMylarElement> elements) {
			// ignore
		}

		public void nodeDeleted(IMylarElement element) {
			// ignore
		}

		public void landmarkAdded(IMylarElement element) {
			// ignore
		}

		public void landmarkRemoved(IMylarElement element) {
			// ignore
		}

		public void edgesChanged(IMylarElement element) {
			// ignore
		}
	};

	private final ITaskActivityListener TASK_LISTENER = new ITaskActivityListener() {

		public void taskActivated(ITask task) {
			// ignore

		}

		public void tasksActivated(List<ITask> tasks) {
			// ignore

		}

		public void taskDeactivated(ITask task) {
			// ignore

		}

		public void localInfoChanged(ITask task) {
			// ignore

		}

		public void repositoryInfoChanged(ITask task) {
			// ignore

		}

		public void tasklistRead() {
			parseInteractionHistory();
		}

		public void taskListModified() {
			// ignore
		}
	};

	public TaskActivityContentProvider(TreeViewer viewer) {
		setupCelendarRanges();
		this.treeViewer = viewer;
		MylarTaskListPlugin.getTaskListManager().addListener(TASK_LISTENER);
		MylarPlugin.getContextManager().addActivityMetaContextListener(CONTEXT_LISTENER);
	}

	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		parseInteractionHistory();
	}

	public void dispose() {
		MylarTaskListPlugin.getTaskListManager().removeListener(TASK_LISTENER);
		MylarPlugin.getContextManager().removeActivityMetaContextListener(CONTEXT_LISTENER);
	}

	public Object[] getElements(Object parent) {
		return dateRangeContainers.toArray();
	}

	private void parseInteractionHistory() {
		if (!MylarTaskListPlugin.getTaskListManager().isTaskListInitialized()) {
			return;
		}
		List<InteractionEvent> events = MylarPlugin.getContextManager().getActivityHistoryMetaContext()
				.getInteractionHistory();
		for (InteractionEvent event : events) {
			parseInteractionEvent(event);
		}
	}

	private void parseInteractionEvent(InteractionEvent event) {
		ITask task = null;
		if (event.getDelta().equals(MylarContextManager.ACTIVITY_ACTIVATED) && rangeTaskContainer == null) {
			task = MylarTaskListPlugin.getTaskListManager().getTaskForHandle(event.getStructureHandle(), true);
			if (task != null) {
				GregorianCalendar calendar = new GregorianCalendar();
				calendar.setTime(event.getDate());
				calendar.getTime();
				rangeTaskContainer = new DateRangeTaskContainer(calendar, task);
				currentHandle = event.getStructureHandle();
			}
		} else if (event.getDelta().equals(MylarContextManager.ACTIVITY_DEACTIVATED) && rangeTaskContainer != null
				&& currentHandle.compareTo(event.getStructureHandle()) == 0) {
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTime(event.getDate());
			cal.getTime();
			rangeTaskContainer.setEnd(cal);
			addToReports(rangeTaskContainer);
			rangeTaskContainer = null;
			task = null;
			currentHandle = "";
		}
	}

	private void addToReports(DateRangeTaskContainer taskContainer) {
		for (DateRangeTaskContainer week : dateRangeContainers) {
			if (week.includes(taskContainer)) {
				week.addTask(taskContainer);
				for (ITask task : taskContainer.getChildren()) {
					addTaskToHistory(task, rangeTaskContainer.getStart());
				}
				treeViewer.refresh(week);
			}
		}
	}

	/** returns null if task not in history * */
	public Calendar getLastOccurrence(String taskHandle) {
		return taskToLastOccurrence.get(taskHandle);
	}

	private void addTaskToHistory(ITask task, Calendar latest) {
		if (taskToLastOccurrence.containsKey(task.getHandleIdentifier())) {
			Calendar calendarOLD = taskToLastOccurrence.get(task.getHandleIdentifier());
			if (latest.after(calendarOLD)) {
				taskToLastOccurrence.put(task.getHandleIdentifier(), latest);
			}
		} else {
			taskToLastOccurrence.put(task.getHandleIdentifier(), latest);
		}
	}

	private void setupCelendarRanges() {

		// Current week
		GregorianCalendar currentBegin = new GregorianCalendar();
		Date startTime = new Date();
		currentBegin.setTime(startTime);
		snapToStartOfWeek(currentBegin);
		GregorianCalendar currentEnd = new GregorianCalendar();
		snapToEndOfWeek(currentEnd);
		thisWeek = new DateRangeTaskContainer(currentBegin, currentEnd, DESCRIPTION_THIS_WEEK);
		dateRangeContainers.add(thisWeek);

		GregorianCalendar previousStart = new GregorianCalendar();
		previousStart.setTime(new Date());
		previousStart.add(Calendar.WEEK_OF_YEAR, -1);
		snapToStartOfWeek(previousStart);
		GregorianCalendar previousEnd = new GregorianCalendar();
		previousEnd.setTime(new Date());
		previousEnd.add(Calendar.WEEK_OF_YEAR, -1);
		snapToEndOfWeek(previousEnd);
		dateRangeContainers.add(new DateRangeTaskContainer(previousStart.getTime(), previousEnd.getTime(),
				DESCRIPTION_PREVIOUS_WEEK));
		
		GregorianCalendar nextStart = new GregorianCalendar();
		nextStart.setTime(new Date());
		nextStart.add(Calendar.WEEK_OF_YEAR, 1);
		snapToStartOfWeek(nextStart);
		GregorianCalendar nextEnd = new GregorianCalendar();
		nextEnd.setTime(new Date());
		nextEnd.add(Calendar.WEEK_OF_YEAR, 1);
		snapToEndOfWeek(nextEnd);
		dateRangeContainers.add(new DateRangeTaskContainer(nextStart.getTime(), nextEnd.getTime(),
				DESCRIPTION_NEXT_WEEK));

	}

	private void snapToStartOfWeek(GregorianCalendar cal) {
		cal.getTime();
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.getTime();
	}

	private void snapToEndOfWeek(GregorianCalendar cal) {
		cal.getTime();
		cal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
		cal.set(Calendar.HOUR_OF_DAY, cal.getMaximum(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, cal.getMaximum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getMaximum(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, cal.getMaximum(Calendar.MILLISECOND));
		cal.getTime();

	}

	public Object getParent(Object child) {
		return new Object[0];
	}

	public Object[] getChildren(Object parent) {
		if (parent instanceof DateRangeTaskContainer) {
			DateRangeTaskContainer taskContainer = (DateRangeTaskContainer) parent;
			return taskContainer.getChildren().toArray();
		} else {
			return new Object[0];
		}
	}

	public boolean hasChildren(Object parent) {
		if (parent instanceof DateRangeTaskContainer) {
			DateRangeTaskContainer dateRangeTaskCategory = (DateRangeTaskContainer) parent;
			return dateRangeTaskCategory.getChildren() != null && dateRangeTaskCategory.getChildren().size() > 0;
		} else {
			return false;
		}
	}
}
