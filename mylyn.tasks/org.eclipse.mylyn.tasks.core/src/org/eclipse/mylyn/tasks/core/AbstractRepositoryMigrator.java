/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
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

/**
 * Implement a connector specific repository migrator to perform connector specific migration procedures on task
 * repositories of the same connector kind upon startup. Override and contribute by the
 * org.eclipse.mylyn.tasks.ui.repositories extension point's taskListMigrator element.
 * 
 * @author Robert Elves
 * @since 3.4
 */
public abstract class AbstractRepositoryMigrator {

	public abstract String getConnectorKind();

	public boolean migrateRepository(TaskRepository repository) {
		return false;
	}

}
