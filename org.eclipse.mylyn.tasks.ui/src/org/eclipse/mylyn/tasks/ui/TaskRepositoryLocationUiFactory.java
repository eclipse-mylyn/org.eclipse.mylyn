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

package org.eclipse.mylyn.tasks.ui;

import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.internal.tasks.ui.TaskRepositoryLocationUi;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskRepositoryLocationFactory;

/**
 * @since 2.2
 * @author Steffen Pingel
 */
public class TaskRepositoryLocationUiFactory extends TaskRepositoryLocationFactory {

	/**
	 * @since 3.0
	 */
	@Override
	public AbstractWebLocation createWebLocation(TaskRepository taskRepository) {
		return new TaskRepositoryLocationUi(taskRepository);
	}

}
