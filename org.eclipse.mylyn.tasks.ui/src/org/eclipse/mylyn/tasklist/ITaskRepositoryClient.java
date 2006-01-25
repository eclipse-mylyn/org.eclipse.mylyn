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

import java.util.List;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.mylar.internal.tasklist.TaskCategory;
import org.eclipse.mylar.internal.tasklist.ui.wizards.AbstractRepositorySettingsPage;

/**
 * Mixes UI with task creation, but OK for now.
 * 
 * @author Mik Kersten
 * @author Brock Janiczak
 */
public interface ITaskRepositoryClient {

	public abstract String getLabel();

	/**
	 * @return the unique type of the repository, e.g. "bugzilla"
	 */
	public abstract String getKind();


	public ITask getFromBugzillaTaskRegistry(String handle);
	
	public void addTaskToArchive(ITask newTask);
	
	public List<ITask> getArchiveTasks();
	
	public void setArchiveCategory(TaskCategory category);
	
	/**
	 * @param id
	 *            identifier, e.g. "123" bug Bugzilla bug 123
	 * @return null if task could not be created
	 */
	public abstract ITask createTaskFromExistingId(TaskRepository repository, String id);

	public abstract AbstractRepositorySettingsPage getSettingsPage();

	public abstract IWizard getQueryWizard(TaskRepository repository);

	public abstract void openEditQueryDialog(IRepositoryQuery query);

	public abstract IWizard getAddExistingTaskWizard(TaskRepository repository);
}
