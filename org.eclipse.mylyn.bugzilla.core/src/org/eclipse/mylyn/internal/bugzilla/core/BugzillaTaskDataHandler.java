/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
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
import org.eclipse.mylyn.tasks.core.data.TaskMapper;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class BugzillaTaskDataHandler extends AbstractTaskDataHandler {

	private enum TaskDataVersion {

		VERSION_0(0f) {
			@Override
			void migrate(TaskRepository repository, TaskData data) {
				// ignore
			}
		},

		VERSION_1_0(1.0f) {
			@Override
			void migrate(TaskRepository repository, TaskData data) {
				// 1: the value was stored in the attribute rather than the key
				for (TaskAttribute attribute : new ArrayList<TaskAttribute>(data.getRoot().getAttributes().values())) {
					if (attribute.getId().equals(BugzillaAttribute.DESC.getKey())) {
						TaskAttribute attrLongDesc = createAttribute(data, BugzillaAttribute.LONG_DESC);
						attrLongDesc.setValue(attribute.getValue());
						data.getRoot().removeAttribute(BugzillaAttribute.DESC.getKey());
					}
				}
				// Old actions not saved so recreate them upon migration
				// delete legacy operations:
				Set<TaskAttribute> operationsToRemove = new HashSet<TaskAttribute>();
				for (TaskAttribute attribute : data.getAttributeMapper().getAttributesByType(data,
						TaskAttribute.TYPE_OPERATION)) {
					operationsToRemove.add(attribute);
				}
				for (TaskAttribute taskAttribute : operationsToRemove) {
					data.getRoot().removeAttribute(taskAttribute.getId());
				}
				RepositoryConfiguration configuration = BugzillaCorePlugin.getRepositoryConfiguration(repository.getRepositoryUrl());
				if (configuration != null) {
					configuration.addValidOperations(data);
				}
			}
		},
		VERSION_2_0(2.0f) {
			@Override
			void migrate(TaskRepository repository, TaskData data) {
				updateAttribute(data, BugzillaAttribute.LONG_DESC);
			}
		},
		VERSION_3_0(3.0f) {
			@Override
			void migrate(TaskRepository repository, TaskData data) {
				updateAttribute(data, BugzillaAttribute.NEW_COMMENT);
			}
		},
		VERSION_4_0(4.0f) {
			@Override
			void migrate(TaskRepository repository, TaskData data) {
				updateAttribute(data, BugzillaAttribute.DEADLINE);
				updateAttribute(data, BugzillaAttribute.ACTUAL_TIME);
			}
		},
		VERSION_4_1(4.1f) {
			@Override
			void migrate(TaskRepository repository, TaskData data) {
				updateAttribute(data, BugzillaAttribute.VOTES);
				TaskAttribute attrDeadline = data.getRoot().getMappedAttribute(BugzillaAttribute.VOTES.getKey());
				if (attrDeadline != null) {
					attrDeadline.getMetaData().setType(BugzillaAttribute.VOTES.getType());
				}
			}
		},
		VERSION_4_2(4.2f) {
			@Override
			void migrate(TaskRepository repository, TaskData data) {
				updateAttribute(data, BugzillaAttribute.CC);
				updateAttribute(data, BugzillaAttribute.DEPENDSON);
				updateAttribute(data, BugzillaAttribute.BLOCKED);
				updateAttribute(data, BugzillaAttribute.BUG_FILE_LOC);
				updateAttribute(data, BugzillaAttribute.KEYWORDS);
				updateAttribute(data, BugzillaAttribute.STATUS_WHITEBOARD);
				updateAttribute(data, BugzillaAttribute.QA_CONTACT);
				updateAttribute(data, BugzillaAttribute.NEWCC);
			}
		},
		VERSION_4_3(4.3f) {
			@Override
			void migrate(TaskRepository repository, TaskData data) {
				// migrate custom attributes
				for (TaskAttribute attribute : data.getRoot().getAttributes().values()) {
					if (attribute.getId().startsWith(BugzillaCustomField.CUSTOM_FIELD_PREFIX)) {
						attribute.getMetaData().setKind(TaskAttribute.KIND_DEFAULT);
						attribute.getMetaData().setReadOnly(false);
						if (attribute.getOptions().size() > 0) {
							attribute.getMetaData().setType(TaskAttribute.TYPE_SINGLE_SELECT);
						} else {
							attribute.getMetaData().setType(TaskAttribute.TYPE_SHORT_TEXT);
						}
					}
				}
			}
		},
		VERSION_4_4(4.4f) {
			@Override
			void migrate(TaskRepository repository, TaskData data) {
				// summary didn't have spell checking, update to short rich text
				updateAttribute(data, BugzillaAttribute.SHORT_DESC);
			}
		},
		VERSION_CURRENT(4.5f) {
			@Override
			void migrate(TaskRepository repository, TaskData data) {
				data.setVersion(TaskDataVersion.VERSION_CURRENT.toString());
			}
		};

		private float versionNumber = 0;

		TaskDataVersion(float verNum) {
			versionNumber = verNum;
		}

		public float getVersionNum() {
			return versionNumber;
		}

		abstract void migrate(TaskRepository repository, TaskData data);

		@Override
		public String toString() {
			return "" + getVersionNum();
		}

		private static void updateAttribute(TaskData data, BugzillaAttribute bugAttribute) {
			TaskAttribute attribute = data.getRoot().getMappedAttribute(bugAttribute.getKey());
			if (attribute != null) {
				attribute.getMetaData().setType(bugAttribute.getType());
				attribute.getMetaData().setReadOnly(bugAttribute.isReadOnly());
				attribute.getMetaData().setKind(bugAttribute.getKind());
			}
		}
	}

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
			if (taskData == null) {
				throw new CoreException(new Status(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
						"Task data could not be retrieved. Please re-synchronize task"));
			}
			return taskData;

		} catch (IOException e) {
			throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
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
			throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
					RepositoryStatus.ERROR_IO, repository.getRepositoryUrl(), e));
		} finally {
			monitor.done();
		}
	}

	@Override
	public void migrateTaskData(TaskRepository taskRepository, TaskData taskData) {

		float bugzillaTaskDataVersion = 0;
		{
			String taskDataVersion = taskData.getVersion();
			if (taskDataVersion != null) {
				try {
					bugzillaTaskDataVersion = Float.parseFloat(taskDataVersion);
				} catch (NumberFormatException e) {
					bugzillaTaskDataVersion = 0;
				}
			}
		}

		for (TaskDataVersion version : TaskDataVersion.values()) {
			if (bugzillaTaskDataVersion <= version.getVersionNum()) {
				version.migrate(taskRepository, taskData);
			}
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
			throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
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

		data.setVersion(TaskDataVersion.VERSION_CURRENT.toString());

		RepositoryConfiguration repositoryConfiguration = BugzillaCorePlugin.getRepositoryConfiguration(repository,
				false, monitor);

		TaskAttribute productAttribute = createAttribute(data, BugzillaAttribute.PRODUCT);
		productAttribute.setValue(product);

		List<String> optionValues = repositoryConfiguration.getProducts();
		Collections.sort(optionValues);
		for (String optionValue : optionValues) {
			productAttribute.putOption(optionValue, optionValue);
		}

		TaskAttribute attributeStatus = createAttribute(data, BugzillaAttribute.BUG_STATUS);
		optionValues = repositoryConfiguration.getStatusValues();
		for (String option : optionValues) {
			attributeStatus.putOption(option, option);
		}

		attributeStatus.setValue(IBugzillaConstants.VALUE_STATUS_NEW);

		createAttribute(data, BugzillaAttribute.SHORT_DESC);

		TaskAttribute attributeVersion = createAttribute(data, BugzillaAttribute.VERSION);
		optionValues = repositoryConfiguration.getVersions(productAttribute.getValue());
		Collections.sort(optionValues);
		for (String option : optionValues) {
			attributeVersion.putOption(option, option);
		}
		if (optionValues.size() > 0) {
			attributeVersion.setValue(optionValues.get(optionValues.size() - 1));
		}

		TaskAttribute attributeComponent = createAttribute(data, BugzillaAttribute.COMPONENT);
		optionValues = repositoryConfiguration.getComponents(productAttribute.getValue());
		Collections.sort(optionValues);
		for (String option : optionValues) {
			attributeComponent.putOption(option, option);
		}
		if (optionValues.size() == 1) {
			attributeComponent.setValue(optionValues.get(0));
		}

		TaskAttribute attributePlatform = createAttribute(data, BugzillaAttribute.REP_PLATFORM);
		optionValues = repositoryConfiguration.getPlatforms();
		for (String option : optionValues) {
			attributePlatform.putOption(option, option);
		}
		if (optionValues.size() > 0) {
			// bug 159397 choose first platform: All
			attributePlatform.setValue(optionValues.get(0));
		}

		TaskAttribute attributeOPSYS = createAttribute(data, BugzillaAttribute.OP_SYS);
		optionValues = repositoryConfiguration.getOSs();
		for (String option : optionValues) {
			attributeOPSYS.putOption(option, option);
		}
		if (optionValues.size() > 0) {
			// bug 159397 change to choose first op_sys All
			attributeOPSYS.setValue(optionValues.get(0));
		}

		TaskAttribute attributePriority = createAttribute(data, BugzillaAttribute.PRIORITY);
		optionValues = repositoryConfiguration.getPriorities();
		for (String option : optionValues) {
			attributePriority.putOption(option, option);
		}
		if (optionValues.size() > 0) {
			// choose middle priority
			attributePriority.setValue(optionValues.get((optionValues.size() / 2)));
		}

		TaskAttribute attributeSeverity = createAttribute(data, BugzillaAttribute.BUG_SEVERITY);
		optionValues = repositoryConfiguration.getSeverities();
		for (String option : optionValues) {
			attributeSeverity.putOption(option, option);
		}
		if (optionValues.size() > 0) {
			// choose middle severity
			attributeSeverity.setValue(optionValues.get((optionValues.size() / 2)));
		}

		TaskAttribute attributeAssignedTo = createAttribute(data, BugzillaAttribute.ASSIGNED_TO);
		attributeAssignedTo.setValue("");

		TaskAttribute attributeBugFileLoc = createAttribute(data, BugzillaAttribute.BUG_FILE_LOC);
		attributeBugFileLoc.setValue("http://");

		createAttribute(data, BugzillaAttribute.DEPENDSON);
		createAttribute(data, BugzillaAttribute.BLOCKED);
		createAttribute(data, BugzillaAttribute.NEWCC);
		createAttribute(data, BugzillaAttribute.LONG_DESC);

		if (data.isNew()) {
			TaskAttribute attrDescription = data.getRoot().getMappedAttribute(TaskAttribute.DESCRIPTION);
			if (attrDescription != null) {
				attrDescription.getMetaData().setReadOnly(false);
			}
			TaskAttribute attrOwner = data.getRoot().getMappedAttribute(TaskAttribute.USER_ASSIGNED);
			if (attrOwner != null) {
				attrOwner.getMetaData().setReadOnly(false);
			}
			TaskAttribute attrAddSelfToCc = data.getRoot().getMappedAttribute(TaskAttribute.ADD_SELF_CC);
			if (attrAddSelfToCc != null) {
				attrAddSelfToCc.getMetaData().setKind(null);
			}
		}

		return true;
	}

	public static TaskAttribute createAttribute(TaskData data, BugzillaAttribute key) {
		return createAttribute(data.getRoot(), key);
	}

	public static TaskAttribute createAttribute(TaskAttribute parent, BugzillaAttribute key) {
		TaskAttribute attribute = parent.createAttribute(key.getKey());
		attribute.getMetaData()
				.defaults()
				.setReadOnly(key.isReadOnly())
				.setKind(key.getKind())
				.setLabel(key.toString())
				.setType(key.getType());
		return attribute;
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
		new TaskMapper(subTaskData).merge(new TaskMapper(parentTaskData));
		subTaskData.getRoot().getMappedAttribute(BugzillaAttribute.DEPENDSON.getKey()).setValue("");
		subTaskData.getRoot().getMappedAttribute(TaskAttribute.DESCRIPTION).setValue("");
		subTaskData.getRoot().getMappedAttribute(TaskAttribute.SUMMARY).setValue("");
		subTaskData.getRoot().getAttribute(BugzillaAttribute.BLOCKED.getKey()).setValue(parentTaskData.getTaskId());
		TaskAttribute parentAttributeAssigned = parentTaskData.getRoot()
				.getMappedAttribute(TaskAttribute.USER_ASSIGNED);
		subTaskData.getRoot().getAttribute(BugzillaAttribute.ASSIGNED_TO.getKey()).setValue(
				parentAttributeAssigned.getValue());
		return true;
	}

	@Override
	public TaskAttributeMapper getAttributeMapper(TaskRepository taskRepository) {
		return new BugzillaAttributeMapper(taskRepository);
	}

}
