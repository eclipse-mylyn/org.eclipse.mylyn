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

package org.eclipse.mylar.tasks.tests;

import java.io.File;

import junit.framework.TestCase;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylar.tasks.ITask;
import org.eclipse.mylar.tasks.MylarTasksPlugin;
import org.eclipse.mylar.tasks.Task;
import org.eclipse.mylar.tasks.TaskListManager;
import org.eclipse.mylar.tasks.internal.TaskCategory;
import org.eclipse.mylar.tasks.internal.TaskList;
import org.eclipse.mylar.tasks.internal.TaskPriorityFilter;
import org.eclipse.mylar.tasks.ui.views.TaskListView;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PartInitException;

/**
 * Tests TaskListView's filtering mechanism. 
 * @author Ken Sueda
 *
 */
public class TaskListUiTest extends TestCase {	
	private TaskList tlist = null;
	private TaskCategory cat1 = null;
	private Task cat1task1 = null;
	private Task cat1task2 = null;
	private Task cat1task3 = null;
	private Task cat1task4 = null;
	private Task cat1task5 = null;
	private Task cat1task1sub1 = null;
	
	private TaskCategory cat2 = null;
	private Task cat2task1 = null;
	private Task cat2task2 = null;
	private Task cat2task3 = null;
	private Task cat2task4 = null;
	private Task cat2task5 = null;
	private Task cat2task1sub1 = null;
	
	
	private final static int CHECK_COMPLETE_FILTER = 1;
	private final static int CHECK_INCOMPLETE_FILTER = 2;
	private final static int CHECK_PRIORITY_FILTER = 3;
	
	public void setUp() throws PartInitException{		
		MylarTasksPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("org.eclipse.mylar.tasks.ui.views.TaskListView");
		File file = new File("foo" + MylarTasksPlugin.FILE_EXTENSION);
        TaskListManager manager = new TaskListManager(file);        
        tlist = manager.createNewTaskList();        
        cat1 = new TaskCategory("First Category");
        
        cat1task1 = new Task(MylarTasksPlugin.getTaskListManager().genUniqueTaskId(), "task 1");
        cat1task1.setPriority("P1");
        cat1task1.setCompleted(true);
        cat1task1.setCategory(cat1);
		cat1.addTask(cat1task1);
		
		cat1task1sub1 = new Task(MylarTasksPlugin.getTaskListManager().genUniqueTaskId(), "sub task 1");
		cat1task1sub1.setPriority("P1");
		cat1task1sub1.setCompleted(true);
		cat1task1sub1.setParent(cat1task1);
        cat1task1.addSubTask(cat1task1sub1);
		
		cat1task2 = new Task(MylarTasksPlugin.getTaskListManager().genUniqueTaskId(), "task 2");
		cat1task2.setPriority("P2");
		cat1task2.setCategory(cat1);
		cat1.addTask(cat1task2);		
		
		cat1task3 = new Task(MylarTasksPlugin.getTaskListManager().genUniqueTaskId(), "task 3");
		cat1task3.setPriority("P3");
		cat1task3.setCompleted(true);
		cat1task3.setCategory(cat1);
		cat1.addTask(cat1task3);
		
		cat1task4 = new Task(MylarTasksPlugin.getTaskListManager().genUniqueTaskId(), "task 4");
		cat1task4.setPriority("P4");
		cat1task4.setCategory(cat1);
		cat1.addTask(cat1task4);
		
		cat1task5 = new Task(MylarTasksPlugin.getTaskListManager().genUniqueTaskId(), "task 5");
		cat1task5.setPriority("P5");
		cat1task5.setCompleted(true);
		cat1task5.setCategory(cat1);
		cat1.addTask(cat1task5);
		
		tlist.addCategory(cat1);
		assertEquals(cat1.getChildren().size(), 5);
		
		cat2 = new TaskCategory("Second Category");
        
        cat2task1 = new Task(MylarTasksPlugin.getTaskListManager().genUniqueTaskId(), "task 1");
        cat2task1.setPriority("P1");
        cat2task1.setCategory(cat2);
		cat2.addTask(cat2task1);
		
		cat2task1sub1 = new Task(MylarTasksPlugin.getTaskListManager().genUniqueTaskId(), "sub task 1");
		cat2task1sub1.setPriority("P1");
		cat2task1sub1.setParent(cat2task1);
		cat2task1.addSubTask(cat2task1sub1);
		
		cat2task2 = new Task(MylarTasksPlugin.getTaskListManager().genUniqueTaskId(), "task 2");
		cat2task2.setPriority("P2");
		cat2task2.setCompleted(true);
		cat2task2.setCategory(cat2);
		cat2.addTask(cat2task2);
		
		cat2task3 = new Task(MylarTasksPlugin.getTaskListManager().genUniqueTaskId(), "task 3");
		cat2task3.setPriority("P3");
		cat2task3.setCategory(cat2);
		cat2.addTask(cat2task3);
		
		cat2task4 = new Task(MylarTasksPlugin.getTaskListManager().genUniqueTaskId(), "task 4");
		cat2task4.setPriority("P4");
		cat2task4.setCompleted(true);
		cat2task4.setCategory(cat2);
		cat2.addTask(cat2task4);
		
		cat2task5 = new Task(MylarTasksPlugin.getTaskListManager().genUniqueTaskId(), "task 5");
		cat2task5.setPriority("P5");
		cat2task5.setCategory(cat2);
		cat2.addTask(cat2task5);
		
		tlist.addCategory(cat2);
		manager.saveTaskList();
	}
	
