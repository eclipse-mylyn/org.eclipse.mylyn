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

package org.eclipse.mylar.internal.tasklist.history.ui;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylar.internal.core.MylarContext;
import org.eclipse.mylar.internal.core.MylarContextManager;
import org.eclipse.mylar.provisional.core.InteractionEvent;
import org.eclipse.mylar.provisional.core.MylarPlugin;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskCategory;

/**
 * @author Rob Elves
 */
public class TaskHistoryContentProvider implements IStructuredContentProvider, ITreeContentProvider {

//	private final TaskHistoryView view;
	

	// private CONSTANTS copied from MylarContextManager
	private static final String ACTIVITY_ACTIVATED = "activated";
	private static final String ACTIVITY_DEACTIVATED = "deactivated";
	private static final String ACTIVITY_HANDLE = "attention";
	
	

	MylarContextManager contextManager;

	private DateRangeTaskCategory thisWeek;

	ArrayList<DateRangeTaskCategory> pastWeeks = new ArrayList<DateRangeTaskCategory>();


//	private final ITaskActivityListener TASK_ACTIVITY_LISTENER = new ITaskActivityListener() {
//
//		public void taskActivated(ITask task) {
//			if (task != null) {
////				 todo update
//			}
//		}
//
//		public void tasksActivated(List<ITask> tasks) {
//			if (tasks.size() == 1) {
//				taskActivated(tasks.get(0));
//			}
//		}
//
//		public void taskDeactivated(ITask task) {
//			// todo update
//		}
//
//		public void localInfoChanged(ITask task) {
//
//		}
//
//		public void repositoryInfoChanged(ITask task) {
//
//		}
//
//		public void tasklistRead() {
//
//		}
//
//		public void taskListModified() {
//	
//		}
//	};
	

	// private ContentTaskFilter contentTaskFilter = new ContentTaskFilter();

	public TaskHistoryContentProvider(TaskHistoryView view) {
//		this.view = view;
		contextManager = MylarPlugin.getContextManager();
		setupCelendarRanges();
		populateReports();
		//MylarTaskListPlugin.getTaskListManager().addListener(TASK_ACTIVITY_LISTENER);
	}

	public void inputChanged(Viewer v, Object oldInput, Object newInput) {

	}

	public void dispose() {
//		MylarTaskListPlugin.getTaskListManager().removeListener(TASK_ACTIVITY_LISTENER);
	}

	public Object[] getElements(Object parent) {
		return pastWeeks.toArray();

	}

	private void populateReports() {
		MylarContext context = contextManager.getActivityHistory();
		List<InteractionEvent> events = context.getInteractionHistory();
		DateRangeTaskCategory rangeTask = null;
		String currentHandle = "";
		for (InteractionEvent event : events) {			
			ITask task = null;
			if (event.getStructureKind().equals(ACTIVITY_HANDLE))
				continue;
			if (event.getDelta().equals(ACTIVITY_ACTIVATED) && rangeTask == null) {
				task = MylarTaskListPlugin.getTaskListManager().getTaskForHandle(event.getStructureHandle(), true);
				if (task != null) {
					GregorianCalendar cal = new GregorianCalendar();
					cal.setTime(event.getDate());
					rangeTask = new DateRangeTaskCategory(cal, task);
					currentHandle = event.getStructureHandle();
					continue;
				}
			}

			if (event.getDelta().equals(ACTIVITY_DEACTIVATED) && rangeTask != null
					&& currentHandle.compareTo(event.getStructureHandle()) == 0) {
				GregorianCalendar cal = new GregorianCalendar();
				cal.setTime(event.getDate());
				rangeTask.setEnd(cal);
				addToReports(rangeTask);
				rangeTask = null;
				task = null;
				currentHandle = "";
			}
		}	
	}

	private void addToReports(DateRangeTaskCategory cr) {
		for (DateRangeTaskCategory week : pastWeeks) {
			if (week.includes(cr)) {
				week.addTask(cr);
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
		thisWeek = new DateRangeTaskCategory(currentBegin, currentEnd);
		pastWeeks.add(thisWeek);

		// previous weeks this month
		for (int x = currentWeek - 1; x > 0; x--) {
			GregorianCalendar calStart = new GregorianCalendar();
			GregorianCalendar calEnd = new GregorianCalendar();
			calStart.set(Calendar.WEEK_OF_MONTH, x);
			calEnd.set(Calendar.WEEK_OF_MONTH, x);
			snapToStartOfWeek(calStart);
			snapToEndOfWeek(calEnd);
			pastWeeks.add(new DateRangeTaskCategory(calStart, calEnd));
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
			pastWeeks.add(new DateRangeTaskCategory(calStart, calEnd));
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
		if (parent instanceof DateRangeTaskCategory) {
			DateRangeTaskCategory cr = (DateRangeTaskCategory) parent;
			return cr.getChildren().toArray();
		} else {
			return new Object[0];
		}		
	}

	public boolean hasChildren(Object parent) {
		if (parent instanceof DateRangeTaskCategory) {
			DateRangeTaskCategory dateRangeTaskCategory = (DateRangeTaskCategory) parent;
			return dateRangeTaskCategory.getChildren() != null && dateRangeTaskCategory.getChildren().size() > 0;
		} else {
			return false;
		}
	}

	class DateRangeTaskCategory extends TaskCategory {
		GregorianCalendar startDate;
		GregorianCalendar endDate;
		
		long totalElapsed = 0;
		long totalEstimated = 0;

		public DateRangeTaskCategory(GregorianCalendar startDate, GregorianCalendar endDate, String description) {
			super(startDate.hashCode()+endDate.hashCode()+"");
			this.startDate = startDate;
			this.endDate = endDate;
			super.setDescription(description);
		}
		
		public DateRangeTaskCategory(GregorianCalendar startDate, GregorianCalendar endDate) {
			super(startDate.hashCode()+endDate.hashCode()+"");			
			String start = DateFormat.getDateInstance(DateFormat.FULL).format(startDate.getTime());
			String end = DateFormat.getDateInstance(DateFormat.FULL).format(endDate.getTime());
			super.setDescription(start+" to "+end);
			this.startDate = startDate;
			this.endDate = endDate;
		}

		public DateRangeTaskCategory(GregorianCalendar startDate, ITask task) {
			super(startDate.toString());
			this.startDate = startDate;
			internalAddTask(task);
		}

		public boolean includes(DateRangeTaskCategory cal) {
			return startDate.before(cal.getStart()) && endDate.after(cal.getEnd());
		}

		public void addTask(DateRangeTaskCategory cat) {
			for (ITask task : cat.getChildren()) {
				totalElapsed += cat.getEnd().getTimeInMillis() - cat.getStart().getTimeInMillis();
				totalEstimated += task.getEstimateTimeHours();
				internalAddTask(task);		
			}					
		}

		public Calendar getStart() {
			return startDate;
		}

		public Calendar getEnd() {
			return endDate;
		}

		public void setEnd(GregorianCalendar c) {
			endDate = c;
		}
		
		public long getTotalElapsed() {
			return totalElapsed;
		}

		public long getTotalEstimated() {
			return totalEstimated;
		}
	}

}
