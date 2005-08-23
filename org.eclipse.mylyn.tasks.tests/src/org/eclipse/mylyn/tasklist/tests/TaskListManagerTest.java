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
package org.eclipse.mylar.tasklist.tests;

import java.io.File;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.mylar.bugzilla.ui.tasklist.BugzillaTask;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.MylarTasklistPlugin;
import org.eclipse.mylar.tasklist.Task;
import org.eclipse.mylar.tasklist.TaskListManager;
import org.eclipse.mylar.tasklist.internal.TaskCategory;
import org.eclipse.mylar.tasklist.internal.TaskList;

/**
 * @author Mik Kersten
 */
public class TaskListManagerTest extends TestCase {

    public void testCreationAndExternalization() {
        File file = new File("foo" + MylarTasklistPlugin.FILE_EXTENSION);
        file.deleteOnExit();
        TaskListManager manager = new TaskListManager(file);
        
//        TaskList tlist = manager.getTaskList();
        Task task1 = new Task(MylarTasklistPlugin.getTaskListManager().genUniqueTaskId(), "task 1", true);
        manager.addRootTask(task1);
        Task sub1 = new Task(MylarTasklistPlugin.getTaskListManager().genUniqueTaskId(), "sub 1", true);
        task1.addSubTask(sub1);    
        sub1.setParent(task1);
        Task task2 = new Task(MylarTasklistPlugin.getTaskListManager().genUniqueTaskId(), "task 2", true);
        manager.addRootTask(task2);

        TaskCategory cat1 = new TaskCategory("Category 1");
        manager.addCategory(cat1);
        Task task3 = new Task(MylarTasklistPlugin.getTaskListManager().genUniqueTaskId(), "task 3", true);
        cat1.addTask(task3);
        Task sub2 = new Task(MylarTasklistPlugin.getTaskListManager().genUniqueTaskId(), "sub 2", true);
        task3.addSubTask(sub2);
        sub2.setParent(task3);
        Task task4 = new Task(MylarTasklistPlugin.getTaskListManager().genUniqueTaskId(), "task 4", true);
        cat1.addTask(task4);
        
        TaskCategory cat2 = new TaskCategory("Category 2");
        manager.addCategory(cat2);
        Task task5 = new Task(MylarTasklistPlugin.getTaskListManager().genUniqueTaskId(), "task 5", true);
        cat2.addTask(task5);
        Task task6 = new Task(MylarTasklistPlugin.getTaskListManager().genUniqueTaskId(), "task 6", true);
        cat2.addTask(task6);    
        
        BugzillaTask report = new BugzillaTask("123", "label 123", true);
        cat2.addTask(report);

        BugzillaTask report2 = new BugzillaTask("124", "label 124", true);
        manager.addRootTask(report2);
        
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
