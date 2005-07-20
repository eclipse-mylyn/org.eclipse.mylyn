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
 * Created on Dec 26, 2004
  */
package org.eclipse.mylar.tasks.internal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylar.tasks.AbstractCategory;
import org.eclipse.mylar.tasks.ITask;
import org.eclipse.mylar.tasks.TaskListImages;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;


/**
 * @author Mik Kersten
 */
public class TaskCategory extends AbstractCategory implements Serializable {

    private static final long serialVersionUID = 3834024740813027380L;
    
    private List<ITask> tasks = new ArrayList<ITask>();
    
    public TaskCategory(String description) {
    	super(description);
    }
    
	public Image getIcon() {
		return TaskListImages.getImage(TaskListImages.CATEGORY);
	}
    

	public String getPriority() {		
		String highestPriority = "P5";
		if (tasks.isEmpty()) {
			return "P1";
		}
		for (ITask task : tasks) {
			if (highestPriority.compareTo(task.getPriority()) > 0) {
				highestPriority = task.getPriority();
			}
		}
		return highestPriority;
	}
	
    public void addTask(ITask task) {
    	tasks.add(task);
    }
    
    public void removeTask(ITask task) {
        tasks.remove(task);
    }
    
    public List<ITask> getChildren() {
        return tasks;
    }   
   
    @Override
    public boolean equals(Object object) {
        if (object == null) return false;
        if (object instanceof TaskCategory) {
           TaskCategory compare = (TaskCategory)object;
           return this.getDescription(false).equals(compare.getDescription(false));
        } else {
            return false;
        }
    }

	public ITask getOrCreateCorrespondingTask() {
		return null;
	}
	public boolean hasCorrespondingActivatableTask() {
		return false;
	}

	public boolean isDirectlyModifiable() {
		return true;
	}
	
	public boolean isActivatable() {
		return false;
	}
	
	public boolean isDragAndDropEnabled() {
		return false;
	}
	
	public Color getForeground() {
       	return null;
	}

	public Font getFont() {
        for (ITask child : getChildren()) {
			if (child.isActive())
				return BOLD;
		}
		return null;
	}
	public boolean isCompleted(){
		return false;
	}
}
