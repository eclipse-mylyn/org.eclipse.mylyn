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

package org.eclipse.mylyn.tasks.ui.editors;

import java.util.Set;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * @author Rob Elves (modifications)
 */
public abstract class AbstractRepositoryTaskEditorInput implements IEditorInput {

	protected String toolTipText = "";

	final protected TaskRepository repository;

	final private String handle;

	private RepositoryTaskData editableTaskData;

	private RepositoryTaskData oldTaskData;
	
	private Set<RepositoryTaskAttribute> oldEdits; 

	protected AbstractRepositoryTaskEditorInput(TaskRepository repository, String handle) {
		this.handle = handle;
		this.repository = repository;
		this.refreshInput();
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
		return editableTaskData;
	}

	/**
	 * returns the old task data
	 */
	public RepositoryTaskData getOldTaskData() {
		return oldTaskData;
	}

	public Set<RepositoryTaskAttribute> getOldEdits() {
		return oldEdits;
	}
	
	public ImageDescriptor getImageDescriptor() {
		return TasksUiImages.REPOSITORY_SMALL;
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

	protected void setEditableTaskData(RepositoryTaskData editableTaskData) {
		this.editableTaskData = editableTaskData;
	}

	protected void setOldTaskData(RepositoryTaskData oldTaskData) {
		this.oldTaskData = oldTaskData;
	}

	public void refreshInput() {
		this.editableTaskData = TasksUiPlugin.getDefault().getTaskDataManager().getEditableCopy(handle);
		this.oldTaskData = TasksUiPlugin.getDefault().getTaskDataManager().getOldTaskData(handle);
		this.oldEdits = TasksUiPlugin.getDefault().getTaskDataManager().getEdits(handle);
	}
}
