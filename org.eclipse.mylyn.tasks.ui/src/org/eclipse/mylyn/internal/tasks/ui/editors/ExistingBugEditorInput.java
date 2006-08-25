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
package org.eclipse.mylar.internal.tasks.ui.editors;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;

/**
 * The <code>IEditorInput</code> implementation for
 * <code>ExistingBugEditor</code>.
 * 
 * @author Mik Kersten
 * @author Rob Elves
 */
public class ExistingBugEditorInput extends AbstractBugEditorInput {

	protected String id;
	protected String url;
	protected AbstractRepositoryTask repositoryTask = null;

	// Called for existing report without a local task
	public ExistingBugEditorInput(String url, TaskRepository repository, RepositoryTaskData taskData) {
		super(repository, taskData);
		this.id = taskData.getId();
		this.url = url;
	}

	public ExistingBugEditorInput(TaskRepository repository, RepositoryTaskData taskData, String bugId)
			throws IOException, GeneralSecurityException {
		super(repository, taskData);
		this.id = bugId;
		// this.repository = repository;
		// this.repositoryTaskData = getOfflineTaskData(repository,
		// proxySettings, bugId);

		String handle = AbstractRepositoryTask.getHandle(repository.getUrl(), bugId);
		ITask task = TasksUiPlugin.getTaskListManager().getTaskList().getTask(handle);
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
		if(repositoryTaskData != null && repositoryTaskData.getLabel() != null) {
			return repositoryTaskData.getLabel();
		} else if(id != null){
			return id;
		} else {
			return "<unknown>";
		}
	}

	/**
	 * @return The id of the bug for this editor input.
	 */
	public String getId() {
		return id;
	}

	
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((repositoryTask == null) ? 0 : repositoryTask.hashCode());
		return result;
	}
	

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ExistingBugEditorInput other = (ExistingBugEditorInput) obj;
		if (repositoryTask == null) {
			if (other.repositoryTask != null) {
				return false;
			} else if(other.getId() != this.getId()) {
				return false;
			}
		} else if (!repositoryTask.equals(other.repositoryTask))
			return false;
		return true;
	}

	
	/**
	 * @return url for the repositoryTask/hit. Used by MylarTaskEditor when opening browser
	 */
	public String getUrl() {
		return url;
	}

}
