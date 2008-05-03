/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

/**
 * Notified of change to task repositories.
 * 
 * @author Mik Kersten
 * @since 2.0
 */
// API 3.0 rename to ITaskRepositoryManagerListener
public interface ITaskRepositoryListener {

	// API 3.0 remove?
	public abstract void repositoriesRead();

	public abstract void repositoryAdded(TaskRepository repository);

	public abstract void repositoryRemoved(TaskRepository repository);

	public abstract void repositorySettingsChanged(TaskRepository repository);

	/**
	 * TODO: Refactor into general delta notification
	 * 
	 * @since 3.0
	 */
	public abstract void repositoryUrlChanged(TaskRepository repository, String oldUrl);

}
