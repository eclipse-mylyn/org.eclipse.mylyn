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

package org.eclipse.mylyn.internal.tasks.core;

import org.eclipse.mylyn.tasks.core.IRepositoryListener;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Rob Elves
 * @since 3.0
 */
public class TaskRepositoryAdapter implements IRepositoryListener {

	public void repositoryAdded(TaskRepository repository) {
		// ignore
	}

	public void repositoryRemoved(TaskRepository repository) {
		// ignore
	}

	public void repositorySettingsChanged(TaskRepository repository) {
		// ignore
	}

	public void repositoryUrlChanged(TaskRepository repository, String oldUrl) {
		// ignore
	}

}
