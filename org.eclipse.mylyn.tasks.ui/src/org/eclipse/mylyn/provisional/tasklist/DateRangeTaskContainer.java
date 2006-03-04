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
import java.util.HashSet;
import java.util.Set;


/**
 * @author Rob Elves
 * @author Mik Kersten
 */
public class DateRangeTaskContainer implements ITaskContainer {
	
	private Set<ITask> tasks = new HashSet<ITask>();
	
	private GregorianCalendar startDate;

	private GregorianCalendar endDate;

	private long totalElapsed = 0;

	private long totalEstimated = 0;
	
	private String description;

	public DateRangeTaskContainer(GregorianCalendar startDate, GregorianCalendar endDate, String description) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.setDescription(description);
	}

	public DateRangeTaskContainer(GregorianCalendar startDate, GregorianCalendar endDate) {
//		super(startDate.hashCode() + endDate.hashCode() + "");
		String start = DateFormat.getDateInstance(DateFormat.FULL).format(startDate.getTime());
		String end = DateFormat.getDateInstance(DateFormat.FULL).format(endDate.getTime());
		this.description = start + " to " + end;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public DateRangeTaskContainer(GregorianCalendar startDate, ITask task) {
		this.description = startDate.toString();
		this.startDate = startDate;
		tasks.add(task);
	}

	public DateRangeTaskContainer(Date time, Date time2, String description) {
		startDate = new GregorianCalendar();
		startDate.setTime(time);
		endDate = new GregorianCalendar();
		endDate.setTime(time2);
		this.description = description; 
	}

	public boolean includes(DateRangeTaskContainer cal) {
		return (startDate.getTimeInMillis() < cal.getStart().getTimeInMillis()) && (endDate.getTimeInMillis() > cal.getEnd().getTimeInMillis());
	}

	public void addTask(DateRangeTaskContainer taskContainer) {
		for (ITask task : taskContainer.getChildren()) {
			totalElapsed += taskContainer.getEnd().getTimeInMillis() - taskContainer.getStart().getTimeInMillis();
			totalEstimated += task.getEstimateTimeHours();
			tasks.add(task);
//			internalAddTask(task);
		}
	}

	public Calendar getStart() {
		return startDate;
	}

	public Calendar getEnd() {
		return endDate;
	}

	public void setEnd(GregorianCalendar calendar) {
		endDate = calendar;
	}

	public long getTotalElapsed() {
		return totalElapsed;
	}

	public long getTotalEstimated() {
		return totalEstimated;
	}

	public Set<ITask> getChildren() {
		return tasks;
	}

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
}