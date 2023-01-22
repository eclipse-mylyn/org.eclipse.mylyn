/*******************************************************************************
 * Copyright (c) 2010 Steffen Pingel and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Steffen Pingel - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core;

import org.eclipse.mylyn.tasks.core.AbstractRepositoryMigrator;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Steffen Pingel
 */
public class TracRepositoryMigrator extends AbstractRepositoryMigrator {

	@Override
	public String getConnectorKind() {
		return TracCorePlugin.CONNECTOR_KIND;
	}

	@Override
	public boolean migrateRepository(TaskRepository repository) {
		if (repository.getCategory() == null) {
			repository.setCategory(TaskRepository.CATEGORY_BUGS);
			return true;
		}
		return false;
	}

}
