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

package org.eclipse.mylar.provisional.tasklist;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Rob Elves
 * @author Mik Kersten
 */
public class DateRangeContainer implements ITaskContainer {

	private Set<ITask> children = new HashSet<ITask>();

	private Map<TaskActivityDurationDelegate, Long> taskToDuration = new HashMap<TaskActivityDurationDelegate, Long>();

	private GregorianCalendar startDate;

	private GregorianCalendar endDate;

	private long totalElapsed = 0;

	private long totalEstimated = 0;

	private String description;

	public DateRangeContainer(GregorianCalendar startDate, GregorianCalendar endDate, String description) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.setDescription(description);
	}

	public DateRangeContainer(GregorianCalendar startDate, GregorianCalendar endDate) {
		// super(startDate.hashCode() + endDate.hashCode() + "");
		String start = DateFormat.getDateInstance(DateFormat.FULL).format(startDate.getTime());
		String end = DateFormat.getDateInstance(DateFormat.FULL).format(endDate.getTime());
		this.description = start + " to " + end;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public DateRangeContainer(Date time, Date time2, String description) {
		startDate = new GregorianCalendar();
		startDate.setTime(time);
		endDate = new GregorianCalendar();
		endDate.setTime(time2);
		this.description = description;
	}

	public boolean includes(Calendar cal) {
		return (startDate.getTimeInMillis() < cal.getTimeInMillis())
				&& (endDate.getTimeInMillis() > cal.getTimeInMillis());
	}

	public void addTask(TaskActivityDurationDelegate taskWrapper) {
		totalElapsed += taskWrapper.getEnd().getTimeInMillis() - taskWrapper.getStart().getTimeInMillis();
		// totalEstimated += taskWrapper.getTask().getEstimateTimeHours();
		children.add(taskWrapper);
		if (taskToDuration.containsKey(taskWrapper)) {
			long previous = taskToDuration.get(taskWrapper);
			long newDuration = previous
					+ (taskWrapper.getEnd().getTimeInMillis() - taskWrapper.getStart().getTimeInMillis());
			taskToDuration.put(taskWrapper, newDuration);
		} else {
			taskToDuration.put(taskWrapper, taskWrapper.getEnd().getTimeInMillis()
					- taskWrapper.getStart().getTimeInMillis());
		}
	}

	public Calendar getStart() {
		return startDate;
	}

	public Calendar getEnd() {
		return endDate;
	}

	public long getTotalElapsed() {
		return totalElapsed;
	}

	public long getElapsed(TaskActivityDurationDelegate taskWrapper) {
		if (taskToDuration.containsKey(taskWrapper)) {
			return taskToDuration.get(taskWrapper);
		} else {
			return 0;
		}
	}

	public long getTotalEstimated() {
		totalEstimated = 0;
		for (ITask task : children) {
			totalEstimated += task.getEstimateTimeHours();
		}
		return totalEstimated;
	}

//	public Set<TaskActivityDurationDelegate> getElements() {
//		return tasks;
//	}

	public boolean isArchive() {
		return false;
	}

	public void setIsArchive(boolean isArchive) {
		// ignore
	}

	public String getPriority() {
		return "";
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getHandleIdentifier() {
		return description;
	}

	public void setHandleIdentifier(String id) {
		// ignore
	}

	public Set<ITask> getChildren() {
//		Set<ITask> taskSet = new HashSet<ITask>();
//		taskSet.addAll(tasks);
		return children;
//		Set<ITask> emptySet = Collections.emptySet();
//		return emptySet;
	}

	public boolean isPresent() {
		return getStart().before(Calendar.getInstance()) && getEnd().after(Calendar.getInstance());
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((startDate == null) ? 0 : startDate.hashCode());
		result = PRIME * result + ((endDate == null) ? 0 : endDate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final DateRangeContainer other = (DateRangeContainer) obj;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		if (endDate == null) {
			if (other.endDate != null)
				return false;
		} else if (!endDate.equals(other.endDate))
			return false;
		return true;
	}
}