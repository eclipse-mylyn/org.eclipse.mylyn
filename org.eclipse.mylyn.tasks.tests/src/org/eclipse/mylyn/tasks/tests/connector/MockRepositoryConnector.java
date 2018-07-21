/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.tests.connector;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskMigrationEvent;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.core.sync.ISynchronizationSession;
import org.eclipse.mylyn.tasks.ui.TasksUi;

/**
 * @author Mik Kersten
 * @author Rob Elves
 * @author Shawn Minto
 * @author Steffen Pingel
 */
public class MockRepositoryConnector extends AbstractRepositoryConnector {

	public static final String CONNECTOR_KIND = "mock";

	/**
	 * @deprecated Use {@link #CONNECTOR_KIND} instead
	 */
	// TODO 3.5 remove
	@Deprecated
	public static final String REPOSITORY_KIND = CONNECTOR_KIND;

	public static final String REPOSITORY_URL = "http://mockrepository.test";

	public static MockRepositoryConnector getDefault() {
		return (MockRepositoryConnector) TasksUi.getRepositoryConnector(CONNECTOR_KIND);
	}

	private AbstractTaskAttachmentHandler attachmentHandler;

	private boolean canQuery;

	private boolean canCreateNewTask;

	private boolean canCreateTaskFromKey;

	private boolean hasLocalCompletionState;

	private String taskIdPrefix = "task";

	private TaskMigrationEvent taskMigrationEvent;

	public MockRepositoryConnector() {
		resetDefaults();
	}

	public void resetDefaults() {
		canQuery = false;
		canCreateNewTask = false;
		canCreateTaskFromKey = false;
		hasLocalCompletionState = false;
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

	public void setHasLocalCompletionState(boolean hasLocalCompletionState) {
		this.hasLocalCompletionState = hasLocalCompletionState;
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
		return CONNECTOR_KIND;
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
		boolean result = false;
		TaskAttribute summery = taskData.getRoot().getAttribute(TaskAttribute.SUMMARY);
		result = summery != null ? !task.getSummary().equals(summery.getValue()) : false;
		if (result) {
			return result;
		}
		TaskAttribute version = taskData.getRoot().getAttribute(TaskAttribute.VERSION);

		return version != null ? !version.getValue().equals(task.getAttribute(TaskAttribute.VERSION)) : false;

	}

	@Override
	public boolean hasLocalCompletionState(TaskRepository taskRepository, ITask task) {
		return hasLocalCompletionState;
	}

	@Override
	public void updateTaskFromTaskData(TaskRepository taskRepository, ITask task, TaskData taskData) {
		TaskAttribute summery = taskData.getRoot().getAttribute(TaskAttribute.SUMMARY);
		if (summery != null) {
			task.setSummary(summery.getValue());
		}
		TaskAttribute version = taskData.getRoot().getAttribute(TaskAttribute.VERSION);
		if (version != null) {
			task.setAttribute(TaskAttribute.VERSION, version.getValue());
		}
	}

//	@Override
//	public Set<RepositoryTemplate> getTemplates() {
//		Set<RepositoryTemplate> templates = new HashSet<RepositoryTemplate>();
//		RepositoryTemplate template = new RepositoryTemplate("Mock Template", REPOSITORY_URL, "utf-8", "1", "new",
//				"prefix", "query", "newAccountUrl", false, true);
//		templates.add(template);
//		return templates;
//	}

	@Override
	public String getTaskIdPrefix() {
		return taskIdPrefix;
	}

	public void setTaskIdPrefix(String taskIdPrefix) {
		this.taskIdPrefix = taskIdPrefix;
	}

	@Override
	public void migrateTask(TaskMigrationEvent event) {
		this.taskMigrationEvent = event;
	}

	public TaskMigrationEvent getTaskMigrationEvent() {
		return taskMigrationEvent;
	}

	public void setTaskMigrationEvent(TaskMigrationEvent taskMigrationEvent) {
		this.taskMigrationEvent = taskMigrationEvent;
	}

}
