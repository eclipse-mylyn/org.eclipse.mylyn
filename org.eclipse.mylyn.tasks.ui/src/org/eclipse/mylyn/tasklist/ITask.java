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

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.eclipse.mylar.tasklist.internal.TaskCategory;

/**
 * @author Mik Kersten
 * 
 * TODO: make IDs be handles, clean up
 */
public interface ITask extends Serializable, ITaskListElement {
    
    public abstract String getHandleIdentifier();
	
    public abstract String getPath();

    public abstract void setPath(String path);
    
    public abstract ITask getParent(); 
    
    public abstract void setParent(ITask parent);

    public abstract boolean isActive();
    
    public abstract void setActive(boolean active, boolean isStalled);
    
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
    
    public abstract String getElapsedTime();
    
    public abstract long getElapsedTimeLong();
    
    public abstract void setElapsedTime(String elapsed);
        
    public abstract int getEstimateTimeHours();
    
//    public abstract String getEstimateTimeForDisplay();
    
    public abstract void setEstimatedTimeHours(int estimated);

    public abstract List<ITask> getChildren();

    public abstract void addSubTask(ITask t);

    public abstract void removeSubTask(ITask t);
    
    public abstract String getDeleteConfirmationMessage();
    
    public abstract void setPriority(String priority);
    
    public abstract void setCategory(ITaskListCategory cat);
    
    public abstract ITaskListCategory getCategory();
    
    public abstract String getElapsedTimeForDisplay();
	
    public abstract long getElapsedMillis();
    
	public abstract Date getEndDate();
	
	public abstract void setEndDate(String date);
	
	public abstract String getEndDateString();
		
	public abstract Date getCreationDate();
	
	public abstract void setCreationDate(String date);
	
	public abstract String getCreationDateString();
	
	
	public abstract void setReminderDate(Date date);
	
	public abstract void setReminderDate(String date);
	
	public abstract Date getReminderDate();
	
	public abstract String getReminderDateString(boolean forDisplay);
	
	public abstract boolean hasBeenReminded();
	
	public abstract void setReminded(boolean reminded);

	abstract void internalSetCategory(TaskCategory category);

	public abstract boolean participatesInTaskHandles();
}