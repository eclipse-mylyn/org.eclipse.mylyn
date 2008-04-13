/*******************************************************************************
 * Copyright (c) 2003, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.tasks.core;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataCollector;

/**
 * Collects QueryHits resulting from repository search.
 * 
 * @author Shawn Minto
 * @author Rob Elves (generalized from bugzilla)
 * @author Steffen Pingel
 * @since 2.0
 * @deprecated
 */
@Deprecated
public class QueryHitCollector extends AbstractTaskDataCollector {

	/**
	 * @deprecated Use {@link AbstractTaskDataCollector#MAX_HITS} instead
	 */
	@Deprecated
	public static final int MAX_HITS = AbstractTaskDataCollector.MAX_HITS;

	private final Set<AbstractTask> taskResults = new HashSet<AbstractTask>();

	private final ITaskFactory taskFactory;

	public QueryHitCollector(ITaskFactory taskFactory) {
		this.taskFactory = taskFactory;
	}

//	public void accept(AbstractTask task) {
//		if (task == null) {
//			throw new IllegalArgumentException();
//		}
//		if (taskResults.size() < MAX_HITS) {
//			taskResults.add(task);
//		}
//	}

	@Override
	public void accept(RepositoryTaskData taskData) {
		if (taskData == null) {
			throw new IllegalArgumentException();
		}

		AbstractTask task;
		try {
			task = taskFactory.createTask(taskData, new NullProgressMonitor());
			if (taskResults.size() < AbstractTaskDataCollector.MAX_HITS) {
				taskResults.add(task);
			}
		} catch (CoreException e) {
			// FIXMEx
			e.printStackTrace();
		}
	}

	public Set<AbstractTask> getTasks() {
		return taskResults;
	}

}
