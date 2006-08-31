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

package org.eclipse.mylar.internal.bugzilla.ui.tasklist;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.internal.bugzilla.core.AbstractReportFactory;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaAttributeFactory;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaReportElement;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaResultCollector;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaServerFacade;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.bugzilla.core.RepositoryQueryResultsFactory;
import org.eclipse.mylar.internal.bugzilla.core.UnrecognizedReponseException;
import org.eclipse.mylar.internal.tasks.ui.views.TaskRepositoriesView;
import org.eclipse.mylar.tasks.core.AbstractAttributeFactory;
import org.eclipse.mylar.tasks.core.AbstractQueryHit;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.IOfflineTaskHandler;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;

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
	
	/** public for testing 
	 * Bugzilla 2.18 uses DATE_FORMAT_1 but later versions use DATE_FORMAT_2
	 * Using lowest common denominator DATE_FORMAT_1  
	 */ 
	public static SimpleDateFormat comment_creation_ts_format = new SimpleDateFormat(DATE_FORMAT_1);
	
	private static SimpleDateFormat attachment_creation_ts_format = new SimpleDateFormat(DATE_FORMAT_1);
	
	private AbstractAttributeFactory attributeFactory = new BugzillaAttributeFactory();
	
	public RepositoryTaskData downloadTaskData(final AbstractRepositoryTask bugzillaTask) throws CoreException {
		TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
				BugzillaCorePlugin.REPOSITORY_KIND, bugzillaTask.getRepositoryUrl());
		
		if(repository == null) throw new CoreException(new Status(IStatus.ERROR, BugzillaCorePlugin.PLUGIN_ID, 0, "Associated repository could not be found. Ensure proper repository configuration of " + bugzillaTask.getRepositoryUrl() + " in "
				+ TaskRepositoriesView.NAME + ".", null ));
		
		Proxy proxySettings = TasksUiPlugin.getDefault().getProxySettings();
		try {
			int bugId = Integer.parseInt(AbstractRepositoryTask.getTaskId(bugzillaTask.getHandleIdentifier()));

			return BugzillaServerFacade.getBug(repository.getUrl(), repository.getUserName(), repository
					.getPassword(), proxySettings, repository.getCharacterEncoding(), bugId);
		} catch (final LoginException e) {
			throw new CoreException(new Status(IStatus.ERROR, BugzillaCorePlugin.PLUGIN_ID, 0, "Report download failed. Ensure proper repository configuration of " + bugzillaTask.getRepositoryUrl() + " in "
					+ TaskRepositoriesView.NAME + ".", e ));
		} catch (final UnrecognizedReponseException e) {
			throw new CoreException(new Status(IStatus.ERROR, BugzillaCorePlugin.PLUGIN_ID, 0, "Report download failed. Unrecognized response from " + bugzillaTask.getRepositoryUrl() + ".", e ));
		} catch (final FileNotFoundException e) {
			throw new CoreException(new Status(IStatus.ERROR, BugzillaCorePlugin.PLUGIN_ID, 0, "Report download from " + bugzillaTask.getRepositoryUrl() + " failed. File not found: "+e.getMessage(), e ));
		} catch (final Exception e) {
			throw new CoreException(new Status(IStatus.ERROR, BugzillaCorePlugin.PLUGIN_ID, 0, "Report download from " + bugzillaTask.getRepositoryUrl() + " failed, please see details.", e ));
		}
	}

	public AbstractAttributeFactory getAttributeFactory() {
		return attributeFactory;
	}
	
	public Date getDateForAttributeType(String attributeKey, String dateString) {
		if(dateString == null || dateString.equals("")) {
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
			Set<AbstractRepositoryTask> tasks) throws Exception {

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

		try {
			urlQueryBase = repository.getUrl() + CHANGED_BUGS_CGI_QUERY
					+ URLEncoder.encode(dateString, repository.getCharacterEncoding()) + CHANGED_BUGS_CGI_ENDDATE;
		} catch (UnsupportedEncodingException e1) {
			MylarStatusHandler.log(e1, "Mylar: Check encoding settings in " + TaskRepositoriesView.NAME + ".");
			urlQueryBase = repository.getUrl() + CHANGED_BUGS_CGI_QUERY + dateString + CHANGED_BUGS_CGI_ENDDATE;
		}

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
				queryForChanged(repository, changedTasks, urlQueryString);
				queryCounter = 0;
				urlQueryString = new String(urlQueryBase + BUG_ID);
				urlQueryString += newurlQueryString;
			} else if (!itr.hasNext()) {
				urlQueryString += newurlQueryString;
				urlQueryString += IBugzillaConstants.CONTENT_TYPE_RDF;
				queryForChanged(repository, changedTasks, urlQueryString);
			} else {
				urlQueryString += newurlQueryString;
			}
		}
		return changedTasks;
	}
	
	private void queryForChanged(TaskRepository repository, Set<AbstractRepositoryTask> changedTasks,
			String urlQueryString) throws Exception {
		RepositoryQueryResultsFactory queryFactory = new RepositoryQueryResultsFactory();
		BugzillaResultCollector collector = new BugzillaResultCollector(TasksUiPlugin.getTaskListManager().getTaskList());
		if(repository.hasCredentials()) {
			urlQueryString = BugzillaServerFacade.addCredentials(urlQueryString, repository.getUserName(), repository.getPassword());
		}
		queryFactory.performQuery(TasksUiPlugin.getTaskListManager().getTaskList(), repository.getUrl(), collector, urlQueryString, TasksUiPlugin.getDefault()
				.getProxySettings(), AbstractReportFactory.RETURN_ALL_HITS, repository.getCharacterEncoding());

		for (AbstractQueryHit hit : collector.getResults()) {
			String handle = AbstractRepositoryTask.getHandle(repository.getUrl(), hit.getId());
			ITask correspondingTask = TasksUiPlugin.getTaskListManager().getTaskList().getTask(handle);
			if (correspondingTask != null && correspondingTask instanceof AbstractRepositoryTask) {
				changedTasks.add((AbstractRepositoryTask) correspondingTask);
			}
		}
	}
}
