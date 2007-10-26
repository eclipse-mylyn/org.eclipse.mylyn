/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryLocation;
import org.eclipse.mylyn.web.core.AbstractWebLocation;

/**
 * @since 2.2
 * @author Steffen Pingel
 */
public class TaskRepositoryLocationFactory {

	public AbstractWebLocation createWebLocation(final TaskRepository taskRepository) {
		return new TaskRepositoryLocation(taskRepository);
	}

}
