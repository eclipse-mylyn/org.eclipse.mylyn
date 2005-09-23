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

package org.eclipse.mylar.tasklist.tests;

import junit.framework.TestCase;

import org.eclipse.mylar.tasklist.MylarTasklistPlugin;
import org.eclipse.mylar.tasklist.Task;
import org.eclipse.mylar.tasklist.TaskListManager;
import org.eclipse.mylar.tasklist.ui.actions.TaskActivateAction;
import org.eclipse.mylar.tasklist.ui.views.TaskListView;
import org.eclipse.ui.PartInitException;

/**
 * @author Wes Coelho
 */
public class TaskHistoryTest extends TestCase {

	protected TaskListManager manager = MylarTasklistPlugin.getTaskListManager(); 
	
	protected Task task1 = null;
	protected Task task2 = null;
	protected Task task3 = null;
	protected Task task4 = null;
		
	protected void setUp() throws Exception {
		super.setUp();
		
		task1 = new Task(MylarTasklistPlugin.getTaskListManager().genUniqueTaskId(), "task 1", true);
		task2 = new Task(MylarTasklistPlugin.getTaskListManager().genUniqueTaskId(), "task 2", true);
		task3 = new Task(MylarTasklistPlugin.getTaskListManager().genUniqueTaskId(), "task 3", true);
		task4 = new Task(MylarTasklistPlugin.getTaskListManager().genUniqueTaskId(), "task 4", true);
		
		manager.addRootTask(task1);
		manager.addRootTask(task2);
		manager.addRootTask(task3);
		manager.addRootTask(task4);
		
	}
		
	public void testHistory(){
		
		(new TaskActivateAction(task1)).run();
		(new TaskActivateAction(task2)).run();
		(new TaskActivateAction(task3)).run();
		
		assertTrue(task3.isActive());
		assertFalse(task2.isActive());
		
		try {
			MylarTasklistPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("org.eclipse.mylar.tasks.ui.views.TaskListView");
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("View not initialized");
		}
		
		assertNotNull(TaskListView.getDefault());  
		TaskListView taskView = TaskListView.getDefault();
		
		taskView.getPreviousTaskAction().run();
		assertTrue(task2.isActive());
		
		taskView.getPreviousTaskAction().run();
		assertTrue(task1.isActive());
		
		taskView.getPreviousTaskAction().run();
		assertTrue(task1.isActive());
		
		taskView.getNextTaskAction().run();
		assertTrue(task2.isActive());
		
		taskView.getNextTaskAction().run();
		assertTrue(task3.isActive());
		
		taskView.getNextTaskAction().run();
		assertTrue(task3.isActive());
		
		taskView.getPreviousTaskAction().run();
		assertTrue(task2.isActive());		
		
		taskView.getNextTaskAction().run();
		assertTrue(task3.isActive());
		
		(new TaskActivateAction(task4)).run();
		taskView.addTaskToHistory(task4); //Simulate clicking on it rather than navigating next or previous
		assertTrue(task4.isActive());
		
		taskView.getNextTaskAction().run();
		assertTrue(task4.isActive());		
		
		taskView.getPreviousTaskAction().run();
		assertTrue(task3.isActive());	
		
		taskView.getNextTaskAction().run();
		assertTrue(task4.isActive());
		
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

}
