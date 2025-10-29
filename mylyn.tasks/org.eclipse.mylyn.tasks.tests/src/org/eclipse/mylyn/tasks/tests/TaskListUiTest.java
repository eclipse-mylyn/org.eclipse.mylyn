/*******************************************************************************
 * Copyright (c) 2004, 2011 Ken Sueda and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Ken Sueda - initial API and implementation
 *     Tasktop Technologies - improvements
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.ui.MoveToCategoryMenuContributor;
import org.eclipse.mylyn.internal.tasks.ui.TaskPriorityFilter;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.actions.NewCategoryAction;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryQuery;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.swt.widgets.TreeItem;

import junit.framework.TestCase;

/**
 * Tests TaskListView's filtering mechanism.
 *
 * @author Ken Sueda
 * @author Steffen Pingel
 */
@SuppressWarnings("nls")
public class TaskListUiTest extends TestCase {

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

	private TaskList taskList;

	private final static int CHECK_COMPLETE_FILTER = 1;

	private final static int CHECK_INCOMPLETE_FILTER = 2;

	private final static int CHECK_PRIORITY_FILTER = 3;

	@Override
	public void setUp() throws Exception {
		taskList = TasksUiPlugin.getTaskList();

		// make sure no unmatched folders exist
		TaskTestUtil.resetTaskListAndRepositories();
		TasksUiPlugin.getDefault().getLocalTaskRepository();
		TasksUiUtil.openTasksViewInActivePerspective();

		cat1 = new TaskCategory("First Category");
		taskList.addCategory(cat1);

		cat1task1 = TasksUiInternal.createNewLocalTask("task 1");
		cat1task1.setPriority(PriorityLevel.P1.toString());
		cat1task1.setCompletionDate(new Date());
		taskList.addTask(cat1task1, cat1);

		cat1task1sub1 = TasksUiInternal.createNewLocalTask("sub task 1");
		cat1task1sub1.setPriority(PriorityLevel.P1.toString());
		cat1task1sub1.setCompletionDate(new Date());
		taskList.addTask(cat1task1sub1, cat1task1);

		cat1task2 = TasksUiInternal.createNewLocalTask("task 2");
		cat1task2.setPriority(PriorityLevel.P2.toString());
		taskList.addTask(cat1task2, cat1);

		cat1task3 = TasksUiInternal.createNewLocalTask("task 3");
		cat1task3.setPriority(PriorityLevel.P3.toString());
		cat1task3.setCompletionDate(new Date());
		taskList.addTask(cat1task3, cat1);

		cat1task4 = TasksUiInternal.createNewLocalTask("task 4");
		cat1task4.setPriority(PriorityLevel.P4.toString());
		taskList.addTask(cat1task4, cat1);

		cat1task5 = TasksUiInternal.createNewLocalTask("task 5");
		cat1task5.setPriority(PriorityLevel.P5.toString());
		cat1task5.setCompletionDate(new Date());
		taskList.addTask(cat1task5, cat1);

		assertEquals(cat1.getChildren().size(), 5);

		cat2 = new TaskCategory("Second Category");
		taskList.addCategory(cat2);

		cat2task1 = TasksUiInternal.createNewLocalTask("task 1");
		cat2task1.setPriority(PriorityLevel.P1.toString());
		taskList.addTask(cat2task1, cat2);

		cat2task1sub1 = TasksUiInternal.createNewLocalTask("sub task 1");
		cat2task1sub1.setPriority(PriorityLevel.P1.toString());
		taskList.addTask(cat2task1sub1, cat2task1);

		cat2task2 = TasksUiInternal.createNewLocalTask("task 2");
		cat2task2.setPriority(PriorityLevel.P2.toString());
		cat2task2.setCompletionDate(new Date());
		taskList.addTask(cat2task2, cat2);

		cat2task3 = TasksUiInternal.createNewLocalTask("task 3");
		cat2task3.setPriority(PriorityLevel.P3.toString());
		taskList.addTask(cat2task3, cat2);

		cat2task4 = TasksUiInternal.createNewLocalTask("task 4");
		cat2task4.setPriority(PriorityLevel.P4.toString());
		cat2task4.setCompletionDate(new Date());
		taskList.addTask(cat2task4, cat2);

		cat2task5 = TasksUiInternal.createNewLocalTask("task 5");
		cat2task5.setPriority(PriorityLevel.P5.toString());
		taskList.addTask(cat2task5, cat2);
	}

	@Override
	public void tearDown() throws Exception {
		// clear everything
	}

