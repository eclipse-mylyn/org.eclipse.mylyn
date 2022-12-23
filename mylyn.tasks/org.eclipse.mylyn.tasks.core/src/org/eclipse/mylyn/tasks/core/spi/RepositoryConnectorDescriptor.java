/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.core.spi;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryMigrator;
import org.eclipse.mylyn.tasks.core.AbstractTaskListMigrator;

/**
 * A descriptor for an {@link AbstractRepositoryConnector} instance.
 *
 * @since 3.10
 * @see RepositoryConnectorBranding
 */
public abstract class RepositoryConnectorDescriptor {

	/**
	 * Creates and returns a connector instance. Only invoked once.
	 *
	 * @return a connector instance
	 */
	@NonNull
	public abstract AbstractRepositoryConnector createRepositoryConnector();

	/**
	 * Creates and returns a task list migrator instance. Only invoked once.
	 *
	 * @return a migrator or null if no migrator is provided
	 */
	@Nullable
	@Deprecated
	public AbstractTaskListMigrator createTaskListMigrator() {
		return null;
	}

	/**
	 * Creates and returns a repository migrator instance. Only invoked once.
	 *
	 * @return a migrator or null if no migrator is provided
	 */
	@Nullable
	public abstract AbstractRepositoryMigrator createRepositoryMigrator();

}
