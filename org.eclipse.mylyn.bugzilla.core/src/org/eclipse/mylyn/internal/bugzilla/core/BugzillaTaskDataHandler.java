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
import org.eclipse.mylyn.commons.net.Policy;
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
		VERSION_4_5(4.5f) {
			@Override
			void migrate(TaskRepository repository, TaskData data) {
				// migrate custom attributes
				RepositoryConfiguration configuration = BugzillaCorePlugin.getRepositoryConfiguration(repository.getRepositoryUrl());

				if (configuration == null) {
					return;
				}

				for (TaskAttribute attribute : data.getRoot().getAttributes().values()) {
					if (attribute.getId().startsWith(BugzillaCustomField.CUSTOM_FIELD_PREFIX)) {

						BugzillaCustomField customField = null;
						String actName = attribute.getId();
						for (BugzillaCustomField bugzillaCustomField : configuration.getCustomFields()) {
							if (actName.equals(bugzillaCustomField.getName())) {
								customField = bugzillaCustomField;
								break;
							}
						}
						if (customField != null) {
							String desc = customField.getDescription();
							attribute.getMetaData().defaults().setLabel(desc).setReadOnly(false);
							attribute.getMetaData().setKind(TaskAttribute.KIND_DEFAULT);
							attribute.getMetaData().setType(TaskAttribute.TYPE_SHORT_TEXT);
							switch (customField.getType()) {
							case 1: // Free Text
								attribute.getMetaData().setType(TaskAttribute.TYPE_SHORT_TEXT);
								break;
							case 2: // Drop Down
								attribute.getMetaData().setType(TaskAttribute.TYPE_SINGLE_SELECT);
								break;
							case 3: // Multiple-Selection Box
								attribute.getMetaData().setType(TaskAttribute.TYPE_MULTI_SELECT);
								break;
							case 4: // Large Text Box
								attribute.getMetaData().setType(TaskAttribute.TYPE_LONG_TEXT);
								break;
							case 5: // Date/Time
								attribute.getMetaData().setType(TaskAttribute.TYPE_DATETIME);
								break;

							default:
								List<String> options = customField.getOptions();
								if (options.size() > 0) {
									attribute.getMetaData().setType(TaskAttribute.TYPE_SINGLE_SELECT);
								} else {
									attribute.getMetaData().setType(TaskAttribute.TYPE_SHORT_TEXT);
								}
							}
							attribute.getMetaData().setReadOnly(false);
						}
					}
				}
			}
		},
		VERSION_CURRENT(4.6f) {
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

		Set<String> taskIds = new HashSet<String>();
		taskIds.add(taskId);
		final TaskData[] retrievedData = new TaskData[1];
		TaskDataCollector collector = new TaskDataCollector() {

			@Override
			public void accept(TaskData taskData) {
				retrievedData[0] = taskData;
			}
		};
		getMultiTaskData(repository, taskIds, collector, monitor);

		if (retrievedData[0] == null) {
			throw new CoreException(new Status(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
					"Task data could not be retrieved. Please re-synchronize task"));
		}
		return retrievedData[0];

//		monitor = Policy.monitorFor(monitor);
//		try {
//			monitor.beginTask("Receiving task", IProgressMonitor.UNKNOWN);
//			BugzillaClient client = connector.getClientManager().getClient(repository, monitor);
//			int bugId = BugzillaRepositoryConnector.getBugId(taskId);
//			TaskData taskData = client.getTaskData(bugId, getAttributeMapper(repository), monitor);
//			if (taskData == null) {
//				throw new CoreException(new Status(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
//						"Task data could not be retrieved. Please re-synchronize task"));
//			}
//			return taskData;
//		} catch (IOException e) {
//			throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
//					RepositoryStatus.ERROR_IO, repository.getRepositoryUrl(), e));
//		} finally {
//			monitor.done();
//		}
	}

	@Override
	public void getMultiTaskData(final TaskRepository repository, Set<String> taskIds,
			final TaskDataCollector collector, IProgressMonitor monitor) throws CoreException {

		monitor = Policy.monitorFor(monitor);

		try {
			monitor.beginTask("Receiving tasks", taskIds.size());
			BugzillaClient client = connector.getClientManager().getClient(repository, monitor);
			final CoreException[] collectionException = new CoreException[1];

			class CollectorWrapper extends TaskDataCollector {

				private final IProgressMonitor monitor2;

				private final TaskDataCollector collector;

				public CollectorWrapper(TaskDataCollector collector, IProgressMonitor monitor2) {
					this.collector = collector;
					this.monitor2 = monitor2;
				}

				@Override
				public void accept(TaskData taskData) {
					try {
						initializeTaskData(repository, taskData, null, new SubProgressMonitor(monitor2, 1));
					} catch (CoreException e) {
						if (collectionException[0] == null) {
							collectionException[0] = e;
						}
					}
					collector.accept(taskData);
					monitor2.worked(1);
				}
			}

			TaskDataCollector collector2 = new CollectorWrapper(collector, monitor);

			client.getTaskData(taskIds, collector2, getAttributeMapper(repository), monitor);

			if (collectionException[0] != null) {
				throw collectionException[0];
			}
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
		monitor = Policy.monitorFor(monitor);
		try {
			monitor.beginTask("Submitting task", IProgressMonitor.UNKNOWN);
			BugzillaClient client = connector.getClientManager().getClient(repository, monitor);
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
		} finally {
			monitor.done();
		}
	}

	@Override
	public boolean initializeTaskData(TaskRepository repository, TaskData taskData, ITaskMapping initializationData,
			IProgressMonitor monitor) throws CoreException {

		// Note: setting current version to latest assumes the data arriving here is either for a new task or is
		// fresh from the repository (not locally stored data that may not have been migrated).
		taskData.setVersion(TaskDataVersion.VERSION_CURRENT.toString());

		RepositoryConfiguration repositoryConfiguration = BugzillaCorePlugin.getRepositoryConfiguration(repository,
				false, monitor);

		if (repositoryConfiguration == null) {
			return false;
		}

		if (taskData.isNew()) {

			if (initializationData == null) {
				return false;
			}

			String product = initializationData.getProduct();
			if (product == null) {
				return false;
			}
			return initializeNewTaskDataAttributes(repositoryConfiguration, taskData, product, monitor);
		} else {
			repositoryConfiguration.configureTaskData(taskData);
		}
		return true;
	}

	/**
	 * Only new, unsubmitted task data or freshly received task data from the repository can be passed in here.
	 */
	private boolean initializeNewTaskDataAttributes(RepositoryConfiguration repositoryConfiguration, TaskData taskData,
			String product, IProgressMonitor monitor) {

		TaskAttribute productAttribute = createAttribute(taskData, BugzillaAttribute.PRODUCT);
		productAttribute.setValue(product);

		List<String> optionValues = repositoryConfiguration.getProducts();
		Collections.sort(optionValues);
		for (String optionValue : optionValues) {
			productAttribute.putOption(optionValue, optionValue);
		}

		TaskAttribute attributeStatus = createAttribute(taskData, BugzillaAttribute.BUG_STATUS);
		optionValues = repositoryConfiguration.getStatusValues();
		for (String option : optionValues) {
			attributeStatus.putOption(option, option);
		}

		attributeStatus.setValue(IBugzillaConstants.VALUE_STATUS_NEW);

		createAttribute(taskData, BugzillaAttribute.SHORT_DESC);

		TaskAttribute attributeVersion = createAttribute(taskData, BugzillaAttribute.VERSION);
		optionValues = repositoryConfiguration.getVersions(productAttribute.getValue());
		Collections.sort(optionValues);
		for (String option : optionValues) {
			attributeVersion.putOption(option, option);
		}
		if (optionValues.size() > 0) {
			attributeVersion.setValue(optionValues.get(optionValues.size() - 1));
		}

		TaskAttribute attributeComponent = createAttribute(taskData, BugzillaAttribute.COMPONENT);
		optionValues = repositoryConfiguration.getComponents(productAttribute.getValue());
		Collections.sort(optionValues);
		for (String option : optionValues) {
			attributeComponent.putOption(option, option);
		}
		if (optionValues.size() == 1) {
			attributeComponent.setValue(optionValues.get(0));
		}

		TaskAttribute attributePlatform = createAttribute(taskData, BugzillaAttribute.REP_PLATFORM);
		optionValues = repositoryConfiguration.getPlatforms();
		for (String option : optionValues) {
			attributePlatform.putOption(option, option);
		}
		if (optionValues.size() > 0) {
			// bug 159397 choose first platform: All
			attributePlatform.setValue(optionValues.get(0));
		}

		TaskAttribute attributeOPSYS = createAttribute(taskData, BugzillaAttribute.OP_SYS);
		optionValues = repositoryConfiguration.getOSs();
		for (String option : optionValues) {
			attributeOPSYS.putOption(option, option);
		}
		if (optionValues.size() > 0) {
			// bug 159397 change to choose first op_sys All
			attributeOPSYS.setValue(optionValues.get(0));
		}

		TaskAttribute attributePriority = createAttribute(taskData, BugzillaAttribute.PRIORITY);
		optionValues = repositoryConfiguration.getPriorities();
		for (String option : optionValues) {
			attributePriority.putOption(option, option);
		}
		if (optionValues.size() > 0) {
			// choose middle priority
			attributePriority.setValue(optionValues.get((optionValues.size() / 2)));
		}

		TaskAttribute attributeSeverity = createAttribute(taskData, BugzillaAttribute.BUG_SEVERITY);
		optionValues = repositoryConfiguration.getSeverities();
		for (String option : optionValues) {
			attributeSeverity.putOption(option, option);
		}
		if (optionValues.size() > 0) {
			// choose middle severity
			attributeSeverity.setValue(optionValues.get((optionValues.size() / 2)));
		}

		TaskAttribute attributeAssignedTo = createAttribute(taskData, BugzillaAttribute.ASSIGNED_TO);
		attributeAssignedTo.setValue("");

		TaskAttribute attributeBugFileLoc = createAttribute(taskData, BugzillaAttribute.BUG_FILE_LOC);
		attributeBugFileLoc.setValue("http://");

		createAttribute(taskData, BugzillaAttribute.DEPENDSON);
		createAttribute(taskData, BugzillaAttribute.BLOCKED);
		createAttribute(taskData, BugzillaAttribute.NEWCC);
		createAttribute(taskData, BugzillaAttribute.LONG_DESC);

		TaskAttribute attrDescription = taskData.getRoot().getMappedAttribute(TaskAttribute.DESCRIPTION);
		if (attrDescription != null) {
			attrDescription.getMetaData().setReadOnly(false);
		}
		TaskAttribute attrOwner = taskData.getRoot().getMappedAttribute(TaskAttribute.USER_ASSIGNED);
		if (attrOwner != null) {
			attrOwner.getMetaData().setReadOnly(false);
		}
		TaskAttribute attrAddSelfToCc = taskData.getRoot().getMappedAttribute(TaskAttribute.ADD_SELF_CC);
		if (attrAddSelfToCc != null) {
			attrAddSelfToCc.getMetaData().setKind(null);
		}

		return true;
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
		TaskMapper mapper = new TaskMapper(parentTaskData);
		initializeTaskData(repository, subTaskData, mapper, monitor);
		new TaskMapper(subTaskData).merge(mapper);
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

	public void postUpdateAttachment(TaskRepository repository, TaskAttribute taskAttribute, String action,
			IProgressMonitor monitor) throws CoreException {
		monitor = Policy.monitorFor(monitor);
		try {
			monitor.beginTask("Updating Attachment", IProgressMonitor.UNKNOWN);
			BugzillaClient client = connector.getClientManager().getClient(repository, monitor);
			try {
				client.postUpdateAttachment(taskAttribute, action, monitor);
			} catch (CoreException e) {
				// TODO: Move retry handling into client
				if (e.getStatus().getCode() == RepositoryStatus.ERROR_REPOSITORY_LOGIN) {
					client.postUpdateAttachment(taskAttribute, action, monitor);
				} else {
					throw e;
				}
			}
		} catch (IOException e) {
			throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
					RepositoryStatus.ERROR_IO, repository.getRepositoryUrl(), e));
		} finally {
			monitor.done();
		}
	}

}
