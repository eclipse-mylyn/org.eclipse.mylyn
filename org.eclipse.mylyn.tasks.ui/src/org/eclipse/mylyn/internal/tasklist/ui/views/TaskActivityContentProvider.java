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
import java.util.GregorianCalendar;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylar.internal.core.MylarContextManager;
import org.eclipse.mylar.provisional.core.InteractionEvent;
import org.eclipse.mylar.provisional.core.MylarPlugin;
import org.eclipse.mylar.provisional.tasklist.DateRangeTaskContainer;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;

/**
 * @author Rob Elves
 * @author Mik Kersten
 */
public class TaskActivityContentProvider implements IStructuredContentProvider, ITreeContentProvider {

	private DateRangeTaskContainer thisWeek;

	private ArrayList<DateRangeTaskContainer> dateRangeContainers = new ArrayList<DateRangeTaskContainer>();
	
	public TaskActivityContentProvider(TaskActivityView view) {
		setupCelendarRanges();
//		populateReports();
	}

	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		populateReports();
	}

	public void dispose() {

	}

	public Object[] getElements(Object parent) {
		return dateRangeContainers.toArray();
	}

	private void populateReports() {
		if (!MylarTaskListPlugin.getTaskListManager().isTaskListInitialized()) {
			return;
		}
		List<InteractionEvent> events = MylarPlugin.getContextManager().getActivityHistoryMetaContext().getInteractionHistory();
		DateRangeTaskContainer rangeTaskContainer = null;
		String currentHandle = "";
		ITask task = null;
		for (InteractionEvent event : events) {
			if (event.getStructureKind().equals(MylarContextManager.ACTIVITY_HANDLE))
				continue;
			if (event.getDelta().equals(MylarContextManager.ACTIVITY_ACTIVATED) && rangeTaskContainer == null) {
				task = MylarTaskListPlugin.getTaskListManager().getTaskForHandle(event.getStructureHandle(), true); 
				if (task != null) {
					GregorianCalendar calendar = new GregorianCalendar();
					calendar.setTime(event.getDate());
					rangeTaskContainer = new DateRangeTaskContainer(calendar, task);
					currentHandle = event.getStructureHandle();
					continue; 
				}
			} 
			
			if (event.getDelta().equals(MylarContextManager.ACTIVITY_DEACTIVATED) && rangeTaskContainer != null
					&& currentHandle.compareTo(event.getStructureHandle()) == 0) {
				GregorianCalendar cal = new GregorianCalendar();
				cal.setTime(event.getDate());
				rangeTaskContainer.setEnd(cal);
				addToReports(rangeTaskContainer);
				rangeTaskContainer = null;
				task = null;
				currentHandle = "";
			}
		}
	}

	private void addToReports(DateRangeTaskContainer taskContainer) {
		for (DateRangeTaskContainer week : dateRangeContainers) {
			if (week.includes(taskContainer)) {
				week.addTask(taskContainer);
			}
		}
	}

	private void setupCelendarRanges() {
		// Current week
		GregorianCalendar currentBegin = new GregorianCalendar();
		int currentWeek = currentBegin.get(Calendar.WEEK_OF_MONTH);
		snapToStartOfWeek(currentBegin);
		GregorianCalendar currentEnd = new GregorianCalendar();
		snapToEndOfWeek(currentEnd);
		thisWeek = new DateRangeTaskContainer(currentBegin, currentEnd);
		dateRangeContainers.add(thisWeek);

		// previous weeks this month
		for (int x = currentWeek - 1; x > 0; x--) {
			GregorianCalendar calStart = new GregorianCalendar();
			GregorianCalendar calEnd = new GregorianCalendar();
			calStart.set(Calendar.WEEK_OF_MONTH, x);
			calEnd.set(Calendar.WEEK_OF_MONTH, x);
			snapToStartOfWeek(calStart);
			snapToEndOfWeek(calEnd);
			dateRangeContainers.add(new DateRangeTaskContainer(calStart, calEnd));
		}

		// Report past month
		GregorianCalendar pastMonth = new GregorianCalendar();

		int previousMonth = pastMonth.get(Calendar.MONTH) - 1;
		if (previousMonth >= Calendar.JANUARY) {
			pastMonth.set(Calendar.MONTH, previousMonth);
		} else {
			pastMonth.set(Calendar.YEAR, pastMonth.get(Calendar.YEAR) - 1);
			pastMonth.set(Calendar.MONTH, Calendar.DECEMBER);
		}

		for (int x = pastMonth.getMaximum(Calendar.WEEK_OF_MONTH); x > 0; x--) {
			GregorianCalendar calStart = (GregorianCalendar) pastMonth.clone();
			GregorianCalendar calEnd = (GregorianCalendar) pastMonth.clone();
			calStart.set(Calendar.WEEK_OF_MONTH, x);
			calEnd.set(Calendar.WEEK_OF_MONTH, x);
			snapToStartOfWeek(calStart);
			snapToEndOfWeek(calEnd);
			dateRangeContainers.add(new DateRangeTaskContainer(calStart, calEnd));
		}

	}

	private void snapToStartOfWeek(GregorianCalendar cal) {
		cal.set(Calendar.DAY_OF_WEEK, cal.getMinimum(Calendar.DAY_OF_WEEK));
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.getTime();
	}

	private void snapToEndOfWeek(GregorianCalendar cal) {
		cal.set(Calendar.DAY_OF_WEEK, cal.getMaximum(Calendar.DAY_OF_WEEK));
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
