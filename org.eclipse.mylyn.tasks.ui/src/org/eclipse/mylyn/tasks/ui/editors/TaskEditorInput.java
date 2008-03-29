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
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorInputFactory;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
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

	private final AbstractTask task;

	private final String taskId;

	private final TaskRepository taskRepository;

	@Deprecated
	public TaskEditorInput(AbstractTask task, boolean newTask) {
		this(TasksUiPlugin.getRepositoryManager().getRepository(task.getConnectorKind(), task.getRepositoryUrl()), task);
	}

	public TaskEditorInput(TaskRepository taskRepository, AbstractTask task) {
		Assert.isNotNull(taskRepository);
		Assert.isNotNull(task);
		this.taskRepository = taskRepository;
		this.task = task;
		this.taskId = task.getTaskId();
	}

	public TaskEditorInput(TaskRepository taskRepository, String taskId) {
		Assert.isNotNull(taskRepository);
		Assert.isNotNull(taskId);
		this.taskRepository = taskRepository;
		this.taskId = taskId;
		this.task = null;
	}

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
		final TaskEditorInput other = (TaskEditorInput) obj;
		if (task != null) {
			return task.equals(other.task);
		} else {
			return taskRepository.equals(other.taskRepository) && taskId.equals(other.taskId);
		}
	}

	public boolean exists() {
		return task != null;
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		if (adapter == IEditorInput.class) {
			return this;
		}
		return null;
	}

	public String getFactoryId() {
		return TaskEditorInputFactory.ID_FACTORY;
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	/**
	 * @deprecated use {@link #getName()}
	 */
	@Deprecated
	public String getLabel() {
		return getName();
	}

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

	public IPersistableElement getPersistable() {
		return this;
	}

	/**
	 * Returns the task if the task is in the task list; returns <code>null</code> otherwise.
	 */
	public AbstractTask getTask() {
		return task;
	}

	public RepositoryTaskData getTaskData() {
		return TasksUiPlugin.getTaskDataManager().getNewTaskData(taskRepository.getUrl(), taskId);
	}

	public TaskRepository getTaskRepository() {
		return taskRepository;
	}

	public String getToolTipText() {
		if (task != null) {
			return task.getSummary();
		} else {
			RepositoryTaskData taskData = getTaskData();
			if (taskData != null) {
				return taskData.getSummary();
			}
		}
		return null;
	}

	@Override
	public int hashCode() {
		return taskId.hashCode();
	}

	@Deprecated
	public boolean isNewTask() {
		return false;
	}

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
}
