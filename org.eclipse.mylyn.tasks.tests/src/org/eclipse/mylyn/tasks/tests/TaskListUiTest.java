/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import junit.framework.TestCase;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylyn.internal.tasks.core.WebTask;
import org.eclipse.mylyn.internal.tasks.ui.MoveToCategoryMenuContributor;
import org.eclipse.mylyn.internal.tasks.ui.TaskPriorityFilter;
import org.eclipse.mylyn.internal.tasks.ui.actions.MarkTaskCompleteAction;
import org.eclipse.mylyn.internal.tasks.ui.actions.NewCategoryAction;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.ITaskListChangeListener;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.core.TaskCategory;
import org.eclipse.mylyn.tasks.core.AbstractTask.PriorityLevel;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryQuery;
import org.eclipse.mylyn.tasks.ui.TaskListManager;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PartInitException;

/**
 * Tests TaskListView's filtering mechanism.
 * 
 * @author Ken Sueda
 * 
 */
public class TaskListUiTest extends TestCase {

	private TaskListManager manager = null;

	private TaskCategory cat1 = null;

	private AbstractTask cat1task1 = null;

	private AbstractTask cat1task2 = null;

	private AbstractTask cat1task3 = null;

	private AbstractTask cat1task4 = null;

	private AbstractTask cat1task5 = null;

	private AbstractTask cat1task1sub1 = null;

	private TaskCategory cat2 = null;

	private AbstractTask cat2task1 = null;

	private AbstractTask cat2task2 = null;

	private AbstractTask cat2task3 = null;

	private AbstractTask cat2task4 = null;

	private AbstractTask cat2task5 = null;

	private AbstractTask cat2task1sub1 = null;

	private final static int CHECK_COMPLETE_FILTER = 1;

	private final static int CHECK_INCOMPLETE_FILTER = 2;

	private final static int CHECK_PRIORITY_FILTER = 3;

