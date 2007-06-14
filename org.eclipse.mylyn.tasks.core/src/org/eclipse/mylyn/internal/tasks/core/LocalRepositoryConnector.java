/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

import java.util.Collections;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.IAttachmentHandler;
import org.eclipse.mylyn.tasks.core.ITaskDataHandler;
import org.eclipse.mylyn.tasks.core.QueryHitCollector;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Rob Elves
 */
public class LocalRepositoryConnector extends AbstractRepositoryConnector {

	public static final String REPOSITORY_LABEL = "Local Tasks";
	
	public static final String REPOSITORY_KIND = "local";

	public static final String REPOSITORY_URL = "local";

	public static final String REPOSITORY_VERSION = "1";

	public static final String DEFAULT_SUMMARY = "New Task";
	
	@Override
	public boolean canCreateNewTask(TaskRepository repository) {
		return true;
	}

	@Override
	public boolean canCreateTaskFromKey(TaskRepository repository) {
		return true;
	}

	@Override
	public AbstractTask createTask(String repositoryUrl, String id, String summary) {
		return new LocalTask(id, summary);
	}

	@Override
	public IAttachmentHandler getAttachmentHandler() {
		// TODO: Implement local attachments
		return null;
	}

	@Override
	public Set<AbstractTask> getChangedSinceLastSync(TaskRepository repository,
			Set<AbstractTask> tasks, IProgressMonitor monitor) throws CoreException {
		return Collections.emptySet();
	}

	@Override
	public String getLabel() {
		return "Local Task Repository";
	}

	@Override
	public String getRepositoryType() {
		return REPOSITORY_KIND;
	}

	@Override
	public String getRepositoryUrlFromTaskUrl(String taskFullUrl) {
		// ignore
		return null;
	}

	@Override
	public ITaskDataHandler getTaskDataHandler() {
		// not currently needed
		return null;
	}

	@Override
	public String getTaskIdFromTaskUrl(String taskFullUrl) {
		// ignore
		return null;
	}

	@Override
	public String getTaskWebUrl(String repositoryUrl, String taskId) {
		// ignore
		return null;
	}

	@Override
	public IStatus performQuery(AbstractRepositoryQuery query, TaskRepository repository, IProgressMonitor monitor,
			QueryHitCollector resultCollector, boolean forced) {
		// ignore
		return null;
	}

	@Override
	public void updateAttributes(TaskRepository repository, IProgressMonitor monitor) throws CoreException {
		// ignore

	}

	@Override
	public void updateTaskFromRepository(TaskRepository repository, AbstractTask repositoryTask,
			IProgressMonitor monitor) throws CoreException {
		// ignore

	}

	@Override
	public void updateTaskFromTaskData(TaskRepository repository, AbstractTask repositoryTask,
			RepositoryTaskData taskData) {
		// ignore

	}
	
	@Override
	public boolean isUserManaged() {
		return false;
	}

}
