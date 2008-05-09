/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.ITaskList;
import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.TaskList;
import org.eclipse.mylyn.internal.tasks.core.UnmatchedTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.externalization.TaskListExternalizationParticipant;
import org.eclipse.mylyn.internal.tasks.core.externalization.TaskListExternalizer;
import org.eclipse.mylyn.internal.tasks.ui.util.TaskListElementImporter;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskActivityListener;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;

/**
 * Provides facilities for using and managing the Task List and task activity information.
 * 
 * TODO: pull task activity management out into new TaskActivityManager NOTE: likely to change for 3.0
 * 
 * @author Mik Kersten
 * @author Rob Elves (task activity)
 * @author Jevgeni Holodkov (insertQueries)
 * @since 3.0
 */
public class TaskListManager {

	private static final long SECOND = 1000;

	private static final long MINUTE = 60 * SECOND;

	private static final long ROLLOVER_DELAY = 30 * MINUTE;

	private final List<ITaskActivityListener> taskActivationListeners = new ArrayList<ITaskActivityListener>();

	private final TaskListExternalizer taskListWriter;

//	private TaskListSaveManager taskListSaveManager;

	private final TaskList taskList = new TaskList();

//	private AbstractTask activeTask;

	private TaskListExternalizationParticipant taskListSaveParticipant;

	private final TaskListElementImporter importer;

	public TaskListManager(TaskListExternalizer taskListWriter, File file) {
		this.taskListWriter = taskListWriter;
		importer = new TaskListElementImporter();
		importer.setDelegateExternalizers(taskListWriter.getExternalizers());
	}

	public ITaskList resetTaskList() {
		deactivateAllTasks();
		//resetAndRollOver();
		taskList.reset();
		prepareOrphanContainers();
		return taskList;
	}

	private void prepareOrphanContainers() {
		for (TaskRepository repository : TasksUi.getRepositoryManager().getAllRepositories()) {
			if (!repository.getConnectorKind().equals(LocalRepositoryConnector.CONNECTOR_KIND)) {
				taskList.addUnmatchedContainer(new UnmatchedTaskContainer(repository.getConnectorKind(),
						repository.getRepositoryUrl()));
			}
		}
	}

	public TaskListElementImporter getTaskListWriter() {
		return importer;
	}

	public boolean readExistingOrCreateNewList() {
		prepareOrphanContainers();
		if (taskListSaveParticipant == null) {
			taskListSaveParticipant = new TaskListExternalizationParticipant(taskList, taskListWriter,
					TasksUiPlugin.getExternalizationManager());
		}

		TasksUiPlugin.getExternalizationManager().load(taskListSaveParticipant);
		TasksUiPlugin.getTaskActivityManager().reloadTimingData();
		//TasksUiExtensionReader.initStartupExtensions(taskListSaveParticipant.getTaskListWriter());
		TasksUiPlugin.getExternalizationManager().addParticipant(taskListSaveParticipant);
		taskList.addChangeListener(taskListSaveParticipant);
		return true;
	}

	public TaskList getTaskList() {
		return taskList;
	}

	/**
	 * Imports Queries to the TaskList and synchronize them with the repository. If the imported query have the name
	 * that overlaps with the existing one, the the suffix [x] is added, where x is a number starting from 1.
	 * 
	 * @param queries
	 * 		to insert
	 * @return the list queries, which were not inserted since because the related repository was not found.
	 */
	public List<RepositoryQuery> insertQueries(List<RepositoryQuery> queries) {
		List<RepositoryQuery> badQueries = new ArrayList<RepositoryQuery>();

		for (RepositoryQuery query : queries) {

			TaskRepository repository = TasksUi.getRepositoryManager().getRepository(query.getConnectorKind(),
					query.getRepositoryUrl());
			if (repository == null) {
				badQueries.add(query);
				continue;
			}

			String handle = resolveIdentifiersConflict(query);
			query.setHandleIdentifier(handle);

			// add query
			TasksUiInternal.getTaskList().addQuery(query);

			AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
					repository.getConnectorKind());
			if (connector != null) {
				TasksUiInternal.synchronizeQuery(connector, query, null, true);
			}

		}

