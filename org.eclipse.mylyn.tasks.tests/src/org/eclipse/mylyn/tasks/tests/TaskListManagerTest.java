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

import java.io.File;

import junit.framework.TestCase;

import org.eclipse.mylar.tasks.MylarTasksPlugin;
import org.eclipse.mylar.tasks.Task;
import org.eclipse.mylar.tasks.TaskListManager;

/**
 * @author Mik Kersten
 */
public class TaskListManagerTest extends TestCase {

    public void testCreationAndExternalization() {
        File file = new File("foo" + MylarTasksPlugin.FILE_EXTENSION);
        file.deleteOnExit();
        TaskListManager manager = new TaskListManager(file);
        
        manager.createNewTaskList();
//        Category category = taskList.createCategory("category");
        Task task1 = new Task(MylarTasksPlugin.getTaskListManager().genUniqueTaskId(), "task 1");
//        category.addTask(task1);
        task1.addSubtask(new Task(MylarTasksPlugin.getTaskListManager().genUniqueTaskId(), "sub task 1"));
        assertEquals(task1.getChildren().size(), 1);

        manager.saveTaskList();
        assertNotNull(manager.getTaskList());
        manager.setTaskList(null);
//        manager.readTaskList();
//        assertNotNull(manager.getTaskList());
//        assertEquals(manager.getTaskList().getCategories().size(), 1);
//        assertEquals(manager.getTaskList().findCategory("category").getTasks().size(), 1);
//        assertEquals(manager.getTaskList().findCategory("category").getTasks().get(0).getLabel(), "task 1");
    }
}
