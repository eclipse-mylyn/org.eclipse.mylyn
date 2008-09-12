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

package org.eclipse.mylyn.tasks.core.data;

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
	public static final int MAX_HITS = 5000;

	public abstract void accept(TaskData taskData);

}
