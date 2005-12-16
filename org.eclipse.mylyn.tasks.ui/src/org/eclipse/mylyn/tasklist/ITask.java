/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
/*
 * Created on Jan 13, 2005
 */
package org.eclipse.mylar.tasklist;

import java.util.Date;
import java.util.List;

import org.eclipse.mylar.tasklist.ui.ITaskListElement;

/**
 * @author Mik Kersten
 * 
 */
public interface ITask extends ITaskListElement {

	public abstract String getHandleIdentifier();

	public abstract ITask getParent();

	public abstract void setParent(ITask parent);

	public abstract boolean isActive();

	public abstract void setActive(boolean active);

	public abstract void addPlan(String plan);

	public List<String> getPlans();

	public abstract void setCompleted(boolean completed);

	public abstract List<String> getRelatedLinks();

	public abstract void setRelatedLinks(List<String> relatedLinks);

	public abstract void addLink(String url);

	public abstract void removeLink(String url);

	public abstract void setIssueReportURL(String url);

	public abstract String getIssueReportURL();

	public abstract String getNotes();

	public abstract void setNotes(String notes);

	/**
	 * @param time in milliseconds
	 */
	public abstract void setElapsedTime(long elapsed);

	/**
	 * TODO: change to millis
	 */
	public abstract int getEstimateTimeHours();

	public abstract void setEstimatedTimeHours(int estimated);

	public abstract List<ITask> getChildren();

	public abstract void addSubTask(ITask task);

	public abstract void removeSubTask(ITask task);

	public abstract void setPriority(String priority);

	public abstract void setCategory(ITaskCategory category);

	/**
	 * @return null if root task
	 */
	public abstract ITaskCategory getCategory();

	public abstract long getElapsedTime();

	public abstract Date getCompletionDate();

	public abstract void setCompletionDate(Date date);

	public abstract Date getCreationDate();

	public abstract void setCreationDate(Date date);

	public abstract void setReminderDate(Date date);

	public abstract Date getReminderDate();

	/**
	 * TODO: move
	 */
	public abstract boolean hasBeenReminded();

	/**
	 * TODO: move
	 */
	public abstract void setReminded(boolean reminded);

	/**
	 * TODO: move
	 */
	public abstract boolean participatesInTaskHandles();

	public abstract boolean isOverdue();
}

//public abstract String getRemoteContextPath();
//
//	public abstract void setRemoteContextPath(String path);
