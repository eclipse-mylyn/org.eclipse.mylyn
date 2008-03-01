/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui;

import java.util.List;

import org.eclipse.core.net.proxy.IProxyChangeEvent;
import org.eclipse.core.net.proxy.IProxyChangeListener;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskRepositoryManager;

/**
 * @author Nathan Hapke
 */
public class TasksUiProxyChangeListener implements IProxyChangeListener {
	private final TaskRepositoryManager taskRepositoryManager;

	public TasksUiProxyChangeListener(TaskRepositoryManager taskRepositoryManager) {
		this.taskRepositoryManager = taskRepositoryManager;
	}

	public void proxyInfoChanged(IProxyChangeEvent event) {
		List<TaskRepository> repos = taskRepositoryManager.getAllRepositories();
		for (TaskRepository repo : repos) {
			if (repo.isDefaultProxyEnabled()) {
				taskRepositoryManager.notifyRepositorySettingsChanged(repo);
			}
		}
	}
}
