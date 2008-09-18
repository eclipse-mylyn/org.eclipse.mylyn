/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests.connector;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentHandler;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.core.sync.ISynchronizationSession;

/**
 * @author Mik Kersten
 * @author Rob Elves
 * @author Shawn Minto
 * @author Steffen Pingel
 */
public class MockRepositoryConnector extends AbstractRepositoryConnector {

	// TODO 3.1 rename to CONNECTOR_KIND
	public static final String REPOSITORY_KIND = "mock";

	public static final String REPOSITORY_URL = "http://mockrepository.test";

	private AbstractTaskAttachmentHandler attachmentHandler;

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
	public String getLabel() {
		return "Mock Repository (for unit tests)";
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
	public IStatus performQuery(TaskRepository repository, IRepositoryQuery query, TaskDataCollector resultCollector,
			ISynchronizationSession event, IProgressMonitor monitor) {
		return Status.OK_STATUS;
	}

	@Override
	public AbstractTaskAttachmentHandler getTaskAttachmentHandler() {
		return attachmentHandler;
	}

	public void setTaskAttachmentHandler(MockAttachmentHandler attachmentHandler) {
		this.attachmentHandler = attachmentHandler;
	}

	@Override
	public TaskData getTaskData(TaskRepository taskRepository, String taskId, IProgressMonitor monitor)
			throws CoreException {
		return null;
	}

	@Override
	public boolean hasTaskChanged(TaskRepository taskRepository, ITask task, TaskData taskData) {
		return false;
	}

	@Override
	public void updateTaskFromTaskData(TaskRepository taskRepository, ITask task, TaskData taskData) {
	}

//	@Override
//	public Set<RepositoryTemplate> getTemplates() {
//		Set<RepositoryTemplate> templates = new HashSet<RepositoryTemplate>();
//		RepositoryTemplate template = new RepositoryTemplate("Mock Template", REPOSITORY_URL, "utf-8", "1", "new",
//				"prefix", "query", "newAccountUrl", false, true);
//		templates.add(template);
//		return templates;
//	}

}
