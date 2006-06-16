/*******************************************************************************
 * Copyright (c) 2003 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.internal.tasklist.ui.editors;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.eclipse.mylar.internal.tasklist.RepositoryTaskData;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;

/**
 * The <code>IEditorInput</code> implementation for
 * <code>ExistingBugEditor</code>.
 * 
 * @author Mik Kersten
 * @author Rob Elves
 */
public class ExistingBugEditorInput extends AbstractBugEditorInput {

	protected int bugId;

	protected AbstractRepositoryTask repositoryTask = null;

	// Called for existing report without a local task
	public ExistingBugEditorInput(TaskRepository repository, RepositoryTaskData taskData) {
		super(repository, taskData);
		this.bugId = taskData.getId();
	}

	public ExistingBugEditorInput(TaskRepository repository, RepositoryTaskData taskData, int bugId)
			throws IOException, GeneralSecurityException {
		super(repository, taskData);
		this.bugId = bugId;
		// this.repository = repository;
		// this.repositoryTaskData = getOfflineTaskData(repository,
		// proxySettings, bugId);

		String handle = AbstractRepositoryTask.getHandle(repository.getUrl(), bugId);
		ITask task = MylarTaskListPlugin.getTaskListManager().getTaskList().getTask(handle);
		if (task != null && task instanceof AbstractRepositoryTask) {
			this.repositoryTask = (AbstractRepositoryTask) task;
		}
	}

	public AbstractRepositoryTask getRepositoryTask() {
		return repositoryTask;
	}

	// // TODO: move?
	// private RepositoryTaskData getOfflineTaskData(final TaskRepository
	// repository, Proxy proxySettings, final int id)
	// throws IOException, GeneralSecurityException {
	// RepositoryTaskData result = null;
	// // Look among the offline reports for a bug with the given id.
	// OfflineTaskManager reportsFile =
	// MylarTaskListPlugin.getDefault().getOfflineReportsFile();
	// if (reportsFile != null) {
	// int offlineId = reportsFile.find(repository.getUrl(), id);
	// // If an offline bug was found, return it if possible.
	// if (offlineId != -1) {
	// RepositoryTaskData bug = reportsFile.elements().get(offlineId);
	// if (bug instanceof RepositoryTaskData) {
	// result = (RepositoryTaskData) bug;
	// }
	// }
	// }
	// return result;
	// }

	public String getName() {
		return repositoryTaskData.getLabel();
	}

	/**
	 * @return The id of the bug for this editor input.
	 */
	public int getBugId() {
		return bugId;
	}

	/**
	 * @return <code>true</code> if the argument is a bug report editor input
	 *         on the same bug id.
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof ExistingBugEditorInput) {
			ExistingBugEditorInput input = (ExistingBugEditorInput) o;
			return getBugId() == input.getBugId();
		}
		return false;
	}

}
