/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.mylyn.internal.github.ui;

import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositoryQueryPage;

/**
 * Base repository query page
 */
public abstract class GitHubRepositoryQueryPage extends
		AbstractRepositoryQueryPage {

	/**
	 * @param pageName
	 * @param taskRepository
	 */
	public GitHubRepositoryQueryPage(String pageName,
			TaskRepository taskRepository) {
		super(pageName, taskRepository);
	}

	/**
	 * @param pageName
	 * @param taskRepository
	 * @param query
	 */
	public GitHubRepositoryQueryPage(String pageName,
			TaskRepository taskRepository, IRepositoryQuery query) {
		super(pageName, taskRepository, query);
	}

	public void setPageComplete(boolean complete) {
		super.setPageComplete(complete);
		// Forward completeness to search container if applicable
		if (inSearchContainer())
			getSearchContainer().setPerformActionEnabled(complete);
	}

}
