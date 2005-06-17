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
import org.eclipse.mylar.tasks.util.XmlUtil;


/**
 * @author Mik Kersten
 */
public class TaskListManager {
    
    private File file;
    private TaskList taskList = new TaskList();
    private List<ITaskActivityListener> listeners = new ArrayList<ITaskActivityListener>();
    private int nextTaskId;
    
    public TaskListManager(File file) {
        this.file = file;
        if (MylarPlugin.getDefault().getPreferenceStore().contains(MylarTasksPlugin.TASK_ID)) { // TODO: fix to MylarTasksPlugin
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
        	if (file.exists()) {
        		XmlUtil.readTaskList(taskList, file);
                for (ITaskActivityListener listener : listeners) listener.tasksActivated(taskList.getActiveTasks());
        	}
            return true;
        } catch (Exception e) { 
        	MylarPlugin.log(this.getClass().toString(), e);
            return false;
        }
    }

    public void saveTaskList() {
        try {   
            XmlUtil.writeTaskList(taskList, file);
            MylarPlugin.getDefault().getPreferenceStore().setValue(MylarTasksPlugin.TASK_ID, nextTaskId);
        } catch (Exception e) {
            e.printStackTrace(); // TODO: fix
//            MylarPlugin.fail(e, "Could not save task list", true);
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
        if (taskList.getRootTasks().contains(task)) {
            taskList.getRootTasks().remove(task);
        } else {
            task.getParent().getChildren().remove(task);
        }
    }
    
    public void addListener(ITaskActivityListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ITaskActivityListener listener) {
        listeners.remove(listener);
    }

    public void activateTask(ITask task) {
        if (task.isCategory()) {
            for (ITask childTask : task.getChildren()) {
                taskList.setActive(childTask, true);
                for (ITaskActivityListener listener : listeners) listener.taskActivated(childTask);
            }
        } else {
            taskList.setActive(task, true);
            for (ITaskActivityListener listener : listeners) listener.taskActivated(task);
        }
    }

    public void deactivateTask(ITask task) { 
        if (task.isCategory()) {
            for (ITask childTask : task.getChildren()) {
                taskList.setActive(childTask, false);
                for (ITaskActivityListener listener : listeners) listener.taskDeactivated(childTask);
            }
        } else {
            taskList.setActive(task, false);
            for (ITaskActivityListener listener : listeners) listener.taskDeactivated(task);
        }
    }
    
    public void updateTaskscapeReference(String prevDir) {    	
    	List<ITask> rootTasks = this.getTaskList().getRootTasks();
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
				} else {
					String absPath = prevDir + "/" + task.getPath() + MylarTasksPlugin.FILE_EXTENSION;
					absPath = absPath.replaceAll("\\\\", "/");
					String rel = RelativePathUtil.findRelativePath(MylarPlugin.getDefault().getUserDataDirectory(), absPath);
					task.setPath(rel);
				}
			}
			updateTaskscapeReferenceHelper(task.getChildren(), prevDir);
    	}
    }
    public void setFile(File f) {
    	if (this.file.exists()) {
    		this.file.delete();
    	}
    	this.file = f;
    }
}
