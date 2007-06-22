/*******************************************************************************
 * Copyright (c) 2003 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.tasks.core;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * Collects QueryHits resulting from repository search
 * 
 * @author Shawn Minto
 * @author Rob Elves (generalized from bugzilla)
 * @author Steffen Pingel
 */
public class QueryHitCollector implements ITaskCollector {

	public static final int MAX_HITS = 5000;

	public static final String MAX_HITS_REACHED = "Max allowed number of hits returned exceeded. Some hits may not be displayed. Please narrow query scope.";

	private final Set<AbstractTask> taskResults = new HashSet<AbstractTask>();

	private final ITaskFactory taskFactory;

	public QueryHitCollector(ITaskFactory taskFactory) {
		this.taskFactory = taskFactory;
	}

	public void accept(AbstractTask task) {
		if (task == null) {
			throw new IllegalArgumentException();
		}
		if (taskResults.size() < MAX_HITS) {
			taskResults.add(task);
		}
	}

	public void accept(RepositoryTaskData taskData) throws CoreException {
		if (taskData == null) {
			throw new IllegalArgumentException();
		}

		AbstractTask task = taskFactory.createTask(taskData, new NullProgressMonitor());
		if (taskResults.size() < MAX_HITS) {
			taskResults.add(task);
		}
	}

	public Set<AbstractTask> getTaskHits() {
		return taskResults;
	}

}
