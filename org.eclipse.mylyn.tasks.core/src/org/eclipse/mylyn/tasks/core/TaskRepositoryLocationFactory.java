/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.core;

import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryLocation;

/**
 * @since 2.2
 * @author Steffen Pingel
 */
public class TaskRepositoryLocationFactory {

	/**
	 * @since 3.0
	 */
	public AbstractWebLocation createWebLocation(final TaskRepository taskRepository) {
		return new TaskRepositoryLocation(taskRepository);
	}

}
