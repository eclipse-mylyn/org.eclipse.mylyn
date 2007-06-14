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
/*
 * Created on 19-Jan-2005
 */
package org.eclipse.mylyn.tasks.ui.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorInputFactory;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;

/**
 * @author Eric Booth
 * @author Rob Elves
 * @author Mik Kersten
 */
public class TaskEditorInput implements IEditorInput, IPersistableElement {

	private static final int MAX_LABEL_LENGTH = 60;

	private AbstractTask task;

	private String summary;

	private boolean newTask = false;

	public TaskEditorInput(AbstractTask task, boolean newTask) {
		this.newTask = newTask;
		this.task = task;
		summary = truncateDescription(task.getSummary());
	}

	private String truncateDescription(String description) {
		if (description == null || description.length() <= MAX_LABEL_LENGTH) {
			return description;
		} else {
			return description.substring(0, MAX_LABEL_LENGTH) + "...";
		}
	}

	public boolean exists() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
	 */
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IEditorInput#getName()
	 */
	public String getName() {
		return this.getLabel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IEditorInput#getPersistable()
	 */
	public IPersistableElement getPersistable() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IEditorInput#getToolTipText()
	 */
	public String getToolTipText() {
		return summary;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		if (adapter == IEditorInput.class) {
			return this;
		}
		return null;
	}

	/**
	 * @return Returns the task.
	 */
	public AbstractTask getTask() {
		return task;
	}

	/**
	 * @return Returns the label.
	 */
	public String getLabel() {
		if (task instanceof AbstractTask) {
			AbstractTask repositoryTask = (AbstractTask)task;
			String idLabel = repositoryTask.getTaskKey();
			
			summary = "";
			if (idLabel != null) {
				summary += idLabel + ": ";
			}
			summary += truncateDescription(task.getSummary());
		} else if (task != null){
			summary = truncateDescription(task.getSummary());
		} 
		return summary;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (newTask ? 1231 : 1237);
		result = prime * result + ((task == null) ? 0 : task.hashCode());
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
		final TaskEditorInput other = (TaskEditorInput) obj;
		if (newTask != other.newTask)
			return false;
		if (task == null) {
			if (other.task != null)
				return false;
		} else if (!task.equals(other.task))
			return false;
		return true;
	}

	public boolean isNewTask() {
		return newTask;
	}

	public String getFactoryId() {
		return TaskEditorInputFactory.ID_FACTORY;
	}

	public void saveState(IMemento memento) {
		TaskEditorInputFactory.saveState(memento, this);
	}
}
