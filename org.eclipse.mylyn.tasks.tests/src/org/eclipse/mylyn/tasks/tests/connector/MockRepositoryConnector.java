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

package org.eclipse.mylyn.tasks.tests.connector;

import java.util.Collections;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.tasks.core.AbstractAttributeFactory;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylyn.tasks.core.IAttachmentHandler;
import org.eclipse.mylyn.tasks.core.ITaskDataHandler;
import org.eclipse.mylyn.tasks.core.QueryHitCollector;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class MockRepositoryConnector extends AbstractRepositoryConnector {

	public static final String REPOSITORY_KIND = "mock";

	public static final String REPOSITORY_URL = "http://mockrepository.test";

	private IAttachmentHandler attachmentHandler;

	@Override
	public boolean canCreateNewTask(TaskRepository repository) {
		// ignore
		return false;
	}

	@Override
	public boolean canCreateTaskFromKey(TaskRepository repository) {
		// ignore
		return false;
	}

	@Override
	public IAttachmentHandler getAttachmentHandler() {
		return attachmentHandler;
	}

	@Override
	public String getLabel() {
		return "Mock Repository (for unit tests)";
	}

	@Override
	public ITaskDataHandler getTaskDataHandler() {
		// ignore
		return new ITaskDataHandler() {

			public AbstractAttributeFactory getAttributeFactory(String repositoryUrl, String repositoryKind,
					String taskKind) {
				// we don't care about the repository information right now
				return new MockAttributeFactory();
			}

			public RepositoryTaskData getTaskData(TaskRepository repository, String taskId, IProgressMonitor monitor) throws CoreException {
				// ignore
				return null;
			}

			public String postTaskData(TaskRepository repository, RepositoryTaskData taskData, IProgressMonitor monitor) throws CoreException {
				// ignore
				return null;
			}

			public boolean initializeTaskData(TaskRepository repository, RepositoryTaskData data,
					IProgressMonitor monitor) throws CoreException {
				// ignore
				return false;
			}

			public AbstractAttributeFactory getAttributeFactory(RepositoryTaskData taskData) {
				// ignore
				return new MockAttributeFactory();
			}

			public Set<String> getSubTaskIds(RepositoryTaskData taskData) {
				return Collections.emptySet();
			}

		};
	}

	@Override
	public String getRepositoryType() {
		return REPOSITORY_KIND;
	}

	@Override
	public String getRepositoryUrlFromTaskUrl(String url) {
		// ignore
		return null;
	}

	@Override
	public String getTaskIdFromTaskUrl(String url) {
		// ignore
		return null;
	}

	@Override
	public String getTaskWebUrl(String repositoryUrl, String taskId) {
		return null;
	}

	@Override
	public void updateAttributes(TaskRepository repository, IProgressMonitor monitor) throws CoreException {
		// ignore
	}

	@Override
	public void updateTaskFromRepository(TaskRepository repository, AbstractRepositoryTask repositoryTask, IProgressMonitor monitor) {
		// ignore
	}

	@Override
	public Set<AbstractRepositoryTask> getChangedSinceLastSync(TaskRepository repository,
			Set<AbstractRepositoryTask> tasks, IProgressMonitor monitor) throws CoreException {
		return Collections.emptySet();
	}

	@Override
	public AbstractRepositoryTask createTask(String repositoryUrl, String id, String summary) {
		// ignore
		return null;
	}

	@Override
	public void updateTaskFromTaskData(TaskRepository repository, AbstractRepositoryTask repositoryTask,
			RepositoryTaskData taskData) {
		// ignore

	}

	@Override
	public IStatus performQuery(AbstractRepositoryQuery query, TaskRepository repository, IProgressMonitor monitor,
			QueryHitCollector resultCollector, boolean forced) {
		// ignore
		return null;
	}

	public void setAttachmentHandler(IAttachmentHandler attachmentHandler) {
		this.attachmentHandler = attachmentHandler;
	}
}
