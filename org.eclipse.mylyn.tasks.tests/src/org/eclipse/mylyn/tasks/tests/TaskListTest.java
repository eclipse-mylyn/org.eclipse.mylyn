/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.core.ITaskList;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.core.TaskList;

/**
 * @author Mik Kersten
 * @author Rob Elves
 * @author Shawn Minto
 */
public class TaskListTest extends TestCase {

	public void testGetCategories() {
		TaskList taskList = new TaskList();
		taskList.addCategory(new TaskCategory("a"));
		assertEquals(2, taskList.getCategories().size());
	}

	public void testLocalSubTaskAdd() {
		ITaskList taskList = new TaskList();
		LocalTask task = new LocalTask("1", "summary");
		LocalTask subTask = new LocalTask("2", "subTask");

		taskList.addTask(task);
		taskList.addTask(subTask, task);

		assertEquals(1, task.getChildren().size());
		assertEquals(subTask, task.getChildren().iterator().next());
	}

	public void testLocalSubTaskAddCycle() {
		TaskList taskList = new TaskList();
		LocalTask task = new LocalTask("1", "summary");
		LocalTask subTask = new LocalTask("2", "subTask");

		taskList.addTask(task);
		taskList.addTask(subTask, task);
		taskList.addTask(task, subTask);

		assertEquals(2, taskList.getAllTasks().size());
		assertEquals(1, taskList.getCategories().size());
		assertEquals(1, taskList.getCategories().iterator().next().getChildren().size());
		assertEquals(1, task.getChildren().size());
		assertEquals(0, subTask.getChildren().size());
		assertEquals(subTask, task.getChildren().iterator().next());
	}

	public void testLocalSubTaskAddDeepCycle() {
		TaskList taskList = new TaskList();
		LocalTask task = new LocalTask("1", "summary");
		LocalTask subTask1 = new LocalTask("2", "subTask");
		LocalTask subTask2 = new LocalTask("3", "subTask");
		LocalTask subTask3 = new LocalTask("4", "subTask");
		LocalTask subTask4 = new LocalTask("5", "subTask");
		LocalTask subTask5 = new LocalTask("6", "subTask");
		LocalTask subTask6 = new LocalTask("7", "subTask");
		LocalTask subTask7 = new LocalTask("8", "subTask");
		LocalTask subTask8 = new LocalTask("9", "subTask");
		LocalTask subTask9 = new LocalTask("10", "subTask");
		LocalTask subTask10 = new LocalTask("11", "subTask");

		taskList.addTask(task);
		taskList.addTask(subTask1, task);
		taskList.addTask(subTask2, subTask1);
		taskList.addTask(subTask3, subTask2);
		taskList.addTask(subTask4, subTask3);
		taskList.addTask(subTask5, subTask4);
		taskList.addTask(subTask6, subTask5);
		taskList.addTask(subTask7, subTask6);
		taskList.addTask(subTask8, subTask7);
		taskList.addTask(subTask9, subTask8);
		taskList.addTask(subTask10, subTask9);

		taskList.addTask(task, subTask10);

		assertEquals(11, taskList.getAllTasks().size());
		assertEquals(1, taskList.getCategories().size());
		assertEquals(1, taskList.getCategories().iterator().next().getChildren().size());
		assertEquals(1, task.getChildren().size());
		assertEquals(subTask1, task.getChildren().iterator().next());

		assertEquals(1, subTask1.getChildren().size());
		assertEquals(subTask2, subTask1.getChildren().iterator().next());

		assertEquals(1, subTask2.getChildren().size());
		assertEquals(subTask3, subTask2.getChildren().iterator().next());

		assertEquals(1, subTask3.getChildren().size());
		assertEquals(subTask4, subTask3.getChildren().iterator().next());

		assertEquals(1, subTask4.getChildren().size());
		assertEquals(subTask5, subTask4.getChildren().iterator().next());

		assertEquals(1, subTask5.getChildren().size());
		assertEquals(subTask6, subTask5.getChildren().iterator().next());

		assertEquals(1, subTask6.getChildren().size());
		assertEquals(subTask7, subTask6.getChildren().iterator().next());

		assertEquals(1, subTask7.getChildren().size());
		assertEquals(subTask8, subTask7.getChildren().iterator().next());

		assertEquals(1, subTask8.getChildren().size());
		assertEquals(subTask9, subTask8.getChildren().iterator().next());

		assertEquals(1, subTask9.getChildren().size());
		assertEquals(subTask10, subTask9.getChildren().iterator().next());

		assertEquals(0, subTask10.getChildren().size());
	}

