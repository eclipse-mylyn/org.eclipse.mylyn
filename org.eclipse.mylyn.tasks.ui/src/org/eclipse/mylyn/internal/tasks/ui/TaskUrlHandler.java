/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.ui;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.commons.workbench.EditorHandle;
import org.eclipse.mylyn.commons.workbench.browser.AbstractUrlHandler;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Steffen Pingel
 */
public class TaskUrlHandler extends AbstractUrlHandler {

	public TaskUrlHandler() {
		// ignore
	}

	@Override
	public EditorHandle openUrl(IWorkbenchPage page, String url, int customFlags) {
		Assert.isNotNull(url);
		AbstractTask task = TasksUiInternal.getTaskByUrl(url);
		if (task != null && !(task instanceof LocalTask)) {
			TaskRepository repository = TasksUi.getRepositoryManager().getRepository(task.getConnectorKind(),
					task.getRepositoryUrl());
			return TasksUiUtil.openTaskWithResult(repository, task.getTaskId());
		} else {
			AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager()
					.getConnectorForRepositoryTaskUrl(url);
			if (connector != null) {
				String repositoryUrl = connector.getRepositoryUrlFromTaskUrl(url);
				if (repositoryUrl != null) {
					String id = connector.getTaskIdFromTaskUrl(url);
					if (id != null) {
						TaskRepository repository = TasksUi.getRepositoryManager().getRepository(
								connector.getConnectorKind(), repositoryUrl);
						if (repository != null) {
							return TasksUiUtil.openTaskWithResult(repository, id);
						}
					}
				}
			}
		}
		return null;
	}

}
