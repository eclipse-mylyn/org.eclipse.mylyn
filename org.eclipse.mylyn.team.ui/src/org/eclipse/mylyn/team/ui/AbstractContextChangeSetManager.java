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

package org.eclipse.mylyn.team.ui;

import java.util.Set;

import org.eclipse.mylyn.context.core.AbstractContextListener;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.internal.tasks.core.ITaskListChangeListener;
import org.eclipse.mylyn.internal.tasks.core.TaskContainerDelta;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskListChangeAdapter;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.ITask;

/**
 * Manages changes sets along with task context and activation.
 * 
 * @author Mik Kersten
 * @since 2.0
 */
@SuppressWarnings("restriction")
public abstract class AbstractContextChangeSetManager extends AbstractContextListener {

	protected boolean isEnabled = false;

	private boolean isInitialized = false;

	public void enable() {
		if (!isEnabled) {
			isEnabled = true;
			TasksUiInternal.getTaskList().addChangeListener(WORKING_SET_LABEL_UPDATE_LISTENER);
			if (!isInitialized) {
				initContextChangeSets(); // otherwise listener will do it
			}

			if (ContextCore.getContextManager().isContextActive()) {
				contextActivated(ContextCore.getContextManager().getActiveContext());
			}
			ContextCore.getContextManager().addListener(this);
		}
	}

	public void disable() {
		ContextCore.getContextManager().removeListener(this);
		TasksUiInternal.getTaskList().removeChangeListener(WORKING_SET_LABEL_UPDATE_LISTENER);
		isEnabled = false;
	}

	protected abstract void initContextChangeSets();

	/**
	 * @since 3.0
	 */
	protected abstract void updateChangeSetLabel(ITask task);

	private final ITaskListChangeListener WORKING_SET_LABEL_UPDATE_LISTENER = new TaskListChangeAdapter() {

		@Override
		public void containersChanged(Set<TaskContainerDelta> containers) {
			for (TaskContainerDelta taskContainerDelta : containers) {
				if (taskContainerDelta.getKind() == TaskContainerDelta.Kind.ROOT && !isInitialized) {
					initContextChangeSets();
					isInitialized = true;
				}
				if (taskContainerDelta.getElement() instanceof ITask) {
					ITask task = (ITask) taskContainerDelta.getElement();
					switch (taskContainerDelta.getKind()) {
					case CONTENT:
						updateChangeSetLabel(task);
						break;
					}
				}
			}
		}
	};
}