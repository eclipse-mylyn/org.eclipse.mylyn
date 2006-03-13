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

package org.eclipse.mylar.internal.tasklist.planner;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.tasklist.planner.ui.TaskPlannerWizardPage;
import org.eclipse.mylar.provisional.tasklist.AbstractQueryHit;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryQuery;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.AbstractTaskContainer;
import org.eclipse.mylar.provisional.tasklist.ITaskListElement;
import org.eclipse.mylar.provisional.tasklist.Task;
import org.eclipse.mylar.provisional.tasklist.TaskList;

/**
 * @author Ken Sueda
 * @author Mik Kersten
 * @author Rob Elves (scope report to specific categories and queries)
 */
public class TaskReportGenerator implements IRunnableWithProgress {

	private boolean finished;

	private TaskList tasklist = null;

	private List<ITaskCollector> collectors = new ArrayList<ITaskCollector>();

	private List<ITask> tasks = new ArrayList<ITask>();

	private Set<ITaskListElement> filterCategories;

	public TaskReportGenerator(TaskList tlist) {
		this(tlist, null);
	}

	public TaskReportGenerator(TaskList tlist, Set<ITaskListElement> filterCategories) {
		tasklist = tlist;
		this.filterCategories = filterCategories != null ? filterCategories : new HashSet<ITaskListElement>();
	}

	public void addCollector(ITaskCollector collector) {
		collectors.add(collector);
	}

	public void collectTasks() {
		try {
			run(new NullProgressMonitor());
		} catch (InvocationTargetException e) {
			// operation was canceled
		} catch (InterruptedException e) {
			MylarStatusHandler.log(e, "Could not collect tasks");
		}
	}

	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		Set<ITaskListElement> rootElements;
		if (filterCategories.size() == 0) {
			rootElements = tasklist.getRootElements();
		} else if(filterCategories.contains(TaskPlannerWizardPage.ROOT_CATEGORY_HACK)) {
			// TODO: Remove when root category issues fixed
			rootElements = new HashSet<ITaskListElement>(tasklist.getRootTasks());
			filterCategories.remove(TaskPlannerWizardPage.ROOT_CATEGORY_HACK);
			rootElements.addAll(filterCategories);
		} else {
			rootElements = filterCategories;
		}

		int estimatedItemsToProcess = rootElements.size();
		monitor.beginTask("Mylar Task Planner", estimatedItemsToProcess);

		for (Object element : rootElements) {
			monitor.worked(1);
			if (element instanceof AbstractTaskContainer) {
				AbstractTaskContainer cat = (AbstractTaskContainer) element;
				for (ITask task : cat.getChildren())
					for (ITaskCollector collector : collectors) {
						collector.consumeTask(task);
					}

			} else if (element instanceof Task) {
				Task task = (Task) element;
				for (ITaskCollector collector : collectors) {
					collector.consumeTask(task);
				}

			} else if (element instanceof AbstractRepositoryQuery) {
				// process queries
				AbstractRepositoryQuery repositoryQuery = (AbstractRepositoryQuery) element;
				for (AbstractQueryHit hit : repositoryQuery.getHits()) {
					ITask correspondingTask = hit.getCorrespondingTask();
					if (correspondingTask != null) {
						for (ITaskCollector collector : collectors) {
							collector.consumeTask(correspondingTask);
						}
					}
				}
			} 
		}
		// Put the results all into one list (tasks)
		for (ITaskCollector collector : collectors) {
			tasks.addAll(collector.getTasks());
		}
		finished = true;
		monitor.done();
	}

	public List<ITask> getAllCollectedTasks() {
		return tasks;
	}

	public boolean isFinished() {
		return finished;
	}
}