	@Override
	public void setUp() throws PartInitException {
		try {
			TaskListView.openInActivePerspective();
			manager = TasksUiPlugin.getTaskListManager();
			cat1 = new TaskCategory("First Category");

			cat1task1 = manager.createNewLocalTask("task 1");
			cat1task1.setPriority(PriorityLevel.P1.toString());
			cat1task1.setCompleted(true);			
			manager.getTaskList().moveToContainer(cat1, cat1task1);

			cat1task1sub1 = manager.createNewLocalTask("sub task 1");
			cat1task1sub1.setPriority(PriorityLevel.P1.toString());
			cat1task1sub1.setCompleted(true);
			manager.getTaskList().addTask(cat1task1sub1, cat1task1);

			cat1task2 = manager.createNewLocalTask("task 2");
			cat1task2.setPriority(PriorityLevel.P2.toString());
			cat1task2.setCategory(cat1);
			manager.getTaskList().moveToContainer(cat1, cat1task2);

			cat1task3 = manager.createNewLocalTask("task 3");
			cat1task3.setPriority(PriorityLevel.P3.toString());
			cat1task3.setCompleted(true);
			cat1task3.setCategory(cat1);
			manager.getTaskList().moveToContainer(cat1, cat1task3);

			cat1task4 = manager.createNewLocalTask("task 4");
			cat1task4.setPriority(PriorityLevel.P4.toString());
			cat1task4.setCategory(cat1);
			manager.getTaskList().moveToContainer(cat1, cat1task4);

			cat1task5 = manager.createNewLocalTask("task 5");
			cat1task5.setPriority(PriorityLevel.P5.toString());
			cat1task5.setCompleted(true);
			cat1task5.setCategory(cat1);
			manager.getTaskList().moveToContainer(cat1, cat1task5);

			manager.getTaskList().addCategory(cat1);
			assertEquals(cat1.getChildren().size(), 5);

			cat2 = new TaskCategory("Second Category");

			cat2task1 = manager.createNewLocalTask("task 1");
			cat2task1.setPriority(PriorityLevel.P1.toString());
			cat2task1.setCategory(cat2);
			manager.getTaskList().moveToContainer(cat2, cat2task1);

			cat2task1sub1 = manager.createNewLocalTask("sub task 1");
			cat2task1sub1.setPriority(PriorityLevel.P1.toString());
			manager.getTaskList().addTask(cat2task1sub1, cat2task1);

			cat2task2 = manager.createNewLocalTask("task 2");
			cat2task2.setPriority(PriorityLevel.P2.toString());
			cat2task2.setCompleted(true);
			cat2task2.setCategory(cat2);
			manager.getTaskList().moveToContainer(cat2, cat2task2);

			cat2task3 = manager.createNewLocalTask("task 3");
			cat2task3.setPriority(PriorityLevel.P3.toString());
			cat2task3.setCategory(cat2);
			manager.getTaskList().moveToContainer(cat2, cat2task3);

			cat2task4 = manager.createNewLocalTask("task 4");
			cat2task4.setPriority(PriorityLevel.P4.toString());
			cat2task4.setCompleted(true);
			cat2task4.setCategory(cat2);
			manager.getTaskList().moveToContainer(cat2, cat2task4);

			cat2task5 = manager.createNewLocalTask("task 5");
			cat2task5.setPriority(PriorityLevel.P5.toString());
			cat2task5.setCategory(cat2);
			manager.getTaskList().moveToContainer(cat2, cat2task5);

			manager.getTaskList().addCategory(cat2);
			manager.saveTaskList();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void tearDown() {
		// clear everything
	}

	public void testMarkWebTaskCompleted() {
		TaskListView view = TaskListView.getFromActivePerspective();
		assertNotNull(view);
		WebTask webTask = new WebTask("1", "1", "", "", "web");
		TasksUiPlugin.getTaskListManager().getTaskList().addTask(webTask,
				TasksUiPlugin.getTaskListManager().getTaskList().getAutomaticCategory());
		view.getViewer().refresh();
		// Arrays.asList(view.getViewer().getVisibleExpandedElements());
		assertFalse(webTask.isCompleted());
		ArrayList<AbstractTaskContainer> tasks = new ArrayList<AbstractTaskContainer>();
		tasks.add(webTask);
		new MarkTaskCompleteAction(tasks).run();
		assertTrue(webTask.isCompleted());
	}

	public void testUiFilter() {
		try {
			assertNotNull(TaskListView.getFromActivePerspective());
			TreeViewer viewer = TaskListView.getFromActivePerspective().getViewer();
			TaskListView.getFromActivePerspective().addFilter(
					TaskListView.getFromActivePerspective().getCompleteFilter());
			viewer.refresh();
			viewer.expandAll();
			TreeItem[] items = viewer.getTree().getItems();
			assertTrue(checkFilter(CHECK_COMPLETE_FILTER, items));
			TaskListView.getFromActivePerspective().removeFilter(
					TaskListView.getFromActivePerspective().getCompleteFilter());

			TaskPriorityFilter filter = TaskListView.getFromActivePerspective().getPriorityFilter();
			filter.displayPrioritiesAbove("P2");
			TaskListView.getFromActivePerspective().addFilter(filter);
			viewer.refresh();
			viewer.expandAll();
			items = viewer.getTree().getItems();

			// check priority tasks
			assertTrue(checkFilter(CHECK_PRIORITY_FILTER, items));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Tests that TaskEditors remove all listeners when closed
	 */
	public void testListenersRemoved() {

		int numListenersBefore = 0;
		int numListenersDuring = 0;
		int numListenersAfter = 0;

		TaskListManager manager = TasksUiPlugin.getTaskListManager();
		Set<ITaskListChangeListener> listeners = manager.getTaskList().getChangeListeners();
		numListenersBefore = listeners.size();

		// open a task in editor
		// cat1task1.setForceSyncOpen(true);
		TasksUiUtil.openEditor(cat1task1, false, true);
		// cat1task1.openTaskInEditor(false);
		// cat1task2.setForceSyncOpen(true);
		// cat1task2.openTaskInEditor(false);
		TasksUiUtil.openEditor(cat1task2, false, true);

		listeners = manager.getTaskList().getChangeListeners();
		numListenersDuring = listeners.size();

		assertEquals(numListenersDuring, numListenersBefore + 2);

		TasksUiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);

		listeners = manager.getTaskList().getChangeListeners();
		numListenersAfter = listeners.size();
		assertEquals(numListenersBefore, numListenersAfter);

	}

	/**
	 * Tests whether an additional NewCategory action is added to the category
	 */
	public void testGetSubMenuManagerContainsAllCategoriesPlusNewCategory() {
		// setup
		MoveToCategoryMenuContributor moveToMenuContrib = new MoveToCategoryMenuContributor();
		List<AbstractTaskContainer> selectedElements = new Vector<AbstractTaskContainer>();
		selectedElements.add(cat1task1);
		int nrOfCategoriesMinusArchiveContainer = manager.getTaskList().getCategories().size() - 1;
		int nrOfSeparators = 1;
		// adding a separator and the New Category... action
		int expectedNrOfSubMenuEntries = nrOfCategoriesMinusArchiveContainer + nrOfSeparators + 1;
		NewCategoryAction newCatActon = new NewCategoryAction();

		// execute sytem under test
		MenuManager menuManager = moveToMenuContrib.getSubMenuManager(selectedElements);
		IContributionItem[] items = menuManager.getItems();
		IContributionItem item = items[menuManager.getItems().length - 1];

		// +1 for separator
		assertEquals(expectedNrOfSubMenuEntries, menuManager.getItems().length);

		if (item instanceof NewCategoryAction) {
			NewCategoryAction action = (NewCategoryAction) item;
			assertEquals(newCatActon.getText(), action.getText());
		}

		// teardown
	}

	/**
	 * Tests visibility of SubMenuManager
	 */
	public void testVisibilityOfSubMenuManager() {
		//setup
		MoveToCategoryMenuContributor moveToMenuContrib = new MoveToCategoryMenuContributor();
		MenuManager menuManager = null;
		List<AbstractTaskContainer> selectedElements = new Vector<AbstractTaskContainer>();
		selectedElements.add(cat1task1);

		List<AbstractTaskContainer> emptySelection = new Vector<AbstractTaskContainer>();

		List<AbstractTaskContainer> categorySelection = new Vector<AbstractTaskContainer>();
		categorySelection.add(cat1);

		List<AbstractTaskContainer> querySelection = new Vector<AbstractTaskContainer>();
		querySelection.add(new MockRepositoryQuery("query", null));

		//execute system under test & assert
		menuManager = moveToMenuContrib.getSubMenuManager(selectedElements);
		assertTrue(menuManager.isVisible());

		menuManager = null;
		menuManager = moveToMenuContrib.getSubMenuManager(emptySelection);
		assertFalse(menuManager.isVisible());

		menuManager = null;
		menuManager = moveToMenuContrib.getSubMenuManager(categorySelection);
		assertFalse(menuManager.isVisible());

		menuManager = null;
		menuManager = moveToMenuContrib.getSubMenuManager(querySelection);
		assertFalse(menuManager.isVisible());

		//teardown
	}

	/**
	 * Tests that the category name is shown in the Move To Category submenu, even when they have an @ in their name
	 */
	public void testCategoryNameIsShownInMoveToCategoryAction() {
		String catNameWithAtBefore = "@CatName";
		String catNameWithAtExpected = "@CatName@";
		String catNameWithAtActual = "";

		String catNameNoAtBefore = "CatName";
		String catNameNoAtExpected = "CatName";
		String catNameNoAtActual = "";

		MoveToCategoryMenuContributor menuContrib = new MoveToCategoryMenuContributor();

		catNameWithAtActual = menuContrib.handleAcceleratorKeys(catNameWithAtBefore);
		catNameNoAtActual = menuContrib.handleAcceleratorKeys(catNameNoAtBefore);

		assertEquals(catNameWithAtExpected, catNameWithAtActual);
		assertEquals(catNameNoAtExpected, catNameNoAtActual);
	}

	public boolean checkFilter(int type, TreeItem[] items) {
		switch (type) {
		case CHECK_COMPLETE_FILTER:
			return checkCompleteIncompleteFilter(items, false);
		case CHECK_INCOMPLETE_FILTER:
			return checkCompleteIncompleteFilter(items, true);
		case CHECK_PRIORITY_FILTER:
			return checkPriorityFilter(items);
		default:
			return false;
		}
	}

	public boolean checkCompleteIncompleteFilter(TreeItem[] items, boolean checkComplete) {
		assertEquals(2, items.length);
		int count = 0;
		for (int i = 0; i < items.length; i++) {
			assertTrue(items[i].getData() instanceof TaskCategory);
			TreeItem[] sub = items[i].getItems();
			for (int j = 0; j < sub.length; j++) {
				assertTrue(sub[j].getData() instanceof AbstractTask);
				AbstractTask task = (AbstractTask) sub[j].getData();
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
				assertTrue(sub[j].getData() instanceof AbstractTask);
				AbstractTask task = (AbstractTask) sub[j].getData();
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

}
