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
import java.net.Proxy;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaAttributeFactory;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaReportElement;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaServerFacade;
import org.eclipse.mylar.internal.bugzilla.core.UnrecognizedReponseException;
import org.eclipse.mylar.internal.tasks.ui.views.TaskRepositoriesView;
import org.eclipse.mylar.tasks.core.AbstractAttributeFactory;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.IOfflineTaskHandler;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class BugzillaOfflineTaskHandler implements IOfflineTaskHandler {
	
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
				BugzillaPlugin.REPOSITORY_KIND, bugzillaTask.getRepositoryUrl());
		Proxy proxySettings = TasksUiPlugin.getDefault().getProxySettings();
		try {
			int bugId = Integer.parseInt(AbstractRepositoryTask.getTaskId(bugzillaTask.getHandleIdentifier()));

			return BugzillaServerFacade.getBug(repository.getUrl(), repository.getUserName(), repository
					.getPassword(), proxySettings, repository.getCharacterEncoding(), bugId);
		} catch (final LoginException e) {
			throw new CoreException(new Status(IStatus.ERROR, BugzillaPlugin.PLUGIN_ID, 0, "Report download failed. Ensure proper repository configuration of " + bugzillaTask.getRepositoryUrl() + " in "
					+ TaskRepositoriesView.NAME + ".", e ));
		} catch (final UnrecognizedReponseException e) {
			throw new CoreException(new Status(IStatus.ERROR, BugzillaPlugin.PLUGIN_ID, 0, "Report download failed. Unrecognized response from " + bugzillaTask.getRepositoryUrl() + ".", e ));
		} catch (final FileNotFoundException e) {
			throw new CoreException(new Status(IStatus.ERROR, BugzillaPlugin.PLUGIN_ID, 0, "Report download from " + bugzillaTask.getRepositoryUrl() + " failed. File not found: "+e.getMessage(), e ));
		} catch (final Exception e) {
			throw new CoreException(new Status(IStatus.ERROR, BugzillaPlugin.PLUGIN_ID, 0, "Report download from " + bugzillaTask.getRepositoryUrl() + " failed, please see details.", e ));
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
			Date lastModified = null;
			if (mappedKey.equals(BugzillaReportElement.DELTA_TS.getKeyString())) {
				lastModified = delta_ts_format.parse(dateString);
			} else if (mappedKey.equals(BugzillaReportElement.CREATION_TS.getKeyString())) {
				lastModified = creation_ts_format.parse(dateString);
			} else if (mappedKey.equals(BugzillaReportElement.BUG_WHEN.getKeyString())) {
				lastModified = comment_creation_ts_format.parse(dateString);
			} else if (mappedKey.equals(BugzillaReportElement.DATE.getKeyString())) {
				lastModified = attachment_creation_ts_format.parse(dateString);
			}
			return lastModified;
		} catch (Exception e) {
			return null;
			// throw new CoreException(new Status(IStatus.ERROR,
			// BugzillaPlugin.PLUGIN_ID, 0,
			// "Error parsing date string: " + dateString, e));
		}
	}
}
