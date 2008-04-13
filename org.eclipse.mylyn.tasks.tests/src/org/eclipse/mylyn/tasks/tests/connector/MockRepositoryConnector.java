/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests.connector;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.tasks.core.AbstractAttachmentHandler;
import org.eclipse.mylyn.tasks.core.AbstractAttributeFactory;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.RepositoryTemplate;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataCollector;
import org.eclipse.mylyn.tasks.core.sync.SynchronizationEvent;

/**
 * @author Mik Kersten
 * @author Rob Elves
 * @author Shawn Minto
 */
public class MockRepositoryConnector extends AbstractRepositoryConnector {

	public static final String REPOSITORY_KIND = "mock";

	public static final String REPOSITORY_URL = "http://mockrepository.test";

	private AbstractAttachmentHandler attachmentHandler;

	private boolean canQuery = false;

	private boolean canCreateNewTask = false;

	private boolean canCreateTaskFromKey = false;

	public void resetDefaults() {
		canQuery = false;
		canCreateNewTask = false;
		canCreateTaskFromKey = false;
	}

	public void setCanCreateNewTask(boolean canCreateNewTask) {
		this.canCreateNewTask = canCreateNewTask;
	}

	public void setCanCreateTaskFromKey(boolean canCreateTaskFromKey) {
		this.canCreateTaskFromKey = canCreateTaskFromKey;
	}

	public void setCanQuery(boolean canQuery) {
		this.canQuery = canQuery;
	}

	@Override
	public boolean canQuery(TaskRepository repository) {
		return canQuery;
	}

	@Override
	public boolean canCreateNewTask(TaskRepository repository) {
		return canCreateNewTask;
	}

	@Override
	public boolean canCreateTaskFromKey(TaskRepository repository) {
		return canCreateTaskFromKey;
	}

	@Override
	public AbstractAttachmentHandler getAttachmentHandler() {
		return attachmentHandler;
	}

	@Override
	public String getLabel() {
		return "Mock Repository (for unit tests)";
	}

	@Override
	public AbstractTaskDataHandler getTaskDataHandler() {
		// ignore
		return new AbstractTaskDataHandler() {

			@Override
			public AbstractAttributeFactory getAttributeFactory(String repositoryUrl, String repositoryKind,
					String taskKind) {
				// we don't care about the repository information right now
				return new MockAttributeFactory();
			}

			@Override
			public RepositoryTaskData getTaskData(TaskRepository repository, String taskId, IProgressMonitor monitor)
					throws CoreException {
				// ignore
				return null;
			}

			@Override
			public String postTaskData(TaskRepository repository, RepositoryTaskData taskData, IProgressMonitor monitor)
					throws CoreException {
				// ignore
				return null;
			}

			@Override
			public boolean initializeTaskData(TaskRepository repository, RepositoryTaskData data,
					IProgressMonitor monitor) throws CoreException {
				// ignore
				return false;
			}

			@Override
			public AbstractAttributeFactory getAttributeFactory(RepositoryTaskData taskData) {
				// ignore
				return new MockAttributeFactory();
			}

			@Override
			public Set<String> getSubTaskIds(RepositoryTaskData taskData) {
				return Collections.emptySet();
			}

		};
	}

	@Override
	public String getConnectorKind() {
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
	public String getTaskUrl(String repositoryUrl, String taskId) {
		return null;
	}

	@Override
	public void updateRepositoryConfiguration(TaskRepository repository, IProgressMonitor monitor) throws CoreException {
		// ignore
	}

	@Override
	public void updateTaskFromRepository(TaskRepository repository, AbstractTask repositoryTask,
			IProgressMonitor monitor) {
		// ignore
	}

	@Override
	public AbstractTask createTask(String repositoryUrl, String id, String summary) {
		// ignore
		return null;
	}

	@Override
	public boolean updateTaskFromTaskData(TaskRepository repository, AbstractTask repositoryTask,
			RepositoryTaskData taskData) {
		return false;
	}

	@Override
	public IStatus performQuery(TaskRepository repository, AbstractRepositoryQuery query,
			AbstractTaskDataCollector resultCollector, SynchronizationEvent event, IProgressMonitor monitor) {
		return Status.OK_STATUS;
	}

	public void setAttachmentHandler(AbstractAttachmentHandler attachmentHandler) {
		this.attachmentHandler = attachmentHandler;
	}

	@Override
	public Set<RepositoryTemplate> getTemplates() {
		Set<RepositoryTemplate> templates = new HashSet<RepositoryTemplate>();
		RepositoryTemplate template = new RepositoryTemplate("Mock Template", REPOSITORY_URL, "utf-8", "1", "new",
				"prefix", "query", "newAccountUrl", false, true);
		templates.add(template);
		return templates;
	}

	@Override
	public RepositoryTaskData getTaskData(TaskRepository repository, String taskId, IProgressMonitor monitor)
			throws CoreException {
		return null;
	}

}