	public void testLocalSubTaskAddMaxSubTaskDepthDeepCycle() {
		TaskList taskList = new TaskList();
		LocalTask task = new LocalTask("1", "summary");
		LocalTask subTask1 = new LocalTask("2", "subTask");
		LocalTask subTask2 = new LocalTask("3", "subTask");
		LocalTask subTask3 = new LocalTask("4", "subTask");
		LocalTask subTask4 = new LocalTask("5", "subTask");
		LocalTask subTask5 = new LocalTask("6", "subTask");
		LocalTask subTask6 = new LocalTask("7", "subTask");
		LocalTask subTask7 = new LocalTask("8", "subTask");
		LocalTask subTask8 = new LocalTask("9", "subTask");
		LocalTask subTask9 = new LocalTask("10", "subTask");
		LocalTask subTask10 = new LocalTask("11", "subTask");
		LocalTask subTask11 = new LocalTask("12", "subTask");

		taskList.addTask(task);
		taskList.addTask(subTask1, task);
		taskList.addTask(subTask2, subTask1);
		taskList.addTask(subTask3, subTask2);
		taskList.addTask(subTask4, subTask3);
		taskList.addTask(subTask5, subTask4);
		taskList.addTask(subTask6, subTask5);
		taskList.addTask(subTask7, subTask6);
		taskList.addTask(subTask8, subTask7);
		taskList.addTask(subTask9, subTask8);
		taskList.addTask(subTask10, subTask9);
		taskList.addTask(subTask11, subTask10);

		taskList.addTask(task, subTask11);

		assertEquals(12, taskList.getAllTasks().size());
		assertEquals(1, taskList.getCategories().size());
		assertEquals(1, taskList.getCategories().iterator().next().getChildren().size());
		assertEquals(1, task.getChildren().size());
		assertEquals(subTask1, task.getChildren().iterator().next());

		assertEquals(1, subTask1.getChildren().size());
		assertEquals(subTask2, subTask1.getChildren().iterator().next());

		assertEquals(1, subTask2.getChildren().size());
		assertEquals(subTask3, subTask2.getChildren().iterator().next());

		assertEquals(1, subTask3.getChildren().size());
		assertEquals(subTask4, subTask3.getChildren().iterator().next());

		assertEquals(1, subTask4.getChildren().size());
		assertEquals(subTask5, subTask4.getChildren().iterator().next());

		assertEquals(1, subTask5.getChildren().size());
		assertEquals(subTask6, subTask5.getChildren().iterator().next());

		assertEquals(1, subTask6.getChildren().size());
		assertEquals(subTask7, subTask6.getChildren().iterator().next());

		assertEquals(1, subTask7.getChildren().size());
		assertEquals(subTask8, subTask7.getChildren().iterator().next());

		assertEquals(1, subTask8.getChildren().size());
		assertEquals(subTask9, subTask8.getChildren().iterator().next());

		assertEquals(1, subTask9.getChildren().size());
		assertEquals(subTask10, subTask9.getChildren().iterator().next());

		assertEquals(1, subTask10.getChildren().size());
		assertEquals(subTask11, subTask10.getChildren().iterator().next());

		assertEquals(0, subTask11.getChildren().size());
	}

