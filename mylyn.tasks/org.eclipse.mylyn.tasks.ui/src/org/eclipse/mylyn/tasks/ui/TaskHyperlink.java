/*******************************************************************************
 * Copyright (c) 2004, 2010 Eugene Kuleshov and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Eugene Kuleshov - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui;

import java.text.MessageFormat;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.mylyn.internal.tasks.ui.Messages;
import org.eclipse.mylyn.internal.tasks.ui.util.TaskOpenEvent;
import org.eclipse.mylyn.internal.tasks.ui.util.TaskOpenListener;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;

/**
 * Immutable. Encapsulates information for linking to tasks from text.
 * 
 * @author Eugene Kuleshov
 * @author Steffen Pingel
 */
public final class TaskHyperlink implements IHyperlink {

	private final IRegion region;

	private final TaskRepository repository;

	private final String taskId;

	private Object selection;

	public TaskHyperlink(IRegion region, TaskRepository repository, String taskId) {
		this.region = region;
		this.repository = repository;
		this.taskId = taskId;
	}

	public IRegion getHyperlinkRegion() {
		return region;
	}

	public String getTaskId() {
		return taskId;
	}

	public String getTypeLabel() {
		return null;
	}

	/**
	 * @Since 2.1
	 * @return
	 */
	public TaskRepository getRepository() {
		return repository;
	}

	public String getHyperlinkText() {
		return MessageFormat.format(Messages.TaskHyperlink_Open_Task_X_in_X, taskId, repository.getRepositoryLabel());
	}

	public void open() {
		if (repository != null) {
			TasksUiInternal.openTask(repository, taskId, new TaskOpenListener() {
				@Override
				public void taskOpened(TaskOpenEvent event) {
					if (selection == null) {
						return;
					}
					if (event.getEditor() instanceof TaskEditor) {
						TaskEditor editor = (TaskEditor) event.getEditor();
						editor.selectReveal(selection);
					}
				}
			});
		} else {
			MessageDialog.openError(null, "Mylyn", Messages.TaskHyperlink_Could_not_determine_repository_for_report); //$NON-NLS-1$
		}
	}

	/**
	 * Returns the selection to select and reveal when opening the task.
	 * 
	 * @return an object or null if not set
	 * @see #setSelection(Object)
	 * @see TaskEditor#selectReveal(Object)
	 * @since 3.4
	 */
	public Object getSelection() {
		return selection;
	}

	/**
	 * Sets the selection to select and reveal when opening the task.
	 * 
	 * @param selection
	 *            the selection
	 * @see #getSelection()
	 * @see TaskEditor#selectReveal(Object)
	 * @since 3.4
	 */
	public void setSelection(Object selection) {
		this.selection = selection;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((region == null) ? 0 : region.hashCode());
		result = prime * result + ((repository == null) ? 0 : repository.hashCode());
		result = prime * result + ((selection == null) ? 0 : selection.hashCode());
		result = prime * result + ((taskId == null) ? 0 : taskId.hashCode());
		return result;
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
		TaskHyperlink other = (TaskHyperlink) obj;
		if (region == null) {
			if (other.region != null) {
				return false;
			}
		} else if (!region.equals(other.region)) {
			return false;
		}
		if (repository == null) {
			if (other.repository != null) {
				return false;
			}
		} else if (!repository.equals(other.repository)) {
			return false;
		}
		if (selection == null) {
			if (other.selection != null) {
				return false;
			}
		} else if (!selection.equals(other.selection)) {
			return false;
		}
		if (taskId == null) {
			if (other.taskId != null) {
				return false;
			}
		} else if (!taskId.equals(other.taskId)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "TaskHyperlink [region=" + region + ", repository=" + repository + ", selection=" + selection //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				+ ", taskId=" + taskId + "]"; //$NON-NLS-1$ //$NON-NLS-2$
	}

}
