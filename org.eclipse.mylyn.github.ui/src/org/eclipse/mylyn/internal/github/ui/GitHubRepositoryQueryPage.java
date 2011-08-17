/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
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