	public void testUiFilter() {
		assertNotNull(TaskListView.getFromActivePerspective());
		TreeViewer viewer = TaskListView.getFromActivePerspective().getViewer();
		TaskListView.getFromActivePerspective().addFilter(TaskListView.getFromActivePerspective().getCompleteFilter());
		viewer.refresh();
		viewer.expandAll();
		TreeItem[] items = viewer.getTree().getItems();
		assertTrue(checkFilter(CHECK_COMPLETE_FILTER, items));
		TaskListView.getFromActivePerspective()
				.removeFilter(TaskListView.getFromActivePerspective().getCompleteFilter());

		TaskPriorityFilter filter = TaskListView.getFromActivePerspective().getPriorityFilter();
		filter.displayPrioritiesAbove("P2");
		TaskListView.getFromActivePerspective().addFilter(filter);
		viewer.refresh();
		viewer.expandAll();
		items = viewer.getTree().getItems();

		// check priority tasks
		assertTrue(checkFilter(CHECK_PRIORITY_FILTER, items));
	}

	/**
	 * Tests that TaskEditors remove all listeners when closed
	 */
	// FIXME re-enable test
	//	public void testListenersRemoved() {
//		int numListenersBefore = 0;
//		int numListenersDuring = 0;
//		int numListenersAfter = 0;
//
//		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
//		assertTrue(activePage.closeAllEditors(false));
//
//		Set<ITaskListChangeListener> listeners = taskList.getChangeListeners();
//		numListenersBefore = listeners.size();
//
//		TasksUiUtil.openTask(cat1task1);
//		TasksUiUtil.openTask(cat1task2);
//
//		listeners = taskList.getChangeListeners();
//		numListenersDuring = listeners.size();
//
//		// each editor adds a listener for the editor and planning part
//		assertEquals(numListenersDuring, numListenersBefore + 4);
//
//		assertTrue(activePage.closeAllEditors(false));
//
//		listeners = taskList.getChangeListeners();
//		numListenersAfter = listeners.size();
//		assertEquals(numListenersBefore, numListenersAfter);
//	}

	/**
	 * Tests whether an additional NewCategory action is added to the category
	 */
	public void testGetSubMenuManagerContainsAllCategoriesPlusNewCategory() {
		// setup
		MoveToCategoryMenuContributor moveToMenuContrib = new MoveToCategoryMenuContributor();
		List<IRepositoryElement> selectedElements = new Vector<>();
		selectedElements.add(cat1task1);
		int numCategories = taskList.getCategories().size();
		int numSeparators = 1;
		// adding a separator and the New Category... action
		int expectedNrOfSubMenuEntries = numCategories + numSeparators + 1;
		NewCategoryAction newCatActon = new NewCategoryAction();

		// execute sytem under test
		MenuManager menuManager = moveToMenuContrib.getSubMenuManager(selectedElements);
		IContributionItem[] items = menuManager.getItems();
		IContributionItem item = items[menuManager.getItems().length - 1];

		// +1 for separator
		assertEquals(expectedNrOfSubMenuEntries, menuManager.getItems().length);

		if (item instanceof NewCategoryAction action) {
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
		List<IRepositoryElement> selectedElements = new Vector<>();
		selectedElements.add(cat1task1);

		List<IRepositoryElement> emptySelection = new Vector<>();

		List<IRepositoryElement> categorySelection = new Vector<>();
		categorySelection.add(cat1);

		List<IRepositoryElement> querySelection = new Vector<>();
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
		return switch (type) {
			case CHECK_COMPLETE_FILTER -> checkCompleteIncompleteFilter(items, false);
			case CHECK_INCOMPLETE_FILTER -> checkCompleteIncompleteFilter(items, true);
			case CHECK_PRIORITY_FILTER -> checkPriorityFilter(items);
			default -> false;
		};
	}

	public boolean checkCompleteIncompleteFilter(TreeItem[] items, boolean checkComplete) {
		assertEquals(2, items.length);
		int count = 0;
		for (TreeItem item : items) {
			if (item.getData() instanceof TaskCategory) {
				TreeItem[] sub = item.getItems();
				for (TreeItem element : sub) {
					assertTrue(element.getData() instanceof ITask);
					ITask task = (ITask) element.getData();
					if (checkComplete) {
						assertTrue(task.isCompleted());
					} else {
						assertFalse(task.isCompleted());
					}
					count++;
				}
			}
		}
		assertEquals(5, count);
		return true;
	}

	public boolean checkPriorityFilter(TreeItem[] items) {
		assertEquals(2, items.length);
		int p2Count = 0;
		int p1Count = 0;
		for (TreeItem item : items) {
			if (item.getData() instanceof TaskCategory) {
				TreeItem[] sub = item.getItems();
				for (TreeItem element : sub) {
					assertTrue(element.getData() instanceof ITask);
					ITask task = (ITask) element.getData();
					assertTrue(task.getPriority().equals("P2") || task.getPriority().equals("P1"));
					if (task.getPriority().equals("P2")) {
						p2Count++;
					} else {
						p1Count++;
					}
				}
			}
		}
		assertEquals(2, p1Count);
		assertEquals(2, p2Count);
		return true;
	}

}
