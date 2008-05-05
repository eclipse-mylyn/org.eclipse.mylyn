/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
/*
 * Created on 19-Jan-2005
 */
package org.eclipse.mylyn.tasks.ui.editors;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorInputFactory;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;

/**
 * Input for task editors.
 * 
 * @author Eric Booth
 * @author Rob Elves
 * @author Mik Kersten
 * @author Steffen Pingel
 * @since 2.0
 */
public class TaskEditorInput implements IEditorInput, IPersistableElement {

	private static final int MAX_LABEL_LENGTH = 60;

	private final ITask task;

	private final TaskRepository taskRepository;

	private Object data;

	/**
	 * @since 3.0
	 */
	@Deprecated
	public TaskEditorInput(AbstractTask task, boolean newTask) {
		this(TasksUi.getRepositoryManager().getRepository(task.getConnectorKind(), task.getRepositoryUrl()), task);
	}

	/**
	 * @since 3.0
	 */
	public TaskEditorInput(TaskRepository taskRepository, ITask task) {
		Assert.isNotNull(taskRepository);
		Assert.isNotNull(task);
		this.taskRepository = taskRepository;
		this.task = task;
	}

	/**
	 * @since 2.0
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		TaskEditorInput other = (TaskEditorInput) obj;
		return task.equals(other.task);
	}

	/**
	 * @since 2.0
	 */
	public boolean exists() {
		return task != null;
	}

	/**
	 * @since 2.0
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		if (adapter == IEditorInput.class) {
			return this;
		} else if (adapter == AbstractTask.class) {
			return task;
		}
		return null;
	}

	/**
	 * @since 2.0
	 */
	public String getFactoryId() {
		return TaskEditorInputFactory.ID_FACTORY;
	}

	/**
	 * @since 2.0
	 */
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	/**
	 * @deprecated use {@link #getName()}
	 * @since 2.0
	 */
	@Deprecated
	public String getLabel() {
		return getName();
	}

	/**
	 * @since 2.0
	 */
	public String getName() {
		String toolTipText = getToolTipText();
		if (toolTipText == null) {
			return null;
		}

		if (task != null) {
			String taskKey = task.getTaskKey();
			if (taskKey != null) {
				return truncate(taskKey + ": " + toolTipText);
			}
		}
		return truncate(toolTipText);
	}

	/**
	 * @since 2.0
	 */
	public IPersistableElement getPersistable() {
		return this;
	}

	/**
	 * Returns the task if the task is in the task list; returns <code>null</code> otherwise.
	 * 
	 * @since 3.0
	 */
	public ITask getTask() {
		return task;
	}

	/**
	 * @since 3.0
	 */
	public TaskRepository getTaskRepository() {
		return taskRepository;
	}

	/**
	 * @since 2.0
	 */
	public String getToolTipText() {
		return task.getSummary();
	}

	/**
	 * @since 2.0
	 */
	@Override
	public int hashCode() {
		return task.getTaskId().hashCode();
	}

	/**
	 * @since 2.0
	 */
	@Deprecated
	public boolean isNewTask() {
		return false;
	}

	/**
	 * @since 2.0
	 */
	public void saveState(IMemento memento) {
		TaskEditorInputFactory.saveState(memento, this);
	}

	private String truncate(String description) {
		if (description == null || description.length() <= MAX_LABEL_LENGTH) {
			return description;
		} else {
			return description.substring(0, MAX_LABEL_LENGTH) + "...";
		}
	}

	/**
	 * @since 3.0
	 */
	public Object getData() {
		return data;
	}

	/**
	 * @since 3.0
	 */
	public void setData(Object data) {
		this.data = data;
	}

}
