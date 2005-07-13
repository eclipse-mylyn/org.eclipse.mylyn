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
 * Created on Dec 21, 2004
  */
package org.eclipse.mylar.tasks.tests;

import java.util.List;
import java.io.File;

import junit.framework.TestCase;

import org.eclipse.mylar.bugzilla.ui.tasks.BugzillaTask;
import org.eclipse.mylar.tasks.TaskCategory;
import org.eclipse.mylar.tasks.ITask;
import org.eclipse.mylar.tasks.MylarTasksPlugin;
import org.eclipse.mylar.tasks.Task;
import org.eclipse.mylar.tasks.TaskList;
import org.eclipse.mylar.tasks.TaskListManager;

/**
 * @author Mik Kersten
 */
public class TaskListManagerTest extends TestCase {

    public void testCreationAndExternalization() {
        File file = new File("foo" + MylarTasksPlugin.FILE_EXTENSION);
        file.deleteOnExit();
        TaskListManager manager = new TaskListManager(file);
        
        TaskList tlist = manager.getTaskList();
        Task task1 = new Task(MylarTasksPlugin.getTaskListManager().genUniqueTaskId(), "task 1");
        tlist.addRootTask(task1);
        Task sub1 = new Task(MylarTasksPlugin.getTaskListManager().genUniqueTaskId(), "sub 1");
        task1.addSubTask(sub1);    
        sub1.setParent(task1);
        Task task2 = new Task(MylarTasksPlugin.getTaskListManager().genUniqueTaskId(), "task 2");
        tlist.addRootTask(task2);

        TaskCategory cat1 = new TaskCategory("Category 1");
        tlist.addCategory(cat1);
        Task task3 = new Task(MylarTasksPlugin.getTaskListManager().genUniqueTaskId(), "task 3");
        cat1.addTask(task3);
        Task sub2 = new Task(MylarTasksPlugin.getTaskListManager().genUniqueTaskId(), "sub 2");
        task3.addSubTask(sub2);
        sub2.setParent(task3);
        Task task4 = new Task(MylarTasksPlugin.getTaskListManager().genUniqueTaskId(), "task 4");
        cat1.addTask(task4);
        
        TaskCategory cat2 = new TaskCategory("Category 2");
        tlist.addCategory(cat2);
        Task task5 = new Task(MylarTasksPlugin.getTaskListManager().genUniqueTaskId(), "task 5");
        cat2.addTask(task5);
        Task task6 = new Task(MylarTasksPlugin.getTaskListManager().genUniqueTaskId(), "task 6");
        cat2.addTask(task6);    
        
        BugzillaTask report = new BugzillaTask("123", "label 123", true);
        cat2.addTask(report);

        BugzillaTask report2 = new BugzillaTask("124", "label 124", true);
        tlist.addRootTask(report2);
        
        assertEquals(manager.getTaskList().getRoots().size(), 5);

        manager.saveTaskList();
        assertNotNull(manager.getTaskList());
        TaskList list = new TaskList();
        manager.setTaskList(list);
        manager.readTaskList();
        assertNotNull(manager.getTaskList());
        assertEquals(3, manager.getTaskList().getRootTasks().size());
        assertEquals(2, manager.getTaskList().getCategories().size());

    	List<ITask> readList = manager.getTaskList().getRootTasks();
    	assertTrue(readList.get(0).getLabel().equals("task 1"));
    	assertTrue(readList.get(0).getChildren().get(0).getLabel().equals("sub 1"));
    	assertTrue(readList.get(1).getLabel().equals("task 2"));
    	assertTrue(readList.get(2) instanceof BugzillaTask);
    	
    	List<TaskCategory> readCats = manager.getTaskList().getTaskCategories();
    	readList = readCats.get(0).getChildren();
    	assertTrue(readList.get(0).getLabel().equals("task 3"));
    	assertTrue(readList.get(0).getChildren().get(0).getLabel().equals("sub 2"));
    	assertTrue(readList.get(1).getLabel().equals("task 4"));
    	readList = readCats.get(1).getChildren();
    	assertTrue(readList.get(0).getLabel().equals("task 5"));
    	assertTrue(readList.get(1).getLabel().equals("task 6"));
    	assertTrue(readList.get(2) instanceof BugzillaTask);
    }

}
