/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants.BUGZILLA_OPERATION;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants.BUGZILLA_REPORT_STATUS;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants.BUGZILLA_RESOLUTION_2_0;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants.BUGZILLA_RESOLUTION_3_0;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.AbstractAttributeFactory;
import org.eclipse.mylyn.tasks.core.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.RepositoryOperation;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class BugzillaTaskDataHandler extends AbstractTaskDataHandler {

	private static final String OPERATION_INPUT_ASSIGNED_TO = "assigned_to";

	private static final String OPERATION_INPUT_DUP_ID = "dup_id";

	private static final String OPERATION_OPTION_RESOLUTION = "resolution";

	private static final String OPERATION_LABEL_CLOSE = "Mark as CLOSED";

	private static final String OPERATION_LABEL_VERIFY = "Mark as VERIFIED";

	private static final String OPERATION_LABEL_REOPEN = "Reopen bug";

	private static final String OPERATION_LABEL_REASSIGN_DEFAULT = "Reassign to default assignee";

	private static final String OPERATION_LABEL_REASSIGN = "Reassign to";

	private static final String OPERATION_LABEL_DUPLICATE = "Mark as duplicate of #";

	private static final String OPERATION_LABEL_RESOLVE = "Resolve as";

	private static final String OPERATION_LABEL_ACCEPT = "Accept (change status to ASSIGNED)";

	private AbstractAttributeFactory attributeFactory = new BugzillaAttributeFactory();

	private BugzillaRepositoryConnector connector;

	public BugzillaTaskDataHandler(BugzillaRepositoryConnector connector) {
		this.connector = connector;
	}

	@Override
	public RepositoryTaskData getTaskData(TaskRepository repository, String taskId, IProgressMonitor monitor)
			throws CoreException {
		try {
			BugzillaClient client = connector.getClientManager().getClient(repository);
			int bugId = BugzillaRepositoryConnector.getBugId(taskId);
			RepositoryTaskData taskData;
			try {
				taskData = client.getTaskData(bugId);
			} catch (CoreException e) {
				// TODO: Move retry handling into client
				if (e.getStatus().getCode() == RepositoryStatus.ERROR_REPOSITORY_LOGIN) {
					taskData = client.getTaskData(bugId);
				} else {
					throw e;
				}
			}
			if (taskData != null) {
				try {
					configureTaskData(repository, taskData);
				} catch (CoreException ce) {
					// retry since data retrieved may be corrupt
					taskData = client.getTaskData(bugId);
					if (taskData != null) {
						configureTaskData(repository, taskData);
					}
				}
				return taskData;
			}
			return null;

		} catch (IOException e) {
			throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.PLUGIN_ID,
					RepositoryStatus.ERROR_IO, repository.getUrl(), e));
		}
	}

	@Override
	public Set<RepositoryTaskData> getMultiTaskData(TaskRepository repository, Set<String> taskIds,
			IProgressMonitor monitor) throws CoreException {
		try {

			Set<RepositoryTaskData> result = new HashSet<RepositoryTaskData>();
			BugzillaClient client = connector.getClientManager().getClient(repository);
			try {
				Map<String, RepositoryTaskData> dataReturned = client.getTaskData(taskIds);
				for (RepositoryTaskData repositoryTaskData : dataReturned.values()) {
					result.add(repositoryTaskData);
				}
			} catch (CoreException e) {
				// TODO: Move retry handling into client
				if (e.getStatus().getCode() == RepositoryStatus.ERROR_REPOSITORY_LOGIN) {
					Map<String, RepositoryTaskData> dataReturned = client.getTaskData(taskIds);
					for (RepositoryTaskData repositoryTaskData : dataReturned.values()) {
						result.add(repositoryTaskData);
					}
				} else {
					throw e;
				}
			}
			for (RepositoryTaskData repositoryTaskData : result) {
				try {
					configureTaskData(repository, repositoryTaskData);
				} catch (CoreException ce) {
					// retry since data retrieved may be corrupt
					//taskData = client.getTaskData(bugId);
					//if (taskData != null) {
					configureTaskData(repository, repositoryTaskData);
					//	}
				}
			}
			return result;

		} catch (IOException e) {
			throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.PLUGIN_ID,
					RepositoryStatus.ERROR_IO, repository.getUrl(), e));
		}
	}

	@Override
	public String postTaskData(TaskRepository repository, RepositoryTaskData taskData, IProgressMonitor monitor)
			throws CoreException {
		try {
			BugzillaClient client = connector.getClientManager().getClient(repository);
			try {
				return client.postTaskData(taskData);
			} catch (CoreException e) {
				// TODO: Move retry handling into client
				if (e.getStatus().getCode() == RepositoryStatus.ERROR_REPOSITORY_LOGIN) {
					return client.postTaskData(taskData);
				} else {
					throw e;
				}

			}

		} catch (IOException e) {
			throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.PLUGIN_ID,
					RepositoryStatus.ERROR_IO, repository.getUrl(), e));
		}
	}

	@Override
	public AbstractAttributeFactory getAttributeFactory(String repositoryUrl, String repositoryKind, String taskKind) {
		// we don't care about the repository information right now
		return attributeFactory;
	}

	@Override
	public AbstractAttributeFactory getAttributeFactory(RepositoryTaskData taskData) {
		return getAttributeFactory(taskData.getRepositoryUrl(), taskData.getRepositoryKind(), taskData.getTaskKind());
	}

	public void configureTaskData(TaskRepository repository, RepositoryTaskData taskData) throws CoreException {
		connector.updateAttributeOptions(repository, taskData);
		addValidOperations(taskData, repository.getUserName(), repository);
	}

	private void addValidOperations(RepositoryTaskData bugReport, String userName, TaskRepository repository)
			throws CoreException {
		BUGZILLA_REPORT_STATUS status;
		try {
			status = BUGZILLA_REPORT_STATUS.valueOf(bugReport.getStatus());
		} catch (RuntimeException e) {
			StatusHandler.log(e, "Unrecognized status: " + bugReport.getStatus());
			status = BUGZILLA_REPORT_STATUS.NEW;
		}
		switch (status) {
		case UNCONFIRMED:
		case REOPENED:
		case NEW:
			addOperation(repository, bugReport, BUGZILLA_OPERATION.none, userName);
			addOperation(repository, bugReport, BUGZILLA_OPERATION.accept, userName);
			addOperation(repository, bugReport, BUGZILLA_OPERATION.resolve, userName);
			addOperation(repository, bugReport, BUGZILLA_OPERATION.duplicate, userName);
			break;
		case ASSIGNED:
			addOperation(repository, bugReport, BUGZILLA_OPERATION.none, userName);
			addOperation(repository, bugReport, BUGZILLA_OPERATION.resolve, userName);
			addOperation(repository, bugReport, BUGZILLA_OPERATION.duplicate, userName);
			break;
		case RESOLVED:
			addOperation(repository, bugReport, BUGZILLA_OPERATION.none, userName);
			addOperation(repository, bugReport, BUGZILLA_OPERATION.reopen, userName);
			addOperation(repository, bugReport, BUGZILLA_OPERATION.verify, userName);
			addOperation(repository, bugReport, BUGZILLA_OPERATION.close, userName);
			break;
		case CLOSED:
			addOperation(repository, bugReport, BUGZILLA_OPERATION.none, userName);
			addOperation(repository, bugReport, BUGZILLA_OPERATION.reopen, userName);
			break;
		case VERIFIED:
			addOperation(repository, bugReport, BUGZILLA_OPERATION.none, userName);
			addOperation(repository, bugReport, BUGZILLA_OPERATION.reopen, userName);
			addOperation(repository, bugReport, BUGZILLA_OPERATION.close, userName);
		}
		String bugzillaVersion;
		try {
			bugzillaVersion = BugzillaCorePlugin.getRepositoryConfiguration(repository, false).getInstallVersion();
		} catch (CoreException e1) {
			// ignore
			bugzillaVersion = "2.18";
		}
		if (bugzillaVersion.compareTo("3.1") < 0
				&& (status == BUGZILLA_REPORT_STATUS.NEW || status == BUGZILLA_REPORT_STATUS.ASSIGNED
						|| status == BUGZILLA_REPORT_STATUS.REOPENED || status == BUGZILLA_REPORT_STATUS.UNCONFIRMED)) {
			// old bugzilla workflow is used
			addOperation(repository, bugReport, BUGZILLA_OPERATION.reassign, userName);
			addOperation(repository, bugReport, BUGZILLA_OPERATION.reassignbycomponent, userName);
		}
	}

	private void addOperation(TaskRepository repository, RepositoryTaskData bugReport, BUGZILLA_OPERATION opcode,
			String userName) {
		RepositoryOperation newOperation = null;
		switch (opcode) {
		case none:
			newOperation = new RepositoryOperation(opcode.toString(), "Leave as " + bugReport.getStatus() + " "
					+ bugReport.getResolution());
			newOperation.setChecked(true);
			break;
		case accept:
			newOperation = new RepositoryOperation(opcode.toString(), OPERATION_LABEL_ACCEPT);
			break;
		case resolve:
			newOperation = new RepositoryOperation(opcode.toString(), OPERATION_LABEL_RESOLVE);
			newOperation.setUpOptions(OPERATION_OPTION_RESOLUTION);
			RepositoryConfiguration config;
			try {
				config = BugzillaCorePlugin.getRepositoryConfiguration(repository, false);
			} catch (CoreException e) {
				config = null;
			}
			if (config != null) {
				for (String resolution : config.getResolutions()) {
					// DUPLICATE and MOVED have special meanings so do not show as resolution
					if (resolution.compareTo("DUPLICATE") != 0 && resolution.compareTo("MOVED") != 0)
						newOperation.addOption(resolution, resolution);
				}
			} else {
				// LATER and REMIND must not be there in Bugzilla >= 3.0 is used
				//If getVersion() returns "Automatic (Use Validate Settings)" we use the Version 3 Resolution 
				if (repository.getVersion().compareTo("3.0") >= 0) {
					for (BUGZILLA_RESOLUTION_3_0 resolution : BUGZILLA_RESOLUTION_3_0.values()) {
						newOperation.addOption(resolution.toString(), resolution.toString());
					}
				} else {
					for (BUGZILLA_RESOLUTION_2_0 resolution : BUGZILLA_RESOLUTION_2_0.values()) {
						newOperation.addOption(resolution.toString(), resolution.toString());
					}
				}
			}
			break;
		case duplicate:
			newOperation = new RepositoryOperation(opcode.toString(), OPERATION_LABEL_DUPLICATE);
			newOperation.setInputName(OPERATION_INPUT_DUP_ID);
			newOperation.setInputValue("");
			break;
		case reassign:
			String localUser = userName;
			newOperation = new RepositoryOperation(opcode.toString(), OPERATION_LABEL_REASSIGN);
			newOperation.setInputName(OPERATION_INPUT_ASSIGNED_TO);
			newOperation.setInputValue(localUser);
			break;
		case reassignbycomponent:
			newOperation = new RepositoryOperation(opcode.toString(), OPERATION_LABEL_REASSIGN_DEFAULT);
			break;
		case reopen:
			newOperation = new RepositoryOperation(opcode.toString(), OPERATION_LABEL_REOPEN);
			break;
		case verify:
			newOperation = new RepositoryOperation(opcode.toString(), OPERATION_LABEL_VERIFY);
			break;
		case close:
			newOperation = new RepositoryOperation(opcode.toString(), OPERATION_LABEL_CLOSE);
			break;
		default:
			break;
		}
		if (newOperation != null) {
			bugReport.addOperation(newOperation);
		}
	}

	@Override
	public boolean initializeTaskData(TaskRepository repository, RepositoryTaskData data, IProgressMonitor monitor)
			throws CoreException {
		// Bugzilla needs a product to create task data
		return false;
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
				if (id.length() == 0)
					continue;
				result.add(id);
			}
		}
		return result;

	}

	@Override
	public boolean canGetMultiTaskData() {
		return true;
	}
	public void initializeSubTaskData(TaskRepository taskRepository, RepositoryTaskData taskData,
			RepositoryTaskData parentTaskData, IProgressMonitor monitor) throws CoreException {
		String project = parentTaskData.getProduct();
		taskData.setAttributeValue(RepositoryTaskAttribute.PRODUCT, project);
		BugzillaRepositoryConnector.setupNewBugAttributes(taskRepository, taskData);
		cloneTaskData(parentTaskData, taskData);
		taskData.setAttributeValue(BugzillaReportElement.BLOCKED.getKeyString(), parentTaskData.getId());
		taskData.setDescription("");
		taskData.setSummary("");
	}

}
