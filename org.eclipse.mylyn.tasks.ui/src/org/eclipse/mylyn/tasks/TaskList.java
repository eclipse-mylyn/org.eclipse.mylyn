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
import java.util.List;

 
/**
 * @author Mik Kersten
 */
public class TaskList implements Serializable {

    private static final long serialVersionUID = 3618984485791021105L;

    private List<ITask> rootTasks = new ArrayList<ITask>();
    private List<Category> categories = new ArrayList<Category>();
    private transient List<ITask> activeTasks = new ArrayList<ITask>();
    
    public void addRootTask(ITask task) {
        rootTasks.add(task); 
    }
    
    public void addCategory(Category cat) {
    	categories.add(cat);
    }
    
    public void setActive(ITask task, boolean active) {
        task.setActive(active);
        if (active) {
            activeTasks.add(task);
        } else {
            activeTasks.remove(task);
        }
    }
    
    public void deleteTask(ITask task) {
    	boolean deleted = deleteTaskHelper(rootTasks, task);
    	if (!deleted) {
    		for (Category cat : categories) {
    			deleted = deleteTaskHelper(cat.getTasks(), task);
    			if (deleted) {
    				return;
    			}
    		}
    	}
		
	}
    
    private boolean deleteTaskHelper(List<ITask> tasks, ITask t) {
    	for (ITask task : tasks) {
    		if (task.getHandle().equals(t.getHandle())) {
    			tasks.remove(task);
    			return true;
    		} else {
   				if (deleteTaskHelper(task.getChildren(), t))
   					return true;
    		}
		}
    	return false;
	}
    
    public void deleteCategory(Category category) {
    	if (category !=  null) {
    		category.getTasks().clear();
    	}
    	categories.remove(category);
    }
    /**
     * TODO: make data structure handle this traversal
     */
    public ITask getTaskForId(String id) {
    	ITask t = null;
    	for (Category cat : categories) {
    		if ( (t = findTaskHelper(cat.getTasks(), id)) != null) {
    			return t;
    		}
    	}
        return findTaskHelper(rootTasks, id);
    } 
    
    private ITask findTaskHelper(List<ITask> tasks, String id) {
        for (ITask task : tasks) {
            if (task.getHandle() == id) {
                return task;
            }
            ITask t = findTaskHelper(task.getChildren(), id);
            if (t != null) {
            	return t;
            }
        }
        return null;
    }
    
    public List<ITask> getActiveTasks() {
        return activeTasks;
    }
    
    public List<ITask> getRootTasks() {
        return rootTasks;
    }
    
    public List<Category> getCategories() {
    	return categories;
    }
       
    public int findLargestTaskHandle() {
    	int max = 0;
    	max = Math.max(largestTaskHandleHelper(rootTasks), max);
    	for (Category cat : categories) {
    		max = Math.max(largestTaskHandleHelper(cat.getTasks()), max);
    	}
    	return max;
    }
    
    private int largestTaskHandleHelper(List<ITask> tasks) {
    	int ihandle = 0;
    	int max = 0;
    	for (ITask t : tasks) {
    		if (t instanceof BugzillaTask) {
    			ihandle = 0;
    		} else {
    			ihandle = Integer.parseInt(t.getHandle().substring(t.getHandle().indexOf('-')+1, t.getHandle().length()));
    		}
    		max = Math.max(ihandle, max);
    		ihandle = largestTaskHandleHelper(t.getChildren());
    		max = Math.max(ihandle, max);
    	}
    	return max;
    }
    
    public List<Object> getRoots() {
    	List<Object> roots = new ArrayList<Object>();
    	for (ITask t : rootTasks) {
    		roots.add(t);
    	}
    	for (Category cat : categories) {
    		roots.add(cat);
    	}
    	return roots;
    }
    
    public void createCategory(String description) {
    	Category c = new Category(description);
    	categories.add(c);
    }
}
