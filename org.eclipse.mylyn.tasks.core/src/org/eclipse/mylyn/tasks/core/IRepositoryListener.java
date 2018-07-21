/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
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

import org.eclipse.jdt.annotation.NonNull;

/**
 * Notified of change to the life-cycle of task repositories.
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
	public abstract void repositoryAdded(@NonNull TaskRepository repository);

	/**
	 * A task repository has been removed.
	 * 
	 * @since 3.0
	 */
	public abstract void repositoryRemoved(@NonNull TaskRepository repository);

	/**
	 * The settings of a repository have been updated.
	 * 
	 * @since 3.0
	 */
	public abstract void repositorySettingsChanged(@NonNull TaskRepository repository);

	/**
	 * TODO: Refactor into general delta notification
	 * 
	 * @since 3.0
	 */
	public abstract void repositoryUrlChanged(@NonNull TaskRepository repository, @NonNull String oldUrl);

}
