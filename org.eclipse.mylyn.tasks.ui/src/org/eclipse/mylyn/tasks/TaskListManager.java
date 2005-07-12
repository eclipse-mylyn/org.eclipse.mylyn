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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.tasks.util.RelativePathUtil;


/**
 * @author Mik Kersten
 */
public class TaskListManager {
    
    private File taskListFile;
    private TaskList taskList = new TaskList();
    private List<ITaskActivityListener> listeners = new ArrayList<ITaskActivityListener>();
    private int nextTaskId;
    
    public TaskListManager(File file) {
        this.taskListFile = file;
        if (MylarPlugin.getDefault() != null && MylarPlugin.getDefault().getPreferenceStore().contains(MylarTasksPlugin.TASK_ID)) { // TODO: fix to MylarTasksPlugin
        	nextTaskId = MylarPlugin.getDefault().getPreferenceStore().getInt(MylarTasksPlugin.TASK_ID);
        } else {
        	nextTaskId = 1;
        }
    }
    
    public TaskList createNewTaskList() {
        return taskList;
    } 

    public String genUniqueTaskId() {
        return "task-" + nextTaskId++;
    }
    
    public boolean readTaskList() {
        try { 
        	if (taskListFile.exists()) {
        		MylarTasksPlugin.getDefault().getTaskListExternalizer().readTaskList(taskList, taskListFile);
        		int maxHandle = taskList.findLargestTaskHandle();
        		if (maxHandle >= nextTaskId) {
        			nextTaskId = maxHandle + 1;
        		}
                for (ITaskActivityListener listener : listeners) listener.tasksActivated(taskList.getActiveTasks());
        	}
            return true;
        } catch (Exception e) { 
        	MylarPlugin.log(e, "Could not read task list");
            return false;
        }
    }

    public void saveTaskList() {
        try {   
        	MylarTasksPlugin.getDefault().getTaskListExternalizer().writeTaskList(taskList, taskListFile);
            MylarPlugin.getDefault().getPreferenceStore().setValue(MylarTasksPlugin.TASK_ID, nextTaskId);
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

    public void deleteTask(ITask task) {
        taskList.setActive(task, false);        
        taskList.deleteTask(task);
    }
    
    public void deleteCategory(AbstractCategory cat) {
    	taskList.deleteCategory(cat);
    }
    
    public void addListener(ITaskActivityListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ITaskActivityListener listener) {
        listeners.remove(listener);
    }

    public void activateTask(ITask task) {
		taskList.setActive(task, true);
		for (ITaskActivityListener listener : listeners) listener.taskActivated(task);
	}

    public void deactivateTask(ITask task) {
		taskList.setActive(task, false);
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
					
					String absPath = d.getPath() + "/" + path + MylarTasksPlugin.FILE_EXTENSION;
					absPath = absPath.replaceAll("\\\\", "/");
					String rel = RelativePathUtil.findRelativePath(MylarPlugin.getDefault().getUserDataDirectory() + "/", absPath);										
					task.setPath(rel);
					taskPropertyChanged(task, "Path");
				} else {
					String absPath = prevDir + "/" + task.getPath() + MylarTasksPlugin.FILE_EXTENSION;
					absPath = absPath.replaceAll("\\\\", "/");
					String rel = RelativePathUtil.findRelativePath(MylarPlugin.getDefault().getUserDataDirectory(), absPath);
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

	public File getTaskListFile() {
		return taskListFile;
	}
}
