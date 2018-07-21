/*******************************************************************************
 * Copyright (c) 2016 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.internal.tasks.core.ITaskJobFactory;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityManager;
import org.eclipse.mylyn.internal.tasks.core.TaskActivityUtil;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.sync.SynchronizationJob;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

public class SynchronizeRelevantTasksJob extends Job {

	private final TaskActivityManager taskActivityManager;

	private final IRepositoryManager repositoryManager;

	private final ITaskJobFactory taskJobFactory;

	private static final int MAX_NUM_TASKS = 10;

	public SynchronizeRelevantTasksJob(TaskActivityManager taskActivityManager, IRepositoryManager repositoryManager,
			ITaskJobFactory taskJobFactory) {
		super(Messages.SynchronizeRelevantTasksJob_SynchronizingRelevantTasks);
		this.taskActivityManager = taskActivityManager;
		this.repositoryManager = repositoryManager;
		this.taskJobFactory = taskJobFactory;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		HashSet<ITask> relevantTasks = new HashSet<ITask>();

		addOpenEditorTasks(relevantTasks);
		addActiveTask(relevantTasks);
		addTodaysTasks(relevantTasks);

		ListMultimap<TaskRepository, ITask> repositoryMap = mapTasksToRepository(relevantTasks);
		scheduleSynchronizationJobs(repositoryMap);

		return Status.OK_STATUS;
	}

	private void scheduleSynchronizationJobs(ListMultimap<TaskRepository, ITask> repositoryMap) {
		List<Job> jobs = new ArrayList<>();
		for (TaskRepository taskRepository : repositoryMap.keySet()) {
			List<ITask> repositoryTasks = repositoryMap.get(taskRepository);
			AbstractRepositoryConnector connector = TasksUi.getRepositoryConnector(taskRepository.getConnectorKind());
			SynchronizationJob synchronizationJob = taskJobFactory.createSynchronizeTasksJob(connector, taskRepository,
					new HashSet<>(repositoryTasks));
			synchronizationJob.setUser(false);
			synchronizationJob.setSystem(true);
			synchronizationJob.schedule();
			jobs.add(synchronizationJob);
		}

		for (Job job : jobs) {
			try {
				job.join();
			} catch (InterruptedException e) {
				// Ignore
			}
		}
	}

	private void addOpenEditorTasks(HashSet<ITask> relevantTasks) {
		IWorkbenchWindow[] workbenchWindows = PlatformUI.getWorkbench().getWorkbenchWindows();
		HashSet<ITask> openTasks = new HashSet<>();
		for (IWorkbenchWindow workbenchWindow : workbenchWindows) {
			IWorkbenchPage[] workbenchPages = workbenchWindow.getPages();
			for (IWorkbenchPage workbenchPage : workbenchPages) {
				IEditorReference[] editorReferences = workbenchPage.getEditorReferences();
				for (IEditorReference editorReference : editorReferences) {
					IEditorPart editorPart = editorReference.getEditor(false);
					if (editorPart instanceof TaskEditor) {
						TaskEditorInput taskEditorInput = ((TaskEditor) editorPart).getTaskEditorInput();
						if (taskEditorInput.exists()) {
							ITask task = taskEditorInput.getTask();
							openTasks.add(task);
						}
					}
				}
			}
		}
		if (openTasks.size() < MAX_NUM_TASKS) {
			relevantTasks.addAll(openTasks);
		}
	}

	private void addActiveTask(HashSet<ITask> relevantTasks) {
		ITask activeTask = taskActivityManager.getActiveTask();
		if (activeTask != null) {
			relevantTasks.add(activeTask);
		}
	}

	private void addTodaysTasks(HashSet<ITask> relevantTasks) {
		Set<ITask> scheduledTasks = taskActivityManager.getScheduledTasks(TaskActivityUtil.getCurrentWeek().getToday());
		for (Iterator<ITask> iterator = scheduledTasks.iterator(); iterator.hasNext();) {
			ITask scheduledTask = iterator.next();
			if (scheduledTask.isCompleted()) {
				iterator.remove();
			}
		}
		if (scheduledTasks.size() < MAX_NUM_TASKS) {
			relevantTasks.addAll(scheduledTasks);
		}
	}

	private ListMultimap<TaskRepository, ITask> mapTasksToRepository(HashSet<ITask> relevantTasks) {
		ListMultimap<TaskRepository, ITask> repositoryMap = ArrayListMultimap.create();
		for (ITask task : relevantTasks) {
			if (!(task instanceof LocalTask)) {
				String connectorKind = task.getConnectorKind();
				String repositoryUrl = task.getRepositoryUrl();
				TaskRepository taskRepository = repositoryManager.getRepository(connectorKind, repositoryUrl);
				repositoryMap.put(taskRepository, task);
			}
		}
		return repositoryMap;
	}
}
