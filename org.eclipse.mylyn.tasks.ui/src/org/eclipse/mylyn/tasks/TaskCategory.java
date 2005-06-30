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
package org.eclipse.mylar.tasks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylar.ui.MylarImages;
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
		return MylarImages.getImage(MylarImages.CATEGORY);
	}
    
    public void addTask(ITask task) {
    	if(task instanceof BugzillaTask){
    		BugzillaTask bugTask = MylarTasksPlugin.getTaskListManager().getTaskList().getFromBugzillaTaskRegistry(task.getHandle());
    		if(bugTask == null){
    			MylarTasksPlugin.getTaskListManager().getTaskList().addToBugzillaTaskRegistry((BugzillaTask)task);
    		} else {
    			task = bugTask;
    		}
    	} 
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
}
