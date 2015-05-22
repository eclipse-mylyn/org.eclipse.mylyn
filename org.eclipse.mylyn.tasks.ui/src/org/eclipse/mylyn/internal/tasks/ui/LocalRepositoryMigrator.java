/*******************************************************************************
 * Copyright (c) 2010, 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryMigrator;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Robert Elves
 * @since 3.4
 */
public class LocalRepositoryMigrator extends AbstractRepositoryMigrator {

	@Override
	public String getConnectorKind() {
		return LocalRepositoryConnector.CONNECTOR_KIND;
	}

	@Override
	public boolean migrateRepository(TaskRepository repository) {
		if (repository.getCategory() == null) {
			repository.setCategory(TaskRepository.CATEGORY_TASKS);
			return true;
		}
		return false;
	}

}
