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
import org.eclipse.mylar.tasks.core.AbstractAttributeFactory;
import org.eclipse.mylar.tasks.core.AbstractQueryHit;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.IOfflineTaskHandler;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskList;
import org.eclipse.mylar.tasks.core.TaskRepository;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class BugzillaOfflineTaskHandler implements IOfflineTaskHandler {

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

	public BugzillaOfflineTaskHandler(TaskList taskList) {
		this.taskList = taskList;
	}

	public RepositoryTaskData downloadTaskData(final AbstractRepositoryTask bugzillaTask, TaskRepository repository,
			Proxy proxySettings) throws CoreException {
		// TaskRepository repository =
		// TasksUiPlugin.getRepositoryManager().getRepository(
		// BugzillaCorePlugin.REPOSITORY_KIND, bugzillaTask.getRepositoryUrl());

		// Proxy proxySettings = TasksUiPlugin.getDefault().getProxySettings();
		try {
			int bugId = Integer.parseInt(AbstractRepositoryTask.getTaskId(bugzillaTask.getHandleIdentifier()));

			return BugzillaServerFacade.getBug(repository.getUrl(), repository.getUserName(), repository.getPassword(),
					proxySettings, repository.getCharacterEncoding(), bugId);
		} catch (final UnrecognizedReponseException e) {
			throw new CoreException(new Status(IStatus.ERROR, BugzillaCorePlugin.PLUGIN_ID, 0,
					"Report download failed. Unrecognized response from " + bugzillaTask.getRepositoryUrl() + ".", e));
		} catch (final FileNotFoundException e) {
			throw new CoreException(new Status(IStatus.ERROR, BugzillaCorePlugin.PLUGIN_ID, 0, "Report download from "
					+ bugzillaTask.getRepositoryUrl() + " failed. File not found: " + e.getMessage(), e));
		} catch (final Exception e) {
			throw new CoreException(new Status(IStatus.ERROR, BugzillaCorePlugin.PLUGIN_ID, 0, "Report download from "
					+ bugzillaTask.getRepositoryUrl() + " failed, please see details.", e));
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
		Iterator itr = tasks.iterator();
		while (itr.hasNext()) {
			queryCounter++;
			ITask task = (ITask) itr.next();
			String newurlQueryString = URLEncoder.encode(AbstractRepositoryTask.getTaskId(task.getHandleIdentifier())
					+ ",", repository.getCharacterEncoding());
			if ((urlQueryString.length() + newurlQueryString.length() + IBugzillaConstants.CONTENT_TYPE_RDF.length()) > MAX_URL_LENGTH) {
				urlQueryString += IBugzillaConstants.CONTENT_TYPE_RDF;
				queryForChanged(repository, changedTasks, urlQueryString, proxySettings);
				queryCounter = 0;
				urlQueryString = new String(urlQueryBase + BUG_ID);
				urlQueryString += newurlQueryString;
			} else if (!itr.hasNext()) {
				urlQueryString += newurlQueryString;
				urlQueryString += IBugzillaConstants.CONTENT_TYPE_RDF;
				queryForChanged(repository, changedTasks, urlQueryString, proxySettings);
			} else {
				urlQueryString += newurlQueryString;
			}
		}
		return changedTasks;
	}

	private void queryForChanged(TaskRepository repository, Set<AbstractRepositoryTask> changedTasks,
			String urlQueryString, Proxy proxySettings) throws UnsupportedEncodingException, CoreException {
		RepositoryQueryResultsFactory queryFactory = new RepositoryQueryResultsFactory();
		BugzillaResultCollector collector = new BugzillaResultCollector(taskList);
		if (repository.hasCredentials()) {
			urlQueryString = BugzillaServerFacade.addCredentials(urlQueryString, repository.getUserName(), repository
					.getPassword());
		}
		try {
			queryFactory.performQuery(taskList, repository.getUrl(), collector, urlQueryString, proxySettings,
					AbstractReportFactory.RETURN_ALL_HITS, repository.getCharacterEncoding());
		} catch (Exception e) {
			throw new CoreException(new Status(IStatus.ERROR, BugzillaCorePlugin.PLUGIN_ID, IStatus.OK, "failed to perform query", e));
		}

		for (AbstractQueryHit hit : collector.getResults()) {
			String handle = AbstractRepositoryTask.getHandle(repository.getUrl(), hit.getId());
			ITask correspondingTask = taskList.getTask(handle);
			if (correspondingTask != null && correspondingTask instanceof AbstractRepositoryTask) {
				changedTasks.add((AbstractRepositoryTask) correspondingTask);
			}
		}
	}
}
