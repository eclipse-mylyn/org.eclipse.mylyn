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
import org.eclipse.mylyn.internal.tasks.core.deprecated.AbstractAttributeFactory;
import org.eclipse.mylyn.internal.tasks.core.deprecated.AbstractTaskDataHandler;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskAttribute;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class BugzillaTaskDataHandler extends AbstractTaskDataHandler {

	private final AbstractAttributeFactory attributeFactory = new BugzillaAttributeFactory();

	private final BugzillaRepositoryConnector connector;

	public BugzillaTaskDataHandler(BugzillaRepositoryConnector connector) {
		this.connector = connector;
	}

	@Override
	public RepositoryTaskData getTaskData(TaskRepository repository, String taskId, IProgressMonitor monitor)
			throws CoreException {
		try {
			BugzillaClient client = connector.getClientManager().getClient(repository,
					new SubProgressMonitor(monitor, IProgressMonitor.UNKNOWN));
			int bugId = BugzillaRepositoryConnector.getBugId(taskId);
			RepositoryTaskData taskData;
			taskData = client.getTaskData(bugId, monitor);
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
			client.getTaskData(taskIds, collector, monitor);
		} catch (IOException e) {
			throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.PLUGIN_ID,
					RepositoryStatus.ERROR_IO, repository.getRepositoryUrl(), e));
		} finally {
			monitor.done();
		}
	}

	@Override
	public String postTaskData(TaskRepository repository, RepositoryTaskData taskData, IProgressMonitor monitor)
			throws CoreException {
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
	public AbstractAttributeFactory getAttributeFactory(String repositoryUrl, String repositoryKind, String taskKind) {
		// we don't care about the repository information right now
		return attributeFactory;
	}

	@Override
	public AbstractAttributeFactory getAttributeFactory(RepositoryTaskData taskData) {
		return getAttributeFactory(taskData.getRepositoryUrl(), taskData.getConnectorKind(), taskData.getTaskKind());
	}

	@Override
	public boolean initializeTaskData(TaskRepository repository, RepositoryTaskData data, IProgressMonitor monitor)
			throws CoreException {

		if (data == null) {
			return false;
		}
		String product = data.getProduct();
		if (product.equals("")) {
			// Bugzilla needs a product to create task data
			// If I see it right the product is never an empty String.
			// but to be save I return false as before bug# 213077
			return false;
		}
		data.removeAllAttributes();

		RepositoryConfiguration repositoryConfiguration = BugzillaCorePlugin.getRepositoryConfiguration(repository,
				false, monitor);

		RepositoryTaskAttribute a = BugzillaClient.makeNewAttribute(BugzillaReportElement.PRODUCT);
		List<String> optionValues = repositoryConfiguration.getProducts();
		Collections.sort(optionValues);
		a.setValue(product);
		a.setReadOnly(true);

		data.addAttribute(BugzillaReportElement.PRODUCT.getKeyString(), a);

		a = BugzillaClient.makeNewAttribute(BugzillaReportElement.BUG_STATUS);
		optionValues = repositoryConfiguration.getStatusValues();
		for (String option : optionValues) {
			a.addOption(option, option);
		}
		a.setValue(IBugzillaConstants.VALUE_STATUS_NEW);

		data.addAttribute(BugzillaReportElement.BUG_STATUS.getKeyString(), a);

		a = BugzillaClient.makeNewAttribute(BugzillaReportElement.SHORT_DESC);
		data.addAttribute(BugzillaReportElement.SHORT_DESC.getKeyString(), a);

		a = BugzillaClient.makeNewAttribute(BugzillaReportElement.VERSION);
		optionValues = repositoryConfiguration.getVersions(data.getProduct());
		Collections.sort(optionValues);
		for (String option : optionValues) {
			a.addOption(option, option);
		}
		if (optionValues.size() > 0) {
			a.setValue(optionValues.get(optionValues.size() - 1));
		}

		data.addAttribute(BugzillaReportElement.VERSION.getKeyString(), a);

		a = BugzillaClient.makeNewAttribute(BugzillaReportElement.COMPONENT);
		optionValues = repositoryConfiguration.getComponents(data.getProduct());
		Collections.sort(optionValues);
		for (String option : optionValues) {
			a.addOption(option, option);
		}
		if (optionValues.size() == 1) {
			a.setValue(optionValues.get(0));
		}

		data.addAttribute(BugzillaReportElement.COMPONENT.getKeyString(), a);

		a = BugzillaClient.makeNewAttribute(BugzillaReportElement.REP_PLATFORM);
		optionValues = repositoryConfiguration.getPlatforms();
		for (String option : optionValues) {
			a.addOption(option, option);
		}
		if (optionValues.size() > 0) {
			// bug 159397 choose first platform: All
			a.setValue(optionValues.get(0));
		}

		data.addAttribute(BugzillaReportElement.REP_PLATFORM.getKeyString(), a);

		a = BugzillaClient.makeNewAttribute(BugzillaReportElement.OP_SYS);
		optionValues = repositoryConfiguration.getOSs();
		for (String option : optionValues) {
			a.addOption(option, option);
		}
		if (optionValues.size() > 0) {
			// bug 159397 change to choose first op_sys All
			a.setValue(optionValues.get(0));
		}

		data.addAttribute(BugzillaReportElement.OP_SYS.getKeyString(), a);

		a = BugzillaClient.makeNewAttribute(BugzillaReportElement.PRIORITY);
		optionValues = repositoryConfiguration.getPriorities();
		for (String option : optionValues) {
			a.addOption(option, option);
		}
		if (optionValues.size() > 0) {
			a.setValue(optionValues.get((optionValues.size() / 2))); // choose middle priority
		}

		data.addAttribute(BugzillaReportElement.PRIORITY.getKeyString(), a);

		a = BugzillaClient.makeNewAttribute(BugzillaReportElement.BUG_SEVERITY);
		optionValues = repositoryConfiguration.getSeverities();
		for (String option : optionValues) {
			a.addOption(option, option);
		}
		if (optionValues.size() > 0) {
			a.setValue(optionValues.get((optionValues.size() / 2))); // choose middle severity
		}

		data.addAttribute(BugzillaReportElement.BUG_SEVERITY.getKeyString(), a);

		a = BugzillaClient.makeNewAttribute(BugzillaReportElement.ASSIGNED_TO);
		a.setValue("");
		a.setReadOnly(false);

		data.addAttribute(BugzillaReportElement.ASSIGNED_TO.getKeyString(), a);

		a = BugzillaClient.makeNewAttribute(BugzillaReportElement.BUG_FILE_LOC);
		a.setValue("http://");
		a.setHidden(false);

		data.addAttribute(BugzillaReportElement.BUG_FILE_LOC.getKeyString(), a);
		a = BugzillaClient.makeNewAttribute(BugzillaReportElement.DEPENDSON);
		a.setValue("");
		a.setReadOnly(false);
		data.addAttribute(BugzillaReportElement.DEPENDSON.getKeyString(), a);
		a = BugzillaClient.makeNewAttribute(BugzillaReportElement.BLOCKED);
		a.setValue("");
		a.setReadOnly(false);
		data.addAttribute(BugzillaReportElement.BLOCKED.getKeyString(), a);
		a = BugzillaClient.makeNewAttribute(BugzillaReportElement.NEWCC);
		a.setValue("");
		a.setReadOnly(false);
		data.addAttribute(BugzillaReportElement.NEWCC.getKeyString(), a);
		return true;
	}

	// TODO: Move to AbstractTaskDataHandler
	@Override
	public Set<String> getSubTaskIds(RepositoryTaskData taskData) {
		Set<String> result = new HashSet<String>();
		RepositoryTaskAttribute attribute = taskData.getAttribute(BugzillaReportElement.DEPENDSON.getKeyString());
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

	@Override
	public boolean canGetMultiTaskData() {
		return true;
	}

	@Override
	public boolean initializeSubTaskData(TaskRepository taskRepository, RepositoryTaskData taskData,
			RepositoryTaskData parentTaskData, IProgressMonitor monitor) throws CoreException {
		String project = parentTaskData.getProduct();
		taskData.setAttributeValue(RepositoryTaskAttribute.PRODUCT, project);
		initializeTaskData(taskRepository, taskData, monitor);
		cloneTaskData(parentTaskData, taskData);
		taskData.setAttributeValue(BugzillaReportElement.BLOCKED.getKeyString(), parentTaskData.getTaskId());
		taskData.setAttributeValue(RepositoryTaskAttribute.USER_ASSIGNED, parentTaskData.getAssignedTo());
		taskData.setDescription("");
		taskData.setSummary("");
		return true;
	}

	@Override
	public boolean canInitializeSubTaskData(ITask task, RepositoryTaskData parentTaskData) {
		return true;
	}

}
