/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.activity.core;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.internal.tasks.index.core.TaskListIndex;
import org.eclipse.mylyn.internal.tasks.index.core.TaskListIndex.TaskCollector;
import org.eclipse.mylyn.internal.tasks.index.ui.IndexReference;
import org.eclipse.mylyn.tasks.activity.core.ActivityEvent;
import org.eclipse.mylyn.tasks.activity.core.ActivityScope;
import org.eclipse.mylyn.tasks.activity.core.TaskActivityScope;
import org.eclipse.mylyn.tasks.activity.core.spi.ActivityProvider;
import org.eclipse.mylyn.tasks.activity.core.spi.IActivitySession;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.osgi.util.NLS;

/**
 * @author Steffen Pingel
 * @author Timur Achmetow
 */
@SuppressWarnings("restriction")
public class TaskActivityProvider extends ActivityProvider {

	private static final String UNKNOWN = "Unknown"; //$NON-NLS-1$

	private IActivitySession session;

	@Override
	public void open(IActivitySession session) {
		this.session = session;
	}

	@Override
	public void query(ActivityScope scope, IProgressMonitor monitor) throws CoreException {
		if (scope instanceof TaskActivityScope) {
			ITask scopeTask = ((TaskActivityScope) scope).getTask();
			String url = scopeTask.getUrl();
			if (url != null) {
				GetAssociatedTasks collector = new GetAssociatedTasks(session);
				IndexReference reference = new IndexReference();
				try {
					TaskListIndex taskListIndex = reference.index();
					taskListIndex.find(NLS.bind("content:\"{0}\"", url), collector, 50); //$NON-NLS-1$
				} finally {
					reference.dispose();
				}
			}
		}
	}

	@Override
	public void close() {
	}

	private static class GetAssociatedTasks extends TaskCollector {
		private final IActivitySession session;

		public GetAssociatedTasks(IActivitySession session) {
			this.session = session;
		}

		@Override
		public void collect(ITask task) {

			Map<String, String> attrMap = new HashMap<String, String>();
			attrMap.put("author", getAuthor(task)); //$NON-NLS-1$
			attrMap.put("taskId", getTaskId(task)); //$NON-NLS-1$			
			ActivityEvent activityEvent = new ActivityEvent(task.getHandleIdentifier(), task.getConnectorKind(),
					task.getSummary(), task.getCreationDate(), attrMap);
			session.fireActivityEvent(activityEvent);
		}

		private String getTaskId(ITask task) {
			if (task.getTaskId() == null) {
				return UNKNOWN;
			}
			return task.getTaskId();
		}

		private String getAuthor(ITask task) {
			if (task.getOwner() == null) {
				return UNKNOWN;
			}
			return task.getOwner();
		}
	}
}
