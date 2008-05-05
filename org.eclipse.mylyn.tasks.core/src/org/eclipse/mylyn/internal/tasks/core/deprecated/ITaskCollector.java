/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.deprecated;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;

/**
 * Abstraction used for collecting tasks, e.g. when performing queries on the repository.
 * 
 * @author Rob Elves
 * @since 2.0
 * @deprecated use {@link TaskDataCollector} instead
 */
@Deprecated
public interface ITaskCollector {

	public void accept(ITask task);

	public void accept(RepositoryTaskData taskData) throws CoreException;

	public Set<AbstractTask> getTasks();
}
