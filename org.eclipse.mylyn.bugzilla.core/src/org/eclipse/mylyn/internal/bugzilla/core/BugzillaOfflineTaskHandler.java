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

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants.BUGZILLA_OPERATION;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants.BUGZILLA_REPORT_STATUS;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants.BUGZILLA_RESOLUTION;
import org.eclipse.mylar.internal.tasks.core.UnrecognizedReponseException;
import org.eclipse.mylar.tasks.core.AbstractAttributeFactory;
import org.eclipse.mylar.tasks.core.AbstractQueryHit;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.IOfflineTaskHandler;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.QueryHitCollector;
import org.eclipse.mylar.tasks.core.RepositoryOperation;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskList;
import org.eclipse.mylar.tasks.core.TaskRepository;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class BugzillaOfflineTaskHandler implements IOfflineTaskHandler {

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

	private static final String BUG_ID = "&bug_id=";

	private static final int MAX_URL_LENGTH = 2000;

	private static final String CHANGED_BUGS_CGI_ENDDATE = "&chfieldto=Now";

	private static final String CHANGED_BUGS_CGI_QUERY = "/buglist.cgi?query_format=advanced&chfieldfrom=";

	private static final String DATE_FORMAT_1 = "yyyy-MM-dd HH:mm";

	private static final String DATE_FORMAT_2 = "yyyy-MM-dd HH:mm:ss";

	private static SimpleDateFormat delta_ts_format = new SimpleDateFormat(DATE_FORMAT_2);

	private static SimpleDateFormat creation_ts_format = new SimpleDateFormat(DATE_FORMAT_1);

	/**
	 * public for testing Bugzilla 2.18 uses DATE_FORMAT_1 but later versions
	 * use DATE_FORMAT_2 Using lowest common denominator DATE_FORMAT_1
	 */
	public static SimpleDateFormat comment_creation_ts_format = new SimpleDateFormat(DATE_FORMAT_1);

	private static SimpleDateFormat attachment_creation_ts_format = new SimpleDateFormat(DATE_FORMAT_1);

	private AbstractAttributeFactory attributeFactory = new BugzillaAttributeFactory();

	private TaskList taskList;

	private BugzillaRepositoryConnector connector;

	public BugzillaOfflineTaskHandler(BugzillaRepositoryConnector connector, TaskList taskList) {
		this.taskList = taskList;
		this.connector = connector;
	}

	public RepositoryTaskData downloadTaskData(TaskRepository repository, String taskId, Proxy proxySettings)
			throws CoreException {
		try {

			BugzillaClient client = connector.getClientManager().getClient(repository);
			client.setProxy(proxySettings);
			int bugId = Integer.parseInt(taskId);
			RepositoryTaskData taskData = client.getTaskData(bugId);
			if (taskData != null) {
				connector.updateAttributeOptions(repository, taskData);
				addValidOperations(taskData, repository.getUserName());
				return taskData;
			}
			return null;

		} catch (final UnrecognizedReponseException e) {
			throw new CoreException(new Status(IStatus.ERROR, BugzillaCorePlugin.PLUGIN_ID, 0,
					"Report download failed. Unrecognized response from " + repository.getUrl() + ".", e));
		} catch (final FileNotFoundException e) {
			throw new CoreException(new Status(IStatus.ERROR, BugzillaCorePlugin.PLUGIN_ID, 0, "Report download from "
					+ repository.getUrl() + " failed. File not found: " + e.getMessage(), e));
		} catch (final Exception e) {
			throw new CoreException(new Status(IStatus.ERROR, BugzillaCorePlugin.PLUGIN_ID, 0, "Report download from "
					+ repository.getUrl() + " failed, please see details.", e));
		}
	}

	public AbstractAttributeFactory getAttributeFactory() {
		return attributeFactory;
	}

	public Date getDateForAttributeType(String attributeKey, String dateString) {
		if (dateString == null || dateString.equals("")) {
			return null;
		}
		try {
			String mappedKey = attributeFactory.mapCommonAttributeKey(attributeKey);
			Date parsedDate = null;
			if (mappedKey.equals(BugzillaReportElement.DELTA_TS.getKeyString())) {
				parsedDate = delta_ts_format.parse(dateString);
			} else if (mappedKey.equals(BugzillaReportElement.CREATION_TS.getKeyString())) {
				parsedDate = creation_ts_format.parse(dateString);
			} else if (mappedKey.equals(BugzillaReportElement.BUG_WHEN.getKeyString())) {
				parsedDate = comment_creation_ts_format.parse(dateString);
			} else if (mappedKey.equals(BugzillaReportElement.DATE.getKeyString())) {
				parsedDate = attachment_creation_ts_format.parse(dateString);
			}
			return parsedDate;
		} catch (Exception e) {
			return null;
			// throw new CoreException(new Status(IStatus.ERROR,
			// BugzillaPlugin.PLUGIN_ID, 0,
			// "Error parsing date string: " + dateString, e));
		}
	}

	public Set<AbstractRepositoryTask> getChangedSinceLastSync(TaskRepository repository,
			Set<AbstractRepositoryTask> tasks, Proxy proxySettings) throws CoreException, UnsupportedEncodingException {

		Set<AbstractRepositoryTask> changedTasks = new HashSet<AbstractRepositoryTask>();

		if (repository.getSyncTimeStamp() == null) {
			return tasks;
		}

		String dateString = repository.getSyncTimeStamp();
		if (dateString == null) {
			dateString = "";
		}
		String urlQueryBase;
		String urlQueryString;

		urlQueryBase = repository.getUrl() + CHANGED_BUGS_CGI_QUERY
				+ URLEncoder.encode(dateString, repository.getCharacterEncoding()) + CHANGED_BUGS_CGI_ENDDATE;
		// } catch (UnsupportedEncodingException e1) {
		// MylarStatusHandler.log(e1, "Mylar: Check encoding settings in " +
		// TaskRepositoriesView.NAME + ".");
		// urlQueryBase = repository.getUrl() + CHANGED_BUGS_CGI_QUERY +
		// dateString + CHANGED_BUGS_CGI_ENDDATE;
		// }

		urlQueryString = new String(urlQueryBase + BUG_ID);

		int queryCounter = -1;
		Iterator<AbstractRepositoryTask> itr = tasks.iterator();
		while (itr.hasNext()) {
			queryCounter++;
			ITask task = itr.next();
			String newurlQueryString = URLEncoder.encode(AbstractRepositoryTask.getTaskId(task.getHandleIdentifier())
					+ ",", repository.getCharacterEncoding());
			if ((urlQueryString.length() + newurlQueryString.length() + IBugzillaConstants.CONTENT_TYPE_RDF.length()) > MAX_URL_LENGTH) {
				queryForChanged(repository, changedTasks, urlQueryString, proxySettings);
				queryCounter = 0;
				urlQueryString = new String(urlQueryBase + BUG_ID);
				urlQueryString += newurlQueryString;
			} else if (!itr.hasNext()) {
				urlQueryString += newurlQueryString;				
				queryForChanged(repository, changedTasks, urlQueryString, proxySettings);
			} else {
				urlQueryString += newurlQueryString;
			}
		}		
		return changedTasks;
	}

	private void queryForChanged(TaskRepository repository, Set<AbstractRepositoryTask> changedTasks,
			String urlQueryString, Proxy proxySettings) throws UnsupportedEncodingException, CoreException {

		QueryHitCollector collector = new QueryHitCollector(taskList);
		BugzillaRepositoryQuery query = new BugzillaRepositoryQuery(repository.getUrl(), urlQueryString, "", "-1",
				taskList);
		try {
			BugzillaClient client = connector.getClientManager().getClient(repository);
			client.search(query, collector, taskList);
		} catch (Exception e) {
			throw new CoreException(new Status(IStatus.ERROR, BugzillaCorePlugin.PLUGIN_ID, IStatus.OK,
					"failed to perform query", e));
		}

		// if (repository.hasCredentials()) {
		// urlQueryString = BugzillaClient.addCredentials(urlQueryString,
		// repository.getCharacterEncoding(),
		// repository.getUserName(), repository.getPassword());
		// }
		// try {
		// queryFactory.performQuery(taskList, repository.getUrl(), collector,
		// AbstractReportFactory.RETURN_ALL_HITS);
		// } catch (Exception e) {
		// throw new CoreException(new Status(IStatus.ERROR,
		// BugzillaCorePlugin.PLUGIN_ID, IStatus.OK,
		// "failed to perform query", e));
		// }

		for (AbstractQueryHit hit : collector.getHits()) {
			String handle = AbstractRepositoryTask.getHandle(repository.getUrl(), hit.getId());
			ITask correspondingTask = taskList.getTask(handle);
			if (correspondingTask != null && correspondingTask instanceof AbstractRepositoryTask) {
				AbstractRepositoryTask repositoryTask = (AbstractRepositoryTask) correspondingTask;
				// Hack to avoid re-syncing last task from previous
				// synchronization
				// This can be removed once we are getting a query timestamp
				// from the repository rather than
				// using the last modified stamp of the last task modified in
				// the return hits.
				// (or the changeddate field in the hit rdf becomes consistent,
				// currently it doesn't return a proper modified date string)
				if (repositoryTask.getTaskData() != null
						&& repositoryTask.getTaskData().getLastModified().equals(repository.getSyncTimeStamp())) {
					String taskId = AbstractRepositoryTask.getTaskId(repositoryTask.getHandleIdentifier());
					RepositoryTaskData taskData = downloadTaskData(repository, taskId, proxySettings);
					if (taskData != null && taskData.getLastModified().equals(repository.getSyncTimeStamp())) {
						continue;
					}
				}
				changedTasks.add(repositoryTask);
			}
		}
	}

	public static void addValidOperations(RepositoryTaskData bugReport, String userName) {
		BUGZILLA_REPORT_STATUS status = BUGZILLA_REPORT_STATUS.valueOf(bugReport.getStatus());
		switch (status) {
		case UNCONFIRMED:
		case REOPENED:
		case NEW:
			addOperation(bugReport, BUGZILLA_OPERATION.none, userName);
			addOperation(bugReport, BUGZILLA_OPERATION.accept, userName);
			addOperation(bugReport, BUGZILLA_OPERATION.resolve, userName);
			addOperation(bugReport, BUGZILLA_OPERATION.duplicate, userName);
			addOperation(bugReport, BUGZILLA_OPERATION.reassign, userName);
			addOperation(bugReport, BUGZILLA_OPERATION.reassignbycomponent, userName);
			break;
		case ASSIGNED:
			addOperation(bugReport, BUGZILLA_OPERATION.none, userName);
			addOperation(bugReport, BUGZILLA_OPERATION.resolve, userName);
			addOperation(bugReport, BUGZILLA_OPERATION.duplicate, userName);
			addOperation(bugReport, BUGZILLA_OPERATION.reassign, userName);
			addOperation(bugReport, BUGZILLA_OPERATION.reassignbycomponent, userName);
			break;
		case RESOLVED:
			addOperation(bugReport, BUGZILLA_OPERATION.none, userName);
			addOperation(bugReport, BUGZILLA_OPERATION.reopen, userName);
			addOperation(bugReport, BUGZILLA_OPERATION.verify, userName);
			addOperation(bugReport, BUGZILLA_OPERATION.close, userName);
			break;
		case CLOSED:
			addOperation(bugReport, BUGZILLA_OPERATION.none, userName);
			addOperation(bugReport, BUGZILLA_OPERATION.reopen, userName);
			break;
		case VERIFIED:
			addOperation(bugReport, BUGZILLA_OPERATION.none, userName);
			addOperation(bugReport, BUGZILLA_OPERATION.reopen, userName);
			addOperation(bugReport, BUGZILLA_OPERATION.close, userName);
		}
	}

	public static void addOperation(RepositoryTaskData bugReport, BUGZILLA_OPERATION opcode, String userName) {
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
			for (BUGZILLA_RESOLUTION resolution : BUGZILLA_RESOLUTION.values()) {
				newOperation.addOption(resolution.toString(), resolution.toString());
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
		// MylarStatusHandler.log("Unknown bugzilla operation code recieved",
		// BugzillaRepositoryUtil.class);
		}
		if (newOperation != null) {
			bugReport.addOperation(newOperation);
		}
	}

	
//	// TODO: getAttributeOptions()
//	public void updateAttributeOptions(TaskRepository taskRepository, RepositoryTaskData existingReport)
//			throws IOException, KeyManagementException, GeneralSecurityException, BugzillaException, CoreException {
//
//		RepositoryConfiguration configuration = BugzillaCorePlugin.getDefault().getRepositoryConfiguration(taskRepository, false);
//		if (configuration == null)
//			return;
//		String product = existingReport.getAttributeValue(BugzillaReportElement.PRODUCT.getKeyString());
//		for (RepositoryTaskAttribute attribute : existingReport.getAttributes()) {
//			BugzillaReportElement element = BugzillaReportElement.valueOf(attribute.getID().trim().toUpperCase());
//			attribute.clearOptions();
//			String key = attribute.getID();
//			if (!product.equals("")) {
//				switch (element) {
//				case TARGET_MILESTONE:
//				case VERSION:
//				case COMPONENT:
//					key = product + "." + key;
//				}
//			}
//
//			List<String> optionValues = configuration.getAttributeValues(key);
//			if(optionValues.size() == 0) {
//				optionValues = configuration.getAttributeValues(attribute.getID());
//			}
//
//			if (element != BugzillaReportElement.OP_SYS && element != BugzillaReportElement.BUG_SEVERITY
//					&& element != BugzillaReportElement.PRIORITY && element != BugzillaReportElement.BUG_STATUS) {
//				Collections.sort(optionValues);
//			}
//			if (element == BugzillaReportElement.TARGET_MILESTONE && optionValues.isEmpty()) {
//				existingReport.removeAttribute(BugzillaReportElement.TARGET_MILESTONE);
//				continue;
//			}
//			for (String option : optionValues) {
//				attribute.addOptionValue(option, option);
//			}
//		}
//	}
}
