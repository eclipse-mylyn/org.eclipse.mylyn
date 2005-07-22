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
import java.util.List;

import org.eclipse.mylar.tasklist.internal.TaskCategory;

/**
 * @author Mik Kersten
 * 
 * TODO: make IDs be handles
 */
public interface ITask extends Serializable, ITaskListElement {
    
    @Override
    public abstract String toString();

    public abstract String getPath();

    public abstract void setPath(String path);

    public abstract String getHandle();

    public abstract void setHandle(String id);

    public abstract String getLabel();

    public abstract void setLabel(String label);
    
    public abstract ITask getParent(); 
    
    public abstract void setParent(ITask parent);

    public abstract boolean isActive();
    
    public abstract void setActive(boolean active);
    
    public abstract boolean isCompleted();
    
    public abstract void setCompleted(boolean completed);
    
    public abstract RelatedLinks getRelatedLinks();
    
    public abstract void setRelatedLinks(RelatedLinks relatedLinks);
    
    public abstract void addLink(String url);
    
    public abstract void removeLink(String url);
    
    public abstract String getNotes();
    
    public abstract void setNotes(String notes);
    
    public abstract String getElapsedTime();
    
    public abstract void setElapsedTime(String elapsed);
    
    public abstract String getEstimatedTime();
    
    public abstract void setEstimatedTime(String estimated);

    public abstract List<ITask> getChildren();

    public abstract void addSubTask(ITask t);

    public abstract void removeSubTask(ITask t);
        	
	public abstract String getToolTipText();

    public abstract String getPriority();
    
    public abstract boolean canEditDescription();
    
    public abstract String getDeleteConfirmationMessage();
    
    public abstract void setPriority(String priority);
    
    public abstract void setCategory(TaskCategory cat);
    
    public abstract TaskCategory getCategory();
    
    public abstract String getElapsedTimeForDisplay();

	public abstract boolean participatesInTaskHandles();
}