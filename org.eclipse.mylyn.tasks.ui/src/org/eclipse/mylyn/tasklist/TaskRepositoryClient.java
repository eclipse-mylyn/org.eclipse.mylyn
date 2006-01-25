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

package org.eclipse.mylar.tasklist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.mylar.internal.tasklist.TaskCategory;
import org.eclipse.mylar.internal.tasklist.ui.wizards.AbstractRepositorySettingsPage;

/**
 * @author Mik Kersten
 */
public abstract class TaskRepositoryClient {

	private Map<String, ITask> archiveMap = new HashMap<String, ITask>();

	private TaskCategory archiveCategory = null;

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

	public abstract AbstractRepositorySettingsPage getSettingsPage();

	public abstract IWizard getQueryWizard(TaskRepository repository);

	public abstract void openEditQueryDialog(IRepositoryQuery query);

	public abstract IWizard getAddExistingTaskWizard(TaskRepository repository);

	public void addTaskToArchive(ITask newTask) {
		if (!archiveMap.containsKey(newTask.getHandleIdentifier())) {
			archiveMap.put(newTask.getHandleIdentifier(), newTask);
			if (archiveCategory != null) {
				archiveCategory.internalAddTask(newTask);
			}
		}
	}

	public ITask getTaskFromArchive(String handle) {
		return archiveMap.get(handle);
	}

	public List<ITask> getArchiveTasks() {
		List<ITask> archiveTasks = new ArrayList<ITask>();
		archiveTasks.addAll(archiveMap.values());
		return archiveTasks;
	}

	public void setArchiveCategory(TaskCategory category) {
		this.archiveCategory = category;
	}
}
