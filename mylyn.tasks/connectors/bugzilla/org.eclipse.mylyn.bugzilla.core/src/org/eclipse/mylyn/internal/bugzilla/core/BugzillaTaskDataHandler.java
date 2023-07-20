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

package org.eclipse.mylyn.internal.bugzilla.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.net.Policy;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCustomField.FieldType;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants.BUGZILLA_REPORT_STATUS_4_0;
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
			void migrate(TaskRepository repository, TaskData data, BugzillaRepositoryConnector connector) {
				// ignore
			}
		},

		VERSION_1_0(1.0f) {
			@Override
			void migrate(TaskRepository repository, TaskData data, BugzillaRepositoryConnector connector) {
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
				for (TaskAttribute attribute : data.getAttributeMapper()
						.getAttributesByType(data, TaskAttribute.TYPE_OPERATION)) {
					operationsToRemove.add(attribute);
				}
				for (TaskAttribute taskAttribute : operationsToRemove) {
					data.getRoot().removeAttribute(taskAttribute.getId());
				}
				RepositoryConfiguration configuration = connector
						.getRepositoryConfiguration(repository.getRepositoryUrl());
				if (configuration != null) {
					configuration.addValidOperations(data);
				}
			}
		},
		VERSION_2_0(2.0f) {
			@Override
			void migrate(TaskRepository repository, TaskData data, BugzillaRepositoryConnector connector) {
				updateAttribute(data, BugzillaAttribute.LONG_DESC);
			}
		},
		VERSION_3_0(3.0f) {
			@Override
			void migrate(TaskRepository repository, TaskData data, BugzillaRepositoryConnector connector) {
				updateAttribute(data, BugzillaAttribute.NEW_COMMENT);
			}
		},
		VERSION_4_0(4.0f) {
			@Override
			void migrate(TaskRepository repository, TaskData data, BugzillaRepositoryConnector connector) {
				updateAttribute(data, BugzillaAttribute.DEADLINE);
				updateAttribute(data, BugzillaAttribute.ACTUAL_TIME);
			}
		},
		VERSION_4_1(4.1f) {
			@Override
			void migrate(TaskRepository repository, TaskData data, BugzillaRepositoryConnector connector) {
				updateAttribute(data, BugzillaAttribute.VOTES);
				TaskAttribute attrDeadline = data.getRoot().getMappedAttribute(BugzillaAttribute.VOTES.getKey());
				if (attrDeadline != null) {
					attrDeadline.getMetaData().setType(BugzillaAttribute.VOTES.getType());
				}
			}
		},
		VERSION_4_2(4.2f) {
			@Override
			void migrate(TaskRepository repository, TaskData data, BugzillaRepositoryConnector connector) {
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
			void migrate(TaskRepository repository, TaskData data, BugzillaRepositoryConnector connector) {
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
			void migrate(TaskRepository repository, TaskData data, BugzillaRepositoryConnector connector) {
				// summary didn't have spell checking, update to short rich text
				updateAttribute(data, BugzillaAttribute.SHORT_DESC);
			}
		},
		VERSION_4_5(4.5f) {
			@Override
			void migrate(TaskRepository repository, TaskData data, BugzillaRepositoryConnector connector) {
				// migrate custom attributes
				RepositoryConfiguration configuration = connector
						.getRepositoryConfiguration(repository.getRepositoryUrl());

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
							switch (customField.getFieldType()) {
							case FreeText:
								attribute.getMetaData().setType(TaskAttribute.TYPE_SHORT_TEXT);
								break;
							case DropDown:
								attribute.getMetaData().setType(TaskAttribute.TYPE_SINGLE_SELECT);
								break;
							case MultipleSelection:
								attribute.getMetaData().setType(TaskAttribute.TYPE_MULTI_SELECT);
								break;
							case LargeText:
								attribute.getMetaData().setType(TaskAttribute.TYPE_LONG_TEXT);
								break;
							case DateTime:
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
		VERSION_4_6(4.6f) {
			@Override
			void migrate(TaskRepository repository, TaskData data, BugzillaRepositoryConnector connector) {
				// set kind for flags
				String bugzillaVersionString = repository.getVersion();
				BugzillaVersion bugzillaVersion = new BugzillaVersion(bugzillaVersionString);

				if (bugzillaVersion.compareTo(BugzillaVersion.BUGZILLA_3_2) >= 0) {
					for (TaskAttribute attribute : data.getRoot().getAttributes().values()) {
						if (attribute.getId().startsWith(BugzillaAttribute.KIND_FLAG)) {
							attribute.getMetaData().setKind(BugzillaAttribute.KIND_FLAG);
						}
					}
				}
			}
		},
		VERSION_4_7(4.7f) {
			@Override
			void migrate(TaskRepository repository, TaskData data, BugzillaRepositoryConnector connector) {
				TaskAttribute exporter = data.getRoot().getAttribute(BugzillaAttribute.EXPORTER_NAME.getKey());
				if (exporter == null) {
					String exporterName = repository.getUserName();
					if (exporterName != null && !exporterName.equals("")) { //$NON-NLS-1$
						exporter = data.getRoot().createAttribute(BugzillaAttribute.EXPORTER_NAME.getKey());
						exporter.setValue(exporterName);
					}
				}
			}
		},
		VERSION_CURRENT(4.8f) {
			@Override
			void migrate(TaskRepository repository, TaskData data, BugzillaRepositoryConnector connector) {
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

		abstract void migrate(TaskRepository repository, TaskData data, BugzillaRepositoryConnector connector);

		@Override
		public String toString() {
			return "" + getVersionNum(); //$NON-NLS-1$
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

	protected final BugzillaRepositoryConnector connector;

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
					"Task data could not be retrieved. Please re-synchronize task")); //$NON-NLS-1$
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
			monitor.beginTask(Messages.BugzillaTaskDataHandler_Receiving_tasks, taskIds.size());
			final BugzillaClient client = connector.getClientManager().getClient(repository, monitor);
			final CoreException[] collectionException = new CoreException[1];
			final Boolean[] updateConfig = new Boolean[1];

			class CollectorWrapper extends TaskDataCollector {

				private final IProgressMonitor monitor2;

				private final TaskDataCollector collector;

				@Override
				public void failed(String taskId, IStatus status) {
					collector.failed(taskId, status);
				}

				public CollectorWrapper(TaskDataCollector collector, IProgressMonitor monitor2) {
					this.collector = collector;
					this.monitor2 = monitor2;
				}

				@Override
				public void accept(TaskData taskData) {
					try {
						initializeTaskData(repository, taskData, null, new SubProgressMonitor(monitor2, 1));
					} catch (CoreException e) {
						// this info CoreException is only used internal
						if (e.getStatus().getCode() == IStatus.INFO && e.getMessage().contains("Update Config")) { //$NON-NLS-1$
							if (updateConfig[0] == null) {
								updateConfig[0] = Boolean.valueOf(true);
							}
						} else if (collectionException[0] == null) {
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
			if (updateConfig[0] != null) {
				SubProgressMonitor subMonitor = new SubProgressMonitor(monitor, 1);
				client.getRepositoryConfiguration(subMonitor, null);
				subMonitor.done();
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
				version.migrate(taskRepository, taskData, connector);
			}
		}
	}

	@Override
	public RepositoryResponse postTaskData(TaskRepository repository, TaskData taskData,
			Set<TaskAttribute> changedAttributes, IProgressMonitor monitor) throws CoreException {
		monitor = Policy.monitorFor(monitor);
		try {
			monitor.beginTask(Messages.BugzillaTaskDataHandler_Submitting_task, IProgressMonitor.UNKNOWN);
			BugzillaClient client = connector.getClientManager().getClient(repository, monitor);
			try {
				return client.postTaskData(taskData, monitor);
			} catch (CoreException e) {
				throw e;
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
		try {
			monitor = Policy.monitorFor(monitor);

			monitor.beginTask("Initialize Task Data", IProgressMonitor.UNKNOWN); //$NON-NLS-1$

			RepositoryConfiguration repositoryConfiguration = connector.getRepositoryConfiguration(repository, false,
					monitor);

			if (repositoryConfiguration == null) {
				throw new CoreException(new BugzillaStatus(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
						RepositoryStatus.ERROR_REPOSITORY_LOGIN, repository.getRepositoryUrl(),
						"Retrieving repository configuration failed.")); //$NON-NLS-1$
			}

			if (taskData.isNew()) {
				String product = null;
				String component = null;
				if (initializationData == null || initializationData.getProduct() == null) {
					if (repositoryConfiguration.getOptionValues(BugzillaAttribute.PRODUCT).size() > 0) {
						product = repositoryConfiguration.getOptionValues(BugzillaAttribute.PRODUCT).get(0);
					}
				} else {
					product = initializationData.getProduct();
				}

				if (product == null) {
					return false;
				}

				if (initializationData != null && initializationData.getComponent() != null
						&& initializationData.getComponent().length() > 0) {
					component = initializationData.getComponent();
				}

				initializeNewTaskDataAttributes(repositoryConfiguration, taskData, product, component, monitor);
				if (connector != null) {
					connector.setPlatformDefaultsOrGuess(repository, taskData);
				}
				return true;

			} else {
				boolean shortLogin = Boolean
						.parseBoolean(repository.getProperty(IBugzillaConstants.REPOSITORY_SETTING_SHORT_LOGIN));
				repositoryConfiguration.configureTaskData(taskData, shortLogin, connector);
			}
//			boolean updateConfig = false;
			for (TaskAttribute taskAttribute : taskData.getRoot().getAttributes().values()) {
				Map<String, String> opt = taskAttribute.getOptions();
				if (opt != null && !opt.isEmpty()) {
					List<String> values = taskAttribute.getValues();
					for (String value : values) {
						if (!opt.containsKey(value)) {
							taskAttribute.putOption(value, value);
//							updateConfig = true;
						}
					}
				}
			}
			//TODO: enable this if we want to do the Configuration update
//			if (updateConfig) {
//				// this info CoreException is only used internal
//				throw new CoreException(new BugzillaStatus(IStatus.INFO, BugzillaCorePlugin.ID_PLUGIN, IStatus.INFO,
//						repository.getRepositoryUrl(), "Update Config")); //$NON-NLS-1$
//			}

		} finally {
			monitor.done();
		}
		return true;
	}

	/**
	 * Only new, unsubmitted task data or freshly received task data from the repository can be passed in here.
	 *
	 * @param component
	 */
	private boolean initializeNewTaskDataAttributes(RepositoryConfiguration repositoryConfiguration, TaskData taskData,
			String product, String component, IProgressMonitor monitor) {

		TaskAttribute productAttribute = createAttribute(taskData, BugzillaAttribute.PRODUCT);
		productAttribute.setValue(product);

		List<String> optionValues = repositoryConfiguration.getOptionValues(BugzillaAttribute.PRODUCT);
		Collections.sort(optionValues);
		for (String optionValue : optionValues) {
			productAttribute.putOption(optionValue, optionValue);
		}

		TaskAttribute attributeStatus = createAttribute(taskData, BugzillaAttribute.BUG_STATUS);
		optionValues = repositoryConfiguration.getOptionValues(BugzillaAttribute.BUG_STATUS);
		for (String option : optionValues) {
			attributeStatus.putOption(option, option);
		}

		BugzillaVersion bugzillaVersion = repositoryConfiguration.getInstallVersion();
		if (bugzillaVersion == null) {
			bugzillaVersion = BugzillaVersion.MIN_VERSION;
		}
		if (bugzillaVersion.compareMajorMinorOnly(BugzillaVersion.BUGZILLA_4_0) < 0) {
			attributeStatus.setValue(repositoryConfiguration.getStartStatus());
		} else {
			if (repositoryConfiguration.getOptionValues(BugzillaAttribute.BUG_STATUS)
					.contains(BUGZILLA_REPORT_STATUS_4_0.IN_PROGRESS.toString())
					|| repositoryConfiguration.getOptionValues(BugzillaAttribute.BUG_STATUS)
							.contains(BUGZILLA_REPORT_STATUS_4_0.CONFIRMED.toString())) {

				attributeStatus.setValue(IBugzillaConstants.BUGZILLA_REPORT_STATUS_4_0.START.toString());
				repositoryConfiguration.addValidOperations(taskData);
			} else {
				attributeStatus.setValue(repositoryConfiguration.getStartStatus());
			}
		}

		createAttribute(taskData, BugzillaAttribute.SHORT_DESC);

		TaskAttribute attributeVersion = createAttribute(taskData, BugzillaAttribute.VERSION);
		optionValues = repositoryConfiguration.getProductOptionValues(BugzillaAttribute.VERSION,
				productAttribute.getValue());
		Collections.sort(optionValues);
		for (String option : optionValues) {
			attributeVersion.putOption(option, option);
		}
		if (optionValues.size() > 0) {
			attributeVersion.setValue(optionValues.get(optionValues.size() - 1));
		}

		TaskAttribute attributeComponent = createAttribute(taskData, BugzillaAttribute.COMPONENT);
		optionValues = repositoryConfiguration.getProductOptionValues(BugzillaAttribute.COMPONENT,
				productAttribute.getValue());
		Collections.sort(optionValues);
		for (String option : optionValues) {
			attributeComponent.putOption(option, option);
		}
		if (optionValues.size() == 1) {
			attributeComponent.setValue(optionValues.get(0));
		}
		if (component != null && optionValues.contains(component)) {
			attributeComponent.setValue(component);
		}

		TaskRepository taskRepository = taskData.getAttributeMapper().getTaskRepository();
		if (BugzillaUtil.getTaskPropertyWithDefaultTrue(taskRepository,
				IBugzillaConstants.BUGZILLA_PARAM_USETARGETMILESTONE)) {
			optionValues = repositoryConfiguration.getProductOptionValues(BugzillaAttribute.TARGET_MILESTONE,
					productAttribute.getValue());
			if (optionValues.size() > 0) {
				TaskAttribute attributeTargetMilestone = createAttribute(taskData, BugzillaAttribute.TARGET_MILESTONE);
				for (String option : optionValues) {
					attributeTargetMilestone.putOption(option, option);
				}
				if (product != null && !product.equals("")) { //$NON-NLS-1$
					String defaultMilestone = repositoryConfiguration.getDefaultMilestones(product);
					if (defaultMilestone != null) {
						attributeTargetMilestone.setValue(defaultMilestone);
					} else {
						if (optionValues.contains("---")) { //$NON-NLS-1$
							attributeTargetMilestone.setValue("---"); //$NON-NLS-1$
						}
					}
				} else {
					if (optionValues.contains("---")) { //$NON-NLS-1$
						attributeTargetMilestone.setValue("---"); //$NON-NLS-1$
					}
				}
			}
		}
		TaskAttribute attributePlatform = createAttribute(taskData, BugzillaAttribute.REP_PLATFORM);
		optionValues = repositoryConfiguration.getOptionValues(BugzillaAttribute.REP_PLATFORM);
		for (String option : optionValues) {
			attributePlatform.putOption(option, option);
		}
		if (optionValues.size() > 0) {
			// bug 159397 choose first platform: All
			attributePlatform.setValue(optionValues.get(0));
		}

		TaskAttribute attributeOPSYS = createAttribute(taskData, BugzillaAttribute.OP_SYS);
		optionValues = repositoryConfiguration.getOptionValues(BugzillaAttribute.OP_SYS);
		for (String option : optionValues) {
			attributeOPSYS.putOption(option, option);
		}
		if (optionValues.size() > 0) {
			// bug 159397 change to choose first op_sys All
			attributeOPSYS.setValue(optionValues.get(0));
		}

		TaskAttribute attributePriority = createAttribute(taskData, BugzillaAttribute.PRIORITY);
		optionValues = repositoryConfiguration.getOptionValues(BugzillaAttribute.PRIORITY);
		for (String option : optionValues) {
			attributePriority.putOption(option, option);
		}
		if (optionValues.size() > 0) {
			// choose middle priority
			attributePriority.setValue(optionValues.get((optionValues.size() / 2)));
		}

		TaskAttribute attributeSeverity = createAttribute(taskData, BugzillaAttribute.BUG_SEVERITY);
		optionValues = repositoryConfiguration.getOptionValues(BugzillaAttribute.BUG_SEVERITY);
		for (String option : optionValues) {
			attributeSeverity.putOption(option, option);
		}
		if (optionValues.size() > 0) {
			// choose middle severity
			attributeSeverity.setValue(optionValues.get((optionValues.size() / 2)));
		}

		TaskAttribute attributeAssignedTo = createAttribute(taskData, BugzillaAttribute.ASSIGNED_TO);
		attributeAssignedTo.setValue(""); //$NON-NLS-1$

		if (BugzillaUtil.getTaskPropertyWithDefaultTrue(taskRepository,
				IBugzillaConstants.BUGZILLA_PARAM_USEQACONTACT)) {
			TaskAttribute attributeQAContact = createAttribute(taskData, BugzillaAttribute.QA_CONTACT);
			attributeQAContact.setValue(""); //$NON-NLS-1$
		}
		TaskAttribute attributeBugFileLoc = createAttribute(taskData, BugzillaAttribute.BUG_FILE_LOC);
		attributeBugFileLoc.setValue("http://"); //$NON-NLS-1$

		createAttribute(taskData, BugzillaAttribute.DEPENDSON);
		createAttribute(taskData, BugzillaAttribute.BLOCKED);
		createAttribute(taskData, BugzillaAttribute.NEWCC);
		createAttribute(taskData, BugzillaAttribute.LONG_DESC);

		List<String> keywords = repositoryConfiguration.getOptionValues(BugzillaAttribute.KEYWORDS);
		if (keywords.size() > 0) {
			createAttribute(taskData, BugzillaAttribute.KEYWORDS);
		}

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

		List<BugzillaCustomField> customFields = new ArrayList<BugzillaCustomField>();
		if (repositoryConfiguration != null) {
			customFields = repositoryConfiguration.getCustomFields();
		}
		for (BugzillaCustomField bugzillaCustomField : customFields) {
			if (bugzillaCustomField.isEnterBug()) {
				List<String> options = bugzillaCustomField.getOptions();
				FieldType fieldType = bugzillaCustomField.getFieldType();
				if (options.size() < 1
						&& (fieldType.equals(FieldType.DropDown) || fieldType.equals(FieldType.MultipleSelection))) {
					continue;
				}
				TaskAttribute attribute = taskData.getRoot().createAttribute(bugzillaCustomField.getName());
				if (attribute != null) {
					attribute.getMetaData().defaults().setLabel(bugzillaCustomField.getDescription());
					attribute.getMetaData().setKind(TaskAttribute.KIND_DEFAULT);

					switch (bugzillaCustomField.getFieldType()) {
					case FreeText:
						attribute.getMetaData().setType(TaskAttribute.TYPE_SHORT_TEXT);
						break;
					case DropDown:
						attribute.getMetaData().setType(TaskAttribute.TYPE_SINGLE_SELECT);
						break;
					case MultipleSelection:
						attribute.getMetaData().setType(TaskAttribute.TYPE_MULTI_SELECT);
						break;
					case LargeText:
						attribute.getMetaData().setType(TaskAttribute.TYPE_LONG_TEXT);
						break;
					case DateTime:
						attribute.getMetaData().setType(TaskAttribute.TYPE_DATETIME);
						break;

					default:
						if (options.size() > 0) {
							attribute.getMetaData().setType(TaskAttribute.TYPE_SINGLE_SELECT);
						} else {
							attribute.getMetaData().setType(TaskAttribute.TYPE_SHORT_TEXT);
						}
					}
					attribute.getMetaData().setReadOnly(false);

					for (String option : options) {
						attribute.putOption(option, option);
					}

					if (bugzillaCustomField.getFieldType() == BugzillaCustomField.FieldType.DropDown
							&& options.size() > 0) {
						attribute.setValue(options.get(0));
					}

				}
			}
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
		if (initializeTaskData(repository, subTaskData, mapper, monitor)) {
			new TaskMapper(subTaskData).merge(mapper);
			subTaskData.getRoot().getMappedAttribute(BugzillaAttribute.DEPENDSON.getKey()).setValue(""); //$NON-NLS-1$
			subTaskData.getRoot().getMappedAttribute(TaskAttribute.DESCRIPTION).setValue(""); //$NON-NLS-1$
			subTaskData.getRoot().getMappedAttribute(TaskAttribute.SUMMARY).setValue(""); //$NON-NLS-1$
			TaskAttribute keywords = subTaskData.getRoot().getMappedAttribute(TaskAttribute.KEYWORDS);
			if (keywords != null) {
				// only if the repository has keywords this attribut exists
				keywords.setValue(""); //$NON-NLS-1$
			}
			subTaskData.getRoot().getAttribute(BugzillaAttribute.BLOCKED.getKey()).setValue(parentTaskData.getTaskId());
			TaskAttribute parentAttributeAssigned = parentTaskData.getRoot()
					.getMappedAttribute(TaskAttribute.USER_ASSIGNED);
			subTaskData.getRoot()
					.getAttribute(BugzillaAttribute.ASSIGNED_TO.getKey())
					.setValue(parentAttributeAssigned.getValue());
			return true;
		} else {
			return false;
		}
	}

	@Override
	public TaskAttributeMapper getAttributeMapper(TaskRepository taskRepository) {
		return new BugzillaAttributeMapper(taskRepository, connector);
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

		if (key == BugzillaAttribute.STATUS_WHITEBOARD) {
			attribute.getMetaData().putValue(TaskAttribute.META_INDEXED_AS_CONTENT, Boolean.TRUE.toString());
		}

		return attribute;
	}

	public void postUpdateAttachment(TaskRepository repository, TaskAttribute taskAttribute, String action,
			IProgressMonitor monitor) throws CoreException {
		monitor = Policy.monitorFor(monitor);
		try {
			monitor.beginTask(Messages.BugzillaTaskDataHandler_updating_attachment, IProgressMonitor.UNKNOWN);
			BugzillaClient client = connector.getClientManager().getClient(repository, monitor);
			try {
				client.postUpdateAttachment(taskAttribute, action, monitor);
			} catch (CoreException e) {
				// TODO: Move retry handling into client
				if (e.getStatus().getCode() == RepositoryStatus.ERROR_REPOSITORY_LOGIN) {
					AuthenticationCredentials creds = repository.getCredentials(AuthenticationType.REPOSITORY);
					if (creds != null && creds.getUserName() != null && creds.getUserName().length() > 0) {
						client.postUpdateAttachment(taskAttribute, action, monitor);
					} else {
						throw e;
					}
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
