/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.commons.workbench.EditorHandle;
import org.eclipse.mylyn.commons.workbench.browser.AbstractUrlHandler;
import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Steffen Pingel
 */
public class GerritUrlHandler extends AbstractUrlHandler {

	// http://git.eclipse.org/r/123 or https://git.eclipse.org/r/#/c/123/
	private static final Pattern URL_PATTERN = Pattern.compile("/?(#/c)?/(\\d+)");

	public GerritUrlHandler() {
		// ignore
	}

	@Override
	public EditorHandle openUrl(IWorkbenchPage page, String url, int customFlags) {
		for (TaskRepository repository : TasksUi.getRepositoryManager().getRepositories(GerritConnector.CONNECTOR_KIND)) {
			String taskId = getTaskId(repository, url);
			if (taskId != null) {
				return TasksUiUtil.openTaskWithResult(repository, taskId);
			}
		}
		return null;
	}

	public String getTaskId(TaskRepository repository, String url) {
		if (url.startsWith(repository.getRepositoryUrl())) {
			String path = "/" + url.substring(repository.getRepositoryUrl().length());
			Matcher matcher = URL_PATTERN.matcher(path);
			if (matcher.find()) {
				return matcher.group(2);
			}
		}
		return null;
	}

}
