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
package org.eclipse.mylar.tasklist;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.tasklist.internal.RelativePathUtil;
import org.eclipse.mylar.tasklist.internal.TaskCategory;
import org.eclipse.mylar.tasklist.internal.TaskList;
import org.eclipse.mylar.tasklist.ui.views.TaskListView;


/**
 * @author Mik Kersten
 */
public class TaskListManager {
    
	public static final int INACTIVITY_TIME = 5; // in minutes 

	public static final long INACTIVITY_TIME_MILLIS = INACTIVITY_TIME * 1000 * 60;
	
	private Map<ITask, TaskActiveTimerListener> listenerMap = new HashMap<ITask, TaskActiveTimerListener>(); 
	
    private File taskListFile;
    private TaskList taskList = new TaskList();
    private List<ITaskActivityListener> listeners = new ArrayList<ITaskActivityListener>();
    private int nextTaskId;    
    
    public TaskListManager(File file) {
        this.taskListFile = file;
        if (MylarPlugin.getDefault() != null && MylarPlugin.getDefault().getPreferenceStore().contains(MylarTasklistPlugin.TASK_ID)) { // TODO: fix to MylarTasklistPlugin
        	nextTaskId = MylarPlugin.getDefault().getPreferenceStore().getInt(MylarTasklistPlugin.TASK_ID);
        } else {
        	nextTaskId = 1;
        }
    }    
        
    public TaskList createNewTaskList() {
    	taskList = new TaskList();
        return taskList;
    } 

    public String genUniqueTaskId() {
        return "task-" + nextTaskId++;
    }
    
    public boolean readTaskList() {
    	MylarTasklistPlugin.getDefault().getTaskListExternalizer().initExtensions();
        try { 
        	if (taskListFile.exists()) {
        		MylarTasklistPlugin.getDefault().getTaskListExternalizer().readTaskList(taskList, taskListFile);
        		int maxHandle = taskList.findLargestTaskHandle();
        		if (maxHandle >= nextTaskId) {
        			nextTaskId = maxHandle + 1;
        		}
                for (ITaskActivityListener listener : listeners) listener.tasksActivated(taskList.getActiveTasks());
        	}
        	if (TaskListView.getDefault() != null) {
        		TaskListView.getDefault().getViewer().refresh();
        	}
            return true;
        } catch (Exception e) { 
        	MylarPlugin.log(e, "Could not read task list");
            return false;
        }
    }

    public void saveTaskList() {
        try {   
        	MylarTasklistPlugin.getDefault().getTaskListExternalizer().writeTaskList(taskList, taskListFile);
            MylarPlugin.getDefault().getPreferenceStore().setValue(MylarTasklistPlugin.TASK_ID, nextTaskId);
        } catch (Exception e) {
            MylarPlugin.fail(e, "Could not save task list", true);
        }
    } 
    
    public TaskList getTaskList() {
        return taskList;
    }
    
    public void setTaskList(TaskList taskList) {
        this.taskList = taskList;
    }

    public void addRootTask(ITask task) {
    	taskList.addRootTask(task);
    }
    
    public void addCategory(ICategory cat) {
    	taskList.addCategory(cat);
    }
    
    public void addQuery(IQuery cat) {
    	taskList.addQuery(cat);
    }
    
    public void deleteTask(ITask task) {
        TaskActiveTimerListener activeListener = listenerMap.remove(task);
        if(activeListener != null)
        	activeListener.stopTimer();
        taskList.setActive(task, false, false);        
        taskList.deleteTask(task);
    }
    
    public void deleteCategory(ICategory cat) {
    	taskList.deleteCategory(cat);
    }
    
    public void deleteQuery(IQuery query) {
    	taskList.deleteQuery(query);
    }
    
    public void addListener(ITaskActivityListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ITaskActivityListener listener) {
        listeners.remove(listener);
    }

    public void activateTask(ITask task) {
    	if (!MylarTasklistPlugin.getDefault().isMultipleMode()) {
    		for (ITask t : taskList.getActiveTasks()) {
    			for (ITaskActivityListener listener : listeners) listener.taskDeactivated(t);    			
    		}
    		taskList.clearActiveTasks();
    	}
		taskList.setActive(task, true, false);
		TaskActiveTimerListener activeListener = new TaskActiveTimerListener(task); 
		listenerMap.put(task, activeListener);
		for (ITaskActivityListener listener : listeners) listener.taskActivated(task);
	}

    public void deactivateTask(ITask task) {
    	TaskActiveTimerListener activeListener = listenerMap.remove(task);
    	if(activeListener != null)
        	activeListener.stopTimer();
    	taskList.setActive(task, false, false);
		for (ITaskActivityListener listener : listeners) listener.taskDeactivated(task);
	}
    
    public void taskPropertyChanged(ITask task, String property) {
    	for (ITaskActivityListener listener : listeners) listener.taskPropertyChanged(task, property);
    }
    
    public void updateTaskscapeReference(String prevDir) {    	
    	List<ITask> rootTasks = this.getTaskList().getRootTasks();
    	for (TaskCategory cat : taskList.getTaskCategories()) {
    			updateTaskscapeReferenceHelper(cat.getChildren(), prevDir);
    	}
    	updateTaskscapeReferenceHelper(rootTasks, prevDir);
    	
    }

    public void updateTaskscapeReferenceHelper(List<ITask> list, String prevDir) {
    	for (ITask task : list) {
			if (!task.getPath().startsWith("task-")) {
				if (task.getPath().startsWith("..")) {					
					String path = task.getPath();					
					File d = new File(prevDir);
					while (path.startsWith("..")) {
						d = d.getParentFile();
						path = path.substring(3, path.length());
					}
					
					String absPath = d.getPath() + "/" + path + MylarTasklistPlugin.FILE_EXTENSION;
					absPath = absPath.replaceAll("\\\\", "/");
					String rel = RelativePathUtil.findRelativePath(MylarPlugin.getDefault().getMylarDataDirectory() + "/", absPath);										
					task.setPath(rel);
					taskPropertyChanged(task, "Path");
				} else {
					String absPath = prevDir + "/" + task.getPath() + MylarTasklistPlugin.FILE_EXTENSION;
					absPath = absPath.replaceAll("\\\\", "/");
					String rel = RelativePathUtil.findRelativePath(MylarPlugin.getDefault().getMylarDataDirectory(), absPath);
					task.setPath(rel);
					taskPropertyChanged(task, "Path");
				}
			}
//			updateTaskscapeReferenceHelper(task.getChildren(), prevDir);
    	}
    }
    
    public void setTaskListFile(File f) {
    	if (this.taskListFile.exists()) {
    		this.taskListFile.delete();
    	}
    	this.taskListFile = f;
    }
    
    public ITask getTaskForHandle(String handle, boolean lookInArchives) {
    	return taskList.getTaskForHandle(handle, lookInArchives);
    }

	public String toXmlString() {
		try {   
        	return MylarTasklistPlugin.getDefault().getTaskListExternalizer().getTaskListXml(taskList);
        } catch (Exception e) {
            MylarPlugin.fail(e, "Could not save task list", true);
        }
        return null;
	}
}
