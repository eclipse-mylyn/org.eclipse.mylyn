/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.index.ui;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.mylyn.internal.tasks.core.IRepositoryModelListener;
import org.eclipse.mylyn.internal.tasks.index.core.TaskListIndex;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;

/**
 * @author David Green
 * @author Steffen Pingel
 */
public class IndexReference extends AbstractIndexReference {

	private static TaskListIndex theIndex;

	private static AtomicInteger referenceCount = new AtomicInteger();

	private static IRepositoryModelListener listener = new IRepositoryModelListener() {
		public void loaded() {
			synchronized (IndexReference.class) {
				if (theIndex != null) {
					theIndex.setLocation(getDefaultIndexLocation());
				}
			}
		}
	};

	static File getDefaultIndexLocation() {
		return new File(TasksUiPlugin.getDefault().getDataDirectory(), ".taskListIndex"); //$NON-NLS-1$
	}

	/**
	 * When not null serves as flag indicating that theIndex is referenced, thus preventing bad behaviour if dispose is
	 * called multiple times.
	 */
	private TaskListIndex index;

	@Override
	public TaskListIndex index() {
		synchronized (IndexReference.class) {
			if (index == null) {
				if (theIndex == null && TasksUiPlugin.getTaskDataManager() != null) {
					final IRepositoryManager repositoryManager = TasksUiPlugin.getRepositoryManager();
					theIndex = new TaskListIndex(TasksUiPlugin.getTaskList(), TasksUiPlugin.getTaskDataManager(),
							repositoryManager, getDefaultIndexLocation());
					TasksUiPlugin.getDefault().addModelListener(listener);
				}
				index = theIndex;
				referenceCount.incrementAndGet();
			}
		}
		return index;
	}

	public void dispose() {
		synchronized (IndexReference.class) {
			if (index != null) {
				index = null;

				if (referenceCount.decrementAndGet() == 0) {
					TasksUiPlugin.getDefault().removeModelListener(listener);
					theIndex.close();
					theIndex = null;
				}
			}
		}
	}

}
