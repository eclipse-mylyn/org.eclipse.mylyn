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

package org.eclipse.mylar.tasks.ui.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * @author Rob Elves (modifications)
 */
public abstract class AbstractTaskEditorInput implements IEditorInput {

	protected String toolTipText = "";

	final protected TaskRepository repository;

	private RepositoryTaskData newTaskData;
	
	private RepositoryTaskData oldTaskData;
	
	protected AbstractTaskEditorInput(TaskRepository repository, String handle) {
		this.repository = repository;
		this.newTaskData = TasksUiPlugin.getDefault().getTaskDataManager().getTaskData(handle);		
		this.oldTaskData = TasksUiPlugin.getDefault().getTaskDataManager().getOldTaskData(handle);				
	}
	
	/**
	 * Sets the tool tip text for this editor input.
	 * 
	 * @param str
	 *            The new tool tip text.
	 */
	protected void setToolTipText(String str) {
		// 03-20-03 Allows editor to store title (once it is known)
		toolTipText = str;
	}

	public boolean exists() {
		return true;
	}

	/**
	 * returns the new task data
	 */
	public RepositoryTaskData getTaskData() {
		return newTaskData;
	}
	
	/**
	 * returns the old task data
	 */
	public RepositoryTaskData getOldTaskData() {
		return oldTaskData;
	}
	

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public String getToolTipText() {
		return getName();
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		return null;
	}

	/**
	 * @return <code>true</code> if the argument is an editor input on the
	 *         same bug.
	 */
	@Override
	public abstract boolean equals(Object o);

	public TaskRepository getRepository() {
		return repository;
	}

	protected void setNewTaskData(RepositoryTaskData newTaskData) {
		this.newTaskData = newTaskData;
	}

	protected void setOldTaskData(RepositoryTaskData oldTaskData) {
		this.oldTaskData = oldTaskData;
	}
}