		return badQueries;
	}

	/**
	 * Utility method that checks, if there is already a query with the same identifier.
	 * 
	 * @param query
	 * @return a handle, that is not in conflict with any existed one in the system. If there were no conflict in the
	 * 	beginning, then the query's own identifier is returned. If there were, then the suffix [x] is applied the
	 * 	query's identifier, where x is a number.
	 * @since 2.1
	 */
	public String resolveIdentifiersConflict(RepositoryQuery query) {
		String patternStr = "\\[(\\d+)\\]$"; // all string that end with [x], where x is a number
		Pattern pattern = Pattern.compile(patternStr);

		// resolve name conflict
		Set<RepositoryQuery> existingQueries = getTaskList().getQueries();
		Map<String, RepositoryQuery> queryMap = new HashMap<String, RepositoryQuery>();
		for (RepositoryQuery existingQuery : existingQueries) {
			queryMap.put(existingQuery.getHandleIdentifier(), existingQuery);
		}

		// suggest a new handle if needed
		String handle = query.getHandleIdentifier();

		while (queryMap.get(handle) != null) {
			Matcher matcher = pattern.matcher(handle);
			boolean matchFound = matcher.find();
			if (matchFound) {
				// increment index
				int index = Integer.parseInt(matcher.group(1));
				index++;
				handle = matcher.replaceAll("[" + index + "]");
			} else {
				handle += "[1]";
			}
		}

		return handle;
	}

	/**
	 * use <code>TasksUi.getTaskActivityManager().getActiveTask()</code>
	 * 
	 * @deprecated
	 */
	@Deprecated
	public ITask getActiveTask() {
		return TasksUi.getTaskActivityManager().getActiveTask();
	}

	/**
	 * Will not save an empty task list to avoid losing data on bad startup.
	 * 
	 * @deprecated use <code>TasksUiPlugin.getExternalizationManager().requestSave()</code>
	 */
	@Deprecated
	public synchronized void saveTaskList() {
		TasksUiPlugin.getExternalizationManager().requestSave();
	}

	/**
	 * use <code>TasksUi.getTaskActivityManager().activateTask(task)</code>
	 * 
	 * @deprecated
	 */
	@Deprecated
	public void activateTask(AbstractTask task) {
		TasksUi.getTaskActivityManager().activateTask(task);
	}

	/**
	 * use <code>TasksUi.getTaskActivityManager().activateTask(task)</code>
	 * 
	 * @deprecated
	 */
	@Deprecated
	public void activateTask(AbstractTask task, boolean addToHistory) {
		TasksUi.getTaskActivityManager().activateTask(task);
	}

	/**
	 * use <code>TasksUi.getTaskActivityManager().deactivateAllTasks()</code>
	 * 
	 * @deprecated
	 */
	@Deprecated
	public void deactivateAllTasks() {
		TasksUi.getTaskActivityManager().deactivateActiveTask();
	}

	/**
	 * use <code>TasksUi.getTaskActivityManager().deactivateTask(task)</code>
	 * 
	 * @deprecated
	 */
	@Deprecated
	public void deactivateTask(ITask task) {
		TasksUi.getTaskActivityManager().deactivateTask(task);
	}

//	public TaskActivationHistory getTaskActivationHistory() {
//	return taskActivityHistory;
//}

//protected void setTaskListSaveManager(TaskListSaveManager taskListSaveManager) {
//	this.taskListSaveManager = taskListSaveManager;
//	this.taskList.addChangeListener(taskListSaveManager);
//}

///**
// * Creates a new local task and schedules for today
// * 
// * @param summary
// * 		if null DEFAULT_SUMMARY (New Task) used.
// */
//public LocalTask createNewLocalTask(String summary) {
//	if (summary == null) {
//		summary = LocalRepositoryConnector.DEFAULT_SUMMARY;
//	}
//	LocalTask newTask = new LocalTask("" + taskList.getNextLocalTaskId(), summary);
//	newTask.setPriority(PriorityLevel.P3.toString());
//	TasksUi.getTaskList().addTask(newTask);
//
//	TasksUiPlugin.getTaskActivityManager().scheduleNewTask(newTask);
//
//	Object selectedObject = null;
//	TaskListView view = TaskListView.getFromActivePerspective();
//	if (view != null) {
//		selectedObject = ((IStructuredSelection) view.getViewer().getSelection()).getFirstElement();
//	}
//	if (selectedObject instanceof TaskCategory) {
//		taskList.addTask(newTask, (TaskCategory) selectedObject);
//	} else if (selectedObject instanceof AbstractTask) {
//		AbstractTask task = (AbstractTask) selectedObject;
//
//		AbstractTaskContainer container = TaskCategory.getParentTaskCategory(task);
//
//		if (container instanceof TaskCategory) {
//			taskList.addTask(newTask, container);
//		} else if (view != null && view.getDrilledIntoCategory() instanceof TaskCategory) {
//			taskList.addTask(newTask, view.getDrilledIntoCategory());
//		} else {
//			taskList.addTask(newTask, TasksUiPlugin.getTaskList().getDefaultCategory());
//		}
//	} else if (view != null && view.getDrilledIntoCategory() instanceof TaskCategory) {
//		taskList.addTask(newTask, view.getDrilledIntoCategory());
//	} else {
//		if (view != null && view.getDrilledIntoCategory() != null) {
//			MessageDialog.openInformation(Display.getCurrent().getActiveShell(), ITasksUiConstants.TITLE_DIALOG,
//					"The new task has been added to the root of the list, since tasks can not be added to a query.");
//		}
//		taskList.addTask(newTask, TasksUiPlugin.getTaskList().getDefaultCategory());
//	}
//	return newTask;
//}

//	/**
//	 * public for testing TODO: Move to TaskActivityManager
//	 */
//	private void resetAndRollOver() {
//		resetAndRollOver(TaskActivityUtil.getCalendar().getTime());
//	}
//
//	private void resetAndRollOver(Date startDate) {
//		if (taskList.isInitialized()) {
//			TasksUiPlugin.getTaskActivityManager().clear();
//			List<InteractionEvent> events = ContextCore.getContextManager()
//					.getActivityMetaContext()
//					.getInteractionHistory();
//			for (InteractionEvent event : events) {
//				TasksUiPlugin.getTaskActivityMonitor().parseInteractionEvent(event);
//			}
//			TasksUiPlugin.getTaskActivityManager().reloadTimingData();
//		}
//	}
}