	public void testLocalSubTaskAddReallyDeepCycle() {
		TaskList taskList = new TaskList();
		LocalTask task = new LocalTask("1", "summary");
		LocalTask subTask1 = new LocalTask("2", "subTask");
		LocalTask subTask2 = new LocalTask("3", "subTask");
		LocalTask subTask3 = new LocalTask("4", "subTask");
		LocalTask subTask4 = new LocalTask("5", "subTask");
		LocalTask subTask5 = new LocalTask("6", "subTask");
		LocalTask subTask6 = new LocalTask("7", "subTask");
		LocalTask subTask7 = new LocalTask("8", "subTask");
		LocalTask subTask8 = new LocalTask("9", "subTask");
		LocalTask subTask9 = new LocalTask("10", "subTask");
		LocalTask subTask10 = new LocalTask("11", "subTask");
		LocalTask subTask11 = new LocalTask("12", "subTask");
		LocalTask subTask12 = new LocalTask("13", "subTask");
		LocalTask subTask13 = new LocalTask("14", "subTask");
		LocalTask subTask14 = new LocalTask("15", "subTask");
		LocalTask subTask15 = new LocalTask("16", "subTask");
		LocalTask subTask16 = new LocalTask("17", "subTask");
		LocalTask subTask17 = new LocalTask("18", "subTask");
		LocalTask subTask18 = new LocalTask("19", "subTask");

		taskList.addTask(task);
		taskList.addTask(subTask1, task);
		taskList.addTask(subTask2, subTask1);
		taskList.addTask(subTask3, subTask2);
		taskList.addTask(subTask4, subTask3);
		taskList.addTask(subTask5, subTask4);
		taskList.addTask(subTask6, subTask5);
		taskList.addTask(subTask7, subTask6);
		taskList.addTask(subTask8, subTask7);
		taskList.addTask(subTask9, subTask8);
		taskList.addTask(subTask10, subTask9);
		taskList.addTask(subTask11, subTask10);
		taskList.addTask(subTask12, subTask11);
		taskList.addTask(subTask13, subTask12);
		taskList.addTask(subTask14, subTask13);
		taskList.addTask(subTask15, subTask14);
		taskList.addTask(subTask16, subTask15);
		taskList.addTask(subTask17, subTask16);
		taskList.addTask(subTask18, subTask17);

		taskList.addTask(task, subTask18);

		assertEquals(19, taskList.getAllTasks().size());
		assertEquals(1, taskList.getCategories().size());
		assertEquals(1, taskList.getCategories().iterator().next().getChildren().size());
		assertEquals(1, task.getChildren().size());
		assertEquals(subTask1, task.getChildren().iterator().next());

		assertEquals(1, subTask1.getChildren().size());
		assertEquals(subTask2, subTask1.getChildren().iterator().next());

		assertEquals(1, subTask2.getChildren().size());
		assertEquals(subTask3, subTask2.getChildren().iterator().next());

		assertEquals(1, subTask3.getChildren().size());
		assertEquals(subTask4, subTask3.getChildren().iterator().next());

		assertEquals(1, subTask4.getChildren().size());
		assertEquals(subTask5, subTask4.getChildren().iterator().next());

		assertEquals(1, subTask5.getChildren().size());
		assertEquals(subTask6, subTask5.getChildren().iterator().next());

		assertEquals(1, subTask6.getChildren().size());
		assertEquals(subTask7, subTask6.getChildren().iterator().next());

		assertEquals(1, subTask7.getChildren().size());
		assertEquals(subTask8, subTask7.getChildren().iterator().next());

		assertEquals(1, subTask8.getChildren().size());
		assertEquals(subTask9, subTask8.getChildren().iterator().next());

		assertEquals(1, subTask9.getChildren().size());
		assertEquals(subTask10, subTask9.getChildren().iterator().next());

		assertEquals(1, subTask10.getChildren().size());
		assertEquals(subTask11, subTask10.getChildren().iterator().next());

		assertEquals(1, subTask11.getChildren().size());
		assertEquals(subTask12, subTask11.getChildren().iterator().next());

		assertEquals(1, subTask11.getChildren().size());
		assertEquals(subTask12, subTask11.getChildren().iterator().next());

		assertEquals(1, subTask12.getChildren().size());
		assertEquals(subTask13, subTask12.getChildren().iterator().next());

		assertEquals(1, subTask13.getChildren().size());
		assertEquals(subTask14, subTask13.getChildren().iterator().next());

		assertEquals(1, subTask14.getChildren().size());
		assertEquals(subTask15, subTask14.getChildren().iterator().next());

		assertEquals(1, subTask15.getChildren().size());
		assertEquals(subTask16, subTask15.getChildren().iterator().next());

		assertEquals(1, subTask16.getChildren().size());
		assertEquals(subTask17, subTask16.getChildren().iterator().next());

		assertEquals(1, subTask17.getChildren().size());
		assertEquals(subTask18, subTask17.getChildren().iterator().next());

		assertEquals(0, subTask18.getChildren().size());
	}
}
