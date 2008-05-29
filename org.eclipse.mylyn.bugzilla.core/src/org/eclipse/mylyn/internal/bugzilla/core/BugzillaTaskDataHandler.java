/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskAttribute;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class BugzillaTaskDataHandler extends AbstractTaskDataHandler {

	private final BugzillaRepositoryConnector connector;

	public BugzillaTaskDataHandler(BugzillaRepositoryConnector connector) {
		this.connector = connector;
	}

	public TaskData getTaskData(TaskRepository repository, String taskId, IProgressMonitor monitor)
			throws CoreException {
		try {
			BugzillaClient client = connector.getClientManager().getClient(repository,
					new SubProgressMonitor(monitor, IProgressMonitor.UNKNOWN));
			int bugId = BugzillaRepositoryConnector.getBugId(taskId);
			TaskData taskData;
			taskData = client.getTaskData(bugId, getAttributeMapper(repository), monitor);
			return taskData;

		} catch (IOException e) {
			throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.PLUGIN_ID,
					RepositoryStatus.ERROR_IO, repository.getRepositoryUrl(), e));
		}
	}

	@Override
	public void getMultiTaskData(TaskRepository repository, Set<String> taskIds, TaskDataCollector collector,
			IProgressMonitor monitor) throws CoreException {
		try {
			monitor.beginTask("Receiving tasks", taskIds.size());
			BugzillaClient client = connector.getClientManager().getClient(repository,
					new SubProgressMonitor(monitor, IProgressMonitor.UNKNOWN));

			client.getTaskData(taskIds, collector, getAttributeMapper(repository), monitor);
		} catch (IOException e) {
			throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.PLUGIN_ID,
					RepositoryStatus.ERROR_IO, repository.getRepositoryUrl(), e));
		} finally {
			monitor.done();
		}
	}

	@Override
	public RepositoryResponse postTaskData(TaskRepository repository, TaskData taskData,
			Set<TaskAttribute> changedAttributes, IProgressMonitor monitor) throws CoreException {
		try {
			BugzillaClient client = connector.getClientManager().getClient(repository,
					new SubProgressMonitor(monitor, IProgressMonitor.UNKNOWN));
			try {
				return client.postTaskData(taskData, monitor);
			} catch (CoreException e) {
				// TODO: Move retry handling into client
				if (e.getStatus().getCode() == RepositoryStatus.ERROR_REPOSITORY_LOGIN) {
					return client.postTaskData(taskData, monitor);
				} else {
					throw e;
				}

			}

		} catch (IOException e) {
			throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.PLUGIN_ID,
					RepositoryStatus.ERROR_IO, repository.getRepositoryUrl(), e));
		}
	}

	@Override
	public boolean initializeTaskData(TaskRepository repository, TaskData data, ITaskMapping initializationData,
			IProgressMonitor monitor) throws CoreException {
		if (initializationData == null) {
			return false;
		}
		String product = initializationData.getProduct();
		if (product == null) {
			return false;
		}
		return initializeTaskData(repository, data, product, monitor);
	}

	public boolean initializeTaskData(TaskRepository repository, TaskData data, String product, IProgressMonitor monitor)
			throws CoreException {
		RepositoryConfiguration repositoryConfiguration = BugzillaCorePlugin.getRepositoryConfiguration(repository,
				false, monitor);

		TaskAttribute productAttribute = createAttribute(data, BugzillaReportElement.PRODUCT);
		productAttribute.setValue(product);

		List<String> optionValues = repositoryConfiguration.getProducts();
		Collections.sort(optionValues);
		for (String optionValue : optionValues) {
			productAttribute.putOption(optionValue, optionValue);
		}

		TaskAttribute attributeStatus = createAttribute(data, BugzillaReportElement.BUG_STATUS);
		optionValues = repositoryConfiguration.getStatusValues();
		for (String option : optionValues) {
			attributeStatus.putOption(option, option);
		}

		attributeStatus.setValue(IBugzillaConstants.VALUE_STATUS_NEW);

		createAttribute(data, BugzillaReportElement.SHORT_DESC);

		TaskAttribute attributeVersion = createAttribute(data, BugzillaReportElement.VERSION);
		optionValues = repositoryConfiguration.getVersions(productAttribute.getValue());
		Collections.sort(optionValues);
		for (String option : optionValues) {
			attributeVersion.putOption(option, option);
		}
		if (optionValues.size() > 0) {
			attributeVersion.setValue(optionValues.get(optionValues.size() - 1));
		}

		TaskAttribute attributeComponent = createAttribute(data, BugzillaReportElement.COMPONENT);
		optionValues = repositoryConfiguration.getComponents(productAttribute.getValue());
		Collections.sort(optionValues);
		for (String option : optionValues) {
			attributeComponent.putOption(option, option);
		}
		if (optionValues.size() == 1) {
			attributeComponent.setValue(optionValues.get(0));
		}

		TaskAttribute attributePlatform = createAttribute(data, BugzillaReportElement.REP_PLATFORM);
		optionValues = repositoryConfiguration.getPlatforms();
		for (String option : optionValues) {
			attributePlatform.putOption(option, option);
		}
		if (optionValues.size() > 0) {
			// bug 159397 choose first platform: All
			attributePlatform.setValue(optionValues.get(0));
		}

		TaskAttribute attributeOPSYS = createAttribute(data, BugzillaReportElement.OP_SYS);
		optionValues = repositoryConfiguration.getOSs();
		for (String option : optionValues) {
			attributeOPSYS.putOption(option, option);
		}
		if (optionValues.size() > 0) {
			// bug 159397 change to choose first op_sys All
			attributeOPSYS.setValue(optionValues.get(0));
		}

		TaskAttribute attributePriority = createAttribute(data, BugzillaReportElement.PRIORITY);
		optionValues = repositoryConfiguration.getPriorities();
		for (String option : optionValues) {
			attributePriority.putOption(option, option);
		}
		if (optionValues.size() > 0) {
			// choose middle priority
			attributePriority.setValue(optionValues.get((optionValues.size() / 2)));
		}

		TaskAttribute attributeSeverity = createAttribute(data, BugzillaReportElement.BUG_SEVERITY);
		optionValues = repositoryConfiguration.getSeverities();
		for (String option : optionValues) {
			attributeSeverity.putOption(option, option);
		}
		if (optionValues.size() > 0) {
			// choose middle severity
			attributeSeverity.setValue(optionValues.get((optionValues.size() / 2)));
		}

		TaskAttribute attributeAssignedTo = createAttribute(data, BugzillaReportElement.ASSIGNED_TO);
		attributeAssignedTo.setValue("");
//		attributeAssignedTo.setReadOnly(false);

		TaskAttribute attributeBugFileLoc = createAttribute(data, BugzillaReportElement.BUG_FILE_LOC);
		attributeBugFileLoc.setValue("http://");
		//a.setHidden(false);

		createAttribute(data, BugzillaReportElement.DEPENDSON);
//		a.setValue("");
//		a.setReadOnly(false);
		createAttribute(data, BugzillaReportElement.BLOCKED);
//		a.setValue("");
//		a.setReadOnly(false);
		createAttribute(data, BugzillaReportElement.NEWCC);
//		a.setValue("");
//		a.setReadOnly(false);

		createAttribute(data, BugzillaReportElement.LONG_DESC);

		return true;
	}

	public static TaskAttribute createAttribute(TaskData data, BugzillaReportElement key) {
		return createAttribute(data.getRoot(), key);
	}

	public static TaskAttribute createAttribute(TaskAttribute parent, BugzillaReportElement key) {
		TaskAttribute attribute = parent.createAttribute(key.getKey());
		attribute.getMetaData()
				.defaults()
				.setReadOnly(key.isReadOnly())
				.setKind(key.getKind())
				.setLabel(key.toString())
				.setType(key.getType());
		return attribute;
	}

	private void addAttributeValue(TaskData data, BugzillaReportElement key, String value) {
		data.getRoot().getAttribute(key.getKey()).addValue(value);
	}

	@Override
	public boolean canGetMultiTaskData(TaskRepository taskRepository) {
		return true;
	}

	@Override
	public boolean canInitializeSubTaskData(TaskRepository taskRepository, ITask task) {
		return true;
	}

	@Override
	public boolean initializeSubTaskData(TaskRepository repository, TaskData subTaskData, TaskData parentTaskData,
			IProgressMonitor monitor) throws CoreException {
		TaskAttribute attributeProject = parentTaskData.getRoot().getMappedAttribute(TaskAttribute.PRODUCT);
		String product = attributeProject.getValue();
		initializeTaskData(repository, subTaskData, product, monitor);
		// TODO:
		//cloneTaskData(parentTaskData, subTaskData);
		TaskAttribute attributeBlocked = createAttribute(subTaskData, BugzillaReportElement.BLOCKED);
		attributeBlocked.setValue(parentTaskData.getTaskId());

		TaskAttribute parentAttributeAssigned = parentTaskData.getRoot()
				.getMappedAttribute(TaskAttribute.USER_ASSIGNED);
		TaskAttribute attributeAssigned = createAttribute(subTaskData, BugzillaReportElement.ASSIGNED_TO);
		attributeAssigned.setValue(parentAttributeAssigned.getValue());

		return true;
	}

	@Override
	public TaskAttributeMapper getAttributeMapper(TaskRepository taskRepository) {
		//client = connector.getClientManager().getClient(taskRepository, new NullProgressMonitor());
		return new BugzillaAttributeMapper(taskRepository);
	}

	//*************************** OLD API

	public Set<String> getSubTaskIds(RepositoryTaskData taskData) {
		Set<String> result = new HashSet<String>();
		RepositoryTaskAttribute attribute = taskData.getAttribute(BugzillaReportElement.DEPENDSON.getKey());
		if (attribute != null) {
			String[] ids = attribute.getValue().split(",");
			for (String id : ids) {
				id = id.trim();
				if (id.length() == 0) {
					continue;
				}
				result.add(id);
			}
		}
		return result;

	}

//	@Override
//	public AbstractAttributeFactory getAttributeFactory(RepositoryTaskData taskData) {
//		return getAttributeFactory(taskData.getRepositoryUrl(), taskData.getConnectorKind(), taskData.getTaskKind());
//	}

}
