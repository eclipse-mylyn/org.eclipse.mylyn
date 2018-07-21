/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.internal.tasks.ui.ScheduledPresentation;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.commands.MarkTaskHandler.MarkTaskCompleteHandler;
import org.eclipse.mylyn.internal.tasks.ui.commands.MarkTaskHandler.MarkTaskReadHandler;
import org.eclipse.mylyn.internal.tasks.ui.views.AbstractTaskListPresentation;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskScheduleContentProvider.Incoming;
import org.eclipse.mylyn.internal.tasks.ui.workingsets.TaskWorkingSetUpdater;
import org.eclipse.mylyn.tasks.core.ITask.SynchronizationState;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.tests.TaskTestUtil;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.internal.Workbench;

/**
 * @author Steffen Pingel
 */
public class MarkTaskHandlerTest extends TestCase {

	private IHandlerService handlerService;

	private IWorkingSet workingSet;

	private IWorkingSetManager workingSetManager;

	@Override
	protected void setUp() throws Exception {
		TaskTestUtil.resetTaskListAndRepositories();

		handlerService = (IHandlerService) PlatformUI.getWorkbench().getService(IHandlerService.class);

		TaskRepository repository = TaskTestUtil.createMockRepository();
		TasksUiPlugin.getRepositoryManager().addRepository(repository);

		// TODO figure out which test leaves a filter enabled
		TaskWorkingSetUpdater.applyWorkingSetsToAllWindows(new HashSet<IWorkingSet>(0));
		TaskTestUtil.openTasksViewInActivePerspective().clearFilters();
		workingSetManager = Workbench.getInstance().getWorkingSetManager();
	}

	@Override
	protected void tearDown() throws Exception {
		TaskTestUtil.resetTaskListAndRepositories();
		if (workingSet != null) {
			workingSetManager.removeWorkingSet(workingSet);
			workingSet = null;
		}
	}

	public void testMarkTaskCompleted() throws Exception {
		MockRepositoryConnector.getDefault().setHasLocalCompletionState(true);
		TaskTask task = TaskTestUtil.createMockTask("1");
		TaskTestUtil.addAndSelectTask(task);
		assertFalse(task.isCompleted());

		handlerService.executeCommand(MarkTaskCompleteHandler.ID_COMMAND, null);
		assertTrue(task.isCompleted());

		try {
			handlerService.executeCommand(MarkTaskCompleteHandler.ID_COMMAND, null);
			fail("Expected NotEnabledException");
		} catch (NotEnabledException e) {
			// expected
		}
	}

	public void testMarkLocalTaskCompleted() throws Exception {
		LocalTask localTask = new LocalTask("1", "");
		TaskTestUtil.addAndSelectTask(localTask);
		assertFalse(localTask.isCompleted());

		handlerService.executeCommand(MarkTaskCompleteHandler.ID_COMMAND, null);
		assertTrue(localTask.isCompleted());

		try {
			handlerService.executeCommand(MarkTaskCompleteHandler.ID_COMMAND, null);
			fail("Expected NotEnabledException");
		} catch (NotEnabledException e) {
			// expected
		}
	}

	public void testMarkWorkingSetIncomingRead() throws Exception {
		workingSet = createAndSelectWorkingSet();
		Incoming incoming = setScheduledPresentationAndSelectIncoming();
		TaskTask taskInWorkingSet = createIncomingTask("1", incoming, workingSet);
		TaskTask taskNotInWorkingSet = createIncomingTask("2", incoming, null);
		assertTrue(workingSetContainsTask(taskInWorkingSet));
		assertFalse(workingSetContainsTask(taskNotInWorkingSet));

		assertTrue(taskInWorkingSet.getSynchronizationState().isIncoming());
		assertTrue(taskNotInWorkingSet.getSynchronizationState().isIncoming());
		handlerService.executeCommand(MarkTaskReadHandler.ID_COMMAND, null);
		assertFalse(taskInWorkingSet.getSynchronizationState().isIncoming());
		assertTrue(taskNotInWorkingSet.getSynchronizationState().isIncoming());
	}

	protected boolean workingSetContainsTask(TaskTask taskInWorkingSet) {
		List<IAdaptable> elements = Arrays.asList(workingSet.getElements());
		for (AbstractTaskContainer parent : taskInWorkingSet.getParentContainers()) {
			if (elements.contains(parent)) {
				return true;
			}
		}
		return false;
	}

	protected Incoming setScheduledPresentationAndSelectIncoming() throws Exception {
		TaskListView taskListView = TaskTestUtil.openTasksViewInActivePerspective();
		for (AbstractTaskListPresentation presentation : TaskListView.getPresentations()) {
			if (presentation.getId().equals(ScheduledPresentation.ID)) {
				taskListView.applyPresentation(presentation);
				break;
			}
		}
		taskListView.setFocusedMode(true);
		taskListView.refresh();
		assertTrue(taskListView.getCurrentPresentation().getId().equals(ScheduledPresentation.ID));
		// select incoming container
		IContentProvider contentProvider = taskListView.getViewer().getContentProvider();
		assertTrue(contentProvider instanceof ITreeContentProvider);
		for (Object element : ((ITreeContentProvider) contentProvider).getElements(taskListView.getViewSite())) {
			if (element instanceof Incoming) {
				taskListView.getViewer().setSelection(new StructuredSelection(element), true);
			}
		}
		assertEquals(taskListView.getSelectedTaskContainers().size(), 1);
		assertTrue(taskListView.getSelectedTaskContainers().get(0) instanceof Incoming);
		return (Incoming) taskListView.getSelectedTaskContainers().get(0);
	}

	protected TaskTask createIncomingTask(String id, Incoming incoming, IWorkingSet workingSet) throws Exception {
		RepositoryQuery query = TaskTestUtil.createMockQuery("query." + id);
		TasksUiPlugin.getTaskList().addQuery(query);
		if (workingSet != null) {
			workingSet.setElements(new IAdaptable[] { query });
		}
		TaskTask task = TaskTestUtil.createMockTask(id);
		TasksUiPlugin.getTaskList().addTask(task, query);
		task.setSynchronizationState(SynchronizationState.INCOMING);
		assertTrue(incoming.getChildren().contains(task));
		return task;
	}

	protected IWorkingSet createAndSelectWorkingSet() {
		IWorkingSet workingSet = workingSetManager.createWorkingSet("Task Working Set", new IAdaptable[] {});
		workingSet.setId(TaskWorkingSetUpdater.ID_TASK_WORKING_SET);
		ArrayList<IWorkingSet> list = new ArrayList<IWorkingSet>();
		list.add(workingSet);
		TaskWorkingSetUpdater.applyWorkingSetsToAllWindows(list);
		assertTrue(TaskWorkingSetUpdater.isWorkingSetEnabled(workingSet));
		return workingSet;
	}

}
