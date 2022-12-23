/*******************************************************************************
 * Copyright (c) 2004, 2015 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.core.data;

import org.eclipse.core.runtime.IStatus;

/**
 * This class is used for collecting tasks, e.g. when performing queries on a repository.
 *
 * @author Rob Elves
 * @since 3.0
 */
public abstract class TaskDataCollector {

	/**
	 * @since 3.0
	 */
	@Deprecated
	public static final int MAX_HITS = 5000;

	public abstract void accept(TaskData taskData);

	/**
	 * @since 3.3
	 */
	public void failed(String taskId, IStatus status) {
	}

}
