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
 * Created on Dec 22, 2004
  */
package org.eclipse.mylar.tasks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.mylar.tasks.BugzillaTask.BugTaskState;

 
/**
 * @author Mik Kersten
 */
public class TaskList implements Serializable {

    private static final long serialVersionUID = 3618984485791021105L;

    private List<ITask> rootTasks = new ArrayList<ITask>();
    private transient List<ITask> activeTasks = new ArrayList<ITask>();
    
    public void addRootTask(ITask task) {
        rootTasks.add(task); 
    }
    
    public void setActive(ITask task, boolean active) {
        task.setActive(active);
        if (active) {
            activeTasks.add(task);
        } else {
            activeTasks.remove(task);
        }
    }
    
    /**
     * TODO: make data structure handle this traversal
     */
    public ITask getTaskForId(String id) {
        return setActiveHelper(rootTasks, id);
    } 
    
    private ITask setActiveHelper(List<ITask> tasks, String id) {
        for (ITask task : tasks) {
            if (task.getHandle() == id) {
                return task;
            } else {
                ITask child = setActiveHelper(task.getChildren(), id);
                if (child != null) return child;
            }
        }
        return null;
    }
    
    public List<ITask> getActiveTasks() {
        return activeTasks;
    }
 
    public List<ITask> getTaskFor(Category category) {
        List<ITask> categoryTasks = new ArrayList<ITask>();
        for (ITask task : rootTasks) {
            if (task.getCategories().contains(category)) categoryTasks.add(task);
        } 
        return categoryTasks;
    }
    
    public List<ITask> getRootTasks() {
        return rootTasks;
    }
    
    public Set<Category> getCategories() {
        Set<Category> categories = new HashSet<Category>();
        for (ITask task : rootTasks) {
            categories.addAll(task.getCategories());
        }  
        return categories;        
    }
    
    public void refreshRestoredTasks() {
    	activeTasks = new ArrayList<ITask>();
    	activateRestoredTasks(rootTasks);
    	restoreParents(rootTasks, null);
    	refreshBugReports(rootTasks);
    }
    private void activateRestoredTasks(List<ITask> tasks) {
    	for (ITask task : tasks) {
    		if (task.isActive()) {
    			setActive(task, true);
    		}
    		activateRestoredTasks(task.getChildren());
    	}
    }
    private void restoreParents(List<ITask> tasks, ITask parent) {
    	for (ITask task : tasks) {
    		task.setParent(parent);    	
    		restoreParents(task.getChildren(), task);
    	}
    }
    private void refreshBugReports(List<ITask> tasks) {
    	for (ITask task : tasks) {
    		if (task instanceof BugzillaTask) {
    				((BugzillaTask)task).readBugReport();
    				((BugzillaTask)task).setState(BugTaskState.FREE);
    		}
    		refreshBugReports(task.getChildren());
    	}
    }
    public List<ITask> getTasksInProgress() {
		List<ITask> inprogress = new ArrayList<ITask>();
		for (ITask task : rootTasks) {
            if (!task.isCompleted()) {
            	inprogress.add(task);
            }
        }
		return inprogress;
	}
    public List<ITask> getCompletedTasks() {
    	List <ITask> complete = new ArrayList<ITask>();
    	for (ITask task : rootTasks) {
    		if (task.isCompleted()) {
    			complete.add(task);
    		}
    	}
    	return complete;
    }
}
