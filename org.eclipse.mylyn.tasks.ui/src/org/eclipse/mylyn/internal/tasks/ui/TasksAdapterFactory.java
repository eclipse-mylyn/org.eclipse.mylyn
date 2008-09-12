/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;

/**
 * Factory for adapting objects to task elements.
 * 
 * @author Steffen Pingel
 */
public class TasksAdapterFactory implements IAdapterFactory {

	private static final Class<?>[] ADAPTER_LIST = new Class[] { ITask.class, TaskRepository.class };

	@SuppressWarnings("unchecked")
	public Class[] getAdapterList() {
		return ADAPTER_LIST;
	}

	public Object getAdapter(final Object adaptable, @SuppressWarnings("unchecked") Class adapterType) {
		if (adapterType == ITask.class) {
			if (adaptable instanceof TaskEditorInput) {
				return ((TaskEditorInput) adaptable).getTask();
			} else if (adaptable instanceof ITaskAttachment) {
				return ((ITaskAttachment) adaptable).getTask();
			}
		}
		if (adapterType == TaskRepository.class) {
			if (adaptable instanceof TaskEditorInput) {
				return ((TaskEditorInput) adaptable).getTaskRepository();
			} else if (adaptable instanceof ITaskAttachment) {
				return ((ITaskAttachment) adaptable).getTaskRepository();
			}
		}
		return null;
	}

}