	public void tearDown() {
		// clear everything
	}
	
	public void testUiFilter() {
		assertNotNull(TaskListView.getDefault());  
		TreeViewer viewer = TaskListView.getDefault().getViewer();
		viewer.setContentProvider(new ContentProvider());
		viewer.refresh();
		MylarTasksPlugin.getTaskListManager().getTaskList().addFilter(TaskListView.getDefault().getCompleteFilter());
		viewer.refresh();
		viewer.expandAll();
		TreeItem[] items = viewer.getTree().getItems();
		assertTrue(checkFilter(CHECK_COMPLETE_FILTER, items));
		MylarTasksPlugin.getTaskListManager().getTaskList().removeFilter(TaskListView.getDefault().getCompleteFilter());
		
		
//		MylarTasksPlugin.getTaskListManager().getTaskList().addFilter(TaskListView.getDefault().getInCompleteFilter());
//		viewer.refresh();
//		viewer.expandAll();
//		items = viewer.getTree().getItems();
//		assertTrue(checkFilter(CHECK_INCOMPLETE_FILTER, items));
//		MylarTasksPlugin.getTaskListManager().getTaskList().removeFilter(TaskListView.getDefault().getInCompleteFilter());
		// check incomplte tasks
		
		
		TaskPriorityFilter filter = (TaskPriorityFilter)TaskListView.getDefault().getPriorityFilter();
		filter.displayPrioritiesAbove("P2");
		MylarTasksPlugin.getTaskListManager().getTaskList().addFilter(filter);
		viewer.refresh();
		viewer.expandAll();
		items = viewer.getTree().getItems();
		
		// check priority tasks
		assertTrue(checkFilter(CHECK_PRIORITY_FILTER, items));		
	}
	
	public boolean checkFilter(int type, TreeItem[] items) {
		switch(type) {
		case CHECK_COMPLETE_FILTER: return checkCompleteIncompleteFilter(items, false);
		case CHECK_INCOMPLETE_FILTER: return checkCompleteIncompleteFilter(items, true); 
		case CHECK_PRIORITY_FILTER: return checkPriorityFilter(items);
		default: return false;
		}
	}
	
	public boolean checkCompleteIncompleteFilter(TreeItem[] items, boolean checkComplete) {
		assertTrue(items.length == 2);
		int count = 0;
		for (int i = 0; i < items.length; i++) {
			assertTrue(items[i].getData() instanceof TaskCategory);
			TreeItem[] sub = items[i].getItems();
			for (int j = 0; j < sub.length; j++) {
				assertTrue(sub[j].getData() instanceof ITask);
				ITask task = (ITask) sub[j].getData();
				if (checkComplete) {
					assertTrue(task.isCompleted());
				} else {
					assertFalse(task.isCompleted());
				}
				count++;
			}			
		}
		assertTrue(count == 5);
		return true;
	}
	
	public boolean checkPriorityFilter(TreeItem[] items) {
		assertTrue(items.length == 2);
		int p2Count = 0;
		int p1Count = 0;
		for (int i = 0; i < items.length; i++) {
			assertTrue(items[i].getData() instanceof TaskCategory);
			TreeItem[] sub = items[i].getItems();
			for (int j = 0; j < sub.length; j++) {
				assertTrue(sub[j].getData() instanceof ITask);
				ITask task = (ITask) sub[j].getData();
				assertTrue(task.getPriority().equals("P2") || task.getPriority().equals("P1"));
				if (task.getPriority().equals("P2")) {
					p2Count++;
				} else {
					p1Count++;
				}
			}			
		}		
		assertEquals(2, p1Count);
		assertEquals(2, p2Count);
		return true;
	}
	
	class ContentProvider implements IStructuredContentProvider, ITreeContentProvider {
        public void inputChanged(Viewer v, Object oldInput, Object newInput) {
        	// don't care if the input changes
        }
        public void dispose() {
        	// don't care if we are disposed
        }
        public Object[] getElements(Object parent) {
            return tlist.getRoots().toArray();            	          
        }
        public Object getParent(Object child) {
            if (child instanceof TaskCategory) {
                return ((Task)child).getCategory();
            }
            return null;
        }
        public Object [] getChildren(Object parent) {
        	return MylarTasksPlugin.getTaskListManager().getTaskList().getFilteredChildrenFor(parent).toArray();
        }
        public boolean hasChildren(Object parent) {  
            if (parent instanceof TaskCategory) {
            	TaskCategory cat = (TaskCategory)parent;
                return cat.getChildren() != null && cat.getChildren().size() > 0;
            } 
            return false;
        }
    }
}
