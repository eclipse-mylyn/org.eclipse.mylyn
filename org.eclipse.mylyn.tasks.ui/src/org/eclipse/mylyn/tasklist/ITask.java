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
 * TODO: make IDs be handles, clean up
 */
public interface ITask extends ITaskListElement {
    
    public abstract String getHandleIdentifier();
	
    public abstract ITask getParent(); 
    
    public abstract void setParent(ITask parent);

    public abstract boolean isActive();
    
    public abstract void setActive(boolean active, boolean isStalled);

	/**
	 * TODO: consider changing to java.net.URL
	 */
    public abstract String getContextPath();

    public abstract void setContextPath(String path);
    
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
        
    public abstract void setEstimatedTimeHours(int estimated);

    public abstract List<ITask> getChildren();

    public abstract void addSubTask(ITask t);

    public abstract void removeSubTask(ITask t);
    
    public abstract String getDeleteConfirmationMessage();
    
    public abstract void setPriority(String priority);
    
    public abstract void setCategory(ITaskCategory cat);
    
    /**
     * @return null if root task
     */
    public abstract ITaskCategory getCategory();
    
    public abstract long getElapsedMillis();
    
	public abstract Date getCompletionDate();
	
	public abstract void setCompletionDate(Date date);
	
	public abstract Date getCreationDate();
	
	public abstract void setCreationDate(Date date);

	public abstract void setReminderDate(Date date);
	
	public abstract Date getReminderDate();
	
	public abstract boolean hasBeenReminded();
	
	public abstract void setReminded(boolean reminded);

	public abstract boolean participatesInTaskHandles();
}

//abstract void internalSetCategory(TaskCategory category);

//public abstract void setEndDate(String date);
//public abstract void setCreationDate(String date);
//public abstract void setReminderDate(String date);	

//public abstract String getElapsedTimeForDisplay();

///** 
// * @deprecated
// */
//public abstract String getEndDateForPersistance();

///** 
// * @deprecated
// */
//public abstract String getReminderDateString(boolean forDisplay);

///** 
// * @deprecated
// */
//public abstract String getCreationDateForPersistance();
