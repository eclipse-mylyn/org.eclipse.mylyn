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
package org.eclipse.mylar.tasklist.internal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylar.tasklist.ICategory;
import org.eclipse.mylar.tasklist.IQuery;
import org.eclipse.mylar.tasklist.IQueryHit;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.ITaskListElement;
import org.eclipse.mylar.tasklist.MylarTasklistPlugin;

 
/**
 * @author Mik Kersten
 */
public class TaskList implements Serializable {

    private static final long serialVersionUID = 3618984485791021105L;

    private List<ITask> rootTasks = new ArrayList<ITask>();
    private List<ICategory> categories = new ArrayList<ICategory>();
    private List<IQuery> queries = new ArrayList<IQuery>();
    private transient List<ITask> activeTasks = new ArrayList<ITask>();
    
    public void addRootTask(ITask task) {
    	rootTasks.add(task);
    	if (MylarTasklistPlugin.getDefault() != null) {
			MylarTasklistPlugin.getDefault().saveTaskListAndContexts();
		}
    }
    
    void internalAddRootTask(ITask task) {
    	rootTasks.add(task);
    }
    
    public void addCategory(ICategory cat) {
    	categories.add(cat);
    	if (MylarTasklistPlugin.getDefault() != null) {
			MylarTasklistPlugin.getDefault().saveTaskListAndContexts();
		}
    }
    
    public void addQuery(IQuery query) {
    	queries.add(query);
    	if (MylarTasklistPlugin.getDefault() != null) {
			MylarTasklistPlugin.getDefault().saveTaskListAndContexts();
		}
    }
    
    /**
     * XXX Only public so that other externalizers can use it
     */
    public void internalAddCategory(ICategory cat) {
    	categories.add(cat);
    }
    
    /**
     * XXX Only public so that other externalizers can use it
     */
    public void internalAddQuery(IQuery query) {
    	queries.add(query);
    }
    
    public void setActive(ITask task, boolean active, boolean isStalled) {
        task.setActive(active, isStalled);
        if (active && !activeTasks.contains(task)) {
            activeTasks.add(task);
        } else if(!active){
            activeTasks.remove(task);
        }

    }
        
    public void deleteTask(ITask task) {
    	boolean deleted = deleteTaskHelper(rootTasks, task);
    	if (!deleted) {
			for (TaskCategory cat : getTaskCategories()) {
				deleted = deleteTaskHelper(cat.getChildren(), task);
				if (deleted) {
					return;
				}
			}
    	}
    	if (MylarTasklistPlugin.getDefault() != null) {
			MylarTasklistPlugin.getDefault().saveTaskListAndContexts();
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
    
    public void deleteCategory(ICategory category) {
    	categories.remove(category);
    	if (MylarTasklistPlugin.getDefault() != null) {
			MylarTasklistPlugin.getDefault().saveTaskListAndContexts();
		}
    }
    
    public void deleteQuery(IQuery query) {
    	queries.remove(query);
    	if (MylarTasklistPlugin.getDefault() != null) {
			MylarTasklistPlugin.getDefault().saveTaskListAndContexts();
		}
    }
    
    public ITask getTaskForHandle(String handle) {
    	ITask t = null;
    	for (ICategory cat : categories) {
			if ((t = findTaskHelper(cat.getChildren(), handle)) != null) {
				return t;
			}
		}
    	for (IQuery query : queries) {
			if ((t = findTaskHelper(query.getChildren(), handle)) != null) {
				return t;
			}
		}
        return findTaskHelper(rootTasks, handle);
    } 
    
    private ITask findTaskHelper(List<? extends ITaskListElement> elements, String handle) {
        for (ITaskListElement element : elements) {
        	if(element instanceof ITask){
        		if(element.getHandle() == handle)
        			return (ITask)element;
        	} else if(element instanceof IQueryHit){
        		IQueryHit hit = (IQueryHit)element;
	            if (hit.getHandle() == handle && hit.hasCorrespondingActivatableTask()) {
	                return hit.getOrCreateCorrespondingTask();
	            }
        	}
        	
        	// for subtasks
            if(element instanceof ITask){
            	ITask searchTask = (ITask)element; 
	            ITask t = findTaskHelper(searchTask.getChildren(), handle);
	            if (t != null) {
	            	return t;
	            }
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
    
    public List<ICategory> getCategories() {
    	return categories;
    }
    
    public List<IQuery> getQueries(){
    	return queries;
    }
       
    public int findLargestTaskHandle() {
    	int max = 0;
    	max = Math.max(largestTaskHandleHelper(rootTasks), max);
    	for (TaskCategory cat : getTaskCategories()) {
        	max = Math.max(largestTaskHandleHelper(cat.getChildren()), max);
    	}
    	return max;
    }
    
    private int largestTaskHandleHelper(List<ITask> tasks) {
    	int ihandle = 0;
    	int max = 0;
    	for (ITask t : tasks) {
    		if(t.participatesInTaskHandles()) {
    			String string = t.getHandle().substring(t.getHandle().indexOf('-')+1, t.getHandle().length());
    			if (string != "") {
    				ihandle = Integer.parseInt(string);
    			}
    		}
    		max = Math.max(ihandle, max);
    		ihandle = largestTaskHandleHelper(t.getChildren());
    		max = Math.max(ihandle, max);
    	}
    	return max;
    }
    
    public List<Object> getRoots() {
    	List<Object> roots = new ArrayList<Object>();
    	for (ITask t : rootTasks) roots.add(t);   			
    	for (ICategory cat : categories) roots.add(cat);
    	for (IQuery query : queries) roots.add(query);
    	return roots;
    }
    
    public List<TaskCategory> getTaskCategories() {
    	List<TaskCategory> cats = new ArrayList<TaskCategory>();
    	for (ICategory cat : categories) {
    		if (cat instanceof TaskCategory) {
    			cats.add((TaskCategory)cat);
    		}
    	}
    	return cats;
    }

	public void clear() {
		activeTasks.clear();
		categories.clear();
		rootTasks.clear();
	}	
	public void clearActiveTasks() {
		for (ITask task : activeTasks) {
			task.setActive(false, false);
		}
		activeTasks.clear();
	}
}
