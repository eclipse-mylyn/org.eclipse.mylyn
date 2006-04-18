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
package org.eclipse.mylar.internal.tasklist.ui.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * @author Eric Booth
 */
public class TaskEditorInput implements IEditorInput {

	private static final int MAX_LABEL_LENGTH = 60;

	private ITask task;

	private String id;

	private String label;

	private boolean newTask = false;
	
	public TaskEditorInput(ITask task, boolean newTask) {
		this.task = task;
		this.newTask = newTask;
		id = task.getHandleIdentifier();
		label = truncateDescription(task.getDescription());
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
		return "Task #" + id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IEditorInput#getPersistable()
	 */
	public IPersistableElement getPersistable() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IEditorInput#getToolTipText()
	 */
	public String getToolTipText() {
		return label;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		return null;
	}

	/**
	 * @return Returns the task.
	 */
	public ITask getTask() {
		return task;
	}

	/**
	 * @return Returns the id.
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return Returns the label.
	 */
	public String getLabel() {
		label = truncateDescription(task.getDescription());
		return label;
	}

	/**
	 * Returns true if the argument is a bug report editor input on the same bug
	 * id.
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof TaskEditorInput) {
			TaskEditorInput input = (TaskEditorInput) o;
			return getId() != null && getId().equals(input.getId());
		}
		return false;
	}

	public boolean isNewTask() {
		return newTask;
	}
}
