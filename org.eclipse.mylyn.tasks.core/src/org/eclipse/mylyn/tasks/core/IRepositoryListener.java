/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

/**
 * Notified of change to the lifecycle of task repositories.
 * 
 * @author Mik Kersten
 * @since 3.0
 */
public interface IRepositoryListener {

	/**
	 * A task repository has been added.
	 * 
	 * @since 3.0
	 */
	public abstract void repositoryAdded(TaskRepository repository);

	/**
	 * A task repository has been removed.
	 * 
	 * @since 3.0
	 */
	public abstract void repositoryRemoved(TaskRepository repository);

	/**
	 * The settings of a repository have been updated.
	 * 
	 * @since 3.0
	 */
	public abstract void repositorySettingsChanged(TaskRepository repository);

	/**
	 * TODO: Refactor into general delta notification
	 * 
	 * @since 3.0
	 */
	public abstract void repositoryUrlChanged(TaskRepository repository, String oldUrl);

}
