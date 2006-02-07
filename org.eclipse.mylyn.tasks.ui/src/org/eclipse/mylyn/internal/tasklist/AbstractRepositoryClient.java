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

package org.eclipse.mylar.internal.tasklist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.mylar.internal.tasklist.ui.wizards.AbstractRepositorySettingsPage;

/**
 * @author Mik Kersten
 */
public abstract class AbstractRepositoryClient {

	private Map<String, ITask> archiveMap = new HashMap<String, ITask>();

	private static final int MAX_REFRESH_JOBS = 5;
	
	private TaskCategory archiveCategory = null;
	
	private List<AbstractRepositoryTask> toBeRefreshed = new LinkedList<AbstractRepositoryTask>();

	private Map<AbstractRepositoryTask, Job> currentlyRefreshing = new HashMap<AbstractRepositoryTask, Job>();
	
	public void requestRefresh(AbstractRepositoryTask task) {
		if (!currentlyRefreshing.containsKey(task) && !toBeRefreshed.contains(task)) {
			toBeRefreshed.add(task);
		}
		updateRefreshState();
	}

	public void removeTaskToBeRefreshed(AbstractRepositoryTask task) {
		toBeRefreshed.remove(task);
		if (currentlyRefreshing.get(task) != null) {
			currentlyRefreshing.get(task).cancel();
			currentlyRefreshing.remove(task);
		}
		updateRefreshState();
	}
	
	public void removeRefreshingTask(AbstractRepositoryTask task) {
		if (currentlyRefreshing.containsKey(task)) {
			currentlyRefreshing.remove(task);
		}
		updateRefreshState();
	}
	
	public void clearAllRefreshes() {
		toBeRefreshed.clear();
		List<Job> l = new ArrayList<Job>();
		l.addAll(currentlyRefreshing.values());
		for (Job j : l) {
			if (j != null)
				j.cancel();
		}
		currentlyRefreshing.clear();
	}

	private void updateRefreshState() {
		if (currentlyRefreshing.size() < MAX_REFRESH_JOBS && toBeRefreshed.size() > 0) {
			AbstractRepositoryTask bugzillaTask = toBeRefreshed.remove(0);
			Job refreshJob = synchronize(bugzillaTask, true, null);
			if (refreshJob != null) {
				currentlyRefreshing.put(bugzillaTask, refreshJob);
			}
		}
	}
	
	public abstract String getLabel();

	/**
	 * @return the unique type of the repository, e.g. "bugzilla"
	 */
	public abstract String getKind();

	/**
	 * @param id
	 *            identifier, e.g. "123" bug Bugzilla bug 123
	 * @return null if task could not be created
	 */
	public abstract ITask createTaskFromExistingId(TaskRepository repository, String id);

	/**
	 * Synchronize state with the repository (e.g. queries, task contents)
	 */
	public abstract void synchronize();
	
	/**
	 * @param listener		can be null
	 * @return TODO
	 */
	public abstract Job synchronize(ITask task, boolean forceUpdate, IJobChangeListener listener);

	public abstract void synchronize(AbstractRepositoryQuery repositoryQuery);
	
	public abstract AbstractRepositorySettingsPage getSettingsPage();

	public abstract IWizard getQueryWizard(TaskRepository repository);

	public abstract void openEditQueryDialog(AbstractRepositoryQuery query);

	public abstract IWizard getAddExistingTaskWizard(TaskRepository repository);

	public void addTaskToArchive(ITask newTask) {
		if (!archiveMap.containsKey(newTask.getHandleIdentifier())) {
			archiveMap.put(newTask.getHandleIdentifier(), newTask);
			if (archiveCategory != null) {
				archiveCategory.internalAddTask(newTask);
			}
		}
	}

	public ITask getTaskFromArchive(String handleIdentifier) {
		return archiveMap.get(handleIdentifier);
	}

	public List<ITask> getArchiveTasks() {
		List<ITask> archiveTasks = new ArrayList<ITask>();
		archiveTasks.addAll(archiveMap.values());
		return archiveTasks;
	}

	public void setArchiveCategory(TaskCategory category) {
		this.archiveCategory = category;
	}

	/**
	 * For testing.
	 */
	public void clearArchive() {
		archiveMap.clear();
	}
}
