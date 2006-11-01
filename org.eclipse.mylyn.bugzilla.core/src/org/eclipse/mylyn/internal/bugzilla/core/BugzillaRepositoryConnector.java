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

package org.eclipse.mylar.internal.bugzilla.core;

import java.io.IOException;
import java.net.Proxy;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants.BugzillaServerVersion;
import org.eclipse.mylar.internal.tasks.core.UnrecognizedReponseException;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.IAttachmentHandler;
import org.eclipse.mylar.tasks.core.IOfflineTaskHandler;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.QueryHitCollector;
import org.eclipse.mylar.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskList;
import org.eclipse.mylar.tasks.core.TaskRepository;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class BugzillaRepositoryConnector extends AbstractRepositoryConnector {

	private static final String CLIENT_LABEL = "Bugzilla (supports uncustomized 2.18-2.22)";

	private BugzillaAttachmentHandler attachmentHandler;

	private BugzillaOfflineTaskHandler offlineHandler;

	private boolean forceSynchExecForTesting = false;

	private BugzillaClientManager clientManager;

	public void init(TaskList taskList) {
		super.init(taskList);
		this.offlineHandler = new BugzillaOfflineTaskHandler(this, taskList);
		BugzillaCorePlugin.getDefault().setConnector(this);
		attachmentHandler = new BugzillaAttachmentHandler(this);
	}

	public String getLabel() {
		return CLIENT_LABEL;
	}

	@Override
	public IAttachmentHandler getAttachmentHandler() {
		return attachmentHandler;
	}

	@Override
	public IOfflineTaskHandler getOfflineTaskHandler() {
		return offlineHandler;
	}

	public String getRepositoryType() {
		return BugzillaCorePlugin.REPOSITORY_KIND;
	}

	public ITask createTaskFromExistingKey(TaskRepository repository, String id, Proxy proxySettings)
			throws CoreException {
		int bugId = -1;
		try {
			if (id != null) {
				bugId = Integer.parseInt(id);
			} else {
				throw new CoreException(new Status(IStatus.ERROR, BugzillaCorePlugin.PLUGIN_ID, IStatus.OK,
						"invalid report id: null", new Exception("Invalid report id: null")));
			}
		} catch (NumberFormatException nfe) {
			if (!forceSynchExecForTesting) {
				throw new CoreException(new Status(IStatus.ERROR, BugzillaCorePlugin.PLUGIN_ID, IStatus.OK,
						"invalid report id: " + id, nfe));
				// MessageDialog.openInformation(null,
				// TasksUiPlugin.TITLE_DIALOG, "Invalid report id: " + id);
			}
			return null;
		}

		String handle = AbstractRepositoryTask.getHandle(repository.getUrl(), bugId);
		ITask task = taskList.getTask(handle);

		if (task == null) {
			RepositoryTaskData taskData = null;
			// try {
			taskData = offlineHandler.downloadTaskData(repository, id, proxySettings);
			// BugzillaClient client = getClientManager().getClient(repository);
			// taskData = client.getTaskData(Integer.parseInt(id));
			if (taskData != null) {
				task = new BugzillaTask(handle, taskData.getId() + ": " + taskData.getDescription(), true);
				((BugzillaTask) task).setTaskData(taskData);
				taskList.addTask(task);
			}
			// } catch (final UnrecognizedReponseException e) {
			// throw new CoreException(new Status(IStatus.ERROR,
			// BugzillaCorePlugin.PLUGIN_ID, 0,
			// "Report retrieval failed. Unrecognized response from " +
			// repository.getUrl() + ".", e));
			// } catch (final FileNotFoundException e) {
			// throw new CoreException(
			// new Status(IStatus.ERROR, BugzillaCorePlugin.PLUGIN_ID, 0,
			// "Report download from "
			// + repository.getUrl() + " failed. File not found: " +
			// e.getMessage(), e));
			// } catch (final Exception e) {
			// throw new CoreException(new Status(IStatus.ERROR,
			// BugzillaCorePlugin.PLUGIN_ID, 0,
			// "Report download from " + repository.getUrl() + " failed, please
			// see details.", e));
			// }
		}
		return task;
	}

	@Override
	public boolean canCreateTaskFromKey(TaskRepository repository) {
		return true;
	}

	@Override
	public boolean canCreateNewTask(TaskRepository repository) {
		return true;
	}

	public List<String> getSupportedVersions() {
		if (supportedVersions == null) {
			supportedVersions = new ArrayList<String>();
			for (BugzillaServerVersion version : BugzillaServerVersion.values()) {
				supportedVersions.add(version.toString());
			}
		}
		return supportedVersions;
	}

	@Override
	public IStatus performQuery(final AbstractRepositoryQuery query, TaskRepository repository, Proxy proxySettings,
			IProgressMonitor monitor, QueryHitCollector resultCollector) {

		IStatus queryStatus = Status.OK_STATUS;
		try {
			BugzillaClient client = getClientManager().getClient(repository);
			client.getSearchHits(query, resultCollector, taskList);
		} catch (UnrecognizedReponseException e) {
			queryStatus = new Status(IStatus.ERROR, BugzillaCorePlugin.PLUGIN_ID, Status.INFO,
					"Unrecognized response from server", e);
		} catch (IOException e) {
			queryStatus = new Status(IStatus.ERROR, BugzillaCorePlugin.PLUGIN_ID, Status.ERROR,
					"Check repository credentials and connectivity.", e);
		} catch (BugzillaException e) {
			queryStatus = new Status(IStatus.ERROR, BugzillaCorePlugin.PLUGIN_ID, IStatus.OK,
					"Unable to perform query due to Bugzilla error", e);
		} catch (GeneralSecurityException e) {
			queryStatus = new Status(IStatus.ERROR, BugzillaCorePlugin.PLUGIN_ID, IStatus.OK,
					"Unable to perform query due to repository configuration error", e);
		}
		return queryStatus;

	}

	public String getRepositoryUrlFromTaskUrl(String url) {
		if (url == null) {
			return null;
		} else {
			int index = url.indexOf(IBugzillaConstants.URL_GET_SHOW_BUG);
			if (index != -1) {
				return url.substring(0, index);
			} else {
				return null;
			}
		}
	}

	// @Override
	// public void updateAttributes(final TaskRepository repository, Proxy
	// proxySettings, IProgressMonitor monitor)
	// throws CoreException {
	// try {
	// BugzillaCorePlugin.getRepositoryConfiguration(true, repository.getUrl(),
	// proxySettings, repository
	// .getUserName(), repository.getPassword(),
	// repository.getCharacterEncoding());
	// } catch (Exception e) {
	// throw new CoreException(new Status(IStatus.ERROR,
	// BugzillaCorePlugin.PLUGIN_ID, IStatus.OK,
	// "could not update repository configuration", e));
	// }
	// }

	// public void updateBugAttributeOptions(TaskRepository taskRepository,
	// RepositoryTaskData existingReport) throws IOException,
	// KeyManagementException, GeneralSecurityException, BugzillaException,
	// CoreException {
	// String product =
	// existingReport.getAttributeValue(BugzillaReportElement.PRODUCT.getKeyString());
	// for (RepositoryTaskAttribute attribute : existingReport.getAttributes())
	// {
	// BugzillaReportElement element =
	// BugzillaReportElement.valueOf(attribute.getID().trim().toUpperCase());
	// attribute.clearOptions();
	// // List<String> optionValues =
	// BugzillaCorePlugin.getRepositoryConfiguration(false, repositoryUrl,
	// // proxySettings, userName, password,
	// characterEncoding).getOptionValues(element, product);
	// List<String> optionValues =
	// this.getRepositoryConfiguration(taskRepository,
	// false).getOptionValues(element.getKeyString(), product);
	// if (element != BugzillaReportElement.OP_SYS && element !=
	// BugzillaReportElement.BUG_SEVERITY
	// && element != BugzillaReportElement.PRIORITY && element !=
	// BugzillaReportElement.BUG_STATUS) {
	// Collections.sort(optionValues);
	// }
	// if (element == BugzillaReportElement.TARGET_MILESTONE &&
	// optionValues.isEmpty()) {
	// existingReport.removeAttribute(BugzillaReportElement.TARGET_MILESTONE);
	// continue;
	// }
	// for (String option : optionValues) {
	// attribute.addOptionValue(option, option);
	// }
	// }
	//
	// }

	@Override
	public void updateTaskState(AbstractRepositoryTask repositoryTask) {
		// TODO: implement once this is consistent with offline task data
	}

	public void setForceSynchExecForTesting(boolean forceSynchExecForTesting) {
		this.forceSynchExecForTesting = forceSynchExecForTesting;
	}

	@Override
	public String getTaskIdPrefix() {
		return "bug";
	}

	public BugzillaClientManager getClientManager() {
		if (clientManager == null) {
			clientManager = new BugzillaClientManager();
		}
		return clientManager;
	}

	@Override
	public void updateAttributes(TaskRepository repository, IProgressMonitor monitor) throws CoreException {

		BugzillaCorePlugin.getDefault().getRepositoryConfiguration(repository, true);

	}

	public void updateAttributeOptions(TaskRepository taskRepository, RepositoryTaskData existingReport)
			throws CoreException {
		String product = existingReport.getAttributeValue(BugzillaReportElement.PRODUCT.getKeyString());
		for (RepositoryTaskAttribute attribute : existingReport.getAttributes()) {
			BugzillaReportElement element = BugzillaReportElement.valueOf(attribute.getID().trim().toUpperCase());
			attribute.clearOptions();
			List<String> optionValues = BugzillaCorePlugin.getDefault().getRepositoryConfiguration(taskRepository,
					false).getOptionValues(element, product);
			if (element != BugzillaReportElement.OP_SYS && element != BugzillaReportElement.BUG_SEVERITY
					&& element != BugzillaReportElement.PRIORITY && element != BugzillaReportElement.BUG_STATUS) {
				Collections.sort(optionValues);
			}
			if (element == BugzillaReportElement.TARGET_MILESTONE && optionValues.isEmpty()) {

				existingReport.removeAttribute(BugzillaReportElement.TARGET_MILESTONE);
				continue;
			}
			for (String option : optionValues) {
				attribute.addOptionValue(option, option);
			}
		}

	}

	/**
	 * Adds bug attributes to new bug model and sets defaults
	 * 
	 * @param proxySettings
	 *            TODO
	 * @param characterEncoding
	 *            TODO
	 * 
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws LoginException
	 * @throws KeyManagementException
	 * @throws BugzillaException
	 */
	public static void setupNewBugAttributes(TaskRepository taskRepository, NewBugzillaReport newReport)
			throws CoreException {

		newReport.removeAllAttributes();

		RepositoryConfiguration repositoryConfiguration = BugzillaCorePlugin.getDefault().getRepositoryConfiguration(
				taskRepository, false);

		RepositoryTaskAttribute a = BugzillaClient.makeNewAttribute(BugzillaReportElement.PRODUCT);
		List<String> optionValues = repositoryConfiguration.getProducts();
		Collections.sort(optionValues);
		// for (String option : optionValues) {
		// a.addOptionValue(option, option);
		// }
		a.setValue(newReport.getProduct());
		a.setReadOnly(true);

		newReport.addAttribute(BugzillaReportElement.PRODUCT.getKeyString(), a);
		// attributes.put(a.getName(), a);

		a = BugzillaClient.makeNewAttribute(BugzillaReportElement.BUG_STATUS);
		optionValues = repositoryConfiguration.getStatusValues();
		for (String option : optionValues) {
			a.addOptionValue(option, option);
		}
		a.setValue(IBugzillaConstants.VALUE_STATUS_NEW);

		newReport.addAttribute(BugzillaReportElement.BUG_STATUS.getKeyString(), a);
		// attributes.put(a.getName(), a);

		a = BugzillaClient.makeNewAttribute(BugzillaReportElement.VERSION);
		optionValues = repositoryConfiguration.getVersions(newReport.getProduct());
		Collections.sort(optionValues);
		for (String option : optionValues) {
			a.addOptionValue(option, option);
		}
		if (optionValues != null && optionValues.size() > 0) {
			a.setValue(optionValues.get(optionValues.size() - 1));
		}

		newReport.addAttribute(BugzillaReportElement.VERSION.getKeyString(), a);
		// attributes.put(a.getName(), a);

		a = BugzillaClient.makeNewAttribute(BugzillaReportElement.COMPONENT);
		optionValues = repositoryConfiguration.getComponents(newReport.getProduct());
		Collections.sort(optionValues);
		for (String option : optionValues) {
			a.addOptionValue(option, option);
		}
		if (optionValues != null && optionValues.size() > 0) {
			a.setValue(optionValues.get(0));
		}

		newReport.addAttribute(BugzillaReportElement.COMPONENT.getKeyString(), a);

		a = BugzillaClient.makeNewAttribute(BugzillaReportElement.REP_PLATFORM);
		optionValues = repositoryConfiguration.getPlatforms();
		Collections.sort(optionValues);
		for (String option : optionValues) {
			a.addOptionValue(option, option);
		}
		if (optionValues != null && optionValues.size() > 0) {
			a.setValue(optionValues.get(0));
		}

		newReport.addAttribute(BugzillaReportElement.REP_PLATFORM.getKeyString(), a);
		// attributes.put(a.getName(), a);

		a = BugzillaClient.makeNewAttribute(BugzillaReportElement.OP_SYS);
		optionValues = repositoryConfiguration.getOSs();
		for (String option : optionValues) {
			a.addOptionValue(option, option);
		}
		if (optionValues != null && optionValues.size() > 0) {
			a.setValue(optionValues.get(optionValues.size() - 1));
		}

		newReport.addAttribute(BugzillaReportElement.OP_SYS.getKeyString(), a);
		// attributes.put(a.getName(), a);

		a = BugzillaClient.makeNewAttribute(BugzillaReportElement.PRIORITY);
		optionValues = repositoryConfiguration.getPriorities();
		for (String option : optionValues) {
			a.addOptionValue(option, option);
		}
		a.setValue(optionValues.get((optionValues.size() / 2)));

		newReport.addAttribute(BugzillaReportElement.PRIORITY.getKeyString(), a);
		// attributes.put(a.getName(), a);

		a = BugzillaClient.makeNewAttribute(BugzillaReportElement.BUG_SEVERITY);
		optionValues = repositoryConfiguration.getSeverities();
		for (String option : optionValues) {
			a.addOptionValue(option, option);
		}
		a.setValue(optionValues.get((optionValues.size() / 2)));

		newReport.addAttribute(BugzillaReportElement.BUG_SEVERITY.getKeyString(), a);
		// attributes.put(a.getName(), a);

		// a = new
		// RepositoryTaskAttribute(BugzillaReportElement.TARGET_MILESTONE);
		// optionValues =
		// BugzillaCorePlugin.getDefault().getgetProductConfiguration(serverUrl).getTargetMilestones(
		// newReport.getProduct());
		// for (String option : optionValues) {
		// a.addOptionValue(option, option);
		// }
		// if(optionValues.size() > 0) {
		// // new bug posts will fail if target_milestone element is
		// included
		// // and there are no milestones on the server
		// newReport.addAttribute(BugzillaReportElement.TARGET_MILESTONE, a);
		// }

		a = BugzillaClient.makeNewAttribute(BugzillaReportElement.ASSIGNED_TO);
		a.setValue("");
		a.setReadOnly(false);

		newReport.addAttribute(BugzillaReportElement.ASSIGNED_TO.getKeyString(), a);
		// attributes.put(a.getName(), a);

		a = BugzillaClient.makeNewAttribute(BugzillaReportElement.BUG_FILE_LOC);
		a.setValue("http://");
		a.setHidden(false);

		newReport.addAttribute(BugzillaReportElement.BUG_FILE_LOC.getKeyString(), a);
		// attributes.put(a.getName(), a);

		// newReport.attributes = attributes;
	}
	
	
	// // TODO: change to getAttributeOptions() and use whereever attribute
	// options are required.
	// public void updateAttributeOptions(TaskRepository taskRepository,
	// RepositoryTaskData existingReport)
	// throws IOException, KeyManagementException, GeneralSecurityException,
	// BugzillaException, CoreException {
	//
	// RepositoryConfiguration configuration =
	// BugzillaCorePlugin.getDefault().getRepositoryConfiguration(taskRepository,
	// false);
	// if (configuration == null)
	// return;
	// String product =
	// existingReport.getAttributeValue(BugzillaReportElement.PRODUCT.getKeyString());
	// for (RepositoryTaskAttribute attribute : existingReport.getAttributes())
	// {
	// BugzillaReportElement element =
	// BugzillaReportElement.valueOf(attribute.getID().trim().toUpperCase());
	// attribute.clearOptions();
	// String key = attribute.getID();
	// if (!product.equals("")) {
	// switch (element) {
	// case TARGET_MILESTONE:
	// case VERSION:
	// case COMPONENT:
	// key = product + "." + key;
	// }
	// }
	//
	// List<String> optionValues = configuration.getAttributeValues(key);
	// if(optionValues.size() == 0) {
	// optionValues = configuration.getAttributeValues(attribute.getID());
	// }
	//
	// if (element != BugzillaReportElement.OP_SYS && element !=
	// BugzillaReportElement.BUG_SEVERITY
	// && element != BugzillaReportElement.PRIORITY && element !=
	// BugzillaReportElement.BUG_STATUS) {
	// Collections.sort(optionValues);
	// }
	// if (element == BugzillaReportElement.TARGET_MILESTONE &&
	// optionValues.isEmpty()) {
	// existingReport.removeAttribute(BugzillaReportElement.TARGET_MILESTONE);
	// continue;
	// }
	// for (String option : optionValues) {
	// attribute.addOptionValue(option, option);
	// }
	// }
	// }

	// /**
	// * Adds bug attributes to new bug model and sets defaults TODO: Make
	// generic
	// * and move TaskRepositoryManager
	// */
	// public void setupNewBugAttributes(TaskRepository taskRepository,
	// NewBugzillaReport newReport) throws CoreException {
	//
	// newReport.removeAllAttributes();
	//
	// RepositoryConfiguration repositoryConfiguration =
	// BugzillaCorePlugin.getDefault().getRepositoryConfiguration(
	// taskRepository, false);
	//
	// RepositoryTaskAttribute a =
	// BugzillaClient.makeNewAttribute(BugzillaReportElement.PRODUCT);
	// List<String> optionValues =
	// repositoryConfiguration.getAttributeValues(BugzillaReportElement.PRODUCT
	// .getKeyString());
	// Collections.sort(optionValues);
	// // for (String option : optionValues) {
	// // a.addOptionValue(option, option);
	// // }
	// a.setValue(newReport.getProduct());
	// a.setReadOnly(true);
	// newReport.addAttribute(BugzillaReportElement.PRODUCT.getKeyString(), a);
	// // attributes.put(a.getName(), a);
	//
	// a = BugzillaClient.makeNewAttribute(BugzillaReportElement.BUG_STATUS);
	// optionValues =
	// repositoryConfiguration.getAttributeValues(BugzillaReportElement.BUG_STATUS.getKeyString());
	// for (String option : optionValues) {
	// a.addOptionValue(option, option);
	// }
	// a.setValue(IBugzillaConstants.VALUE_STATUS_NEW);
	// newReport.addAttribute(BugzillaReportElement.BUG_STATUS.getKeyString(),
	// a);
	// // attributes.put(a.getName(), a);
	//
	// a = BugzillaClient.makeNewAttribute(BugzillaReportElement.VERSION);
	// optionValues =
	// repositoryConfiguration.getAttributeValues(BugzillaReportElement.VERSION.getKeyString());
	// Collections.sort(optionValues);
	// for (String option : optionValues) {
	// a.addOptionValue(option, option);
	// }
	// if (optionValues != null && optionValues.size() > 0) {
	// a.setValue(optionValues.get(optionValues.size() - 1));
	// }
	// newReport.addAttribute(BugzillaReportElement.VERSION.getKeyString(), a);
	// // attributes.put(a.getName(), a);
	//
	// a = BugzillaClient.makeNewAttribute(BugzillaReportElement.COMPONENT);
	// optionValues =
	// repositoryConfiguration.getAttributeValues(BugzillaReportElement.COMPONENT.getKeyString());
	// Collections.sort(optionValues);
	// for (String option : optionValues) {
	// a.addOptionValue(option, option);
	// }
	// if (optionValues != null && optionValues.size() > 0) {
	// a.setValue(optionValues.get(0));
	// }
	// newReport.addAttribute(BugzillaReportElement.COMPONENT.getKeyString(),
	// a);
	//
	// a = BugzillaClient.makeNewAttribute(BugzillaReportElement.REP_PLATFORM);
	// optionValues =
	// repositoryConfiguration.getAttributeValues(BugzillaReportElement.REP_PLATFORM.getKeyString());
	// Collections.sort(optionValues);
	// for (String option : optionValues) {
	// a.addOptionValue(option, option);
	// }
	// if (optionValues != null && optionValues.size() > 0) {
	// a.setValue(optionValues.get(0));
	// }
	// newReport.addAttribute(BugzillaReportElement.REP_PLATFORM.getKeyString(),
	// a);
	// // attributes.put(a.getName(), a);
	//
	// a = BugzillaClient.makeNewAttribute(BugzillaReportElement.OP_SYS);
	// optionValues =
	// repositoryConfiguration.getAttributeValues(BugzillaReportElement.OP_SYS.getKeyString());
	// for (String option : optionValues) {
	// a.addOptionValue(option, option);
	// }
	// if (optionValues != null && optionValues.size() > 0) {
	// a.setValue(optionValues.get(optionValues.size() - 1));
	// }
	// newReport.addAttribute(BugzillaReportElement.OP_SYS.getKeyString(), a);
	// // attributes.put(a.getName(), a);
	//
	// a = BugzillaClient.makeNewAttribute(BugzillaReportElement.PRIORITY);
	// optionValues =
	// repositoryConfiguration.getAttributeValues(BugzillaReportElement.PRIORITY.getKeyString());
	// for (String option : optionValues) {
	// a.addOptionValue(option, option);
	// }
	// a.setValue(optionValues.get((optionValues.size() / 2)));
	// newReport.addAttribute(BugzillaReportElement.PRIORITY.getKeyString(), a);
	// // attributes.put(a.getName(), a);
	//
	// a = BugzillaClient.makeNewAttribute(BugzillaReportElement.BUG_SEVERITY);
	// optionValues =
	// repositoryConfiguration.getAttributeValues(BugzillaReportElement.BUG_SEVERITY.getKeyString());
	// for (String option : optionValues) {
	// a.addOptionValue(option, option);
	// }
	// a.setValue(optionValues.get((optionValues.size() / 2)));
	// newReport.addAttribute(BugzillaReportElement.BUG_SEVERITY.getKeyString(),
	// a);
	// // attributes.put(a.getName(), a);
	//
	// // a = new
	// // RepositoryTaskAttribute(BugzillaReportElement.TARGET_MILESTONE);
	// // optionValues =
	// //
	// BugzillaPlugin.getDefault().getProductConfiguration(serverUrl).getTargetMilestones(
	// // newReport.getProduct());
	// // for (String option : optionValues) {
	// // a.addOptionValue(option, option);
	// // }
	// // if(optionValues.size() > 0) {
	// // // new bug posts will fail if target_milestone element is included
	// // // and there are no milestones on the server
	// // newReport.addAttribute(BugzillaReportElement.TARGET_MILESTONE, a);
	// // }
	//
	// a = BugzillaClient.makeNewAttribute(BugzillaReportElement.ASSIGNED_TO);
	// a.setValue("");
	// a.setReadOnly(false);
	// newReport.addAttribute(BugzillaReportElement.ASSIGNED_TO.getKeyString(),
	// a);
	// // attributes.put(a.getName(), a);
	//
	// a = BugzillaClient.makeNewAttribute(BugzillaReportElement.BUG_FILE_LOC);
	// a.setValue("http://");
	// a.setHidden(false);
	// newReport.addAttribute(BugzillaReportElement.BUG_FILE_LOC.getKeyString(),
	// a);
	// // attributes.put(a.getName(), a);
	//
	// // newReport.attributes = attributes;
	// }

}
