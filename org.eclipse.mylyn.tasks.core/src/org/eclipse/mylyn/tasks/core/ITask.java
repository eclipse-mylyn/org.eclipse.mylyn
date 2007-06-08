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
/*
 * Created on Jan 13, 2005
 */
package org.eclipse.mylyn.tasks.core;

import java.util.Date;
import java.util.Set;

/**
 * @author Mik Kersten
 */
public interface ITask extends ITaskListElement {

	public abstract String getHandleIdentifier();

	public abstract boolean isActive();

	public abstract void setActive(boolean active);

	public String getTaskKind();

	public void setKind(String kind);
	
	public abstract boolean isCompleted();

	public abstract void setCompleted(boolean completed);

	public abstract boolean hasValidUrl();

	public abstract void setTaskUrl(String url);

	public abstract String getTaskUrl();

	public abstract String getNotes();

	public abstract void setNotes(String notes);

	public abstract int getEstimateTimeHours();

	public abstract void setEstimatedTimeHours(int estimated);

	public abstract Set<ITask> getChildren();


	public abstract void setPriority(String priority);

	@Deprecated
	public abstract void setContainer(AbstractTaskContainer category);

	/**
	 * @return null if root task
	 */
	public abstract AbstractTaskContainer getContainer();

	public abstract Date getCompletionDate();

	public abstract void setCompletionDate(Date date);

	public abstract Date getCreationDate();

	public abstract void setCreationDate(Date date);

	public abstract void setScheduledForDate(Date date);

	public abstract Date getScheduledForDate();
	
	public abstract void setDueDate(Date date);
	
	public abstract Date getDueDate();

	public abstract boolean hasBeenReminded();

	public abstract void setReminded(boolean reminded);

	public abstract boolean isPastReminder();

	public abstract void setSummary(String attribute);
}
