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

import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;

/** 
 * @author Mik Kersten
 * @author Rob Elves
 */
public class RepositoryTaskEditorInput extends AbstractTaskEditorInput {

	protected String id;

	protected String url;

	protected AbstractRepositoryTask repositoryTask = null;

//	// Called for existing report without a local task
//	public RepositoryTaskEditorInput(String url, TaskRepository repository, RepositoryTaskData newData, RepositoryTaskData oldData) {
//		super(repository, newData, oldData);
//		this.id = newData.getId();
//		this.url = url;
//	}

	public RepositoryTaskEditorInput(TaskRepository repository, String handle, String taskUrl) {
		super(repository, handle);
		this.id = AbstractRepositoryTask.getTaskId(handle);
		this.url = taskUrl;		
		ITask task = TasksUiPlugin.getTaskListManager().getTaskList().getTask(handle);
		if (task != null && task instanceof AbstractRepositoryTask) {
			this.repositoryTask = (AbstractRepositoryTask) task;
		}
	}

	public AbstractRepositoryTask getRepositoryTask() {
		return repositoryTask;
	}
	
//	/**
//	 * @return Returns the label.
//	 */
//	public String getLabel() {
//		if (repositoryTask != null) {			
//			String idLabel = repositoryTask.getIdLabel();
//			
//			label = "";
//			if (idLabel != null) {
//				label += idLabel + ": ";
//			}
//			label += truncateDescription(task.getDescription());
//		} else if (task != null){
//			label = truncateDescription(task.getDescription());
//		} 
//		return label;
//	}
	
	public String getName() {
		if(repositoryTask != null) {
			String idLabel = repositoryTask.getIdLabel();
			
			String label = "";
			if (idLabel != null) {
				label += idLabel + ": ";
			}
			label += repositoryTask.getSummary();
			return label;
			//return repositoryTask.getIdLabel();//getDescription();
		} else if (getTaskData() != null && getTaskData().getLabel() != null) {
			return getTaskData().getId()+": "+getTaskData().getLabel();
		} else if (id != null) {
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
		final RepositoryTaskEditorInput other = (RepositoryTaskEditorInput) obj;
		if (repositoryTask == null) {
			if (other.repositoryTask != null) {
				return false;
			} else if (other.getId() != this.getId()) {
				return false;
			}
		} else if (!repositoryTask.equals(other.repositoryTask))
			return false;
		return true;
	}

	/**
	 * @return url for the repositoryTask/hit. Used by MylarTaskEditor when
	 *         opening browser
	 */
	public String getUrl() {
		return url;
	}

}
